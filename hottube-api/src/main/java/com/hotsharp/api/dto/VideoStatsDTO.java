package com.hotsharp.api.dto;

import lombok.Data;

@Data
public class VideoStatsDTO {
    private Integer vid;
    private Integer play;
    private Integer danmu;
    private Integer good;
    private Integer bad;
    private Integer coin;
    private Integer collect;
    private Integer share;
    private Integer comment;
}
