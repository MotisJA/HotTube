package com.hotsharp.user.controller;

import com.hotsharp.api.dto.UserDTO;
import com.hotsharp.common.result.Result;
import com.hotsharp.common.result.Results;
import com.hotsharp.user.service.IUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "用户信息管理接口")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * 获取用户信息
     * @param uid 用户ID
     * @return  用户信息
     */
    @GetMapping("/info/get-one")
    public Result<UserDTO> getOneUserInfo(@RequestParam("uid") Integer uid) {
        return Results.success(userService.getUserById(uid));
    }

    /**
     * 更新用户部分个人信息
     * @param nickname  昵称
     * @param desc  个性签名
     * @param gender    性别：0 女 1 男 2 保密
     * @return
     */
    @PostMapping("/info/update")
    public Result updateUserInfo(@RequestParam("nickname") String nickname,
                                 @RequestParam("description") String desc,
                                 @RequestParam("gender") Integer gender) {
        return null;
    }

    /**
     * 更新用户头像
     * @param file  头像文件
     * @return  成功则返回新头像url
     */
    @PostMapping("/avatar/update")
    public Result updateUserAvatar(@RequestParam("file") MultipartFile file) {
        return null;
    }


}
