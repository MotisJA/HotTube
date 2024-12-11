package com.hotsharp.api.client;

import com.hotsharp.api.dto.VideoStatusDTO;
import com.hotsharp.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("favorite-service")
public interface FavoriteClient {

    @GetMapping("/favorite/video/status/{vid}")
    VideoStatusDTO getVideoStatusByVid(@PathVariable("vid") Integer vid);

    @PostMapping("/favorite/video/status/update")
    public Result updateStatus(@RequestParam("vid") Integer vid,
                               @RequestParam("column") String column,
                               @RequestParam("increase") boolean increase,
                               @RequestParam("count") Integer count);
}
