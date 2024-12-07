package com.hotsharp.user.service.impl;

import com.hotsharp.api.dto.UserDTO;
import com.hotsharp.common.utils.RedisUtil;
import com.hotsharp.user.domain.po.User;
import com.hotsharp.user.mapper.UserMapper;
import com.hotsharp.user.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    @Qualifier("taskExecutor")
    private Executor taskExecutor;

    @Override
    public UserDTO getUserById(Integer id) {
        // 从Redis中获取用户数据
        User user = redisUtil.getObject("user:" + id, User.class);
        if (user == null) {
            // 如果Redis中没有数据，从MySQL中获取并更新到Redis
            user = userMapper.selectById(id);
            if (user == null) {
                return null; // 如果用户不存在，返回null
            }
            User finalUser = user;
            CompletableFuture.runAsync(() -> {
                redisUtil.setExObjectValue("user:" + finalUser.getUid(), finalUser); // 异步更新Redis
            }, taskExecutor);
        }

        // 构建UserDTO对象
        UserDTO userDTO = new UserDTO().setUid(user.getUid()).setState(user.getState());

        // 如果用户状态为2（账号已注销），设置默认值
        if (user.getState() == 2) {
            return userDTO.setNickname("账号已注销")
                    .setAvatar_url("https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png")
                    .setBg_url("https://tinypic.host/images/2023/11/15/69PB2Q5W9D2U7L.png")
                    .setGender(2).setDescription("-").setExp(0).setCoin(0.0).setVip(0).setAuth(0)
                    .setVideoCount(0).setFollowsCount(0).setFansCount(0).setLoveCount(0).setPlayCount(0);
        }

        // 设置用户的实际信息
        userDTO.setNickname(user.getNickname())
                .setAvatar_url(user.getAvatar()).setBg_url(user.getBackground()).setGender(user.getGender())
                .setDescription(user.getDescription()).setExp(user.getExp()).setCoin(user.getCoin())
                .setVip(user.getVip()).setAuth(user.getAuth()).setAuthMsg(user.getAuthMsg())
                .setFollowsCount(0).setFansCount(0);

        // 获取用户视频统计信息
        Set<Object> set = redisUtil.zReverange("user_video_upload:" + user.getUid(), 0L, -1L);
        if (set == null || set.isEmpty()) {
            return userDTO.setVideoCount(0).setLoveCount(0).setPlayCount(0);
        }

//        // 并行获取视频统计信息
//        List<CompletableFuture<VideoStats>> futures = set.stream()
//                .map(vid -> CompletableFuture.supplyAsync(() -> videoStatsService.getVideoStatsById((Integer) vid)))
//                .collect(Collectors.toList());
//
//        List<VideoStats> list = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
//
//        // 计算视频数量、点赞数和播放数
//        int video = list.size(), love = 0, play = 0;
//        for (VideoStats videoStats : list) {
//            love += videoStats.getGood();
//            play += videoStats.getPlay();
//        }
//
//        // 设置视频统计信息
//        userDTO.setVideoCount(video).setLoveCount(love).setPlayCount(play);

        return userDTO;
    }
}
