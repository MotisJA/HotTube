package com.hotsharp.favorite.controller;

import com.hotsharp.common.result.Result;
import com.hotsharp.common.result.Results;
import com.hotsharp.common.utils.UserContext;
import com.hotsharp.favorite.domain.po.UserVideo;
import com.hotsharp.favorite.service.IFavoriteService;
import com.hotsharp.favorite.service.IFavoriteVideoService;
import com.hotsharp.favorite.service.IUserVideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Tag(name = "用户收藏点赞动作接口")
@RestController
public class FavoriteActionController {

    @Autowired
    private IUserVideoService userVideoService;

    @Autowired
    private IFavoriteVideoService favoriteVideoService;

    @Autowired
    private IFavoriteService favoriteService;

    /**
     * 点赞或点踩
     * @param vid   视频ID
     * @param isLove    赞还是踩 true赞 false踩
     * @param isSet     点还是取消 true点 false取消
     * @return 返回用户与该视频更新后的交互数据
     */
    @Operation(summary = "点赞或点踩")
    @PostMapping("/favorite/video/love-or-not")
    public Result<UserVideo> loveOrNot(@RequestParam("vid") Integer vid,
                                       @RequestParam("isLove") boolean isLove,
                                       @RequestParam("isSet") boolean isSet) {
        Integer uid = UserContext.getUserId();
        return Results.success(userVideoService.setLoveOrUnlove(uid, vid, isLove, isSet));
    }

    /**
     * 获取用户收藏了该视频的收藏夹列表
     * @param vid   视频id
     * @return  收藏了该视频的收藏夹列表
     */
    @Operation(summary = "获取用户收藏了该视频的收藏夹列表")
    @GetMapping("/favorite/video/collected-fids")
    public Result<?> getCollectedFids(@RequestParam("vid") Integer vid) {
        Integer uid = UserContext.getUserId();
        Set<Integer> fids = favoriteService.findFidsOfUserFavorites(uid);
        Set<Integer> collectedFids = favoriteVideoService.findFidsOfCollected(vid, fids);
        return Results.success(collectedFids);
    }

    /**
     * 收藏或取消收藏某视频
     * @param vid   视频ID
     * @param addArray  包含需要添加收藏的多个收藏夹ID组成的字符串，形式如 1,12,13,20 不能含有字符"["和"]"
     * @param removeArray   包含需要移出收藏的多个收藏夹ID组成的字符串，形式如 1,12,13,20 不能含有字符"["和"]"
     * @return  无数据返回
     */
    @Operation(summary = "收藏或取消收藏某视频")
    @PostMapping("/favorite/video/collect")
    public Result<?> collectVideo(@RequestParam("vid") Integer vid,
                                  @RequestParam("adds") String[] addArray,
                                  @RequestParam("removes") String[] removeArray) {
        Integer uid = UserContext.getUserId();
        Set<Integer> fids = favoriteService.findFidsOfUserFavorites(uid);
        Set<Integer> addSet = Arrays.stream(addArray).map(Integer::parseInt).collect(Collectors.toSet());
        Set<Integer> removeSet = Arrays.stream(removeArray).map(Integer::parseInt).collect(Collectors.toSet());
        boolean allElementsInFids = fids.containsAll(addSet) && fids.containsAll(removeSet);    // 判断添加或移出的收藏夹是否都属于该用户
        if (!allElementsInFids) {
            return Results.failure(403, "无权操作该收藏夹");
        }
        Set<Integer> collectedFids = favoriteVideoService.findFidsOfCollected(vid, fids);   // 原本该用户已收藏该视频的收藏夹ID集合
        if (!addSet.isEmpty()) {
            favoriteVideoService.addToFav(uid, vid, addSet);
        }
        if (!removeSet.isEmpty()) {
            favoriteVideoService.removeFromFav(uid, vid, removeSet);
        }
        boolean isCollect = !addSet.isEmpty() && collectedFids.isEmpty();
        boolean isCancel = addSet.isEmpty() && !collectedFids.isEmpty() && collectedFids.size() == removeSet.size() && collectedFids.containsAll(removeSet);
        if (isCollect) {
            userVideoService.collectOrCancel(uid, vid, true);
        } else if (isCancel) {
            userVideoService.collectOrCancel(uid, vid, false);
        }
        return Results.success();
    }

    /**
     * 取消单个视频在单个收藏夹的收藏
     * @param vid   视频vid
     * @param fid   收藏夹id
     * @return  响应对象
     */
    @Operation(summary = "取消单个视频在单个收藏夹的收藏")
    @PostMapping("/favorite/video/cancel-collect")
    public Result<?> cancelCollect(@RequestParam("vid") Integer vid, @RequestParam("fid") Integer fid) {
        Integer uid = UserContext.getUserId();
        Set<Integer> fids = favoriteService.findFidsOfUserFavorites(uid);
        Set<Integer> removeSet = new HashSet<>();
        removeSet.add(fid);
        if (!fids.containsAll(removeSet)) {
            return Results.failure(403, "无权操作该收藏夹");
        }
        Set<Integer> collectedFids = favoriteVideoService.findFidsOfCollected(vid, fids);   // 原本该用户已收藏该视频的收藏夹ID集合
        favoriteVideoService.removeFromFav(uid, vid, removeSet);
        // 判断是否是最后一个取消收藏的收藏夹，是就要标记视频为未收藏
        boolean isCancel = !collectedFids.isEmpty() && collectedFids.size() == removeSet.size() && collectedFids.containsAll(removeSet);
        if (isCancel) {
            userVideoService.collectOrCancel(uid, vid, false);
        }
        return Results.success();
    }
}
