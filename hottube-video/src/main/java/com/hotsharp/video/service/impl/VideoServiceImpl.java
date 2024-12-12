package com.hotsharp.video.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.hotsharp.common.constant.VideoConstant;
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
import com.hotsharp.video.service.VideoService;
import com.hotsharp.video.utils.FfmpegUtil;
import com.hotsharp.video.utils.MinioUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
public class VideoServiceImpl implements VideoService {

//    @Resource
//    private UserContext UserContext;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private FileProperty fileProperty;

    @Resource
    private FfmpegUtil ffmpegUtil;

    @Resource
    private MinioUtil minioUtil;

    @Resource
    private MinioProperty minioProperty;

    @Resource
    private ThreadPoolTaskExecutor minioUploadThreadPool;

    @Resource
    private VideoMapper videoMapper;

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
        // TODO: 封面上传 写入实体
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
                .coverUrl("")
                .videoUrl("")
                .uid(UserContext.getUserId()).build();
        videoMapper.insert(video); // TODO : 返回vid
        int i = video.getVid();
        Integer userId = UserContext.getUserId();
        String uploadId = videoUploadDTO.getHash() + userId;
        redisUtil.setExValue(RedisConstant.VIDEO_URL_PREFIX + uploadId, i, 24, TimeUnit.HOURS);
        merge(videoUploadDTO.getHash());
    }

    @Async("videoProcessThreadPool") // 异步执行
    void merge(String hash) {
        Integer userId = UserContext.getUserId();
        String uploadId = hash + userId;
        String path = System.getProperty("user.dir") + "/" + fileProperty.getTmp() + uploadId;
        File dir = new File(path);
        // 1. 合并文件分片
        String videoPath = mergeChunks(dir, uploadId);
        // 2. 转码 转码后的文件存在m3u8文件夹下
        String destFolder = System.getProperty("user.dir") + "/" + fileProperty.getM3u8() + uploadId;
        File destDir = new File(destFolder);
        if (!destDir.exists()) destDir.mkdir();
        ffmpegUtil.convertToM3U8(videoPath, destFolder);
        // 3. 上传文件 更换m3u8中的路径
        String url = null;
        try {
            url = uploadM3U8ToMinio(uploadId);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // TODO: 删除缓存和本地文件
        // 4. 更新数据库中视频文件的信息
        if (StringUtils.isNotEmpty(url)) sendBackUrl(uploadId, url);
        else throw new RuntimeException("error convert : " + uploadId + " url is empty ...");
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
        deleteFile(targetDir);
    }

    private void deleteFile(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                for (File listFile : file.listFiles()) {
                    deleteFile(listFile);
                }
            }
            file.delete();
        }
    }

    /**
     * 更新数据库的url
     * @param uploadId
     * @param url
     */
    private void sendBackUrl(String uploadId, String url) {
        int videoId = redisUtil.getObject(RedisConstant.VIDEO_URL_PREFIX + uploadId, Integer.class);
        Video video = new Video();
        video.setVid(videoId);
        video.setVideoUrl(url);
        video.setStatus(VideoConstant.VIDEO_STATUS_AUDIT);
        videoMapper.updateById(video);
    }

    /**
     * 上传流视频到服务器
     *    注意需要更改路径
     * @param uploadId
     */
    private String uploadM3U8ToMinio(String uploadId) throws InterruptedException {
        String dir = fileProperty.getM3u8() + uploadId;
        File file = new File(dir);
        if (!file.exists()) return "";
        String fullPath = dir + "/" + uploadId + ".m3u8";
        replaceUrl(fullPath, minioProperty.getBaseUrl());
        File[] files = file.listFiles();
        // 依次上传和处理
        CountDownLatch latch = new CountDownLatch(files.length);  // 计数器，等待所有文件上传完成
        for (File f : files) {
            minioUploadThreadPool.execute(() -> {
                minioUtil.upload(f, uploadId);
                latch.countDown();
            });
        }
        latch.await();
        return minioProperty.getBaseUrl() + FileConstant.MINIO_VIDEO_PREFIX + uploadId + "/" + uploadId + ".m3u8";
    }

    /**
     * 替换文件路径
     * @param fullPath
     * @param baseUrl
     */
    private void replaceUrl(String fullPath, String baseUrl) {
        // 读
        File file = new File(fullPath);
        try (FileReader in = new FileReader(file);
             BufferedReader bufIn = new BufferedReader(in);
             CharArrayWriter tempStream = new CharArrayWriter()) {
            // 替换
            String line = null;
            while ( (line = bufIn.readLine()) != null) {
                // 替换每行中, 符合条件的字符串
                line = line.replaceAll(FileConstant.M3U8_REGEX, baseUrl+"$1");
                // 将该行写入内存
                tempStream.write(line);
                // 添加换行符
                tempStream.append(System.getProperty("line.separator"));
            }
            FileWriter out = new FileWriter(file);
            tempStream.writeTo(out);
            out.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 合并分片文件
     */
    private String mergeChunks(File dir, String uploadId) {
        File[] chunkFiles = dir.listFiles();
        Arrays.sort(chunkFiles, Comparator.comparing(File::getName)); // 按照文件名排序
        File fullVideo = new File(dir, uploadId+FileConstant.VIDEO_SUFFIX);
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
