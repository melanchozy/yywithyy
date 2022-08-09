package com.yy.blog.vo;

import lombok.Data;

import java.util.List;

@Data
public class ArticleVo {
    private String id;

    private String title;

    private String summary;

    private Long weight;
    /**
     * 创建时间
     */
    private String createDate;

    private UserVo author;

    private ArticleBodyVo body;

    private List<TagVo> tags;

    private CategoryVo category;
}
