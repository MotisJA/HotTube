package com.hotsharp.user.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "minio")
@Data
public class MinioProperty {

    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucket;

    /**
     * 返回基础网址
     * @return
     */
    public String getBaseUrl () {
        return endpoint + bucket + "/";
    }

}
