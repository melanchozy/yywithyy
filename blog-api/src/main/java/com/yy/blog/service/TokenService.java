package com.yy.blog.service;

import com.yy.blog.vo.UserVo;

public interface TokenService {
    Object checkToken(String token);
}
