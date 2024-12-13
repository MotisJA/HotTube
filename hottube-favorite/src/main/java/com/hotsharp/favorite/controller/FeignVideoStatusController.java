package com.hotsharp.favorite.controller;

import com.hotsharp.common.result.Result;
import com.hotsharp.common.result.Results;
import com.hotsharp.common.domain.VideoStats;
import com.hotsharp.favorite.service.VideoStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class FeignVideoStatusController {

    @Autowired
    private VideoStatsService videoStatsService;

    @GetMapping("/favorite/video/stats/{vid}")
    public VideoStats getVideoStatsByVid(@PathVariable("vid") Integer vid) {
        return videoStatsService.selectByVid(vid);
    }

    @PostMapping("/favorite/video/stats/update")
    public Result updateStats(@RequestParam("vid") Integer vid,
                               @RequestParam("column") String column,
                               @RequestParam("increase") boolean increase,
                               @RequestParam("count") Integer count) {
        videoStatsService.updateStats(vid, column, increase, count);
        return Results.success();
    }

    @PutMapping("/favorite/video/stats")
    public Result add (@RequestBody VideoStats videoStats) {
        videoStatsService.add(videoStats);
        return Results.success();
    }
}
