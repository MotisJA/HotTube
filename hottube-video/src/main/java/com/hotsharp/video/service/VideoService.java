package com.hotsharp.video.service;

import com.hotsharp.video.pojo.dto.VideoInitDTO;
import com.hotsharp.video.pojo.vo.VideoUploadVo;
import org.springframework.web.multipart.MultipartFile;

public interface VideoService {
    VideoUploadVo init(VideoInitDTO videoDTO);

    VideoUploadVo uploadTrunk(MultipartFile file, Integer trunkIndex, String uploadId);

    void complete(String uploadId);
}
