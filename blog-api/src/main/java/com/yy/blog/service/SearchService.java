package com.yy.blog.service;

import com.yy.blog.vo.SearchVo;
import com.yy.blog.vo.params.SearchParams;

import java.util.List;

public interface SearchService {
    SearchVo search(SearchParams searchParams);

    void deleteById(Long id);

    void insertById(Long id);

    List<String> getTags(String key);

    void updateTagIndex();
    void updateArticleIndex();
    // mq监听tag操作队列
    void insertTagById(Long id);
    void deleteTagById(Long id);
}
