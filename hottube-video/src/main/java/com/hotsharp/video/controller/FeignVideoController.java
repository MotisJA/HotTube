package com.hotsharp.video.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hotsharp.common.domain.Video;
import com.hotsharp.common.result.Result;
import com.hotsharp.common.result.Results;
import com.hotsharp.video.mapper.VideoMapper;
import com.hotsharp.video.service.VideoService;
import com.hotsharp.video.service.impl.VideoServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class FeignVideoController {

    @Resource
    private VideoMapper videoMapper;

    @Resource
    private VideoService videoService;

    @GetMapping("/video/{id}")
    public Result<Video> getVideoById(@PathVariable("id") Integer id) {
        return Results.success(videoMapper.selectById(id));
    }

    @GetMapping("/video/mapper/get")
    Result<List<Video>> selectVideos(@RequestParam Video video){
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>(video);
        return Results.success(videoMapper.selectList(queryWrapper));
    }

    @PostMapping("/video/feign/getbyids")
    public List<Map<String, Object>> getVideosWithDataByIdList(@RequestBody List<Integer> list){
        return videoService.getVideosWithDataByIdList(list);
    }
}
