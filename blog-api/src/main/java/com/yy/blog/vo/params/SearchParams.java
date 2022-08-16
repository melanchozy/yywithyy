package com.yy.blog.vo.params;

import lombok.Data;

@Data
public class SearchParams {
    private String key;
    private Integer page;
    private Integer size;
}
