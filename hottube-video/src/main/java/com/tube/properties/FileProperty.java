package com.tube.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "file.video")
@Data
@Component
public class FileProperty {
    private String tmp;
    private String m3u8;
}
