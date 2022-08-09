package com.yy.blog.service;

import com.yy.blog.dao.pojo.Tag;
import com.yy.blog.vo.Result;
import com.yy.blog.vo.TagVo;
import com.yy.blog.vo.params.TagParams;

import java.util.List;

public interface TagsService {
    List<TagVo> findTagsByArticleId(Long articleId);
    Result findAll();
    Result newTag(String tagName);
    int updateTagByTagId(Long id);
    Result delTag(Long id);
    Result upTag(TagParams tagParams);
}
