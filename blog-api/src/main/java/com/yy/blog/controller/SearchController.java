package com.yy.blog.controller;

import com.yy.blog.service.SearchService;
import com.yy.blog.vo.Result;
import com.yy.blog.vo.SearchVo;
import com.yy.blog.vo.params.SearchParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("search")
public class SearchController {
    @Autowired
    private SearchService searchService;
    @PostMapping("/list")
    public Result search(@RequestBody SearchParams searchParams){
        SearchVo search = searchService.search(searchParams);
        if(search==null){
            return Result.fail(-998,"Failed");
        }
        return Result.success(search);
    }
    // 标签的自动补全
    @GetMapping("tag")
    public  List<String> getTags(@RequestParam("key") String key){
        return searchService.getTags(key);
    }

}
