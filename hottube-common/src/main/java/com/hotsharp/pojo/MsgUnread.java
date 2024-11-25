package com.hotsharp.pojo;

import java.io.Serializable;
import lombok.Data;

/**
 * 消息未读数
 * @TableName msg_unread
 */
@Data
public class MsgUnread implements Serializable {
    /**
     * 用户ID
     */
    private Integer uid;

    /**
     * 回复我的
     */
    private Integer reply;

    /**
     * @我的
     */
    private Integer at;

    /**
     * 收到的赞
     */
    private Integer love;

    /**
     * 系统通知
     */
    private Integer system;

    /**
     * 我的消息
     */
    private Integer whisper;

    /**
     * 动态
     */
    private Integer dynamic;

    private static final long serialVersionUID = 1L;
}