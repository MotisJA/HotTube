package com.tube.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.tube.constant.RedisConstant;
import com.tube.pojo.dto.VideoInitDTO;
import com.tube.pojo.vo.VideoUploadVo;
import com.tube.properties.FileProperty;
import com.tube.service.VideoService;
import com.tube.utils.RedisUtil;
import com.tube.utils.UserUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class VideoServiceImpl implements VideoService {

    @Resource
    private UserUtil userUtil;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private FileProperty fileProperty;

    @Override
    public VideoUploadVo init(VideoInitDTO videoDTO) {
        // 通过文件hash判断文件是否已经上传一部分
        Long userId = userUtil.getUserId();
        String userFileHash = videoDTO.getFileHash()+userId;
        String uploadId = DigestUtil.sha256(userFileHash).toString();
        String key = uploadId + RedisConstant.VIDEO_UPLOAD_PREFIX;
        VideoUploadVo videoUploadVo = redisUtil.get(key, VideoUploadVo.class);
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
        redisUtil.put(key, videoUploadVo, RedisConstant.VIDEO_UPLOAD_TIMEOUT, TimeUnit.HOURS);
        return videoUploadVo;
    }

    @Override
    public VideoUploadVo uploadTrunk(MultipartFile file, Integer trunkIndex, String uploadId) {
        String key = RedisConstant.VIDEO_UPLOAD_PREFIX + uploadId;
        VideoUploadVo uploadVo = redisUtil.get(key, VideoUploadVo.class);
        // 查看是否重复上传
        if (uploadVo.getTrunks().contains(trunkIndex)) return uploadVo;
        String path = fileProperty.getTmp() + uploadId;
        // 确保目录存在，如果不存在则创建
        File targetDir = new File(path);
        if (!targetDir.exists()) {
            targetDir.mkdirs();  // 创建文件夹
        }
        // 将分片文件存储到指定路径，文件名包含分片索引
        String fileName = trunkIndex + "_" + file.getOriginalFilename();  // 根据分片索引生成文件名
        File targetFile = new File(path, fileName);
        try {
            // 将上传的文件存储到目标文件
            file.transferTo(targetFile);
            // 返回上传成功信息，VideoUploadVo 可以封装上传的状态和信息
            uploadVo.getTrunks().add(trunkIndex);
            redisUtil.put(key, uploadVo, RedisConstant.VIDEO_UPLOAD_TIMEOUT, TimeUnit.HOURS);
            return uploadVo;
        } catch (IOException e) {
            e.printStackTrace();
            return uploadVo;
        }
    }
}
