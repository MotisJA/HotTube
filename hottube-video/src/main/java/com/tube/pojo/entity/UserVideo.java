package com.tube.pojo.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户视频关联表
 * @TableName user_video
 */
@Data
public class UserVideo implements Serializable {
    /**
     * 唯一标识
     */
    private Integer id;

    /**
     * 观看视频的用户UID
     */
    private Integer uid;

    /**
     * 视频ID
     */
    private Integer vid;

    /**
     * 播放次数
     */
    private Integer play;

    /**
     * 点赞 0没赞 1已点赞
     */
    private Integer love;

    /**
     * 不喜欢 0没点 1已不喜欢
     */
    private Integer unlove;

    /**
     * 投币数 0-2 默认0
     */
    private Integer coin;

    /**
     * 收藏 0没收藏 1已收藏
     */
    private Integer collect;

    /**
     * 最近播放时间
     */
    private Date playTime;

    /**
     * 点赞时间
     */
    private Date loveTime;

    /**
     * 投币时间
     */
    private Date coinTime;

    private static final long serialVersionUID = 1L;
}