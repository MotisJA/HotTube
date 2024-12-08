package com.hotsharp.video.pojo.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class VideoUploadVo {
    private String uploadId;
    private List<Integer> trunks;
}
