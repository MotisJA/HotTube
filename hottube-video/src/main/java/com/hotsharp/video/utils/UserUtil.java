package com.hotsharp.video.utils;

import org.springframework.stereotype.Component;

/**
 * 获取用户信息
 */
@Component
public class UserUtil {

    private final Long userId = 123456L; // 暂时模拟 后面对接

    public Long getUserId () {
        return userId;
    }
}
