package com.hotsharp.service;

import com.hotsharp.pojo.dto.VideoDTO;
import com.hotsharp.pojo.entity.Video;

import java.util.List;

public interface VideoService {
    void publish(VideoDTO videoDTO);

    void updateStatus(Integer vid, Integer status);

    List<Video> list();

    boolean deleteByVid(Integer vid);
}
