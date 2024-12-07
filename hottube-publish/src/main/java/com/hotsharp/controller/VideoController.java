package com.hotsharp.controller;

import com.hotsharp.common.result.Result;
import com.hotsharp.common.result.Results;
import com.hotsharp.pojo.dto.VideoDTO;
import com.hotsharp.service.VideoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/video/publish")
public class VideoController {

    @Resource
    private VideoService videoService;

    @PostMapping
    public Result publish(@RequestBody VideoDTO videoDTO) {
        videoService.publish(videoDTO);
        return Results.success();
    }

    @PutMapping("/status/{vid}")
    public Result updateStatus(@PathVariable Integer vid, @RequestParam Integer status) {
        videoService.updateStatus(vid, status);
        return Results.success();
    }

    @GetMapping("/list")
    public Result list() {
        return Results.success(videoService.list());
    }

    @DeleteMapping("/{vid}")
    public Result delete(@PathVariable Integer vid) {
        if(videoService.deleteByVid(vid)) return Results.success();
        return Results.failure();
    }
}
