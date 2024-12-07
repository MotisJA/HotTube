package com.hotsharp.configuration;

import com.hotsharp.properties.MinioProperty;
import io.minio.MinioClient;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfiguration {

    @Resource
    private MinioProperty minioProperty;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioProperty.getEndpoint())
                .credentials(minioProperty.getAccessKey(), minioProperty.getSecretKey())
                .build();
    }

}
