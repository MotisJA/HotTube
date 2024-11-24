package com.tube.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.tube.constant.RedisConstant;
import com.tube.constant.VideoConstant;
import com.tube.pojo.dto.VideoInitDTO;
import com.tube.pojo.vo.VideoUploadVo;
import com.tube.properties.FileProperty;
import com.tube.service.VideoService;
import com.tube.utils.FfmpegUtil;
import com.tube.utils.RedisUtil;
import com.tube.utils.UserUtil;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

@Component
public class VideoServiceImpl implements VideoService {

    @Resource
    private UserUtil userUtil;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private FileProperty fileProperty;

    @Resource
    private FfmpegUtil ffmpegUtil;

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

    @Override
    @Async("videoProcessThreadPool") // 异步执行
    public void complete(String uploadId) {
        // TODO: 文件合并、转码、上传、地址返回
        String path = fileProperty.getTmp() + uploadId;
        File dir = new File(path);
        // 1. 合并文件分片
        String videoPath = mergeChunks(dir, uploadId);
        // 2. 转码 转码后的文件存在m3u8文件夹下
        String destFolder = fileProperty.getM3u8() + uploadId;
        File destDir = new File(destFolder);
        if (!destDir.exists()) destDir.mkdir();
        ffmpegUtil.convertToM3U8(videoPath, destFolder);
        // 3. 上传文件 更换m3u8中的路径
        // 4. 更新数据库中视频文件的信息
    }

    /**
     * 合并分片文件
     */
    private String mergeChunks(File dir, String uploadId) {
        File[] chunkFiles = dir.listFiles();
        Arrays.sort(chunkFiles, Comparator.comparing(File::getName)); // 按照文件名排序
        File fullVideo = new File(dir, uploadId+VideoConstant.VIDEO_SUFFIX);
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fullVideo))) {
            for (File chunk : chunkFiles) {
                try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(chunk))) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = bis.read(buffer)) != -1) {
                        bos.write(buffer, 0, bytesRead);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error merging chunks", e);
        }
        return fullVideo.getAbsolutePath();
    }

}
