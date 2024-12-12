package com.hotsharp.video.controller;

import com.hotsharp.common.result.Result;
import com.hotsharp.common.result.Results;
import com.hotsharp.video.pojo.dto.VideoInitDTO;
import com.hotsharp.video.pojo.dto.VideoUploadDTO;
import com.hotsharp.video.service.VideoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class VideoController {

    @Resource
    private VideoService videoService;

    /**
     * 初始化接口 创建临时目录
     * @param videoInitDTO
     * @return
     */
    @PostMapping("/video/upload/init")
    public Result init (@RequestBody VideoInitDTO videoInitDTO) {
        return Results.success(videoService.init(videoInitDTO));
    }

    @GetMapping("/video/ask-chunk")
    public Result askChunk(@RequestParam String hash) {
        return Results.success(videoService.ask(hash));
    }

    /**
     * @return
     */
    @PostMapping("/video/upload-chunk")
    public Result upload (@RequestParam("chunk") MultipartFile chunk,
                          @RequestParam("hash") String hash,
                          @RequestParam("index") Integer index) {
        return Results.success(videoService.uploadTrunk(chunk, index, hash));
    }

    @GetMapping("/video/cancel-upload")
    public Result cancel(@RequestParam String hash) {
        videoService.cancel(hash);
        return Results.success();
    }

    @PostMapping("/video/add")
    public Result complete (VideoUploadDTO videoUploadDTO) {
        videoService.complete(videoUploadDTO);
        return Results.success();
    }
}
