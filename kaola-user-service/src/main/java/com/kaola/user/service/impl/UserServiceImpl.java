package com.kaola.user.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kaola.common.util.JwtUtil;
import com.kaola.user.mapper.SystemSettingMapper;
import com.kaola.user.mapper.UserMapper;
import com.kaola.user.model.dto.UserDTO;
import com.kaola.user.model.entity.User;
import com.kaola.user.model.vo.LoginVO;
import com.kaola.user.model.vo.UserVO;
import com.kaola.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final SystemSettingMapper systemSettingMapper;
    private final RestTemplate restTemplate;
    private final StringRedisTemplate redisTemplate;

    private static final String SMS_CODE_KEY_PREFIX = "sms:code:";
    private static final Duration SMS_CODE_TTL = Duration.ofMinutes(5);
    private static final String MOCK_SMS_CODE = "123456";
    private static final String ORDER_SERVICE_URL = "http://localhost:8086";

    @Override
    public LoginVO login(String code) {
        log.info("用户微信登录, code: {}", code);

        String loginMode = systemSettingMapper.findValueByKey("wx_login_mode");
        boolean isMockMode = loginMode == null || "mock".equals(loginMode) || code.startsWith("devlocal");

        String openId;
        if (isMockMode) {
            // Mock模式: code作为openId标识，固定返回测试用户
            openId = "mock_" + (code.isEmpty() ? "devlocal" : code);
            log.info("Mock登录模式, openId: {}", openId);
        } else {
            // 真实微信登录
            openId = getOpenIdFromWeChat(code);
            if (openId == null) {
                throw new RuntimeException("微信登录失败，请重试");
            }
        }

        // 查找或创建用户
        User user = userMapper.findByOpenId(openId);
        if (user == null) {
            user = new User();
            user.setOpenId(openId);
            user.setNickname(isMockMode ? "测试用户" : "微信用户");
            user.setStatus(1);
            user.setPoint(0);
            user.setLevel(1);
            userMapper.insert(user);
            log.info("新用户注册, openId: {}, userId: {}", openId, user.getId());
        }

        return buildLoginVO(user, isNewUser(user.getId()));
    }

    @Override
    public boolean sendVerifyCode(String phone) {
        log.info("发送验证码, phone: {}", phone);
        // 存入Redis（Mock环境固定为123456，真实环境调用短信服务后存入随机码）
        redisTemplate.opsForValue().set(SMS_CODE_KEY_PREFIX + phone, MOCK_SMS_CODE, SMS_CODE_TTL);
        log.info("验证码已存入Redis: phone={}, code={}, ttl={}min", phone, MOCK_SMS_CODE, SMS_CODE_TTL.toMinutes());
        return true;
    }

    @Override
    public LoginVO phoneLogin(String phone, String verifyCode) {
        log.info("手机号登录, phone: {}", phone);

        if (verifyCode == null || verifyCode.trim().isEmpty()) {
            throw new RuntimeException("验证码不能为空");
        }

        // 从Redis校验验证码
        String cachedCode = redisTemplate.opsForValue().get(SMS_CODE_KEY_PREFIX + phone);
        if (cachedCode == null) {
            throw new RuntimeException("验证码已过期，请重新发送");
        }
        if (!verifyCode.equals(cachedCode)) {
            throw new RuntimeException("验证码错误");
        }
        // 验证通过后删除验证码（一次性使用）
        redisTemplate.delete(SMS_CODE_KEY_PREFIX + phone);

        User user = userMapper.findByPhone(phone);
        if (user == null) {
            // 自动注册
            user = new User();
            user.setPhone(phone);
            user.setNickname("用户" + phone.substring(7));
            user.setStatus(1);
            user.setPoint(0);
            user.setLevel(1);
            userMapper.insert(user);
            log.info("手机号新用户注册, phone: {}, userId: {}", phone, user.getId());
        }

        return buildLoginVO(user, isNewUser(user.getId()));
    }

    private String getOpenIdFromWeChat(String code) {
        try {
            String appId = systemSettingMapper.findValueByKey("wx_app_id");
            String appSecret = systemSettingMapper.findValueByKey("wx_app_secret");
            if (appId == null || appId.isEmpty() || appSecret == null || appSecret.isEmpty()) {
                log.error("微信AppID或AppSecret未配置，请在系统设置中配置");
                return null;
            }
            String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appId, appSecret, code);
            String response = restTemplate.getForObject(url, String.class);
            JSONObject json = JSON.parseObject(response);
            if (json.containsKey("errcode")) {
                log.error("微信登录失败: {}", response);
                return null;
            }
            return json.getString("openid");
        } catch (Exception e) {
            log.error("调用微信登录API失败", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private boolean isNewUser(Long userId) {
        try {
            String url = ORDER_SERVICE_URL + "/order/internal/user/" + userId + "/has-completed";
            java.util.Map response = restTemplate.getForObject(url, java.util.Map.class);
            if (response != null && Integer.valueOf(0).equals(response.get("code"))) {
                // data=true 表示有已完成订单（老客），isNewUser 取反
                return !Boolean.TRUE.equals(response.get("data"));
            }
        } catch (Exception e) {
            log.warn("查询用户历史完成订单失败，默认为老客: userId={}", userId, e);
        }
        return false;
    }

    private LoginVO buildLoginVO(User user, boolean isNewUser) {
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(jwtUtil.generateToken(user.getId()));
        loginVO.setTokenType("Bearer");
        loginVO.setExpiresIn(86400L);
        loginVO.setIsNewUser(isNewUser);
        loginVO.setNeedBindPhone(user.getPhone() == null || user.getPhone().isEmpty());

        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setNickname(user.getNickname() != null ? user.getNickname() : "用户" + user.getId());
        userVO.setAvatar(user.getAvatar());
        userVO.setPhone(user.getPhone());
        userVO.setGender(user.getGender() != null ? user.getGender() : 0);
        userVO.setCity(user.getCity());
        userVO.setPoint(user.getPoint() != null ? user.getPoint() : 0);
        userVO.setLevel(user.getLevel() != null ? user.getLevel() : 1);
        userVO.setLevelName(getLevelName(user.getLevel()));
        userVO.setCreateTime(user.getCreateTime());
        loginVO.setUserInfo(userVO);

        return loginVO;
    }

    /** 会员等级名称（与 t_user.level 注释一致） */
    private String getLevelName(Integer level) {
        if (level == null) return "普通会员";
        switch (level) {
            case 1: return "普通会员";
            case 2: return "银卡会员";
            case 3: return "金卡会员";
            case 4: return "钻石会员";
            default: return "普通会员";
        }
    }

    @Override
    public UserVO getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            UserVO vo = new UserVO();
            vo.setId(userId);
            vo.setNickname("用户" + userId);
            return vo;
        }
        return buildUserVO(user);
    }

    @Override
    public boolean updateUserInfo(Long userId, UserDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null) return false;
        if (dto.getNickname() != null) user.setNickname(dto.getNickname());
        if (dto.getAvatar() != null) user.setAvatar(dto.getAvatar());
        if (dto.getGender() != null) user.setGender(dto.getGender());
        if (dto.getCity() != null) user.setCity(dto.getCity());
        userMapper.updateById(user);
        return true;
    }

    @Override
    public boolean bindPhone(Long userId, String phone, String code) {
        User user = userMapper.selectById(userId);
        if (user == null) return false;
        // TODO: 验证短信验证码
        user.setPhone(phone);
        userMapper.updateById(user);
        return true;
    }

    private UserVO buildUserVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setPhone(user.getPhone());
        vo.setGender(user.getGender() != null ? user.getGender() : 0);
        vo.setCity(user.getCity());
        vo.setPoint(user.getPoint() != null ? user.getPoint() : 0);
        vo.setLevel(user.getLevel() != null ? user.getLevel() : 1);
        vo.setLevelName(getLevelName(user.getLevel()));
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }
}
