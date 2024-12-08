package com.hotsharp.video.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "ffmpeg")
@Component
@Data
public class FfmpegProperty {
    private String path;
}
