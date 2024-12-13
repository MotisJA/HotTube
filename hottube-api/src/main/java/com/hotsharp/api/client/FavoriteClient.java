package com.hotsharp.api.client;

import com.hotsharp.common.domain.Favorite;
import com.hotsharp.common.domain.VideoStats;
import com.hotsharp.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("favorite-service")
public interface FavoriteClient {

    @GetMapping("/favorite/video/status/{vid}")
    VideoStats getVideoStatusByVid(@PathVariable("vid") Integer vid);

    @PostMapping("/favorite/video/status/update")
    Result updateStatus(@RequestParam("vid") Integer vid,
                               @RequestParam("column") String column,
                               @RequestParam("increase") boolean increase,
                               @RequestParam("count") Integer count);

    @PostMapping("/favorite/feign/insert")
    void insertFavorite(@RequestBody Favorite favorite);
}
