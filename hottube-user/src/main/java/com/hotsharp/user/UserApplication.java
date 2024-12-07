package com.hotsharp.user;

import com.hotsharp.api.config.DefaultFeignConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableFeignClients(basePackages = "com.hotsharp.api.client", defaultConfiguration = DefaultFeignConfig.class)
@SpringBootApplication
@ComponentScan("com.hotsharp.common.utils")
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
