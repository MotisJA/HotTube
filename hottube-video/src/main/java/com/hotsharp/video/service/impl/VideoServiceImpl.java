package com.hotsharp.video.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hotsharp.api.client.FavoriteClient;
import com.hotsharp.api.client.UserClient;
import com.hotsharp.api.dto.UserDTO;
import com.hotsharp.common.constant.ContentType;
import com.hotsharp.common.domain.User;
import com.hotsharp.common.domain.Video;
import com.hotsharp.common.domain.VideoStats;
import com.hotsharp.common.utils.RedisUtil;
import com.hotsharp.common.utils.UserContext;
import com.hotsharp.video.constant.RedisConstant;
import com.hotsharp.video.mapper.VideoMapper;
import com.hotsharp.video.pojo.dto.VideoInitDTO;
import com.hotsharp.video.pojo.dto.VideoUploadDTO;
import com.hotsharp.video.pojo.vo.VideoUploadVo;
import com.hotsharp.video.properties.FileProperty;
import com.hotsharp.video.properties.MinioProperty;
import com.hotsharp.video.service.CategoryService;
import com.hotsharp.video.service.VideoProcessService;
import com.hotsharp.video.service.VideoService;
import com.hotsharp.video.utils.MinioUtil;
import jakarta.annotation.Resource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class VideoServiceImpl implements VideoService {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private FileProperty fileProperty;


    @Resource
    private MinioUtil minioUtil;

    @Resource
    private MinioProperty minioProperty;

    @Resource
    private ThreadPoolTaskExecutor minioUploadThreadPool;

    @Resource
    private VideoMapper videoMapper;

    @Resource
    private VideoProcessService videoProcessService;

    @Resource
    private CategoryService categoryService;

    @Resource(name = "taskExecutor")
    private Executor taskExecutor;

    @Resource
    private UserClient userClient;

    @Resource
    private FavoriteClient favoriteClient;

    @Override
    public VideoUploadVo init(VideoInitDTO videoDTO) {
        // 通过文件hash判断文件是否已经上传一部分
        Integer userId = UserContext.getUserId();
        String userFileHash = videoDTO.getFileHash()+userId;
        String uploadId = DigestUtil.sha256(userFileHash).toString();
        String key = uploadId + RedisConstant.VIDEO_UPLOAD_PREFIX;
        VideoUploadVo videoUploadVo = redisUtil.getObject(key, VideoUploadVo.class);
        // 是则返回上传进度
        if (videoUploadVo != null) {
            return videoUploadVo;
        }
        // 创建文件夹
        String path = fileProperty.getTmp() + uploadId;
        // 确保目录存在，如果不存在则创建
        File targetDir = new File(path);
        if (!targetDir.exists()) {
            targetDir.mkdirs();  // 创建文件夹
        }
        // 存入redis 设置超时时间24h
        videoUploadVo = VideoUploadVo.builder().uploadId(uploadId).build();
        redisUtil.setExValue(key, videoUploadVo);
        return videoUploadVo;
    }

    @Override
    public VideoUploadVo uploadTrunk(MultipartFile file, Integer index, String hash) {
        Integer userId = UserContext.getUserId();
        String uploadId = hash+userId;
        String key = RedisConstant.VIDEO_UPLOAD_PREFIX + uploadId;
        VideoUploadVo uploadVo = redisUtil.getObject(key, VideoUploadVo.class);
        // 查看是否重复上传
        if (uploadVo != null && uploadVo.getTrunk() != index-1) return uploadVo;
        if (null == uploadVo) uploadVo = new VideoUploadVo();
        String path = System.getProperty("user.dir") + "/" + fileProperty.getTmp() + uploadId;
        // 确保目录存在，如果不存在则创建
        File targetDir = new File(path);
        if (!targetDir.exists()) {
            targetDir.mkdirs();  // 创建文件夹
        }
        // 将分片文件存储到指定路径，文件名包含分片索引
        String fileName = index + "_" + hash + ".mp4";  // 根据分片索引生成文件名
        File targetFile = new File(path, fileName);
        try {
            // 将上传的文件存储到目标文件
            file.transferTo(targetFile);
            // 返回上传成功信息，VideoUploadVo 可以封装上传的状态和信息
            uploadVo.setTrunk(index);
            redisUtil.setExObjectValue(key, uploadVo);
            return uploadVo;
        } catch (IOException e) {
            e.printStackTrace();
            return uploadVo;
        }
    }

    @Override
    public void complete(VideoUploadDTO videoUploadDTO) {
        String objectName = minioUtil.upload(videoUploadDTO.getCover(), ContentType.JPE);
        String img = minioProperty.getBaseUrl() + objectName;
        Video video = Video.builder()
                .title(videoUploadDTO.getTitle())
                .type(videoUploadDTO.getType())
                .auth(videoUploadDTO.getAuth())
                .duration(videoUploadDTO.getDuration())
                .mcId(videoUploadDTO.getMcid())
                .scId(videoUploadDTO.getScid())
                .tags(videoUploadDTO.getTags())
                .desc(videoUploadDTO.getDesc())
                .uploadDate(new Date())
                .coverUrl(img)
                .status(VideoConstant.VIDEO_STATUS_CONVERTING)
                .videoUrl("")
                .uid(UserContext.getUserId()).build();
        videoMapper.insert(video);
        int i = video.getVid();
        Integer userId = UserContext.getUserId();
        String uploadId = videoUploadDTO.getHash() + userId;
        videoProcessService.merge(i, videoUploadDTO.getHash(), userId);
    }

    @Override
    public int ask(String hash) {
        Integer userId = UserContext.getUserId();
        String userFileHash = hash + userId;
        String uploadId = DigestUtil.sha256(userFileHash).toString();
        String key = uploadId + RedisConstant.VIDEO_UPLOAD_PREFIX;
        VideoUploadVo videoUploadVo = redisUtil.getObject(key, VideoUploadVo.class);
        return videoUploadVo==null ? 0 : videoUploadVo.getTrunk()+1; // 返回下一个需要上传的分片
    }

    @Override
    public void cancel(String hash) {
        // 删除redis
        Integer userId = UserContext.getUserId();
        String userFileHash = hash + userId;
        String uploadId = DigestUtil.sha256(userFileHash).toString();
        String key = uploadId + RedisConstant.VIDEO_UPLOAD_PREFIX;
        redisUtil.removeCache(key);
        // 找到文件夹 删除文件
        String path = fileProperty.getTmp() + uploadId;
        File targetDir = new File(path);
        videoProcessService.deleteFile(targetDir);
    }

    @Override
    public List<Map<String, Object>> getVideosWithDataByIds(Set<Object> set, Integer index, Integer quantity) {
        if (index == null) index = 1;
        if (quantity == null) quantity = 10;
        int startIndex = (index - 1) * quantity;
        int endIndex = startIndex + quantity;
        // 检查数据是否足够满足分页查询
//        if (startIndex > set.size()) {
//            // 如果数据不足以填充当前分页，返回空列表 ??? TODO: 没明白为什么不让查询
//            return Collections.emptyList();
//        }
        // 使用线程安全的集合类 CopyOnWriteArrayList 保证多线程处理共享List不会出现并发问题
        List<Video> videoList = new CopyOnWriteArrayList<>();
        // 直接数据库分页查询
        List<Object> idList = new ArrayList<>(set);
        endIndex = Math.min(endIndex, idList.size());
        List<Object> sublist = idList.subList(startIndex, endIndex);
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("vid", sublist).ne("status", 3);
        videoList = videoMapper.selectList(queryWrapper);
        if (videoList.isEmpty()) return Collections.emptyList();
        // 并行处理每一个视频，提高效率
        // 先将videoList转换为Stream
        Stream<Video> videoStream = videoList.stream();
        List<Map<String, Object>> mapList = videoStream.parallel() // 利用parallel()并行处理
                .map(video -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("video", video);
                    CompletableFuture<Void> userFuture = CompletableFuture.runAsync(() -> {
                        UserDTO userDTO = userClient.getUserById(video.getUid()).getData();
                        User user = new User();
                        BeanUtil.copyProperties(userDTO, user);
                        map.put("user", user);
                        VideoStats videoStats = favoriteClient.getVideoStatusByVid(video.getVid());
                        map.put("stats", videoStats);
                    }, taskExecutor);
                    CompletableFuture<Void> categoryFuture = CompletableFuture.runAsync(() -> {
                        map.put("category", categoryService.getCategoryById(video.getMcId(), video.getScId()));
                    }, taskExecutor);
                    // 使用join()等待全部任务完成
                    userFuture.join();
                    categoryFuture.join();
                    return map;
                })
                .collect(Collectors.toList());
        return mapList;
    }

    @Override
    public List<Integer> getActiveVideoIds() {
        QueryWrapper<Video> queryWrapper = new QueryWrapper();
        queryWrapper.eq("status", 2);
        queryWrapper.select("vid");
        return videoMapper.selectObjs(queryWrapper);
    }

    @Override
    public Map<String, Object> getVideoWithDataById(Integer vid) {
        Map<String, Object> map = new HashMap<>();
        // 先查询 redis
        Video video = redisUtil.getObject(RedisConstant.VIDEO_PREFIX + vid, Video.class);
        if (video == null) {
            // redis 查不到再查数据库
            QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("vid", vid);
            video = videoMapper.selectOne(queryWrapper);
            if (video != null) {
                Video finalVideo1 = video;
                CompletableFuture.runAsync(() -> {
                    redisUtil.setExObjectValue("video:" + vid, finalVideo1);    // 异步更新到redis
                }, taskExecutor);
            } else  {
                return null;
            }
        }
        // 多线程异步并行查询用户信息和分区信息并封装
        Video finalVideo = video;
        CompletableFuture<Void> userFuture = CompletableFuture.runAsync(() -> {
            map.put("user", userClient.getUserById(finalVideo.getUid()));
            map.put("stats", favoriteClient.getVideoStatusByVid(finalVideo.getVid()));
        }, taskExecutor);
        CompletableFuture<Void> categoryFuture = CompletableFuture.runAsync(() -> {
            map.put("category", categoryService.getCategoryById(finalVideo.getMcId(), finalVideo.getScId()));
        }, taskExecutor);
        map.put("video", video);
        // 使用join()等待userFuture和categoryFuture任务完成
        userFuture.join();
        categoryFuture.join();
        return map;
    }
}
