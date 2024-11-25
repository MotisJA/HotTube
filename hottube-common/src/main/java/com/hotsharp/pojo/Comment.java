package com.hotsharp.pojo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 评论表
 * @TableName comment
 */
@Data
public class Comment implements Serializable {
    /**
     * 评论主id
     */
    private Integer id;

    /**
     * 评论的视频id
     */
    private Integer vid;

    /**
     * 发送者id
     */
    private Integer uid;

    /**
     * 根节点评论的id,如果为0表示为根节点
     */
    private Integer rootId;

    /**
     * 被回复的评论id，只有root_id为0时才允许为0，表示根评论
     */
    private Integer parentId;

    /**
     * 回复目标用户id
     */
    private Integer toUserId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 该条评论的点赞数
     */
    private Integer love;

    /**
     * 不喜欢的数量
     */
    private Integer bad;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 是否置顶 0普通 1置顶
     */
    private Integer isTop;

    /**
     * 软删除 0未删除 1已删除
     */
    private Integer isDeleted;

    private static final long serialVersionUID = 1L;
}