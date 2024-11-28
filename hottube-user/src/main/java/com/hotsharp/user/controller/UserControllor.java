package com.hotsharp.user.controller;

import com.hotsharp.common.result.Result;
import com.hotsharp.common.result.Results;
import com.hotsharp.user.domain.dto.LoginFormDTO;
import com.hotsharp.user.domain.dto.RegisterFormDTO;
import com.hotsharp.user.domain.po.User;
import com.hotsharp.user.domain.vo.UserLoginVO;
import com.hotsharp.user.domain.vo.UserRegisterVO;
import com.hotsharp.user.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "用户相关接口")
public class UserControllor {

    private final IUserService userService;

    @Operation(summary = "获取用户信息")
    @GetMapping("/{userId}")
    public Result<User> getUserInfo(@PathVariable Long userId){
        return Results.success(userService.getById(userId));
    }

    @Operation(summary = "用户登录接口")
    @PostMapping("login")
    public Result<UserLoginVO> login(@RequestBody @Validated LoginFormDTO loginFormDTO){
        return Results.success(userService.login(loginFormDTO));
    }

    @Operation(summary = "用户注册接口")
    @PostMapping("register")
    public Result<UserRegisterVO> register(RegisterFormDTO registerFormDTO){
        return Results.success(userService.register(registerFormDTO));
    }

}
