package com.hotsharp.favorite.controller;

import com.hotsharp.common.result.Result;
import com.hotsharp.common.result.Results;
import com.hotsharp.common.utils.UserContext;
import com.hotsharp.favorite.domain.po.Favorite;
import com.hotsharp.favorite.service.IFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
public class FavoriteController {

    @Autowired
    private IFavoriteService favoriteService;

    /**
     * 站内用户请求某个用户的收藏夹列表（需要jwt鉴权）
     * @param uid   被查看的用户ID
     * @return  包含收藏夹列表的响应对象
     */
    @GetMapping("/favorite/get-all/user")
    public Result<List<Favorite>> getAllFavoritesForUser(@RequestParam("uid") Integer uid){
        // 获取当前用户
        Integer loginUid = UserContext.getUserId();
        if (Objects.equals(loginUid, uid)) {
            return Results.success(favoriteService.getFavorites(uid, true));
        }
        return Results.success(favoriteService.getFavorites(uid, false));
    }

    /**
     * 游客请求某个用户的收藏夹列表（不需要jwt鉴权）
     * @param uid   被查看的用户ID
     * @return  包含收藏夹列表的响应对象
     */
    @GetMapping("/favorite/get-all/visitor")
    public Result<List<Favorite>> getAllFavoritiesForVisitor(@RequestParam("uid") Integer uid) {
        return Results.success(favoriteService.getFavorites(uid, false));
    }

    /**
     * 创建一个新收藏夹
     * @param title 标题  限 80 字（需前端做合法判断）
     * @param desc  简介  限 200 字（需前端做合法判断）
     * @param visible   是否公开 0否 1是
     * @return  包含新创建的收藏夹信息的响应对象
     */
    @PostMapping("/favorite/create")
    public Result<Favorite> createFavorite(@RequestParam("title") String title,
                                         @RequestParam("desc") String desc,
                                         @RequestParam("visible") Integer visible) {
        Integer uid = UserContext.getUserId();
        return Results.success(favoriteService.addFavorite(uid, title, desc, visible));
    }
}