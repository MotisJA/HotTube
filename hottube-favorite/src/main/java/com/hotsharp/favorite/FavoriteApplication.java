package com.hotsharp.favorite;

import com.hotsharp.api.config.DefaultFeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableFeignClients(basePackages = "com.hotsharp.api.client", defaultConfiguration = DefaultFeignConfig.class)
@MapperScan("com.hotsharp.favorite.mapper")
@SpringBootApplication
@ComponentScan({"com.hotsharp.common.utils", "com.hotsharp.favorite"})
public class FavoriteApplication {
    public static void main(String[] args) {
        SpringApplication.run(FavoriteApplication.class, args);
    }
}
