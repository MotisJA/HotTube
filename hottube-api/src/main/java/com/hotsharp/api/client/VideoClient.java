package com.hotsharp.api.client;

import com.hotsharp.common.domain.Video;
import com.hotsharp.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@FeignClient("video-service")
public interface VideoClient {

    @GetMapping("/video/{id}")
    Result<Video> getVideoById(@PathVariable("id") Integer id);

    @GetMapping("/video/mapper/get")
    Result<List<Video>> selectVideos(@RequestParam Video video);

    @PostMapping("/video/feign/getbyids")
    List<Map<String, Object>> getVideosWithDataByIdList(@RequestBody List<Integer> list);

    @PostMapping("/video/feign/getbydesc")
    List<Map<String, Object>> getVideosWithDataByIdsOrderByDesc(@RequestBody List<Integer> idList,
                                                                @RequestParam @Nullable String column,
                                                                @RequestParam Integer page,
                                                                @RequestParam Integer quantity);
}
