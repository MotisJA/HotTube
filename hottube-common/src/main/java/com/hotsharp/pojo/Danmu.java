package com.hotsharp.pojo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 弹幕表
 * @TableName danmu
 */
@Data
public class Danmu implements Serializable {
    /**
     * 弹幕ID
     */
    private Integer id;

    /**
     * 视频ID
     */
    private Integer vid;

    /**
     * 用户ID
     */
    private Integer uid;

    /**
     * 弹幕内容
     */
    private String content;

    /**
     * 字体大小
     */
    private Integer fontsize;

    /**
     * 弹幕模式 1滚动 2顶部 3底部
     */
    private Integer mode;

    /**
     * 弹幕颜色 6位十六进制标准格式
     */
    private String color;

    /**
     * 弹幕所在视频的时间点
     */
    private Double timePoint;

    /**
     * 弹幕状态 1默认过审 2被举报审核中 3删除
     */
    private Integer state;

    /**
     * 发送弹幕的日期时间
     */
    private Date createDate;

    private static final long serialVersionUID = 1L;
}