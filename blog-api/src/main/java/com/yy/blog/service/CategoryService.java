package com.yy.blog.service;

import com.yy.blog.vo.CategoryVo;
import com.yy.blog.vo.Result;
import com.yy.blog.vo.params.CategoryParams;

public interface CategoryService {
    CategoryVo findCategoryById(Long categoryId);
    Result findAll();
    String newCate(CategoryParams categoryParams);
    Result delCate(Long cateId);
    Result upCate(CategoryParams categoryParams);
}
