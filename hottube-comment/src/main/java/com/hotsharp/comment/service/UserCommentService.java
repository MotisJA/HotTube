package com.hotsharp.comment.service;

import java.util.Map;

public interface UserCommentService {
    Map<String, Object> getUserLikeAndDislike(Integer uid);

    void userSetLikeOrUnlike(Integer uid, Integer id, boolean isLike, boolean isSet);

}
