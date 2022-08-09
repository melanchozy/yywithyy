package com.yy.blog.dao.pojo;

import lombok.Data;

@Data
public class Article {
    private Long id;

    private String title;

    private String summary;
    /**
     * 作者id
     */
    private Long authorId;
    /**
     * 内容id
     */
    private Long bodyId;
    /**
     *类别id
     */
    private Long categoryId;
    /**
     * 创建时间
     */
    private Long createDate;
    // 是否逻辑删除，是1，不是0
    private Integer isDelete;
    // 置顶权重
    private Long weight;
}
