package com.hotsharp.search;

import com.hotsharp.api.config.DefaultFeignConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableFeignClients(basePackages = "com.hotsharp.api.client", defaultConfiguration = DefaultFeignConfig.class)
@ComponentScan(basePackages = {"com.hotsharp.search", "com.hotsharp.common.utils"})
@SpringBootApplication
public class SearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(SearchApplication.class, args);
    }
}
