package com.hotsharp.common.config;


import com.hotsharp.common.utils.RedisUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {
    @Bean
    public RedisUtil redisUtil(){
        return new RedisUtil();
    }
}
