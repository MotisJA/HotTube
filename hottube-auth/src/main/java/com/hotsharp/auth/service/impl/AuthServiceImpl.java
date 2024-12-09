package com.hotsharp.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotsharp.common.exception.BadRequestException;
import com.hotsharp.auth.config.JwtProperties;
import com.hotsharp.auth.domain.dto.LoginFormDTO;
import com.hotsharp.auth.domain.dto.RegisterFormDTO;
import com.hotsharp.auth.domain.po.User;
import com.hotsharp.auth.domain.vo.UserLoginVO;
import com.hotsharp.auth.domain.vo.UserRegisterVO;
import com.hotsharp.auth.mapper.UserMapper;
import com.hotsharp.auth.service.IAuthService;
import com.hotsharp.auth.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl extends ServiceImpl<UserMapper, User> implements IAuthService {

    private final PasswordEncoder passwordEncoder;

    private final JwtTool jwtTool;

    private final JwtProperties jwtProperties;

    @Override
    public UserLoginVO login(LoginFormDTO loginDTO) {
        // 1.数据校验
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        // 2.根据用户名或手机号查询
        User user = lambdaQuery().eq(User::getUsername, username).one();
        Assert.notNull(user, "用户名错误");
        // 3.校验密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("用户名或密码错误");
        }
        // 4.生成TOKEN
        String token = jwtTool.createToken(Long.valueOf(user.getUid()), jwtProperties.getTokenTTL());
        // 5.封装VO返回
        UserLoginVO vo = new UserLoginVO()
                .setUserId(Long.valueOf(user.getUid()))
                .setUsername(user.getUsername())
                .setToken(token);
        return vo;
    }

    @Override
    public UserRegisterVO register(RegisterFormDTO registerFormDTO){
        String username = registerFormDTO.getUsername();
        String password = registerFormDTO.getPassword();
        String nickname = registerFormDTO.getNickname();
        // 1.校验用户名是否存在
        User user = lambdaQuery().eq(User::getUsername, username).one();
        Assert.isNull(user, "用户名已存在");
        // 2.密码加密
        String encodePassword = passwordEncoder.encode(password);
        // 3.保存用户
        User newUser = new User()
                .setUsername(username)
                .setPassword(encodePassword)
                .setNickname(nickname)
                .setCreateDate(new Date());
        save(newUser);
        // 4.生成TOKEN
        String token = jwtTool.createToken(Long.valueOf(newUser.getUid()), jwtProperties.getTokenTTL());
        // 5.返回VO
        UserRegisterVO vo = new UserRegisterVO()
                .setUserId(Long.valueOf(newUser.getUid()))
                .setUsername(username)
                .setToken(null);
        return vo;
    }
}
