package com.hotsharp.api.config;

import com.hotsharp.common.utils.UserContext;
import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;

public class DefaultFeignConfig {

    /// 定义Feign的日志级别
    @Bean
    public Logger.Level fullFeignLoggerLevel(){
        return Logger.Level.FULL;
    }

    @Bean
    public RequestInterceptor userInfoRequestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                Integer userId = UserContext.getUserId();
                if (userId != null) {
                    template.header("user-info", userId.toString());
                }
            }
        };
    }
}
