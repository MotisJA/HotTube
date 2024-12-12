package com.hotsharp.video.service;

import com.hotsharp.video.pojo.dto.VideoInitDTO;
import com.hotsharp.video.pojo.dto.VideoUploadDTO;
import com.hotsharp.video.pojo.vo.VideoUploadVo;
import org.springframework.web.multipart.MultipartFile;

public interface VideoService {
    VideoUploadVo init(VideoInitDTO videoDTO);

    VideoUploadVo uploadTrunk(MultipartFile file, Integer index, String hash);

    void complete(VideoUploadDTO videoUploadDTO);

    int ask(String hash);

    void cancel(String hash);

}
