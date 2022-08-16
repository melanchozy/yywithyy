package com.yy.blog.dao.eso;

import lombok.Data;

import java.util.List;
@Data
public class TagEso {
    private String id;

    private String name;

    private List<String> suggestion;
}
