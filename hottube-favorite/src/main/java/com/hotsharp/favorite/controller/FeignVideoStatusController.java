package com.hotsharp.favorite.controller;

import com.hotsharp.api.dto.VideoStatusDTO;
import com.hotsharp.common.result.Result;
import com.hotsharp.common.result.Results;
import com.hotsharp.favorite.mapper.VideoStatusMapper;
import com.hotsharp.favorite.service.VideoStatusService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class FeignVideoStatusController {

    @Autowired
    private VideoStatusMapper videoStatusMapper;

    @Autowired
    private VideoStatusService videoStatsService;

    @GetMapping("/favorite/video/status/{vid}")
    public VideoStatusDTO getVideoStatusByVid(@PathVariable("vid") Integer vid) {
        VideoStatusDTO videoStatusDTO = new VideoStatusDTO();
        BeanUtils.copyProperties(videoStatusMapper.selectById(vid), videoStatusDTO);
        return videoStatusDTO;
    }

    @PostMapping("/favorite/video/status/update")
    public Result updateStatus(@RequestParam("vid") Integer vid,
                               @RequestParam("column") String column,
                               @RequestParam("increase") boolean increase,
                               @RequestParam("count") Integer count) {
        videoStatsService.updateStatus(vid, column, increase, count);
        return Results.success();
    }
}
