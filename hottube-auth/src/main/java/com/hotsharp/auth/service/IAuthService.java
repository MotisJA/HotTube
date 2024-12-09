package com.hotsharp.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotsharp.auth.domain.dto.LoginFormDTO;
import com.hotsharp.auth.domain.dto.RegisterFormDTO;
import com.hotsharp.auth.domain.po.User;
import com.hotsharp.auth.domain.vo.UserLoginVO;
import com.hotsharp.auth.domain.vo.UserRegisterVO;

public interface IAuthService extends IService<User> {

    UserLoginVO login(LoginFormDTO loginFormDTO);

    UserRegisterVO register(RegisterFormDTO registerFormDTO);

}
