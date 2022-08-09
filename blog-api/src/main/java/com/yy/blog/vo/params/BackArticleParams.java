package com.yy.blog.vo.params;

import lombok.Data;

@Data
public class BackArticleParams {
    private Long id;
    private String title;
    private String summary;
    private Long createDate;
    private Long categoryId;
    private Long weight;
}
