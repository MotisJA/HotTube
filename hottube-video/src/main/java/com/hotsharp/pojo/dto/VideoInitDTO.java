package com.hotsharp.pojo.dto;

import lombok.Data;

@Data
public class VideoInitDTO {
    private String filename;
    /** 文件大小 - 字节数 **/
    private Long fileSize;
    private String fileHash;
}
