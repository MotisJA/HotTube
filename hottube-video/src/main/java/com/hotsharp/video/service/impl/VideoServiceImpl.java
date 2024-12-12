package com.hotsharp.video.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.hotsharp.common.constant.ContentType;
import com.hotsharp.common.domain.Video;
import com.hotsharp.common.utils.RedisUtil;
import com.hotsharp.common.utils.UserContext;
import com.hotsharp.video.constant.FileConstant;
import com.hotsharp.video.constant.RedisConstant;
import com.hotsharp.video.mapper.VideoMapper;
import com.hotsharp.video.pojo.dto.VideoInitDTO;
import com.hotsharp.video.pojo.dto.VideoUploadDTO;
import com.hotsharp.video.pojo.vo.VideoUploadVo;
import com.hotsharp.video.properties.FileProperty;
import com.hotsharp.video.properties.MinioProperty;
import com.hotsharp.video.service.VideoProcessService;
import com.hotsharp.video.service.VideoService;
import com.hotsharp.video.utils.MinioUtil;
import jakarta.annotation.Resource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.Date;

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
}
