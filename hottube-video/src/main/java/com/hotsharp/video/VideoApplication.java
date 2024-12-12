package com.hotsharp.video;

import com.hotsharp.api.config.DefaultFeignConfig;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableFeignClients(basePackages = "com.hotsharp.api.client", defaultConfiguration = DefaultFeignConfig.class)
@SpringBootApplication
@Slf4j
@MapperScan("com.hotsharp.video.mapper")
@ComponentScan({"com.hotsharp.common.utils", "com.hotsharp.video"})
@EnableAsync // 开启异步方式
public class VideoApplication {

    public static void main(String[] args) {
        SpringApplication.run(VideoApplication.class, args);
        log.info("## Video application start successfully!");
    }
}
