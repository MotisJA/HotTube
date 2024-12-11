package com.hotsharp.api.client;

import com.hotsharp.common.domain.Video;
import com.hotsharp.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("video-service")
public interface VideoClient {

    @GetMapping("/video/{id}")
    Result<Video> getVideoById(@PathVariable("id") Integer id);

    @GetMapping("/video/mapper/get")
    Result<List<Video>> selectVideos(@RequestParam Video video);
}
