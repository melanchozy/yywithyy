package com.yy.blog.controller;

import com.yy.blog.service.ArticleService;
import com.yy.blog.service.UserService;
import com.yy.blog.vo.Result;
import com.yy.blog.vo.params.ArticleParams;
import com.yy.blog.vo.params.PageParams;
import com.yy.blog.vo.params.SaveParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("articles")
public class ArticleController {
    @Autowired
    private ArticleService articleService;


    /*
    * 主页展示文章
    * */
    @PostMapping
    public Result listArticle(@RequestBody PageParams pageParams){

        return articleService.listArticle(pageParams);
    }

    /*
    * 跳转文章详情
    * */
    @PostMapping("detail/{id}")
    public Result findArticleById(@PathVariable("id") Long articleId){
        return articleService.findArticleById(articleId);
    }

    @GetMapping("new")
    public Result newArticle(@RequestHeader("Authorization") String token){
        return articleService.newArticle(token);
    }

    @PostMapping("publish")
    public Result writeArticle(@RequestBody ArticleParams articleParam){
        return articleService.publishArticle(articleParam);
    }

    @PostMapping("save")
    public Result saveArricle(@RequestBody SaveParams saveParams){
        return articleService.saveArticle(saveParams);
    }

}
