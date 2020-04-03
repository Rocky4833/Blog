package com.qtx.dao;

import com.qtx.po.Comment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {

    //根据comment创建时间倒序排序
    //查询父类评论为空的  即顶级评论节点
    List<Comment> findByBlogIdAndParentCommentNull(Long blogId, Sort sort);
}
