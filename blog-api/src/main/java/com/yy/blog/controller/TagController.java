package com.yy.blog.controller;

import com.yy.blog.service.CategoryService;
import com.yy.blog.service.TagsService;
import com.yy.blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("tag")
public class TagController {
    @Autowired
    private TagsService tagsService;
    @GetMapping
    public Result listTag(){
        Result all = tagsService.findAll();
        return all;
    }
    @PostMapping("new")
    public Result newTag(@RequestParam("name") String tagName){
        Result res = tagsService.newTag(tagName);
        return res;
    }
}
