package com.yy.blog.service.impl;

import com.yy.blog.dao.pojo.User;
import com.yy.blog.service.LoginService;
import com.yy.blog.service.UserService;
import com.yy.blog.utils.JWTUtils;
import com.yy.blog.vo.Result;
import com.yy.blog.vo.params.LoginParams;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserService UserService;
    @Autowired
    private RedisTemplate redisTemplate;
    private static final String salt = ".yyloveyy*";

    @Override
    public Result login(LoginParams loginparams) {

        String account = loginparams.getAccount();
        String password = loginparams.getPassword();
        //检查参数是否合法
        if (StringUtils.isBlank(account) || StringUtils.isBlank(password)) {
            return Result.fail(10001, "bad account or password");
        }
        // 密码加密
        password = DigestUtils.md5Hex(password + salt);
        //查询用户名密码
        User sysUser = UserService.findUser(account, password);
        //不存在 登录失败
        if (sysUser == null) {
            return Result.fail(10001, "bad account or password");
        }
        //存在 生成token 返回给前端
        String token = JWTUtils.createToken(sysUser.getId());
//        //用户id存入Redis
//        redisTemplate.opsForValue().set("token",token);
        return Result.success(token);
    }
}

