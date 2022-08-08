package com.yy.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yy.blog.dao.mapper.UserMapper;
import com.yy.blog.dao.pojo.User;
import com.yy.blog.service.TokenService;
import com.yy.blog.service.UserService;
import com.yy.blog.vo.Result;
import com.yy.blog.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper UserMapper;

    @Autowired
    private TokenService tokenService;

    @Override
    public User findUserById(Long userId) {
        User sysUser = UserMapper.selectById(userId);
        if (sysUser == null) {
            sysUser = new User();
            sysUser.setNickname("zy");
        }
        return sysUser;
    }

    @Override
    public UserVo findUserVoById(Long userId) {
        User user = UserMapper.selectById(userId);
        UserVo userVo=new UserVo();
        BeanUtils.copyProperties(user,userVo);
        userVo.setId(String.valueOf(user.getId()));
        return userVo;
    }

    @Override
    public User findUser(String account, String password) {
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getAccount,account);
        queryWrapper.eq(User::getPassword,password);
        queryWrapper.select(User::getAccount,User::getId,User::getAvatar,User::getNickname);
        queryWrapper.last("limit 1");

        return UserMapper.selectOne(queryWrapper);
    }

    @Override
    public Result findUserByToken(String token) {
        Object id=tokenService.checkToken(token);
        if(id==null){
            return Result.fail(-800, "please login");
        }
        Long userId=Long.valueOf(String.valueOf(id));
        User user = UserMapper.selectById(userId);
        UserVo userVo=new UserVo();
        BeanUtils.copyProperties(user,userVo);
        userVo.setId(String.valueOf(user.getId()));
        return Result.success(userVo);
    }

    @Override
    public Long findUserIdByToken(String token) {
        Object id=tokenService.checkToken(token);
        return Long.valueOf(String.valueOf(id));
    }
}
