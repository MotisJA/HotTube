package com.hotsharp.video.pojo.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class VideoUploadDTO {
    MultipartFile chunk;
    Integer index;
    String hash;
}
