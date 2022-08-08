package com.yy.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestController {
    @Autowired
    private RedisTemplate redisTemplate;
    @RequestMapping("index")
    public String index(){
        System.out.println("test..");
        return "index";
    }
    @RequestMapping("redis")
    public String redis(){
        System.out.println("redis..");
        if(redisTemplate.opsForValue().get("name")!=null){
            redisTemplate.opsForValue().set("name","yy");
            System.out.println("yes");
        }else{
            redisTemplate.opsForValue().set("name","zy");
        }
        return "1null";
    }
}
