package com.yy.blog.service;

import com.yy.blog.vo.Result;
import com.yy.blog.vo.UserVo;
import com.yy.blog.vo.params.LoginParams;


public interface LoginService {
    /**
     * 登录功能
     * @param loginparams
     * @return
     */
    Result login(LoginParams loginparams);

//    Result logout(String token);
//
//    Result register(LoginParams loginParams);
}
