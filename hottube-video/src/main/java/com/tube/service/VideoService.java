package com.tube.service;

import com.tube.pojo.dto.VideoInitDTO;
import com.tube.pojo.vo.VideoUploadVo;
import org.springframework.web.multipart.MultipartFile;

public interface VideoService {
    VideoUploadVo init(VideoInitDTO videoDTO);

    VideoUploadVo uploadTrunk(MultipartFile file, Integer trunkIndex, String uploadId);
}
