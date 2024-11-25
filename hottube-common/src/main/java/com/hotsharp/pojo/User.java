package com.hotsharp.pojo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户表
 * @TableName user
 */
@Data
public class User implements Serializable {
    /**
     * 用户ID
     */
    private Integer uid;

    /**
     * 用户账号
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像url
     */
    private String avatar;

    /**
     * 主页背景图url
     */
    private String background;

    /**
     * 性别 0女 1男 2未知
     */
    private Integer gender;

    /**
     * 个性签名
     */
    private String description;

    /**
     * 经验值
     */
    private Integer exp;

    /**
     * 硬币数
     */
    private Double coin;

    /**
     * 会员类型 0普通用户 1月度大会员 2季度大会员 3年度大会员
     */
    private Integer vip;

    /**
     * 状态 0正常 1封禁 2注销
     */
    private Integer state;

    /**
     * 角色类型 0普通用户 1管理员 2超级管理员
     */
    private Integer role;

    /**
     * 官方认证 0普通用户 1个人认证 2机构认证
     */
    private Integer auth;

    /**
     * 认证说明
     */
    private String authMsg;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 注销时间
     */
    private Date deleteDate;

    private static final long serialVersionUID = 1L;
}