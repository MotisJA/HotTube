package com.hotsharp.favorite.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotsharp.common.utils.RedisUtil;
import com.hotsharp.favorite.domain.po.Favorite;
import com.hotsharp.favorite.mapper.FavoriteMapper;
import com.hotsharp.favorite.service.IFavoriteService;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements IFavoriteService {
    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    @Qualifier("taskExecutor")
    private Executor taskExecutor;

    @Override
    public List<Favorite> getFavorites(Integer uid, boolean isOwner) {
        // 从缓存中获取
        String key = "favorites:" + uid;
        String string = redisUtil.getObjectString(key);
        List<Favorite> list = JSONArray.parseArray(string, Favorite.class);
        if (list != null) {
            return filterFavorites(list, isOwner);
        }
        // 从数据库中获取
        QueryWrapper<Favorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid).ne("is_delete", 1).orderByDesc("fid");
        list = favoriteMapper.selectList(queryWrapper);
        // 批量设置收藏夹封面
        if (list != null && !list.isEmpty()) {
//            // 使用事务批量操作 减少连接sql的开销
//            try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
//                // 设置收藏夹封面
//                list.stream().parallel().forEach(favorite -> {
//                    if (favorite.getCover() == null) {
//                        Set<Object> set = redisUtil.zReverange("favorite_video:" + favorite.getFid(), 0, 0);    // 找到最近一个收藏的视频
//                        if (set != null && !set.isEmpty()) {
//                            Integer vid = (Integer) set.iterator().next();
//                             Video video = videoMapper.selectById(vid);
//                             favorite.setCover(video.getCoverUrl());
//                        }
//                    }
//                });
//                sqlSession.commit();
//            }
            List<Favorite> finalList = list;
            CompletableFuture.runAsync(() -> redisUtil.setExObjectValue(key, finalList), taskExecutor);
            return filterFavorites(list, isOwner);
        }
        return Collections.emptyList();
    }

    @Override
    public Favorite addFavorite(Integer uid, String title, String desc, Integer visible) {
        // 字段合法判断由前端完成
        Favorite favorite = new Favorite(null, uid, 2, visible, null, title, desc, 0, null);
        favoriteMapper.insert(favorite);
        redisUtil.delValue("favorites:" + uid);
        return favorite;
    }

    @Override
    public Favorite updateFavorite(Integer fid, Integer uid, String title, String desc, Integer visible) {
        Favorite favorite = favoriteMapper.selectById(fid);
        if (!Objects.equals(favorite.getUid(), uid)) {
            return null;
        }
        UpdateWrapper<Favorite> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("fid", fid).set("title", title).set("description", desc).set("visible", visible);
        favoriteMapper.update(null, updateWrapper);
        redisUtil.delValue("favorites:" + uid);
        return favorite;
    }

    @Override
    public void delFavorite(Integer fid, Integer uid) {

    }

    // 提取某用户的全部收藏夹信息的 FID 整合成集合
    public Set<Integer> findFidsOfUserFavorites(Integer uid) {
        List<Favorite> list = getFavorites(uid, true);
        if (list == null) return new HashSet<>();
        return list.stream()
                .map(Favorite::getFid)
                .collect(Collectors.toSet());
    }

    // 过滤收藏夹列表，根据是否公开
    private List<Favorite> filterFavorites(List<Favorite> list, boolean isOwner) {
        if (isOwner) {
            return list;
        }
        List<Favorite> filteredList = new ArrayList<>();
        for (Favorite favorite : list) {
            if (favorite.getVisible() == 1) {
                filteredList.add(favorite);
            }
        }
        return filteredList;
    }
}
