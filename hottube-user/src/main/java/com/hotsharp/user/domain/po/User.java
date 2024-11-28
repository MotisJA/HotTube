package com.hotsharp.user.domain.po;

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
@TableName("users")
public class User implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;                // 用户ID

    private String userName;           // 用户名

    private String password;           // 密码

    private String role;               // 用户角色

    private String avatar;             // 头像 URL

    private String backgroundImage;    // 背景图片 URL

    private String signature;          // 个性签名

    private LocalDateTime createdAt;   // 创建时间

    private LocalDateTime updatedAt;   // 更新时间

    private LocalDateTime deletedAt;   // 删除时间（软删除）

}

