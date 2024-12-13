package com.hotsharp.favorite.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotsharp.api.client.VideoClient;
import com.hotsharp.common.domain.Video;
import com.hotsharp.common.result.Result;
import com.hotsharp.common.result.Results;
import com.hotsharp.common.utils.RedisUtil;
import com.hotsharp.common.utils.UserContext;
import com.hotsharp.common.domain.Favorite;
import com.hotsharp.favorite.domain.po.FavoriteVideo;
import com.hotsharp.favorite.mapper.FavoriteVideoMapper;
import com.hotsharp.favorite.service.IFavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Tag(name = "收藏夹获取和创建接口")
@RestController
public class FavoriteController {

    @Autowired
    private IFavoriteService favoriteService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private FavoriteVideoMapper favoriteVideoMapper;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private VideoClient videoClient;

    /**
     * 站内用户请求某个用户的收藏夹列表（需要jwt鉴权）
     * @param uid   被查看的用户ID
     * @return  包含收藏夹列表的响应对象
     */
    @Operation(summary = "获取用户的收藏夹列表")
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
    @Operation(summary = "游客请求某个用户的收藏夹列表")
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
    @Operation(summary = "创建一个新收藏夹")
    @PostMapping("/favorite/create")
    public Result<Favorite> createFavorite(@RequestParam("title") String title,
                                           @RequestParam("desc") String desc,
                                           @RequestParam("visible") Integer visible) {
        Integer uid = UserContext.getUserId();
        return Results.success(favoriteService.addFavorite(uid, title, desc, visible));
    }

    /**
     * 获取某个收藏夹的视频
     * @param fid   收藏夹ID
     * @param rule  排序规则 1 最近收藏 2 最多播放 3 最新投稿
     * @param page  分页  从1开始
     * @param quantity  每页查询数量
     * @return  视频信息列表
     */
    @GetMapping("/favorite/video/user-collect")
    public Result getUserCollectVideos(@RequestParam("fid") Integer fid,
                                               @RequestParam("rule") Integer rule,
                                               @RequestParam("page") Integer page,
                                               @RequestParam("quantity") Integer quantity) {
        Result customResponse = new Result();
        Set<Object> set;
        if (rule == 1) {
            set = redisUtil.zReverange("favorite_video:" + fid, (long) (page - 1) * quantity, (long) page * quantity);
        } else {
            set = redisUtil.zReverange("favorite_video:" + fid, 0, -1);
        }
        if (set == null || set.isEmpty()) {
            customResponse.setData(Collections.emptyList());
            return customResponse;
        }
        List<Integer> list = new ArrayList<>();
        set.forEach(vid -> {
            list.add((Integer) vid);
        });
        List<Map<String, Object>> result;
        switch (rule) {
            case 1:
                result = videoClient.getVideosWithDataByIdsOrderByDesc(list, null, page, quantity);
                break;
            case 2:
                result = videoClient.getVideosWithDataByIdsOrderByDesc(list, "play", page, quantity);
                break;
            case 3:
                result = videoClient.getVideosWithDataByIdsOrderByDesc(list, "upload_date", page, quantity);
                break;
            default:
                result = videoClient.getVideosWithDataByIdsOrderByDesc(list, null, page, quantity);
        }
        if (result.size() == 0) {
            customResponse.setData(result);
            return customResponse;
        }
        ObjectMapper mapper = new ObjectMapper();
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            result.stream().parallel().forEach(map -> {
                Video video = mapper.convertValue(map.get("video"), Video.class);
                QueryWrapper<FavoriteVideo> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("vid", video.getVid()).eq("fid", fid);
                map.put("info", favoriteVideoMapper.selectOne(queryWrapper));
            });
            sqlSession.commit();
        }
        customResponse.setData(result);
        return customResponse;
    }

    /**
     * 获取用户最近点赞视频列表
     * @param uid   用户uid
     * @param offset    偏移量，即当前已查询到多少条视频
     * @param quantity  查询数量
     * @return  视频信息列表
     */
    @GetMapping("/favorite/video/user-love")
    public Result getUserLoveMovies(@RequestParam("uid") Integer uid,
                                            @RequestParam("offset") Integer offset,
                                            @RequestParam("quantity") Integer quantity) {
        Result customResponse = new Result();
        Set<Object> set = redisUtil.zReverange("love_video:" + uid, (long) offset, (long) offset + quantity - 1);
        if (set == null || set.isEmpty()) {
            customResponse.setData(Collections.emptyList());
            return customResponse;
        }
        List<Integer> list = new ArrayList<>();
        set.forEach(vid -> {
            list.add((Integer) vid);
        });
        customResponse.setData(videoClient.getVideosWithDataByIdsOrderByDesc(list, null, 1, list.size()));
        return customResponse;
    }
}
