package com.yy.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yy.blog.dao.mapper.TagMapper;
import com.yy.blog.dao.pojo.Tag;
import com.yy.blog.service.TagsService;
import com.yy.blog.vo.Result;
import com.yy.blog.vo.TagVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
public class TagsServiceImpl implements TagsService {
    @Autowired
    private TagMapper tagMapper;
    public TagVo copy(Tag tag){
        TagVo tagVo = new TagVo();
        BeanUtils.copyProperties(tag,tagVo);
        tagVo.setId(String.valueOf(tag.getId()));
        return tagVo;
    }
    public List<TagVo> copyList(List<Tag> tagList){
        List<TagVo> tagVoList = new ArrayList<>();
        for (Tag tag : tagList) {
            tagVoList.add(copy(tag));
        }
        return tagVoList;
    }
    @Override
    public List<TagVo> findTagsByArticleId(Long articleId) {
        List<Tag> tags = tagMapper.findTagsByArticleId(articleId);
        return copyList(tags);
    }

    @Override
    public Result findAll() {
        LambdaQueryWrapper queryWrapper=new LambdaQueryWrapper<>();
        List<Tag> tags = tagMapper.selectList(queryWrapper);
        return Result.success(tags);
    }

    @Override
    public Result newTag(String tagName) {
        if(isBlank(tagName)){
            return Result.fail(-999,"input tagname");
        }
        long random = (long) (Math.random() * 971006);
        long tagId = System.currentTimeMillis()+random;
        Tag tag=new Tag();
        tag.setTagName(tagName);
        tag.setId(tagId);
        int res=tagMapper.insert(tag);
        if(res!=0){
            return Result.success(tag);
        }else{
            return Result.fail(-999,"fail to new tag");
        }

    }
}
