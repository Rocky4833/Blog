package com.qtx.web;

import com.qtx.po.Comment;
import com.qtx.po.User;
import com.qtx.service.BlogService;
import com.qtx.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;

@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private BlogService blogService;
    @Value("${comment.avatar}")
    private String avatar;

    @GetMapping("/comments/{blogId}")
    public String comments(@PathVariable Long blogId, Model model){
        model.addAttribute("comments",commentService.listCommentByBlogId(blogId));
        //留言列表区域局部加载
        return "blog :: commentList";
    }

    @PostMapping("/comments")
    public String post(Comment comment, HttpSession session){
        Long blogId = comment.getBlog().getId();
        comment.setBlog(blogService.getBlog(blogId));
        //后台登入页面 管理员登入会在session域中保存信息
        //于是可以通过session.getAttribute()获得管理员对象
        User user = (User) session.getAttribute("user");
        //用户不为空 表示管理员已登入
        //管理员头像设置为自己本身的
        //当前端页面留言评论区域提交后台服务时需要作如下判断
        //用以区分是管理员还是普通访客
        if(user != null){
            comment.setAvatar(user.getAvatar());
            comment.setAdminComment(true);//adminComment为true 前端页面留言区域会显示th:if="${comment.adminCommnet}" 所在标签的内容
        }else{
            comment.setAvatar(avatar);
        }
        commentService.saveComment(comment);
        return "redirect:/comments/"+blogId;
    }
}
