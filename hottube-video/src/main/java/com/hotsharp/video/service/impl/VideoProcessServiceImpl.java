package com.hotsharp.video.service.impl;

import com.hotsharp.common.constant.VideoConstant;
import com.hotsharp.common.domain.Video;
import com.hotsharp.common.utils.RedisUtil;
import com.hotsharp.video.constant.FileConstant;
import com.hotsharp.video.constant.RedisConstant;
import com.hotsharp.video.mapper.VideoMapper;
import com.hotsharp.video.properties.FileProperty;
import com.hotsharp.video.properties.MinioProperty;
import com.hotsharp.video.service.VideoProcessService;
import com.hotsharp.video.utils.FfmpegUtil;
import com.hotsharp.video.utils.MinioUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.CountDownLatch;

@Service
public class VideoProcessServiceImpl implements VideoProcessService {

    @Resource
    private FileProperty fileProperty;

    @Resource
    private FfmpegUtil ffmpegUtil;

    @Resource
    private VideoMapper videoMapper;

    @Resource
    private MinioProperty minioProperty;

    @Resource
    private MinioUtil minioUtil;

    @Resource
    private ThreadPoolTaskExecutor minioUploadThreadPool;

    @Resource
    private RedisUtil redisUtil;

    @Override
    @Async("videoProcessThreadPool") // 异步执行
    public void merge(int vid, String hash, Integer userId) {
        String uploadId = hash + userId;
        String path = System.getProperty("user.dir") + "/" + fileProperty.getTmp() + uploadId;
        File dir = new File(path);
        // 1. 合并文件分片
        String videoPath = mergeChunks(dir, uploadId); // 这里进去不出来了？
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
        // 删除缓存
        String key = RedisConstant.VIDEO_UPLOAD_PREFIX + uploadId;
        redisUtil.removeCache(key);
        // 删除文件
        String dir1 = System.getProperty("user.dir") + "/" + fileProperty.getTmp() + uploadId;
        String dir2 = System.getProperty("user.dir") + "/" + fileProperty.getM3u8() + uploadId;
        deleteFile(new File(dir1));
        deleteFile(new File(dir2));
        // 4. 更新数据库中视频文件的信息
        if (StringUtils.isNotEmpty(url)) sendBackUrl(vid, uploadId, url);
        else throw new RuntimeException("error convert : " + uploadId + " url is empty ...");
    }

    @Override
    public void deleteFile(File file) {
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
     * 上传流视频到服务器
     *    注意需要更改路径
     * @param uploadId
     */
    private String uploadM3U8ToMinio(String uploadId) throws InterruptedException {
        String dir = System.getProperty("user.dir") + "/" + fileProperty.getM3u8() + uploadId;
        File file = new File(dir);
        if (!file.exists()) return "";
        String fullPath = dir + "/" + uploadId + ".m3u8";
        replaceUrl(fullPath, minioProperty.getBaseUrl()+"videos/");
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
     * 更新数据库的url
     * @param uploadId
     * @param url
     */
    private void sendBackUrl(int vid, String uploadId, String url) {
        Video video = new Video();
        video.setVid(vid);
        video.setVideoUrl(url);
        video.setStatus(VideoConstant.VIDEO_STATUS_AUDIT);
        videoMapper.updateById(video);
    }

    /**
     * 合并分片文件
     */
    private String mergeChunks(File dir, String uploadId) {
        File[] chunkFiles = dir.listFiles();
        Arrays.sort(chunkFiles, Comparator.comparing(File::getName)); // 按照文件名排序
        File fullVideo = new File(dir, uploadId+ "." + FileConstant.VIDEO_SUFFIX);
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
