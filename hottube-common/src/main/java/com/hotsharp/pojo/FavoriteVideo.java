package com.hotsharp.pojo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 视频收藏夹关系表
 * @TableName favorite_video
 */
@Data
public class FavoriteVideo implements Serializable {
    /**
     * 唯一标识
     */
    private Integer id;

    /**
     * 视频ID
     */
    private Integer vid;

    /**
     * 收藏夹ID
     */
    private Integer fid;

    /**
     * 收藏时间
     */
    private Date time;

    /**
     * 是否移除 null否 1已移除
     */
    private Integer isRemove;

    private static final long serialVersionUID = 1L;
}