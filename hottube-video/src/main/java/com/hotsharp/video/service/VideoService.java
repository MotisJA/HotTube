package com.hotsharp.video.service;

import com.hotsharp.video.pojo.dto.VideoInitDTO;
import com.hotsharp.video.pojo.dto.VideoUploadDTO;
import com.hotsharp.video.pojo.vo.VideoUploadVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface VideoService {
    VideoUploadVo init(VideoInitDTO videoDTO);

    VideoUploadVo uploadTrunk(MultipartFile file, Integer index, String hash);

    void complete(VideoUploadDTO videoUploadDTO);

    int ask(String hash);

    void cancel(String hash);

    List<Map<String, Object>> getVideosWithDataByIds(Set<Object> set, Integer index, Integer count);

    List<Integer> getActiveVideoIds();
}
