package com.yy.blog.service;

import com.yy.blog.dao.pojo.User;
import com.yy.blog.vo.Result;
import com.yy.blog.vo.UserVo;
public interface UserService {

    User findUserById(Long userId);
    UserVo findUserVoById(Long userId);

    User findUser(String account, String password);

    Result findUserByToken(String token);

    Long findUserIdByToken(String token);
}
