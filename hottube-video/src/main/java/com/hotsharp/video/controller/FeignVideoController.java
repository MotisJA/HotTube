package com.hotsharp.video.controller;

import com.hotsharp.api.dto.VideoDTO;
import com.hotsharp.common.result.Result;
import com.hotsharp.common.result.Results;
import com.hotsharp.video.mapper.VideoMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

@RestController
public class FeignVideoController {

    @Resource
    private VideoMapper videoMapper;

    @GetMapping("/video/{id}")
    public Result<VideoDTO> getVideoById(@PathVariable("id") Integer id) {
        VideoDTO videoDTO = new VideoDTO();
        BeanUtils.copyProperties(videoMapper.selectById(id), videoDTO);
        return Results.success(videoDTO);
    }
}
