package com.hotsharp.video.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 分区表
 * @TableName category
 */
@Data
@TableName("category")
public class Category implements Serializable {
    /**
     * 主分区ID
     */
    private String mcId;

    /**
     * 子分区ID
     */
    private String scId;

    /**
     * 主分区名称
     */
    private String mcName;

    /**
     * 子分区名称
     */
    private String scName;

    /**
     * 描述
     */
    private String desc;

    /**
     * 推荐标签
     */
    private String rcmTag;

    private static final long serialVersionUID = 1L;
}