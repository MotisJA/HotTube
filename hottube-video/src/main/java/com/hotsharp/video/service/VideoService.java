package com.hotsharp.video.service;

import com.hotsharp.video.pojo.dto.VideoInitDTO;
import com.hotsharp.video.pojo.dto.VideoUploadDTO;
import com.hotsharp.video.pojo.vo.VideoUploadVo;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
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

    List<Map<String, Object>> getVideosWithDataByIdList(List<Integer> list);

    List<Integer> getActiveVideoIds();

    Map<String, Object> getVideoWithDataById(Integer vid);

    List<Map<String, Object>> getVideosWithDataByIdsOrderByDesc(List<Integer> idList, @Nullable String column, Integer page, Integer quantity);
}
