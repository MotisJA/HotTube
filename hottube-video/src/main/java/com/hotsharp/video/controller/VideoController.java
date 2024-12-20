package com.hotsharp.video.controller;

import com.hotsharp.common.constant.BaseErrorCode;
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
     * 随机推荐
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
            return Results.failure(BaseErrorCode.SERVICE_ERROR.code(), "没有找到该视频");
        }
//        Video video = (Video) map.get("video");
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

    /**
     * 累加获取更多视频
     * @param vids  曾经查询过的视频id列表，用于去重
     * @return  每次返回新的10条视频，以及其id列表，并标注是否还有更多视频可以获取
     */
    @GetMapping("/video/cumulative/visitor")
    public Result cumulativeVideosForVisitor(@RequestParam("vids") String vids) {
        Result customResponse = new Result();
        Map<String, Object> map = new HashMap<>();
        List<Integer> vidsList = new ArrayList<>();
        if (vids.trim().length() > 0) {
            vidsList = Arrays.stream(vids.split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());  // 从字符串切分出id列表
        }
        Set<Object> set = redisUtil.getMembers("video_status:1");
        if (set == null) {
            map.put("videos", new ArrayList<>());
            map.put("vids", new ArrayList<>());
            map.put("more", false);
            customResponse.setData(map);
            return customResponse;
        }
        vidsList.forEach(set::remove);  // 去除已获取的元素
        Set<Object> idSet = new HashSet<>();    // 存放将要返回的id集合
        Random random = new Random();
        // 随机获取10个vid
        for (int i = 0; i < 10 && !set.isEmpty(); i++) {
            Object[] arr = set.toArray();
            int randomIndex = random.nextInt(set.size());
            idSet.add(arr[randomIndex]);
            set.remove(arr[randomIndex]);   // 查过的元素移除
        }
        List<Map<String, Object>> videoList = new ArrayList<>();
        if (!idSet.isEmpty()) {
            videoList = videoService.getVideosWithDataByIds(idSet, 1, 10);
            Collections.shuffle(videoList);     // 随机打乱列表顺序
        }
        map.put("videos", videoList);
        map.put("vids", idSet);
        if (!set.isEmpty()) {
            map.put("more", true);
        } else {
            map.put("more", false);
        }
        customResponse.setData(map);
        return customResponse;
    }
}
