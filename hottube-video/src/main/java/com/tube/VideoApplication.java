package com.tube;

import com.hotsharp.api.config.DefaultFeignConfig;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.hotsharp.api.client", defaultConfiguration = DefaultFeignConfig.class)
@SpringBootApplication
@Slf4j
@MapperScan("com.tube.mapper")
public class VideoApplication {

    public static void main(String[] args) {
        SpringApplication.run(VideoApplication.class, args);
        log.info("## Video application start successfully!");
    }
}
