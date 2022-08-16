package com.yy.blog.vo;

import com.yy.blog.dao.eso.ArticleEso;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchVo {
    private Long total;
    private List<ArticleEso> articleEsoList;
}
