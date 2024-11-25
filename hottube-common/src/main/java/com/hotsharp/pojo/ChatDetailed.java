package com.hotsharp.pojo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 聊天记录表
 * @TableName chat_detailed
 */
@Data
public class ChatDetailed implements Serializable {
    /**
     * 唯一主键
     */
    private Integer id;

    /**
     * 消息发送者
     */
    private Integer userId;

    /**
     * 消息接收者
     */
    private Integer anotherId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 发送者是否删除
     */
    private Integer userDel;

    /**
     * 接受者是否删除
     */
    private Integer anotherDel;

    /**
     * 是否撤回
     */
    private Integer withdraw;

    /**
     * 消息发送时间
     */
    private Date time;

    private static final long serialVersionUID = 1L;
}