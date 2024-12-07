package com.hotsharp.comment.service;


import com.hotsharp.comment.domain.po.Comment;
import com.hotsharp.comment.domain.po.CommentTree;

import java.util.List;

public interface CommentService {
    List<CommentTree> getCommentTreeByVid(Integer vid, Long offset, Integer type);

    CommentTree sendComment(Integer vid, Integer uid, Integer rootId, Integer parentId, Integer toUserId, String content);

    void deleteComment(Integer id, Integer uid, boolean isAdmin);

    List<Comment> getChildCommentsByRootId(Integer rootId, Integer vid, Long start, Long stop);

    List<Comment> getRootCommentsByVid(Integer vid, Long offset, Integer type);

    CommentTree getMoreCommentsById(Integer id);

    /*
    评论点赞点踩相关
     */
    void updateLikeAndDisLike(Integer id, boolean addLike);

    void updateComment(Integer id, String column, boolean incr, Integer count);
}
