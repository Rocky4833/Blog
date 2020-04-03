package com.qtx.web.admin;

import com.qtx.po.Type;
import com.qtx.service.TypeService;
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
public class TypeController {

    @Autowired
    private TypeService typeService;

    //分页对象参数：每页展示多少条数据，按照主键id进行排序，排序方式为降序
    @GetMapping("/types")
    public String types(@PageableDefault(size = 5,sort = {"id"},direction = Sort.Direction.DESC)
                                Pageable pageable, Model model) {
        model.addAttribute("page",typeService.listType(pageable));
        return "admin/types";
    }

    @GetMapping("/types/input")
    public String input(Model model){
        model.addAttribute("type",new Type());
        return "admin/types-input";
    }

    @GetMapping("/types/{id}/input")
    public String editInput(@PathVariable Long id, Model model){
        model.addAttribute("type",typeService.getType(id));
        return "admin/types-input";
    }

    //@Valid:表示该该对象需要校验
    //BindingResult:该对象会接收校验后的结果，用于前端展示,[前端如何拿到校验后的数据？]
    @PostMapping("/types")
    public String post(@Valid Type type, BindingResult result, RedirectAttributes attributes){
      //后端校验分类新增不能重复
      //首先要通过name查询数据
      //如果查询不到，说明数据库未曾添加该分类，新增成功，否则新增失败
        Type type1= typeService.getTypeByName(type.getName());
       if(type1 != null){
           result.rejectValue("name","nameError","不能添加重复分类");
       }

        //后端校验分类新增分类名称不能为空
        if(result.hasErrors()){
            return "admin/types-input";
        }

        Type t = typeService.saveType(type);
        if(t == null){
            attributes.addFlashAttribute("message","新增失败");
        }else{
            attributes.addFlashAttribute("message","新增成功");
        }
        return "redirect:/admin/types";
    }

    //BindingResult和Type必须连写
    @PostMapping("/types/{id}")
    public String editPost(@Valid Type type, BindingResult result,@PathVariable Long id ,RedirectAttributes attributes){
        //后端校验分类新增不能重复
        //首先要通过name查询数据
        //如果查询不到，说明数据库未曾添加该分类，新增成功，否则新增失败
        Type type1= typeService.getTypeByName(type.getName());
        if(type1 != null){
            result.rejectValue("name","nameError","不能添加重复分类");
        }

        //后端校验分类修改分类名称不能为空
        if(result.hasErrors()){
            return "admin/types-input";
        }

        Type t = typeService.updateType(id,type);
        if(t == null){
            attributes.addFlashAttribute("message","更新失败");
        }else{
            attributes.addFlashAttribute("message","更新成功");
        }
        return "redirect:/admin/types";
    }

    @GetMapping("/types/{id}/delete")
    public String delete(@PathVariable Long id,RedirectAttributes attributes){
            typeService.delateType(id);
            attributes.addFlashAttribute("message","删除成功");
            return "redirect:/admin/types";
    }
}
