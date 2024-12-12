package com.hotsharp.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hotsharp.api.client.FavoriteClient;
import com.hotsharp.api.client.MessageClient;
import com.hotsharp.api.client.UserClient;
import com.hotsharp.api.dto.UserDTO;
import com.hotsharp.auth.mapper.UserMapper;
import com.hotsharp.auth.service.IUserAccountService;
import com.hotsharp.common.domain.Favorite;
import com.hotsharp.common.domain.MsgUnread;
import com.hotsharp.common.domain.User;
import com.hotsharp.common.result.Result;
import com.hotsharp.common.result.Results;
import com.hotsharp.common.utils.ESUtil;
import com.hotsharp.common.utils.JwtUtil;
import com.hotsharp.common.utils.RedisUtil;
import com.hotsharp.common.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserAccountServiceImpl implements IUserAccountService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private MessageClient messageClient;

    @Autowired
    private FavoriteClient favoriteClient;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserMapper userMapper;

//    @Autowired
//    private MsgUnreadMapper msgUnreadMapper;

//    @Autowired
//    private FavoriteMapper favoriteMapper;

//    @Autowired
//    private RedisUtil redisUtil;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ESUtil esUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    @Qualifier("taskExecutor")
    private Executor taskExecutor;

    /**
     * 用户注册
     * @param username 账号
     * @param password 密码
     * @param confirmedPassword 确认密码
     * @return CustomResponse对象
     */
    @Override
    @Transactional
    public Result register(String username, String password, String confirmedPassword) throws IOException {
        Result result = new Result<>();
        if (username == null) {
            return Results.failure(403, "账号不能为空");
        }
        if (password == null || confirmedPassword == null) {

            return Results.failure(403, "密码不能为空");
        }
        username = username.trim();   //删掉用户名的空白符
        if (username.length() == 0) {
            return Results.failure(403, "账号不能为空");
        }
        if (username.length() > 50) {
            return Results.failure(403, "账号长度不能大于50");
        }
        if (password.length() == 0 || confirmedPassword.length() == 0 ) {
            return Results.failure(403, "密码不能为空");
        }
        if (password.length() > 50 || confirmedPassword.length() > 50 ) {
            return Results.failure(403, "密码长度不能大于50");
        }
        if (!password.equals(confirmedPassword)) {
            return Results.failure(403, "两次输入的密码不一致");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        queryWrapper.ne("state", 2);
        User user = userMapper.selectOne(queryWrapper);   //查询数据库里值等于username并且没有注销的数据
        if (user != null) {
            return Results.failure(403, "账号已存在");
        }

        QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.orderByDesc("uid").last("limit 1");    // 降序选第一个
        User last_user = userMapper.selectOne(queryWrapper1);
        int new_user_uid;
        if (last_user == null) {
            new_user_uid = 1;
        } else {
            new_user_uid = last_user.getUid() + 1;
        }
        String encodedPassword = passwordEncoder.encode(password);  // 密文存储
        String avatar_url = "https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png";
        String bg_url = "https://tinypic.host/images/2023/11/15/69PB2Q5W9D2U7L.png";
        Date now = new Date();
        User new_user = new User(
                null,
                username,
                encodedPassword,
                "用户_" + new_user_uid,
                avatar_url,
                bg_url,
                2,
                "这个人很懒，什么都没留下~",
                0,
                (double) 0,
                0,
                0,
                0,
                0,
                null,
                now,
                null
        );
        userMapper.insert(new_user);
        UserContext.setUser(new_user.getUid());
        messageClient.insertMsgUnread(new MsgUnread(new_user.getUid(),0,0,0,0,0,0));
        favoriteClient.insertFavorite(new Favorite(null, new_user.getUid(), 1, 1, null, "默认收藏夹", "", 0, null));
        esUtil.addUser(new_user);
        return Results.success(user).setMessage("注册成功！欢迎加入HotTube");
    }

    /**
     * 用户登录
     * @param username 账号
     * @param password 密码
     * @return CustomResponse对象
     */
    @Override
    public Result login(String username, String password) {
        Result result = new Result<>();

        //验证是否能正常登录
        //将用户名和密码封装成一个类，这个类不会存明文了，将是加密后的字符串
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);

        // 用户名或密码错误会抛出异常
        Authentication authenticate;
        try {
            authenticate = authenticationProvider.authenticate(authenticationToken);
        } catch (Exception e) {
            return Results.failure(403, "账号或密码不正确");
        }

        //将用户取出来
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticate.getPrincipal();
        User user = loginUser.getUser();

        // 顺便更新redis中的数据
        redisUtil.setExObjectValue("user:" + user.getUid(), user);  // 默认存活1小时

        // 检查账号状态，1 表示封禁中，不允许登录
        if (user.getState() == 1) {
            return Results.failure(403, "账号异常，封禁中");
        }

        //将uid封装成一个jwttoken，同时token也会被缓存到redis中
        String token = jwtUtil.createToken(String.valueOf(user.getUid()), "user");

        try {
            // 把完整的用户信息存入redis，时间跟token一样，注意单位
            // 这里缓存的user信息建议只供读取uid用，其中的状态等非静态数据可能不准，所以 redis另外存值
            redisUtil.setExObjectValue("security:user:" + user.getUid(), user, 60L * 60 * 24 * 2, TimeUnit.SECONDS);
            // 将该用户放到redis中在线集合
//            redisUtil.addMember("login_member", user.getUid());
        } catch (Exception e) {
            log.error("存储redis数据失败");
            throw e;
        }

        // 每次登录顺便返回user信息，就省去再次发送一次获取用户个人信息的请求
        UserDTO userDTO = new UserDTO();
        userDTO.setUid(user.getUid());
        userDTO.setNickname(user.getNickname());
        userDTO.setAvatar_url(user.getAvatar());
        userDTO.setBg_url(user.getBackground());
        userDTO.setGender(user.getGender());
        userDTO.setDescription(user.getDescription());
        userDTO.setExp(user.getExp());
        userDTO.setCoin(user.getCoin());
        userDTO.setVip(user.getVip());
        userDTO.setState(user.getState());
        userDTO.setAuth(user.getAuth());
        userDTO.setAuthMsg(user.getAuthMsg());

        Map<String, Object> final_map = new HashMap<>();
        final_map.put("token", token);
        final_map.put("user", userDTO);

        return Results.success(final_map).setMessage("登录成功");
    }

    /**
     * 获取用户个人信息
     * @return CustomResponse对象
     */
    @Override
    public Result personalInfo() {
        Integer loginUserId = UserContext.getUserId();
        UserDTO userDTO = userClient.getUserById(loginUserId).getData();

        // 检查账号状态，1 表示封禁中，不允许登录，2表示账号注销了
        if (userDTO.getState() == 2) {
            return Results.failure(404, "账号已注销");
        }
        if (userDTO.getState() == 1) {
            return Results.failure(403, "账号异常，封禁中");
        }

        return Results.success(userDTO);
    }

    /**
     * 退出登录，清空redis中相关用户登录认证
     */
    @Override
    public void logout() {
        Integer LoginUserId = UserContext.getUserId();
        // 清除redis中该用户的登录认证数据
        redisUtil.delValue("token:user:" + LoginUserId);
        redisUtil.delValue("security:user:" + LoginUserId);
        redisUtil.delMember("login_member", LoginUserId);   // 从在线用户集合中移除
        redisUtil.deleteKeysWithPrefix("whisper:" + LoginUserId + ":"); // 清除全部在聊天窗口的状态
        messageClient.disconnectUser(LoginUserId);
    }

    @Override
    public Result updatePassword(String pw, String npw) {
        if (npw == null || npw.length() == 0) {
            return Results.failure(500, "密码不能为空");
        }

        // 取出当前登录的用户
        UsernamePasswordAuthenticationToken authenticationToken1 =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails1 = (UserDetailsImpl) authenticationToken1.getPrincipal();
        User user = userDetails1.getUser();

        // 验证旧密码
        UsernamePasswordAuthenticationToken authenticationToken2 =
                new UsernamePasswordAuthenticationToken(user.getUsername(), pw);
        try {
            authenticationProvider.authenticate(authenticationToken2);
        } catch (Exception e) {
            return Results.failure(403, "密码不正确");
        }

        if (Objects.equals(pw, npw)) {
            return Results.failure(500, "新密码不能与旧密码相同");
        }

        String encodedPassword = passwordEncoder.encode(npw);  // 密文存储

        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", user.getUid()).set("password", encodedPassword);
        userMapper.update(null, updateWrapper);

        logout();
        return Results.success();
    }
}
