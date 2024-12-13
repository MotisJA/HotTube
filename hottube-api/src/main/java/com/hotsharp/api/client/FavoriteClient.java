package com.hotsharp.api.client;

import com.hotsharp.common.domain.Favorite;
import com.hotsharp.common.domain.VideoStats;
import com.hotsharp.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("favorite-service")
public interface FavoriteClient {

    @GetMapping("/favorite/video/stats/{vid}")
    VideoStats getVideoStatsByVid(@PathVariable("vid") Integer vid);

    @PostMapping("/favorite/video/stats/update")
    Result updateStats(@RequestParam("vid") Integer vid,
                               @RequestParam("column") String column,
                               @RequestParam("increase") boolean increase,
                               @RequestParam("count") Integer count);

    @PostMapping("/favorite/feign/insert")
    void insertFavorite(@RequestBody Favorite favorite);

    @PutMapping("/favorite/video/stats")
    Result insertVideoStats(@RequestBody VideoStats videoStats);
}
