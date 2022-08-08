package com.yy.blog.vo.params;

import com.yy.blog.vo.TagVo;
import lombok.Data;

import java.util.List;
@Data
public class ArticleParams {
    private String token;
    private String articleId;
    private ArticleBodyParams body;
    private String categoryId;
    private String summary;
    private List<TagVo> tags;
    private String title;
}
