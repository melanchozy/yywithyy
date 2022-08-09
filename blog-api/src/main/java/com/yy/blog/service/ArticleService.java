package com.yy.blog.service;

import com.yy.blog.dao.pojo.Article;
import com.yy.blog.vo.Result;
import com.yy.blog.vo.params.ArticleParams;
import com.yy.blog.vo.params.PageParams;
import com.yy.blog.vo.params.SaveParams;

import java.util.List;

public interface ArticleService {
    /**
     * 分页查询 文章列表
     * @param pageParams
     * @return
     */
    Result listArticle(PageParams pageParams);

    /*
    * 按文章id查询文章
    * */
    Result findArticleById(Long articleId);

    /**
     * write article
     * @param articleParam
     * @return
     */
    Result publishArticle(ArticleParams articleParam);

    Result saveArticle(SaveParams saveParams);

    int updateCateByCateId(Long cateId);

    Result newArticle(String token);
}
