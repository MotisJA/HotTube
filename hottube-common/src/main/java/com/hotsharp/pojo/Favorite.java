package com.hotsharp.pojo;

import java.io.Serializable;
import lombok.Data;

/**
 * 收藏夹
 * @TableName favorite
 */
@Data
public class Favorite implements Serializable {
    /**
     * 收藏夹ID
     */
    private Integer fid;

    /**
     * 所属用户ID
     */
    private Integer uid;

    /**
     * 收藏夹类型 1默认收藏夹 2用户创建
     */
    private Integer type;

    /**
     * 对外开放 0隐藏 1公开
     */
    private Integer visible;

    /**
     * 收藏夹封面
     */
    private String cover;

    /**
     * 标题
     */
    private String title;

    /**
     * 简介
     */
    private String description;

    /**
     * 收藏夹中视频数量
     */
    private Integer count;

    /**
     * 是否删除 0否 1已删除
     */
    private Integer isDelete;

    private static final long serialVersionUID = 1L;
}