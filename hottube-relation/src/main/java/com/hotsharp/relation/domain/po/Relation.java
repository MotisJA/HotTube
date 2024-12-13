package com.hotsharp.relation.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("relation")
public class Relation implements Serializable {

    @TableId(value = "follower_id", type = IdType.AUTO)
    private Long followerId;           // 关注者ID

    private Long followedId;           // 被关注者ID

    private boolean status;              // 该关注条目的状态

    private LocalDateTime createdDate;   // 创建时间

    private LocalDateTime deletedDate;   // 删除时间（软删除）

}