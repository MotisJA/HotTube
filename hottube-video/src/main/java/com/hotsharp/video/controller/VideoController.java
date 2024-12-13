package com.hotsharp.video.controller;

import com.hotsharp.common.result.Result;
import com.hotsharp.common.result.Results;
import com.hotsharp.common.utils.RedisUtil;
import com.hotsharp.video.constant.RedisConstant;
import com.hotsharp.video.pojo.dto.VideoInitDTO;
import com.hotsharp.video.pojo.dto.VideoUploadDTO;
import com.hotsharp.video.service.VideoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class VideoController {

    @Resource
    private VideoService videoService;

    @Resource
    private RedisUtil redisUtil;

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

    /**
     * 游客访问随机推荐
     * @return
     */
    @GetMapping("/video/random/visitor")
    public Result randomVideos() {
        Set<Object> set = redisUtil.srandmember(RedisConstant.VIDEO_STATUS_ACTIVE, 11);
        if (null == set || set.isEmpty()) {
            // 如果没有数据则查询并插入
            List<Integer> dbIds = videoService.getActiveVideoIds();
            if (dbIds != null && !dbIds.isEmpty()) {
                // 将数据库中获取的ID放入Redis
                redisUtil.addMembers(RedisConstant.VIDEO_STATUS_ACTIVE,  dbIds.stream().collect(Collectors.toList()));
                // 再次从Redis中获取数据
                set = redisUtil.srandmember(RedisConstant.VIDEO_STATUS_ACTIVE, 11);
            }
        }
        List<Map<String, Object>> list = new ArrayList<>();
        if (null != set && !set.isEmpty()) {
            list = videoService.getVideosWithDataByIds(set, 1, 11);
            Collections.shuffle(list);
        }
        return Results.success(list);
    }
}
