package com.hotsharp.video.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hotsharp.common.domain.Video;
import com.hotsharp.common.result.Result;
import com.hotsharp.common.result.Results;
import com.hotsharp.video.mapper.VideoMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FeignVideoController {

    @Resource
    private VideoMapper videoMapper;

    @GetMapping("/video/{id}")
    public Result<Video> getVideoById(@PathVariable("id") Integer id) {
        return Results.success(videoMapper.selectById(id));
    }

    @GetMapping("/video/mapper/get")
    Result<List<Video>> selectVideos(@RequestParam Video video){
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>(video);
        return Results.success(videoMapper.selectList(queryWrapper));
    }
}
