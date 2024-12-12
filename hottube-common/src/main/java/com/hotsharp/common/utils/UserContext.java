package com.hotsharp.common.utils;

public class UserContext {
    private static final ThreadLocal<Long> tl = new ThreadLocal<>();

    /**
     * 保存当前登录用户信息到ThreadLocal
     * @param userId 用户id
     */
    public static void setUser(Integer userId) {
        tl.set(Long.valueOf(userId));
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 用户id
     */
    public static Integer getUserId() {
        if(tl.get() != null){
            return tl.get().intValue();
        }
        return null;
    }

    /**
     * 移除当前登录用户信息
     */
    public static void removeUser(){
        tl.remove();
    }
}
