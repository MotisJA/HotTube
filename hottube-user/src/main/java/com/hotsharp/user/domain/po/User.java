package com.hotsharp.user.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
public class User implements Serializable {

    @TableId(value = "uid", type = IdType.AUTO)
    private Long uid; // 用户ID

    private String username; // 用户账号

    private String password; // 用户密码

    private String nickname; // 用户昵称

    private String avatar; // 用户头像 URL

    private String background; // 主页背景图 URL

    private Byte gender; // 性别 0女 1男 2未知

    private String description; // 个性签名

    private Long exp; // 经验值

    private Double coin; // 硬币数

    private Byte vip; // 会员类型 0普通用户 1月度大会员 2季度大会员 3年度大会员

    private Byte state; // 状态 0正常 1封禁 2注销

    private Byte role; // 角色类型 0普通用户 1管理员 2超级管理员

    private Byte auth; // 官方认证 0普通用户 1个人认证 2机构认证

    private String authMsg; // 认证说明

    private LocalDateTime createDate; // 创建时间

    private LocalDateTime deleteDate; // 注销时间
}


