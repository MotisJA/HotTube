package com.hotsharp.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hotsharp.api.dto.UserDTO;
import com.hotsharp.common.domain.User;
import com.hotsharp.common.result.Result;
import com.hotsharp.common.result.Results;
import com.hotsharp.common.utils.ESUtil;
import com.hotsharp.common.utils.RedisUtil;
import com.hotsharp.user.mapper.UserMapper;
import com.hotsharp.user.properties.MinioProperty;
import com.hotsharp.user.service.IUserService;
import com.hotsharp.user.utils.MinioUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Autowired
    private ESUtil esUtil;

    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private MinioProperty minioProperty;

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

        // 如果用户状态为 2（账号已注销），设置默认值
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

    @Override
    public List<UserDTO> getUserByIdList(List<Integer> list) {
        if (list.isEmpty()) return Collections.emptyList();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("uid", list).ne("state", 2);
        List<User> users = userMapper.selectList(queryWrapper);
        if (users.isEmpty()) return Collections.emptyList();
        return list.stream().parallel().flatMap(
                uid -> {
                    User user = users.stream()
                            .filter(u -> Objects.equals(u.getUid(), uid))
                            .findFirst()
                            .orElse(null);
                    if (user == null) return Stream.empty();
                    UserDTO userDTO = new UserDTO(
                            user.getUid(),
                            user.getNickname(),
                            user.getAvatar(),
                            user.getBackground(),
                            user.getGender(),
                            user.getDescription(),
                            user.getExp(),
                            user.getCoin(),
                            user.getVip(),
                            user.getState(),
                            user.getAuth(),
                            user.getAuthMsg(),
                            0,0,0,0,0
                    );
                    Set<Object> set = redisUtil.zReverange("user_video_upload:" + user.getUid(), 0L, -1L);
                    if (set == null || set.size() == 0) {
                        return Stream.of(userDTO);
                    }

//                    // 并发执行每个视频数据统计的查询任务
//                    List<VideoStats> videoStatsList = set.stream().parallel()
//                            .map(vid -> videoStatsService.getVideoStatsById((Integer) vid))
//                            .collect(Collectors.toList());
//
//                    int video = videoStatsList.size(), love = 0, play = 0;
//                    for (VideoStats videoStats : videoStatsList) {
//                        love = love + videoStats.getGood();
//                        play = play + videoStats.getPlay();
//                    }
//                    userDTO.setVideoCount(video);
//                    userDTO.setLoveCount(love);
//                    userDTO.setPlayCount(play);
                    return Stream.of(userDTO);
                }
        ).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Result updateUserInfo(Integer uid, String nickname, String desc, Integer gender) throws IOException {
        if (nickname == null || nickname.trim().length() == 0) {
            return Results.failure(500, "昵称不能为空");
        }
        if (nickname.length() > 24 || desc.length() > 100) {
            return Results.failure(500, "输入字符过长");
        }
        if (Objects.equals(nickname, "账号已注销")) {
            return Results.failure(500, "昵称非法");
        }
        // 查重
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("nickname", nickname).ne("uid", uid);
        User user = userMapper.selectOne(queryWrapper);
        if (user != null) {
            return Results.failure(500, "该昵称已被其他用户占用");
        }
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", uid)
                .set("nickname", nickname)
                .set("description", desc)
                .set("gender", gender);
        userMapper.update(null, updateWrapper);
        User new_user = new User().setUid(uid).setNickname(nickname);
        esUtil.updateUser(new_user);
        redisUtil.delValue("user:" + uid);
        return Results.success();
    }

    @Override
    public Result updateUserAvatar(Integer uid, MultipartFile multipartFile) throws IOException {
        // 保存封面到OSS，返回URL
        String avatar_url = minioUtil.uploadImage(multipartFile);
        // 查旧的头像地址
        User user = userMapper.selectById(uid);
        // 先更新数据库
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", uid).set("avatar", avatar_url);
        userMapper.update(null, updateWrapper);
        CompletableFuture.runAsync(() -> {
            redisUtil.delValue("user:" + uid);  // 删除redis缓存
            // 如果就头像不是初始头像就去删除 minio 的源文件
            if (user.getAvatar().startsWith(minioProperty.getBaseUrl())) {
                String filename = user.getAvatar().substring(minioProperty.getBaseUrl().length());
//                System.out.println("要删除的源文件：" + filename);
                minioUtil.remove(filename);
            }
        }, taskExecutor);
        return Results.success(avatar_url).setMessage("更新头像成功");
    }
}
