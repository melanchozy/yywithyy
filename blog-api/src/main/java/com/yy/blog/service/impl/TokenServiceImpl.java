package com.yy.blog.service.impl;

import com.yy.blog.service.TokenService;
import com.yy.blog.service.UserService;
import com.yy.blog.utils.JWTUtils;
import com.yy.blog.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public class TokenServiceImpl implements TokenService {
//    @Autowired
//    private UserService UserService;
    @Override
    public Object checkToken(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        Map<String, Object> stringObjectMap = JWTUtils.checkToken(token);
        if (stringObjectMap == null) {
            return null;
        }
        Object userId = stringObjectMap.get("userId");
        return userId;
    }
}
