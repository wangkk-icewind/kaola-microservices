package com.kaola.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaola.auth.mapper.AdminUserMapper;
import com.kaola.auth.model.entity.AdminRole;
import com.kaola.auth.model.entity.AdminUser;
import com.kaola.auth.model.vo.AdminLoginVO;
import com.kaola.auth.service.AdminRoleService;
import com.kaola.auth.service.AdminUserService;
import com.kaola.common.core.exception.BusinessException;
import com.kaola.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理员用户服务实现
 *
 * @author Kaola Team
 */
@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, AdminUser> implements AdminUserService {

    private final AdminRoleService adminRoleService;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public AdminLoginVO login(String username, String password) {
        // 查询用户
        AdminUser user = getByUsername(username);
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 检查状态
        if (user.getStatus() != 1) {
            throw new BusinessException("账户已被禁用");
        }

        // 获取角色信息
        AdminRole role = null;
        List<String> permissions = new ArrayList<>();
        if (user.getRoleId() != null) {
            role = adminRoleService.getById(user.getRoleId());
            if (role != null && role.getPermissions() != null) {
                try {
                    permissions = objectMapper.readValue(role.getPermissions(),
                        new TypeReference<List<String>>() {});
                } catch (Exception e) {
                    // 解析失败使用空列表
                }
            }
        }

        // 生成token，所有admin用户都使用ADMIN角色
        String token = jwtUtil.generateToken(user.getId(), java.util.Map.of("role", "ADMIN"));

        // 门店信息暂时置为null，待store-service创建后通过OpenFeign调用
        String storeName = null;
        // TODO: 集成store-service后，通过OpenFeign调用获取门店信息
        // if (user.getStoreId() != null) {
        //     StoreDTO store = storeClient.getStoreById(user.getStoreId());
        //     if (store != null) {
        //         storeName = store.getName();
        //     }
        // }

        // 构建返回结果
        AdminLoginVO vo = new AdminLoginVO();
        vo.setToken(token);
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setRoleId(user.getRoleId());
        vo.setRoleName(role != null ? role.getName() : "");
        vo.setStoreId(user.getStoreId());
        vo.setStoreName(storeName);
        vo.setPermissions(permissions);

        return vo;
    }

    @Override
    public AdminUser getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<AdminUser>()
            .eq(AdminUser::getUsername, username));
    }

    /**
     * 加密密码
     */
    public static String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
