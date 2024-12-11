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
     * TODO:
     * 1.客户端发送上传请求 服务端根据文件名判断是否存在上传记录（redis）
     *      - 如果没有就生成标识-路径 返回同时存入redis
     * 2.开始上传
     */

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
    public Result upload (@RequestBody VideoUploadDTO dto) {
        return Results.success(videoService.uploadTrunk(dto.getChunk(), dto.getIndex(), dto.getHash()));
    }

    @GetMapping("/video/cancel-upload")
    public Result cancel(@RequestParam String hash) {
        videoService.cancel(hash);
        return Results.success();
    }

    @GetMapping("/video/upload/complete")
    public Result complete (@RequestParam String uploadId) {
        videoService.complete(uploadId);
        return Results.success();
    }
}
