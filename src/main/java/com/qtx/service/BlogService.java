package com.qtx.service;

import com.qtx.po.Blog;
import com.qtx.vo.BlogQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface BlogService {
    /**
     * 根据主键id查询博客
     * @param id：主键id
     * @return:Blog对象
     */
    Blog getBlog(Long id);

    Blog getAndConvert(Long id);

    /**
     * 根据组合条件分页查询博客
     * @param pageable:分页查询对象
     * @param blog：将前端页面查询参数封装为Blog对象
     * @return Blog类型的Page对象
     */
    Page<Blog> listBlog(Pageable pageable, BlogQuery blog);
    Page<Blog> listBlog(Pageable pageable);
    Page<Blog> listBlog(Long tagId,Pageable pageable);
    Page<Blog> listBlog(String query,Pageable pageable);
    List<Blog> listRecommendBlogTop(Integer size);
   //字符串类型的年份和对应的博客列表
    Map<String,List<Blog>> archiveBlog();
    Long countBlog();

    /**
     * 根据传递的Blog对象保存博客
     * @param blog：Blog对象
     * @return Blog对象 用于博客列表页面展示
     */
    Blog saveBlog(Blog blog);

    /**
     * 根据主键id先查询出需要修改的对象
     * 然后将新的Blog对象赋值给它
     * @param id: 主键id
     * @param blog: Blog对象
     * @return Blog对象
     */
    Blog updateBlog(Long id,Blog blog);

    /**
     * 根据主键id删除Blog对象
     * @param id：主键id
     */
    void deleteBlog(Long id);
}
