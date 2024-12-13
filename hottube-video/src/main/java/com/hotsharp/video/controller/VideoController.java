package com.hotsharp.video.controller;

import com.hotsharp.common.domain.Video;
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

    /**
     * 获取单个视频的信息
     * @param vid
     * @return
     */
    @GetMapping("/video/getone")
    public Result getOneVideo(@RequestParam("vid") Integer vid) {
        Map<String, Object> map = videoService.getVideoWithDataById(vid);
        if (map == null) {
            return Results.failure(404, "没有找到该视频");
        }
        Video video = (Video) map.get("video");
        return Results.success(map);
    }

    @GetMapping("/video/user-works-count")
    public Result getUserWorksCount(@RequestParam("uid") Integer uid) {
        return Results.success(redisUtil.zCard("user_video_upload:" + uid)).setMessage("OK");
    }

    /**
     * 获取用户视频投稿
     * @param uid   用户id
     * @param rule  排序方式 1 投稿日期 2 播放量 3 点赞数
     * @param page  分页 从1开始
     * @param quantity  每页查询数量
     * @return  视频信息列表
     */
    @GetMapping("/video/user-works")
    public Result getUserWorks(@RequestParam("uid") Integer uid,
                                       @RequestParam("rule") Integer rule,
                                       @RequestParam("page") Integer page,
                                       @RequestParam("quantity") Integer quantity) {
        Result customResponse = new Result();
        Map<String, Object> map = new HashMap<>();
        Set<Object> set = redisUtil.zReverange("user_video_upload:" + uid, 0, -1);
        if (set == null || set.isEmpty()) {
            map.put("count", 0);
            map.put("list", Collections.emptyList());
            customResponse.setData(map);
            return customResponse;
        }
        List<Integer> list = new ArrayList<>();
        set.forEach(vid -> {
            list.add((Integer) vid);
        });
        map.put("count", set.size());
        switch (rule) {
            case 1:
                map.put("list", videoService.getVideosWithDataByIdsOrderByDesc(list, "upload_date", page, quantity));
                break;
            case 2:
                map.put("list", videoService.getVideosWithDataByIdsOrderByDesc(list, "play", page, quantity));
                break;
            case 3:
                map.put("list", videoService.getVideosWithDataByIdsOrderByDesc(list, "good", page, quantity));
                break;
            default:
                map.put("list", videoService.getVideosWithDataByIdsOrderByDesc(list, "upload_date", page, quantity));
        }
        customResponse.setData(map);
        return customResponse;
    }
}
