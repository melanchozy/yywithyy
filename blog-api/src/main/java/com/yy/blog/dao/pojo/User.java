package com.yy.blog.dao.pojo;

import lombok.Data;

@Data
public class User {
    private Long id;

    private String account;

    private String avatar;

    private Long createDate;

    private Long lastLogin;

    private String nickname;

    private String password;

    private String salt;
}
