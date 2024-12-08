package com.hotsharp.auth.controller;

import com.hotsharp.auth.service.IUserAccountService;
import com.hotsharp.common.result.Result;
import com.hotsharp.common.result.Results;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "用户账户接口")
@RestController
@RequestMapping("/auth/account")
public class UserAccountController {

    @Autowired
    private IUserAccountService userAccountService;

    /**
     * 注册接口
     * @param map 包含 username password confirmedPassword 的 map
     * @return CustomResponse对象
     */
    @Operation(summary = "用户注册接口")
    @PostMapping("/register")
    public Result register(@RequestBody Map<String, String> map) {
        String username = map.get("username");
        String password = map.get("password");
        String confirmedPassword = map.get("confirmedPassword");
        try {
            return userAccountService.register(username, password, confirmedPassword);
        } catch (Exception e) {
            e.printStackTrace();
            return Results.failure("500", "注册失败");
        }
    }

    /**
     * 登录接口
     * @param map 包含 username password 的 map
     * @return CustomResponse对象
     */
    @Operation(summary = "用户登录接口")
    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> map) {
        String username = map.get("username");
        String password = map.get("password");
        return userAccountService.login(username, password);
    }


}
