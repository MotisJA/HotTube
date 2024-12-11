package com.hotsharp.auth.service;

import com.hotsharp.common.result.Result;

import java.io.IOException;

public interface IUserAccountService {
    /**
     * 用户注册
     * @param username 账号
     * @param password 密码
     * @param confirmedPassword 确认密码
     * @return CustomResponse对象
     */
    Result register(String username, String password, String confirmedPassword) throws IOException;

    /**
     * 用户登录
     * @param username 账号
     * @param password 密码
     * @return CustomResponse对象
     */
    Result login(String username, String password);

//    /**
//     * 管理员登录
//     * @param username 账号
//     * @param password 密码
//     * @return CustomResponse对象
//     */
//    Result adminLogin(String username, String password);

    /**
     * 获取用户个人信息
     * @return CustomResponse对象
     */
    Result personalInfo();

    void logout();

    Result updatePassword(String pw, String npw);
//
//    /**
//     * 获取管理员个人信息
//     * @return CustomResponse对象
//     */
//    Result adminPersonalInfo();
//
//    /**
//     * 退出登录，清空redis中相关用户登录认证
//     */
//    void logout();
//
//    /**
//     * 管理员退出登录，清空redis中相关管理员登录认证
//     */
//    void adminLogout();
//
//    /**
//     * 重置密码
//     * @param pw    旧密码
//     * @param npw   新密码
//     * @return  响应对象
//     */
//    Result updatePassword(String pw, String npw);
}
