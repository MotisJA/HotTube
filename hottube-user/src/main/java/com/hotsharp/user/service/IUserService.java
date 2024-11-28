package com.hotsharp.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotsharp.user.domain.dto.LoginFormDTO;
import com.hotsharp.user.domain.dto.RegisterFormDTO;
import com.hotsharp.user.domain.po.User;
import com.hotsharp.user.domain.vo.UserLoginVO;
import com.hotsharp.user.domain.vo.UserRegisterVO;

public interface IUserService extends IService<User> {

    UserLoginVO login(LoginFormDTO loginFormDTO);

    UserRegisterVO register(RegisterFormDTO registerFormDTO);
}
