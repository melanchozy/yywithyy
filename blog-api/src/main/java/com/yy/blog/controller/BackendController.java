package com.yy.blog.controller;

import com.yy.blog.dao.mapper.YyMapper;
import com.yy.blog.dao.pojo.Yy;
import com.yy.blog.vo.Result;
import com.yy.blog.vo.params.YyParams;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("backend")
public class BackendController {

    @Autowired
    private YyMapper yyMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("yy")
    public Result updateYy(@RequestBody YyParams yyParams){
        Yy yy = new Yy();
        BeanUtils.copyProperties(yyParams,yy);
        int i = yyMapper.updateById(yy);
        if(i!=0){
            redisTemplate.delete("yys");
            return Result.success("Successfully modified yy information");
        }
        return Result.fail(-988,"failed to update YY");
    }
}
