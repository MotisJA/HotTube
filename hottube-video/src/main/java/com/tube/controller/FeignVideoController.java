package com.tube.controller;

import com.hotsharp.common.result.Result;
import com.hotsharp.common.result.Results;
import com.tube.mapper.VideoMapper;
import com.tube.pojo.entity.Video;
import com.tube.service.VideoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/video")
public class FeignVideoController {

    @Resource
    private VideoMapper videoMapper;

    @GetMapping()
    public Result<Video> getVideoById(@RequestParam("id") Integer id) {
        return Results.success(videoMapper.selectById(id));
    }
}
