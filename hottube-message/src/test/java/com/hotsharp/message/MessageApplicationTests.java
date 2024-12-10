package com.hotsharp.message;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class MessageApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void testUserInfoInterceptorBean() {
        // 检查UserInfoInterceptor是否被注册为Bean
        boolean beanExists = applicationContext.containsBean("userInfoInterceptor");
        assertThat(beanExists).isTrue();
    }
}