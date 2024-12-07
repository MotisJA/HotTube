package com.hotsharp.service;

import com.hotsharp.pojo.dto.VideoInitDTO;
import com.hotsharp.pojo.vo.VideoUploadVo;
import org.springframework.web.multipart.MultipartFile;

public interface VideoService {
    VideoUploadVo init(VideoInitDTO videoDTO);

    VideoUploadVo uploadTrunk(MultipartFile file, Integer trunkIndex, String uploadId);

    void complete(String uploadId);
}
