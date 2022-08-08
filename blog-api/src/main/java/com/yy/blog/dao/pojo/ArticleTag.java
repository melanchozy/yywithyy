package com.yy.blog.dao.pojo;

import lombok.Data;

@Data
public class ArticleTag {
    /**
     * 记录文章与tags的对应关系
     */
    private Long id;
    private Long articleId;
    private Long tagId;
}
