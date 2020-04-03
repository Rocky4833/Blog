package com.qtx.service;

import com.qtx.dao.BlogReposity;
import com.qtx.exception.NotFindException;
import com.qtx.po.Blog;
import com.qtx.po.Type;
import com.qtx.util.MarkdownUtils;
import com.qtx.util.MyBeanUtils;
import com.qtx.vo.BlogQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.criteria.*;
import java.util.*;

@Service
public class BlogServiceImpl implements BlogService {
   @Autowired
   private BlogReposity blogReposity;

    @Override
    public Blog getBlog(Long id) {
        return blogReposity.getOne(id);
    }

    @Transactional
    @Override
    public Blog getAndConvert(Long id) {
        Blog blog = blogReposity.getOne(id);
        if(blog == null){
           throw new NotFindException("博客资源不存在");
        }
        //防止数据库博客内容变成HTML格式
        //创建一个新的Blog对象b
        Blog b = new Blog();
        //将通过id查询到的blog对象复制给b对象
        BeanUtils.copyProperties(blog,b);
        //b对象通过getContent方法获取markdown格式的博客对象内容
        String content = b.getContent();
        //利用工具类MarkdownUtils将markdown格式的内容content转换成HTML格式
        //重新赋值给b对象的content属性
        b.setContent( MarkdownUtils.markdownToHtmlExtensions(content));
        //每次刷新博客详情页面
        // 会根据当前博客主键id调用blogReposity.updateViews(id)方法
        // 实现浏览次数加一
        blogReposity.updateViews(id);
        return b;
    }

    /**
     * 分页动态查询[根据不同条件组合查询]
     * @param pageable:分页查询对象
     * @param blog：将前端页面查询参数封装为Blog对象
     * @return
     */
    @Override
    public Page<Blog> listBlog(Pageable pageable, BlogQuery blog) {
        return blogReposity.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                //创建一个List集合，用来存放查询条件
                List<Predicate> predicates = new ArrayList<>();
                //动态的条件组合
                //根据title模糊查询
                if(!"".equals(blog.getTitle())&&blog.getTitle()!=null){
                    predicates.add(cb.like(root.<String>get("title"),"%"+ blog.getTitle()+"%"));
                }
                //根据Type的主键Id判断是否相等查询
                if(blog.getTypeId() != null){
                    predicates.add(cb.equal(root.<Type>get("type").get("id"),blog.getTypeId()));
                }
                //根据是否推荐查询
                if(blog.isRecommend()){
                    predicates.add(cb.equal(root.<Boolean>get("recommend"),blog.isRecommend()));
                }
                cq.where(predicates.toArray(new Predicate[predicates.size()]));

                return null;
            }
        },pageable);
    }

    @Override
    public Page<Blog> listBlog(Pageable pageable) {
        return blogReposity.findAll(pageable);
    }

    @Override
    public Page<Blog> listBlog(Long tagId, Pageable pageable) {
        return blogReposity.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                //关联查询 构建查询条件
                Join join = root.join("tags");
                return cb.equal(join.get("id"),tagId);
            }
        },pageable);
    }

    @Override
    public Page<Blog> listBlog(String query, Pageable pageable) {
        return blogReposity.findByQuery(query,pageable);
    }

    @Override
    public List<Blog> listRecommendBlogTop(Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updateTime");
        Pageable pageable= PageRequest.of(0,size,sort);
        return blogReposity.findTop(pageable);
    }

    @Override
    public Map<String, List<Blog>> archiveBlog() {
        //根据年份分组 查询年份集合
        List<String> years = blogReposity.findGroupYears();
        Map<String, List<Blog>> map =new HashMap<>();
        for(String year : years){
            //循环遍历years
            //根据每一个year 查询对应year的博客列表
            //然后一并封装到一个map集合中
            map.put(year,blogReposity.findByYear(year));
        }
        return map;
    }

    @Override
    public Long countBlog() {
        return blogReposity.count();
    }

    @Transactional
    @Override
    public Blog saveBlog(Blog blog) {
        if(blog.getId()==null){
            //新增博客初始化
            blog.setCreateTime(new Date());
            blog.setUpdateTime(new Date());
            blog.setViews(0);
        }else{
            //修改博客初始化
            blog.setUpdateTime(new Date());
        }
        return blogReposity.save(blog);
    }

    @Transactional
    @Override
    public Blog updateBlog(Long id, Blog blog) {
        Blog b = blogReposity.getOne(id);
        if(b == null){
            throw new NotFindException("该博客不存在");
        }
        BeanUtils.copyProperties(blog,b, MyBeanUtils.getNullPropertyNames(blog));
        b.setUpdateTime(new Date());
        return blogReposity.save(b);
    }

    @Transactional
    @Override
    public void deleteBlog(Long id) {
        blogReposity.deleteById(id);
    }
}
