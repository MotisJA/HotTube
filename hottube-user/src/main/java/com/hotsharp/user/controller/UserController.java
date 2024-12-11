package com.hotsharp.user.controller;

import com.hotsharp.api.dto.UserDTO;
import com.hotsharp.common.result.Result;
import com.hotsharp.common.result.Results;
import com.hotsharp.common.utils.UserContext;
import com.hotsharp.user.service.IUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "用户信息管理接口")
@RestController
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * 获取用户信息
     * @param uid 用户ID
     * @return  用户信息
     */
    @GetMapping("/user/info/get-one")
    public Result<UserDTO> getUserById(@RequestParam("uid") Integer uid) {
        return Results.success(userService.getUserById(uid));
    }

    /**
     * 更新用户部分个人信息
     * @param nickname  昵称
     * @param desc  个性签名
     * @param gender    性别：0 女 1 男 2 保密
     * @return
     */
    @PostMapping("/user/info/update")
    public Result updateUserInfo(@RequestParam("nickname") String nickname,
                                 @RequestParam("description") String desc,
                                 @RequestParam("gender") Integer gender) {
        Integer uid = UserContext.getUserId();
        try {
            return userService.updateUserInfo(uid, nickname, desc, gender);
        } catch (Exception e) {
            e.printStackTrace();
            return Results.failure(500, "更新用户信息失败");
        }
    }

    /**
     * 更新用户头像
     * @param file  头像文件
     * @return  成功则返回新头像url
     */
    @PostMapping("/user/avatar/update")
    public Result updateUserAvatar(@RequestParam("file") MultipartFile file) {
        Integer uid = UserContext.getUserId();
        try {
            return userService.updateUserAvatar(uid, file);
        } catch (Exception e) {
            e.printStackTrace();
            return Results.failure(500, "头像更新失败");
        }
    }

}
