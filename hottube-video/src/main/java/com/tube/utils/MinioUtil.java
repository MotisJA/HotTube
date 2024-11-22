package com.tube.utils;

import com.tube.properties.MinioProperty;
import io.micrometer.common.util.StringUtils;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import jakarta.annotation.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public class MinioUtil {

    @Resource
    private MinioProperty properties;

    @Resource
    private MinioClient minioClient;

    /**
     * 文件上传
     *
     * @param file 文件
     * @return Boolean
     */
    public String upload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)) {
            throw new RuntimeException("上传文件文件名为空");
        }
        String preName = originalFilename.substring(0,originalFilename.lastIndexOf("."));
        String pastName = originalFilename.substring(originalFilename.lastIndexOf("."));
        String objectName =  preName + UUID.randomUUID() + pastName;
        try {
            PutObjectArgs objectArgs = PutObjectArgs.builder().bucket(properties.getBucket()).object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1).contentType(file.getContentType()).build();
            minioClient.putObject(objectArgs);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return objectName;
    }

    /**
     * 获取预览的路径
     */
    public String preview(String objectName) {
        // 查看文件地址
        GetPresignedObjectUrlArgs build = new GetPresignedObjectUrlArgs().builder().bucket(properties.getBucket()).object(objectName).method(Method.GET).build();
        try {
            return minioClient.getPresignedObjectUrl(build);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件下载
     *
     */
    public InputStream download(String objectName) {
        try {
            GetObjectArgs objectArgs = GetObjectArgs.builder().bucket(properties.getBucket()).object(objectName).build();
            return minioClient.getObject(objectArgs);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据对象名删除对象
     * @param objectName
     * @return
     */
    public boolean remove(String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(properties.getBucket()).object(objectName).build());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 复制对象
     * @param src 复制源对象名
     * @return
     */
    public String copy(String src){
        // 处理对象名
        String target;
        String pastName = src.substring(src.lastIndexOf('.'));
        String preName = src.substring(0, src.lastIndexOf('.')-36);// 减去36个uuid的位置
        target = preName + UUID.randomUUID() + pastName;
        try {
            minioClient.copyObject(CopyObjectArgs.builder()
                    .source(CopySource.builder().bucket(properties.getBucket()).object(src).build())
                    .bucket(properties.getBucket())
                    .object(target)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("复制对象失败...");
        }
        return target;
    }


    /**
     * 查看存储bucket是否存在
     * @return boolean
     */
    public Boolean bucketExist() {
        Boolean found;
        try {
            found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(properties.getBucket()).build());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return found;
    }

    /**
     * 创建存储bucket
     *
     * @return Boolean
     */
    public Boolean makeBucket() {
        try {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(properties.getBucket())
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 删除存储bucket
     *
     * @return Boolean
     */
    public Boolean removeHotBucket(String bucketName) {
        try {
            minioClient.removeBucket(RemoveBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 获取全部bucket
     */
    public List<Bucket> getAllHotBuckets() {
        try {
            List<Bucket> buckets = minioClient.listBuckets();
            return buckets;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}