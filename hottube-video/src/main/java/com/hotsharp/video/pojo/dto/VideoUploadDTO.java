package com.hotsharp.video.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoUploadDTO {
    MultipartFile cover;
    String hash;
    String title;
    Integer type;
    Integer auth;
    Double duration;
    String mcid;
    String scid;
    String tags;
    String desc;
}
