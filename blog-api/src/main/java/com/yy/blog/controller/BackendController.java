package com.yy.blog.controller;

import com.yy.blog.dao.mapper.YyMapper;
import com.yy.blog.dao.pojo.Yy;
import com.yy.blog.service.ArticleService;
import com.yy.blog.service.CategoryService;
import com.yy.blog.vo.Result;
import com.yy.blog.vo.params.CategoryParams;
import com.yy.blog.vo.params.YyParams;
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

}
