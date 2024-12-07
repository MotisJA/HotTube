package com.hotsharp.favorite.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hotsharp.common.utils.RedisUtil;
import com.hotsharp.favorite.domain.po.UserVideo;
import com.hotsharp.favorite.mapper.UserVideoMapper;
import com.hotsharp.favorite.service.IUserVideoService;
import com.hotsharp.favorite.service.VideoStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class UserVideoServiceImpl implements IUserVideoService {

    @Autowired
    private UserVideoMapper userVideoMapper;

    @Autowired
    private VideoStatsService videoStatsService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    @Qualifier("taskExecutor")
    private Executor taskExecutor;

    /**
     * 更新播放次数以及最近播放时间，顺便返回记录信息，没有记录则创建新记录
     * @param uid   用户ID
     * @param vid   视频ID
     * @return 更新后的数据信息
     */
    @Override
    public UserVideo updatePlay(Integer uid, Integer vid) {
        QueryWrapper<UserVideo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid).eq("vid", vid);
        UserVideo userVideo = userVideoMapper.selectOne(queryWrapper);
        if (userVideo == null) {
            // 记录不存在，创建新记录
            userVideo = new UserVideo(null, uid, vid, 1, 0, 0, 0, 0, new Date(), null, null);
            userVideoMapper.insert(userVideo);
        } else if (System.currentTimeMillis() - userVideo.getPlayTime().getTime() <= 30000) {
            // 如果最近30秒内播放过则不更新记录，直接返回
            return userVideo;
        } else {
            userVideo.setPlay(userVideo.getPlay() + 1);
            userVideo.setPlayTime(new Date());
            userVideoMapper.updateById(userVideo);
        }
        // 异步线程更新video表和redis
        CompletableFuture.runAsync(() -> {
            redisUtil.zset("user_video_history:" + uid, vid);   // 添加到/更新观看历史记录
            videoStatsService.updateStats(vid, "play", true, 1);
        }, taskExecutor);
        return userVideo;
    }

    /**
     * 点赞或点踩，返回更新后的信息
     * @param uid   用户ID
     * @param vid   视频ID
     * @param isLove    赞还是踩 true赞 false踩
     * @param isSet     设置还是取消  true设置 false取消
     * @return  更新后的信息
     */
    @Override
    public UserVideo setLoveOrUnlove(Integer uid, Integer vid, boolean isLove, boolean isSet) {
        String key = "love_video:" + uid;
        QueryWrapper<UserVideo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid).eq("vid", vid);
        UserVideo userVideo = userVideoMapper.selectOne(queryWrapper);

        if (isLove && isSet) {
            // 处理点赞
            return handleLove(uid, vid, key, userVideo);
        } else if (isLove) {
            // 处理取消点赞
            return handleUnlove(uid, vid, key, userVideo);
        } else if (isSet) {
            // 处理点踩
            return handleDislike(uid, vid, key, userVideo);
        } else {
            // 处理取消点踩
            return handleCancelDislike(uid, vid, userVideo);
        }
    }

    /**
     * 收藏或取消收藏
     * @param uid   用户ID
     * @param vid   视频ID
     * @param isCollect 是否收藏 true收藏 false取消
     * @return  返回更新后的信息
     */
    @Override
    public void collectOrCancel(Integer uid, Integer vid, boolean isCollect) {
        UpdateWrapper<UserVideo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", uid).eq("vid", vid);
        if (isCollect) {
            updateWrapper.set("collect", 1);
        } else {
            updateWrapper.set("collect", 0);
        }
        CompletableFuture.runAsync(() -> {
            videoStatsService.updateStats(vid, "collect", isCollect, 1);
        }, taskExecutor);
        userVideoMapper.update(null, updateWrapper);
    }

    private UserVideo handleLove(Integer uid, Integer vid, String key, UserVideo userVideo) {
        if (userVideo.getLove() == 1) {
            return userVideo;
        }
        userVideo.setLove(1);
        UpdateWrapper<UserVideo> updateWrapper = new UpdateWrapper<UserVideo>()
                .eq("uid", uid).eq("vid", vid)
                .set("love", 1).set("love_time", new Date());
        if (userVideo.getUnlove() == 1) {
            userVideo.setUnlove(0);
            updateWrapper.set("unlove", 0);
            CompletableFuture.runAsync(() -> {
            videoStatsService.updateGoodAndBad(vid, true);
            }, taskExecutor);
        } else {
            CompletableFuture.runAsync(() -> {
            videoStatsService.updateStats(vid, "good", true, 1);
            }, taskExecutor);
        }
        redisUtil.zset(key, vid);
        userVideoMapper.update(null, updateWrapper);
        notifyUp(uid, vid);
        return userVideo;
    }

    private UserVideo handleUnlove(Integer uid, Integer vid, String key, UserVideo userVideo) {
        if (userVideo.getLove() == 0) {
            return userVideo;
        }
        userVideo.setLove(0);
        UpdateWrapper<UserVideo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", uid).eq("vid", vid).set("love", 0);
        userVideoMapper.update(null, updateWrapper);
        redisUtil.zsetDelMember(key, vid);
        CompletableFuture.runAsync(() -> {
        videoStatsService.updateStats(vid, "good", false, 1);
        }, taskExecutor);
        return userVideo;
    }

    private UserVideo handleDislike(Integer uid, Integer vid, String key, UserVideo userVideo) {
        if (userVideo.getUnlove() == 1) {
            return userVideo;
        }
        userVideo.setUnlove(1);
        UpdateWrapper<UserVideo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", uid).eq("vid", vid).set("unlove", 1);
        if (userVideo.getLove() == 1) {
            userVideo.setLove(0);
            updateWrapper.set("love", 0);
            redisUtil.zsetDelMember(key, vid);
            CompletableFuture.runAsync(() -> {
            videoStatsService.updateGoodAndBad(vid, false);
            }, taskExecutor);
        } else {
            CompletableFuture.runAsync(() -> {
            videoStatsService.updateStats(vid, "bad", true, 1);
            }, taskExecutor);
        }
        userVideoMapper.update(null, updateWrapper);
        return userVideo;
    }

    private UserVideo handleCancelDislike(Integer uid, Integer vid, UserVideo userVideo) {
        if (userVideo.getUnlove() == 0) {
            return userVideo;
        }
        userVideo.setUnlove(0);
        UpdateWrapper<UserVideo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", uid).eq("vid", vid).set("unlove", 0);
        userVideoMapper.update(null, updateWrapper);
        CompletableFuture.runAsync(() -> {
        videoStatsService.updateStats(vid, "bad", false, 1);
        }, taskExecutor);
        return userVideo;
    }

    // 通知视频的上传者（UP主）他们的视频被点赞了
    private void notifyUp(Integer uid, Integer vid) {
//        CompletableFuture.runAsync(() -> {
//        Video video = videoMapper.selectById(vid);
//        if (!Objects.equals(video.getUid(), uid)) {
//            redisUtil.zset("be_loved_zset:" + video.getUid(), vid);
//            msgUnreadService.addOneUnread(video.getUid(), "love");
//            Map<String, Object> map = new HashMap<>();
//            map.put("type", "接收");
//            Set<Channel> channels = IMServer.userChannel.get(video.getUid());
//            if (channels != null) {
//                for (Channel channel : channels) {
//                    channel.writeAndFlush(IMResponse.message("love", map));
//                }
//            }
//        }
//        }, taskExecutor);
    }
}
