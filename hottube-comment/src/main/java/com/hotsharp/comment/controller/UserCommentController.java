package com.hotsharp.comment.controller;

import com.hotsharp.comment.service.UserCommentService;
import com.hotsharp.common.result.Result;
import com.hotsharp.common.result.Results;
import com.hotsharp.common.utils.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "用户评论点赞点踩接口")
@Slf4j
@RestController
public class UserCommentController {
//    @Autowired
//    private UserContext currentUser;

    @Autowired
    private UserCommentService userCommentService;

    /**
     * 获取用户点赞点踩评论集合
     */
    @Operation(summary = "获取用户点赞点踩评论集合")
    @GetMapping("/comment/get-like-and-dislike")
    public Result getLikeAndDislike() {
        Integer uid = UserContext.getUserId();
        return Results.success(userCommentService.getUserLikeAndDislike(uid));
    }

    /**
     * 点赞或点踩某条评论
     * @param id    评论id
     * @param isLike true 赞 false 踩
     * @param isSet  true 点 false 取消
     */
    @Operation(summary = "点赞或点踩某条评论")
    @PostMapping("/comment/love-or-not")
    public Result loveOrNot(@RequestParam("id") Integer id,
                          @RequestParam("isLike") boolean isLike,
                          @RequestParam("isSet") boolean isSet) {
        Integer uid = UserContext.getUserId();
        userCommentService.userSetLikeOrUnlike(uid, id, isLike, isSet);
        return Results.success();
    }

    /**
     * 获取UP主觉得很淦的评论
     * @param uid   UP主uid
     * @return  点赞的评论id列表
     */
    @Operation(summary = "获取UP主觉得很淦的评论")
    @GetMapping("/comment/get-up-like")
    public Result getUpLike(@RequestParam("uid") Integer uid) {
        Map<String, Object> map = userCommentService.getUserLikeAndDislike(uid);
        return Results.success(map.get("userLike"));
    }
}
