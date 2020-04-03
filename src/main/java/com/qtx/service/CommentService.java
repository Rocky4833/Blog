package com.qtx.service;

import com.qtx.po.Comment;

import java.util.List;

public interface CommentService {
    // 根据blogId查询comment列表
    List<Comment> listCommentByBlogId(Long blogId);

    Comment saveComment(Comment comment);
}
