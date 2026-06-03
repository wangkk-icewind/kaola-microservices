package com.kaola.user.controller;

import com.kaola.common.core.dto.Result;
import com.kaola.user.model.dto.LoginDTO;
import com.kaola.user.model.dto.PhoneLoginDTO;
import com.kaola.user.model.dto.UserDTO;
import com.kaola.user.model.vo.LoginVO;
import com.kaola.user.model.vo.UserVO;
import com.kaola.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

/**
 * 用户接口
 *
 * @author Kaola Team
 */
@Slf4j
@Tag(name = "用户接口", description = "用户登录、信息管理等接口")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    /**
     * 微信登录
     *
     * @param code 微信授权码
     * @return 登录结果
     */
    @Operation(summary = "微信登录", description = "通过微信授权码进行登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = userService.login(loginDTO.getCode(), loginDTO.getPhoneCode());
        return Result.success(loginVO);
    }

    /**
     * 手机号登录
     */
    @Operation(summary = "手机号登录", description = "通过手机号和验证码登录")
    @PostMapping("/phone-login")
    public Result<LoginVO> phoneLogin(@Valid @RequestBody PhoneLoginDTO dto) {
        LoginVO loginVO = userService.phoneLogin(dto.getPhone(), dto.getCode(), dto.getWxCode());
        return Result.success(loginVO);
    }

    /**
     * 发送短信验证码
     */
    @Operation(summary = "发送验证码", description = "向手机号发送短信验证码")
    @PostMapping("/send-code")
    public Result<Boolean> sendVerifyCode(@RequestParam String phone) {
        try {
            return Result.success(userService.sendVerifyCode(phone));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @Operation(summary = "获取用户信息", description = "获取当前登录用户的详细信息")
    @GetMapping("/info")
    public Result<UserVO> getUserInfo(
            @Parameter(description = "用户ID", hidden = true)
            @RequestHeader("X-User-Id") Long userId) {
        UserVO userVO = userService.getUserInfo(userId);
        return Result.success(userVO);
    }

    /**
     * 更新用户信息
     *
     * @param userId 用户ID
     * @param dto    更新数据
     * @return 操作结果
     */
    @Operation(summary = "更新用户信息", description = "更新当前登录用户的信息")
    @PutMapping("/info")
    public Result<Boolean> updateUserInfo(
            @Parameter(description = "用户ID", hidden = true)
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UserDTO dto) {
        boolean success = userService.updateUserInfo(userId, dto);
        return Result.success(success);
    }

    /**
     * 绑定手机号
     *
     * @param userId 用户ID
     * @param phone  手机号
     * @param code   验证码
     * @return 操作结果
     */
    @Operation(summary = "绑定手机号", description = "为当前用户绑定手机号")
    @PostMapping("/bindPhone")
    public Result<Boolean> bindPhone(
            @Parameter(description = "用户ID", hidden = true)
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "手机号", required = true)
            @RequestParam @NotBlank(message = "手机号不能为空") String phone,
            @Parameter(description = "验证码", required = true)
            @RequestParam @NotBlank(message = "验证码不能为空") String code) {
        boolean success = userService.bindPhone(userId, phone, code);
        return Result.success(success);
    }
}
