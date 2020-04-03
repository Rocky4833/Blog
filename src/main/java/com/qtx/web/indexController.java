package com.qtx.web;

import com.qtx.exception.NotFindException;
import com.qtx.service.BlogService;
import com.qtx.service.TagService;
import com.qtx.service.TypeService;
import com.qtx.vo.BlogQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

//异常模拟
@Controller
public class indexController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private TagService tagService;


    @GetMapping("/")
    public String index(@PageableDefault(size = 5,sort={"updateTime"},
            direction = Sort.Direction.DESC) Pageable pageable,
                        Model model) {
        model.addAttribute("page",blogService.listBlog(pageable));
        model.addAttribute("types",typeService.listTypeTop(6));
        model.addAttribute("tags",tagService.listTagTop(10));
        model.addAttribute("recommendBlogs",blogService.listRecommendBlogTop(8));
        return "index";
    }

    @PostMapping("/search")
    public String search(@PageableDefault(size = 5,sort={"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                         @RequestParam String query, Model model){
        model.addAttribute("page",blogService.listBlog("%"+query+"%",pageable));
        model.addAttribute("query",query);
        return "search";
    }

    //根据首页博客列表每条博客对应的id
    // 查询博客内容跳转到博客详情页面展示
    @GetMapping("/blog/{id}")
    public String blog(@PathVariable Long id,Model model){
       model.addAttribute("blog",blogService.getAndConvert(id));
        return "blog";
    }

    //动态加载所有页面底部最新博客
    @GetMapping("/index/footer/newblog")
    public String indexnewblogs(Model model){
        model.addAttribute("newblogs",blogService.listRecommendBlogTop(3));
        return "index :: newblogList";
    }

    @GetMapping("/about/footer/newblog")
    public String aboutnewblogs(Model model){
        model.addAttribute("newblogs",blogService.listRecommendBlogTop(3));
        return "about :: newblogList";
    }

    @GetMapping("/archives/footer/newblog")
    public String archivesnewblogs(Model model){
        model.addAttribute("newblogs",blogService.listRecommendBlogTop(3));
        return "archives :: newblogList";
    }

    @GetMapping("/blog/footer/newblog")
    public String blognewblogs(Model model){
        model.addAttribute("newblogs",blogService.listRecommendBlogTop(3));
        return "blog :: newblogList";
    }

    @GetMapping("/search/footer/newblog")
    public String searchnewblogs(Model model){
        model.addAttribute("newblogs",blogService.listRecommendBlogTop(3));
        return "search :: newblogList";
    }

    @GetMapping("/tags/footer/newblog")
    public String tagsnewblogs(Model model){
        model.addAttribute("newblogs",blogService.listRecommendBlogTop(3));
        return "tags :: newblogList";
    }

    @GetMapping("/types/footer/newblog")
    public String newblogs(Model model){
        model.addAttribute("newblogs",blogService.listRecommendBlogTop(3));
        return "types :: newblogList";
    }

}
