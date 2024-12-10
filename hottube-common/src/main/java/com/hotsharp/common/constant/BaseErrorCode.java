package com.hotsharp.common.constant;

public enum BaseErrorCode implements IErrorCode {

    // ========== 一级宏观错误码 客户端错误 ==========
    CLIENT_ERROR(100001, "用户端错误"),

    // ========== 二级宏观错误码 用户注册错误 ==========
    USER_REGISTER_ERROR(100100, "用户注册错误"),
    USER_NAME_VERIFY_ERROR(100110, "用户名校验失败"),
    USER_NAME_EXIST_ERROR(100111, "用户名已存在"),
    USER_NAME_SENSITIVE_ERROR(100112, "用户名包含敏感词"),
    USER_NAME_SPECIAL_CHARACTER_ERROR(100113, "用户名包含特殊字符"),
    PASSWORD_VERIFY_ERROR(100120, "密码校验失败"),
    PASSWORD_SHORT_ERROR(100121, "密码长度不够"),
    PHONE_VERIFY_ERROR(100151, "手机格式校验失败"),

    // ========== 二级宏观错误码 系统请求缺少幂等Token ==========
    IDEMPOTENT_TOKEN_NULL_ERROR(100200, "幂等Token为空"),
    IDEMPOTENT_TOKEN_DELETE_ERROR(100201, "幂等Token已被使用或失效"),

    // ========== 一级宏观错误码 系统执行出错 ==========
    SERVICE_ERROR(200001, "系统执行出错"),
    // ========== 二级宏观错误码 系统执行超时 ==========
    SERVICE_TIMEOUT_ERROR(200100, "系统执行超时"),

    // ========== 一级宏观错误码 调用第三方服务出错 ==========
    REMOTE_ERROR(300001, "调用第三方服务出错");

    private final Integer code;
    private final String message;

    BaseErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}