package com.yy.blog.service;

import com.yy.blog.vo.Result;
import com.yy.blog.vo.TagVo;

import java.util.List;

public interface TagsService {
    List<TagVo> findTagsByArticleId(Long articleId);
    Result findAll();
    Result newTag(String tagName);
}
