package com.yy.blog.controller;

import com.yy.blog.dao.mapper.YyMapper;
import com.yy.blog.dao.pojo.Tag;
import com.yy.blog.dao.pojo.Yy;
import com.yy.blog.service.ArticleService;
import com.yy.blog.service.CategoryService;
import com.yy.blog.service.TagsService;
import com.yy.blog.vo.Result;
import com.yy.blog.vo.params.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import static org.apache.commons.lang3.StringUtils.isBlank;

@RestController
@RequestMapping("backend")
public class BackendController {

    @Autowired
    private YyMapper yyMapper;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private TagsService tagsService;

    @PostMapping("yy")
    public Result updateYy(@RequestBody YyParams yyParams){
        Yy yy = new Yy();
        BeanUtils.copyProperties(yyParams,yy);
        int i = yyMapper.updateById(yy);
        if(i!=0){
            redisTemplate.delete("yys");
            return Result.success("Successfully modified yy information");
        }
        return Result.fail(-988,"failed to update YY");
    }

    // 新增category
    @PostMapping("newcate")
    public Result newCate(@RequestBody CategoryParams categoryParams){
        return Result.success(categoryService.newCate(categoryParams));
    }
    // 删除category
    @PostMapping("delcate")
    @Transactional
    public Result delCate(@RequestParam String cateId){
        if(isBlank(cateId)){
            return Result.fail(-998,"wrong category id");
        }
        long id = Long.parseLong(cateId);
        if(id==10086){
            return Result.fail(-998,"can not delete this category");
        }
        //更改该分类下的所有文章分类为未知分类
        articleService.updateCateByCateId(id);
        //删除该分类
        return categoryService.delCate(id);
    }
    // 修改category
    @PostMapping("upcate")
    public Result updateCate(@RequestBody CategoryParams categoryParams){
        return categoryService.upCate(categoryParams);
    }

    // 新建tag
    @PostMapping("newtag")
    public Result newTag(@RequestParam String name){
        if(isBlank(name)){
            return Result.fail(-998,"wrong name");
        }
        return tagsService.newTag(name);
    }
    // 删除tag
    @PostMapping("deltag")
    @Transactional
    public Result delTag(@RequestParam String tagId){
        if(isBlank(tagId)){
            return Result.fail(-998,"wrong tag id");
        }
        long id = Long.parseLong(tagId);
        if(id==10086){
            return Result.fail(-998,"can not delete this tag");
        }
        //更改该标签下的所有文章该标签为未知标签
        tagsService.updateTagByTagId(id);
        //删除该标签
        return tagsService.delTag(id);
    }
    // 修改tag
    @PostMapping("uptag")
    public Result updateTag(@RequestBody TagParams tagParams){
        if(isBlank(tagParams.getName())){
            return Result.fail(-998,"wrong tag name");
        }
        if(isBlank(tagParams.getId())){
            return Result.fail(-998,"wrong tag id");
        }
        return tagsService.upTag(tagParams);
    }

    // 逻辑删除文章
    @PostMapping("ldelart")
    public Result logicdelArticle(@RequestParam String articleId){
        if(isBlank(articleId)){
            return Result.fail(-998,"wrong article id");
        }
        long id = Long.parseLong(articleId);
        return articleService.logicdel(id);
    }
    // 恢复被逻辑删除的文章
    @PostMapping("reart")
    public Result reArticle(@RequestParam String articleId){
        if(isBlank(articleId)){
            return Result.fail(-998,"wrong article id");
        }
        long id = Long.parseLong(articleId);
        return articleService.reArt(id);
    }
    // 彻底删除文章
    @PostMapping("delart")
    public Result delArticle(@RequestParam String articleId){
        if(isBlank(articleId)){
            return Result.fail(-998,"wrong article id");
        }
        long id = Long.parseLong(articleId);
        return articleService.delArt(id);
    }
    // 分页查询文章
    @PostMapping("articles")
    public Result articles(@RequestBody PageParams pageParams){
        return articleService.allArticle(pageParams);
    }
    // 修改文章信息（文章标题、简述、时间、分类、置顶）
    @PostMapping("upart")
    public Result upArticle(@RequestBody BackArticleParams backArticleParams){
        return articleService.updateArticle(backArticleParams);
    }
}
