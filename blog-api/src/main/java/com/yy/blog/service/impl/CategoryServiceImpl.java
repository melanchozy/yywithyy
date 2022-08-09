package com.yy.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yy.blog.dao.mapper.CategoryMapper;
import com.yy.blog.dao.pojo.Category;
import com.yy.blog.service.ArticleService;
import com.yy.blog.service.CategoryService;
import com.yy.blog.vo.CategoryVo;
import com.yy.blog.vo.Result;
import com.yy.blog.vo.params.CategoryParams;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;


    @Override
    public CategoryVo findCategoryById(Long categoryId) {
        Category category = categoryMapper.selectById(categoryId);
        CategoryVo categoryVo=new CategoryVo();
        BeanUtils.copyProperties(category,categoryVo);
        categoryVo.setId(String.valueOf(category.getId()));
        return categoryVo;
    }

    @Override
    public Result findAll() {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        List<Category> categories = categoryMapper.selectList(queryWrapper);
        return Result.success(categories);
    }

    @Override
    public String newCate(CategoryParams categoryParams) {
        Category category = new Category();
        category.setCategoryName(categoryParams.getCategoryName());
        category.setAvatar(categoryParams.getAvatar());
        category.setDescription(categoryParams.getDescription());
        long random = (long) (Math.random() * 971006);
        long cateId = System.currentTimeMillis() + random;
        category.setId(cateId);
        int insert = categoryMapper.insert(category);
        if(insert!=0){
            return "Successfully create new category";
        }
        return "Failed to create new category";
    }

    @Override
    public Result delCate(Long cateId) {
        int i1 = categoryMapper.deleteById(cateId);
        if(i1==0){
            return Result.fail(-998,"failed to delete category");
        }
        return Result.success("Successfully delete category");
    }

    @Override
    public Result upCate(CategoryParams categoryParams) {
        Category category = new Category();
        category.setId(Long.parseLong(categoryParams.getId()));
        category.setCategoryName(categoryParams.getCategoryName());
        category.setAvatar(categoryParams.getAvatar());
        category.setDescription(categoryParams.getDescription());
        int i = categoryMapper.updateById(category);
        if(i==0){
            return Result.fail(-998,"failed to update category");
        }
        return Result.success("Successfully update category");
    }
}
