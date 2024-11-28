package com.hotsharp.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotsharp.exception.BadRequestException;
import com.hotsharp.user.config.JwtProperties;
import com.hotsharp.user.domain.dto.LoginFormDTO;
import com.hotsharp.user.domain.dto.RegisterFormDTO;
import com.hotsharp.user.domain.po.User;
import com.hotsharp.user.domain.vo.UserLoginVO;
import com.hotsharp.user.domain.vo.UserRegisterVO;
import com.hotsharp.user.mapper.UserMapper;
import com.hotsharp.user.service.IUserService;
import com.hotsharp.user.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final PasswordEncoder passwordEncoder;

    private final JwtTool jwtTool;

    private final JwtProperties jwtProperties;

    @Override
    public UserLoginVO login(LoginFormDTO loginDTO) {
        // 1.数据校验
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        // 2.根据用户名或手机号查询
        User user = lambdaQuery().eq(User::getUserName, username).one();
        Assert.notNull(user, "用户名错误");
        // 3.校验密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("用户名或密码错误");
        }
        // 4.生成TOKEN
        String token = jwtTool.createToken(user.getId(), jwtProperties.getTokenTTL());
        // 5.封装VO返回
        UserLoginVO vo = new UserLoginVO()
                .setUserId(user.getId())
                .setUsername(user.getUserName())
                .setToken(token);
        return vo;
    }

    @Override
    public UserRegisterVO register(RegisterFormDTO registerFormDTO){
        String username = registerFormDTO.getUsername();
        String password = registerFormDTO.getPassword();
        // 1.校验用户名是否存在
        User user = lambdaQuery().eq(User::getUserName, username).one();
        Assert.isNull(user, "用户名已存在");
        // 2.密码加密
        String encodePassword = passwordEncoder.encode(password);
        // 3.保存用户
        User newUser = new User()
                .setUserName(username)
                .setPassword(encodePassword);
        save(newUser);
        // 4.生成TOKEN
        String token = jwtTool.createToken(newUser.getId(), jwtProperties.getTokenTTL());
        // 5.返回VO
        UserRegisterVO vo = new UserRegisterVO()
                .setUserId(newUser.getId())
                .setUsername(username)
                .setToken(null);
        return vo;
    }
}
