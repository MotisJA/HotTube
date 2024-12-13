package com.hotsharp.favorite.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hotsharp.common.domain.Favorite;
import com.hotsharp.common.domain.VideoStats;
import com.hotsharp.favorite.domain.po.FavoriteVideo;
import com.hotsharp.favorite.mapper.FavoriteMapper;
import com.hotsharp.favorite.mapper.FavoriteVideoMapper;
import com.hotsharp.favorite.mapper.VideoStatsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nullable;
import java.util.List;

@RestController
public class FavoriteFeignController {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private VideoStatsMapper videoStatsMapper;

    @PostMapping("/favorite/feign/insert")
    public void insertFavorite(@RequestBody Favorite favorite) {
        favoriteMapper.insert(favorite);
    }

    @PostMapping("/favorite/video/stats/list")
    List<VideoStats> selectList(@RequestBody List<Integer> idList,
                                @RequestParam @Nullable String column,
                                @RequestParam Integer page,
                                @RequestParam Integer quantity){
        QueryWrapper<VideoStats> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("vid", idList).orderByDesc(column).last("LIMIT " + quantity + " OFFSET " + (page - 1) * quantity);
        List<VideoStats> list = videoStatsMapper.selectList(queryWrapper);
        return list;
    }
}
