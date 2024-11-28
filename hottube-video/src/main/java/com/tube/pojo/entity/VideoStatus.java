package com.tube.pojo.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 视频数据统计表
 * @TableName video_status
 */
@Data
public class VideoStatus implements Serializable {
    /**
     * 视频ID
     */
    private Integer vid;

    /**
     * 播放量
     */
    private Integer play;

    /**
     * 弹幕数
     */
    private Integer danmu;

    /**
     * 点赞数
     */
    private Integer good;

    /**
     * 点踩数
     */
    private Integer bad;

    /**
     * 投币数
     */
    private Integer coin;

    /**
     * 收藏数
     */
    private Integer collect;

    /**
     * 分享数
     */
    private Integer share;

    /**
     * 评论数量统计
     */
    private Integer comment;

    private static final long serialVersionUID = 1L;
}