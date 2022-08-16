package com.yy.blog.dao.eso;

import lombok.Data;

@Data
public class ArticleEso {
    private String id;

    private String title;

    private String summary;

    private String createDate;

    private String categoryVo;

    private String tagVo;

    private String userVo;

    private String content;

}
