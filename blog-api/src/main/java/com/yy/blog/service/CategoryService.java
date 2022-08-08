package com.yy.blog.service;

import com.yy.blog.vo.CategoryVo;
import com.yy.blog.vo.Result;

public interface CategoryService {
    CategoryVo findCategoryById(Long categoryId);
    Result findAll();
}
