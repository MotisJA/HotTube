package com.hotsharp.relation;

import com.hotsharp.relation.config.JwtProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@MapperScan("com.hotsharp.relation.mapper")
@EnableConfigurationProperties({JwtProperties.class})
@SpringBootApplication
public class RelationApplication {
    public static void main(String[] args) {
        SpringApplication.run(RelationApplication.class, args);
    }
}