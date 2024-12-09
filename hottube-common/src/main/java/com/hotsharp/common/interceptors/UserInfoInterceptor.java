package com.hotsharp.common.interceptors;

import cn.hutool.core.util.StrUtil;
import com.hotsharp.common.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserInfoInterceptor implements HandlerInterceptor {

    /**
     * 在请求处理之前拦截请求，提取用户信息并存入 ThreadLocal。
     *
     * @param request  HTTP 请求对象
     * @param response HTTP 响应对象
     * @param handler  请求的处理器
     * @return true 表示继续处理请求，false 表示拦截请求
     * @throws Exception 发生异常时抛出
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头中获取用户信息
        String userInfo = request.getHeader("uid");

        // 判断用户信息是否为空，如果不为空则解析并存入 UserContext
        if (StrUtil.isNotBlank(userInfo)) {
            try {
                Long userId = Long.valueOf(userInfo);
                UserContext.setUser(userId); // 存储用户信息到 ThreadLocal
            } catch (NumberFormatException e) {
                // 如果 user-info 无法解析为 Long，返回 400 错误
                System.err.println("无效的 uid 值：" + userInfo);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return false; // 拦截请求
            }
        }

        // 放行请求
        return true;
    }

    /**
     * 在请求处理完成后清理 ThreadLocal 中的用户信息，防止内存泄漏。
     *
     * @param request  HTTP 请求对象
     * @param response HTTP 响应对象
     * @param handler  请求的处理器
     * @param ex       处理过程中抛出的异常
     * @throws Exception 发生异常时抛出
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理 ThreadLocal 中的用户信息
        UserContext.removeUser();
    }
}
