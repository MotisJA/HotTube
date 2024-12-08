package com.hotsharp.api.client;

import com.hotsharp.api.dto.VideoStatusDTO;
import com.hotsharp.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("favorite-service")
@RequestMapping("/favorite")
public interface FavoriteClient {

    @GetMapping("/video/status/{vid}")
    VideoStatusDTO getVideoStatusByVid(@PathVariable("vid") Integer vid);

    @PostMapping("/video/status/update")
    public Result updateStatus(@RequestParam("vid") Integer vid,
                               @RequestParam("column") String column,
                               @RequestParam("increase") boolean increase,
                               @RequestParam("count") Integer count);
}
