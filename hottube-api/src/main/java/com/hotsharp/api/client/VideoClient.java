package com.hotsharp.api.client;

import com.hotsharp.api.dto.VideoDTO;
import com.hotsharp.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("video-service")
@RequestMapping("/video")
public interface VideoClient {

    @GetMapping("/{id}")
    Result<VideoDTO> getVideoById(@PathVariable("id") Integer id);
}
