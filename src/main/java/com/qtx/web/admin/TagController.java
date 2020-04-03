package com.qtx.web.admin;

import com.qtx.po.Tag;
import com.qtx.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin")
public class TagController {

    @Autowired
    private TagService tagService;

    //分页对象参数：每页展示多少条数据，按照主键id进行排序，排序方式为降序
    @GetMapping("/tags")
    public String tags(@PageableDefault(size = 5,sort = {"id"},direction = Sort.Direction.DESC)
                                Pageable pageable, Model model) {
        model.addAttribute("page",tagService.listTag(pageable));
        return "admin/tags";
    }

    @GetMapping("/tags/input")
    public String input(Model model){
        model.addAttribute("tag",new Tag());
        return "admin/tags-input";
    }

    @GetMapping("/tags/{id}/input")
    public String editInput(@PathVariable Long id, Model model){
        model.addAttribute("tag",tagService.getTag(id));
        return "admin/tags-input";
    }

    //@Valid:表示该该对象需要校验
    //BindingResult:该对象会接收校验后的结果，用于前端展示,[前端如何拿到校验后的数据？]
    @PostMapping("/tags")
    public String post(@Valid Tag tag, BindingResult result, RedirectAttributes attributes){
      //后端校验标签新增不能重复
      //首先要通过name查询数据
      //如果查询不到，说明数据库未曾添加该标签，新增成功，否则新增失败
        Tag tag1= tagService.getTagByName(tag.getName());
       if(tag1 != null){
           result.rejectValue("name","nameError","不能添加重复标签");
       }

        //后端校验分类新增分类名称不能为空
        if(result.hasErrors()){
            return "admin/tags-input";
        }

        Tag t = tagService.saveTag(tag);
        if(t == null){
            attributes.addFlashAttribute("message","新增失败");
        }else{
            attributes.addFlashAttribute("message","新增成功");
        }
        return "redirect:/admin/tags";
    }

    //BindingResult和Tag必须连写
    @PostMapping("/tags/{id}")
    public String editPost(@Valid Tag tag, BindingResult result,@PathVariable Long id ,RedirectAttributes attributes){
        //后端校验标签新增不能重复
        //首先要通过name查询数据
        //如果查询不到，说明数据库未曾添加该标签，新增成功，否则新增失败
        Tag tag1= tagService.getTagByName(tag.getName());
        if(tag1 != null){
            result.rejectValue("name","nameError","不能添加重复标签");
        }

        //后端校验标签修改分类名称不能为空
        if(result.hasErrors()){
            return "admin/tags-input";
        }

        Tag t = tagService.updateTag(id,tag);
        if(t == null){
            attributes.addFlashAttribute("message","更新失败");
        }else{
            attributes.addFlashAttribute("message","更新成功");
        }
        return "redirect:/admin/tags";
    }

    @GetMapping("/tags/{id}/delete")
    public String delete(@PathVariable Long id,RedirectAttributes attributes){
            tagService.delateTag(id);
            attributes.addFlashAttribute("message","删除成功");
            return "redirect:/admin/tags";
    }
}
