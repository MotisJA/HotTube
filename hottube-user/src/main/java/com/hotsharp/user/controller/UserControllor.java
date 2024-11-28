package com.hotsharp.user.controller;

import com.hotsharp.result.Result;
import com.hotsharp.result.Results;
import com.hotsharp.user.domain.dto.LoginFormDTO;
import com.hotsharp.user.domain.dto.RegisterFormDTO;
import com.hotsharp.user.domain.po.User;
import com.hotsharp.user.domain.vo.UserLoginVO;
import com.hotsharp.user.domain.vo.UserRegisterVO;
import com.hotsharp.user.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "用户相关接口")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserControllor {

    private final IUserService userService;

    @ApiOperation("获取用户信息")
    @GetMapping("/{userId}")
    public Result<User> getUserInfo(@PathVariable Long userId){
        return Results.success(userService.getById(userId));
    }

    @ApiOperation("用户登录接口")
    @PostMapping("login")
    public Result<UserLoginVO> login(@RequestBody @Validated LoginFormDTO loginFormDTO){
        return Results.success(userService.login(loginFormDTO));
    }

    @ApiOperation("用户注册接口")
    @PostMapping("register")
    public Result<UserRegisterVO> register(RegisterFormDTO registerFormDTO){
        return Results.success(userService.register(registerFormDTO));
    }

}
