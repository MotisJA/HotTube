package com.hotsharp.auth.controller;

import com.hotsharp.common.result.Result;
import com.hotsharp.common.result.Results;
import com.hotsharp.auth.domain.dto.LoginFormDTO;
import com.hotsharp.auth.domain.dto.RegisterFormDTO;
import com.hotsharp.auth.domain.po.User;
import com.hotsharp.auth.domain.vo.UserLoginVO;
import com.hotsharp.auth.domain.vo.UserRegisterVO;
import com.hotsharp.auth.service.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "登录认证接口")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthControllor {

    private final IAuthService userService;

    @Operation(summary = "获取用户信息")
    @GetMapping("/{userId}")
    public Result<User> getUserInfo(@PathVariable Long userId){
        return Results.success(userService.getById(userId));
    }

    @Operation(summary = "用户登录接口")
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody @Validated LoginFormDTO loginFormDTO){
        return Results.success(userService.login(loginFormDTO));
    }

    @Operation(summary = "用户注册接口")
    @PostMapping("/register")
    public Result<UserRegisterVO> register(RegisterFormDTO registerFormDTO){

        return Results.success(userService.register(registerFormDTO));
    }
}
