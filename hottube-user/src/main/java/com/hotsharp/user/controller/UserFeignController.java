package com.hotsharp.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hotsharp.api.dto.UserDTO;
import com.hotsharp.common.domain.User;
import com.hotsharp.user.mapper.UserMapper;
import com.hotsharp.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserFeignController {

    @Autowired
    private IUserService userService;

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/user/info/get-list")
    public List<UserDTO> getUserByIdList(@RequestBody List<Integer> list) {
        return userService.getUserByIdList(list);
    }

    @PostMapping("/user/info/get")
    List<User> getUserList(@RequestBody User user){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>(user);
        return userMapper.selectList(queryWrapper);
    }
}
