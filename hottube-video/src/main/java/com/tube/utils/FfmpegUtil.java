package com.tube.utils;

import com.tube.constant.VideoConstant;
import com.tube.properties.FfmpegProperty;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class FfmpegUtil {

    @Resource
    private FfmpegProperty ffmpegProperty;

    public boolean convertOss(String folderUrl,String fileName){
        if (!checkFile(folderUrl + fileName)){
            System.out.println("文件不存在!");
            return false;
        }
        //验证文件后缀
        String suffix = StringUtils.substringAfter(fileName, ".");
        String fileFullName = StringUtils.substringBefore(fileName, ".");
        if (!validFileType(suffix)){
            return false;
        }
        return processM3U8(folderUrl,fileName,fileFullName);
    }

    /**
     * 验证上传文件后缀
     * @param type
     * @return
     */
    private boolean validFileType (String type) {
        return VideoConstant.VIDEO_SUFFIX.equals(type);
    }

    /**
     * 验证是否是文件格式
     * @param path
     * @return
     */
    private boolean checkFile(String path) {
        return new File(path).isFile();
    }

    // ffmpeg能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等）

    /**
     * ffmpeg程序转换m3u8
     * @param folderUrl
     * @param fileName
     * @param fileFullName
     * @return
     */
    private boolean processM3U8(String folderUrl,String fileName, String fileFullName) {
        //这里就写入执行语句就可以了
        List commend = new ArrayList();
        commend.add(ffmpegProperty.getPath());
        commend.add("-i");
        commend.add(folderUrl+fileName);
        commend.add("-c:v");
        commend.add("libx264");
        commend.add("-hls_time");
        commend.add("20");
        commend.add("-hls_list_size");
        commend.add("0");
        commend.add("-c:a");
        commend.add("aac");
        commend.add("-strict");
        commend.add("-2");
        commend.add("-f");
        commend.add("hls");
        commend.add(folderUrl+ fileFullName +".m3u8");
        try {
            ProcessBuilder builder = new ProcessBuilder();//java
            builder.command(commend);
            Process p = builder.start();
            int i = doWaitFor(p);
            p.destroy();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 监听ffmpeg运行过程
     * @param p
     * @return
     */
    public int doWaitFor(Process p) {
        int exitValue = -1; // Returned to caller when p is finished
        try (InputStream in = p.getInputStream();
            InputStream err = p.getErrorStream();
            BufferedReader inReader = new BufferedReader(new InputStreamReader(in));
            BufferedReader errReader = new BufferedReader(new InputStreamReader(err))) {
            Thread stdoutThread = new Thread(() -> inReader.lines().forEach(System.out::println));
            Thread stderrThread = new Thread(() -> errReader.lines().forEach(System.err::println));
            // Start the threads to handle stdout and stderr
            stdoutThread.start();
            stderrThread.start();
            // Wait for the process to complete
            exitValue = p.waitFor();
            // Ensure all output is processed before returning
            stdoutThread.join();
            stderrThread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exitValue;
    }

}
