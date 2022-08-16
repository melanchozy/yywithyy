package com.yy.blog.service;

import com.yy.blog.dao.eso.ArticleEso;
import com.yy.blog.vo.Result;
import com.yy.blog.vo.params.ArticleParams;
import com.yy.blog.vo.params.BackArticleParams;
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

    /**
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
    Result logicdel(Long articleId);
    // 恢复逻辑删除的文章
    Result reArt(Long articleId);
    // 彻底删除文章
    Result delArt(Long articleId);
    // 后台查询所有文章
    Result allArticle(PageParams pageParams);
    // 更新文章信息
    Result updateArticle(BackArticleParams backArticleParams);

    //查询转为eso对象
    List<ArticleEso> findAllToEso();
    ArticleEso findToEso(Long articleId);
}
