package com.tube.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 线程池配置
 */
@Configuration
@EnableAsync
public class ThreadPoolConfiguration {

    /**
     * 线程池参数根据minIO设置，如果开启线程太多会被MinIO拒绝
     * @return
     */
    @Bean("videoProcessThreadPool")
    public ThreadPoolTaskExecutor asyncServiceExecutorForVideoProcess() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数，采用IO密集 h/(1-拥塞)
        executor.setCorePoolSize(3);
        // 设置最大线程数 不做限制
        executor.setMaxPoolSize(8);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(60);
        // 设置默认线程名称
        executor.setThreadNamePrefix("video-process-task-");
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //执行初始化
        executor.initialize();
        return executor;
    }

    /**
     * 线程池参数根据minIO设置，如果开启线程太多会被MinIO拒绝
     * @return
     */
    @Bean("minioUploadThreadPool")
    public ThreadPoolTaskExecutor asyncServiceExecutorForMinioUpload() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数，采用IO密集 h/(1-拥塞)
        executor.setCorePoolSize(6);
        // 设置最大线程数,由于minIO连接数量有限，此处尽力设计大点
        executor.setMaxPoolSize(500);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(30);
        // 设置默认线程名称
        executor.setThreadNamePrefix("minio-upload-task-");
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //执行初始化
        executor.initialize();
        return executor;
    }

}
