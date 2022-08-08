package com.yy.blog.controller;

import com.yy.blog.service.CategoryService;
import com.yy.blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("cate")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @GetMapping
    public Result listCategory(){
        Result all = categoryService.findAll();
        return all;
    }
}
