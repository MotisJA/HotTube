package com.hotsharp.favorite.controller;

import com.hotsharp.api.dto.VideoStatsDTO;
import com.hotsharp.common.result.Result;
import com.hotsharp.common.result.Results;
import com.hotsharp.favorite.mapper.VideoStatsMapper;
import com.hotsharp.favorite.service.VideoStatsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class FeignVideoStatusController {

    @Autowired
    private VideoStatsMapper videoStatsMapper;

    @Autowired
    private VideoStatsService videoStatsService;

    @GetMapping("/favorite/video/status/{vid}")
    public VideoStatsDTO getVideoStatusByVid(@PathVariable("vid") Integer vid) {
        VideoStatsDTO videoStatsDTO = new VideoStatsDTO();
        BeanUtils.copyProperties(videoStatsMapper.selectById(vid), videoStatsDTO);
        return videoStatsDTO;
    }

    @PostMapping("/favorite/video/status/update")
    public Result updateStatus(@RequestParam("vid") Integer vid,
                               @RequestParam("column") String column,
                               @RequestParam("increase") boolean increase,
                               @RequestParam("count") Integer count) {
        videoStatsService.updateStats(vid, column, increase, count);
        return Results.success();
    }
}
