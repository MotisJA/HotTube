package com.tube.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 视频表
 * @TableName video
 */
@Data
@TableName("video")
public class Video implements Serializable {
    /**
     * 视频ID
     */
    @TableId
    private Integer vid;

    /**
     * 投稿用户ID
     */
    private Integer uid;

    /**
     * 标题
     */
    private String title;

    /**
     * 类型 1自制 2转载
     */
    private Integer type;

    /**
     * 作者声明 0不声明 1未经允许禁止转载
     */
    private Integer auth;

    /**
     * 播放总时长 单位秒
     */
    private Double duration;

    /**
     * 主分区ID
     */
    private String mcId;

    /**
     * 子分区ID
     */
    private String scId;

    /**
     * 标签 回车分隔
     */
    private String tags;

    /**
     * 简介
     */
    private String desc;

    /**
     * 封面url
     */
    private String coverUrl;

    /**
     * 视频url
     */
    private String videoUrl;

    /**
     * 状态 0转码中 1审核中 2已过审 3未通过 4已删除
     */
    private Integer status;

    /**
     * 上传时间
     */
    private Date uploadDate;

    /**
     * 删除时间
     */
    private Date deleteDate;

    private static final long serialVersionUID = 1L;
}