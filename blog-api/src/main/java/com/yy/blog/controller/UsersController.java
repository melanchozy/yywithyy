package com.yy.blog.controller;

import com.yy.blog.dao.pojo.Yy;
import com.yy.blog.service.UserService;
import com.yy.blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("users")
public class UsersController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("currentUser")
    public Result currentUser(@RequestHeader("Authorization") String token){
        return userService.findUserByToken(token);
    }

    @GetMapping("yy")
    public Result getYy(){
        Object yys = redisTemplate.opsForValue().get("yys");
        if(yys!=null){
            return Result.success(yys);
        }
        List<Yy> yy = userService.findYy();
        if(yy==null){
            return Result.fail(-998,"no yys' information.");
        }
        redisTemplate.opsForValue().set("yys",yy);
        return Result.success(yy);
    }

}
