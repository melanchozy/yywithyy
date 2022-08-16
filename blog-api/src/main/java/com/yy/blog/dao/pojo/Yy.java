package com.yy.blog.dao.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class Yy {
    private Long id;
    private String name;
    private String gender;
    private String github;
    private String email;
    private String post;
    private String avatar;
    @TableField(value = "aboutme")
    private String aboutMe;
}
