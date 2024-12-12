package com.hotsharp.favorite.controller;

import com.hotsharp.common.domain.Favorite;
import com.hotsharp.favorite.mapper.FavoriteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FavoriteFeignController {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @PostMapping("/favorite/feign/insert")
    public void insertFavorite(@RequestBody Favorite favorite) {
        favoriteMapper.insert(favorite);
    }
}
