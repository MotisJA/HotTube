package com.hotsharp.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HotTube系统API")
                        .version("1.0")
                        .description("Knife4j集成接口文档")
                        .termsOfService("http://www.lhz.com")
                        .license(new License().name("Apache 2.0")));
    }
}
