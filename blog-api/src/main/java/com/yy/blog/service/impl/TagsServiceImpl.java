package com.yy.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yy.blog.dao.eso.TagEso;
import com.yy.blog.dao.mapper.ArticleTagMapper;
import com.yy.blog.dao.mapper.TagMapper;
import com.yy.blog.dao.pojo.Article;
import com.yy.blog.dao.pojo.ArticleTag;
import com.yy.blog.dao.pojo.Tag;
import com.yy.blog.service.TagsService;
import com.yy.blog.utils.Mq;
import com.yy.blog.vo.Result;
import com.yy.blog.vo.TagVo;
import com.yy.blog.vo.params.TagParams;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
public class TagsServiceImpl implements TagsService {
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

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
            rabbitTemplate.convertAndSend(Mq.ART_EXCHANGE,Mq.TAG_INSERT_KEY,tagId);
            return Result.success(tag);
        }else{
            return Result.fail(-999,"fail to new tag");
        }
    }

    // 删除标签1/2，根据tag_id更新tag—article表中的对应tag
    @Override
    public int updateTagByTagId(Long id) {
        LambdaUpdateWrapper<ArticleTag> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ArticleTag::getTagId,id)
                .set(ArticleTag::getTagId,10086);
        int update = articleTagMapper.update(null,updateWrapper);
        return update;
    }
    // 删除标签2/2
    @Override
    public Result delTag(Long id) {
        int i1 = tagMapper.deleteById(id);
        if(i1==0){
            return Result.fail(-998,"failed to delete tag");
        }
        rabbitTemplate.convertAndSend(Mq.ART_EXCHANGE,Mq.TAG_DELETE_KEY,id);
        return Result.success("Successfully delete tag");
    }

    @Override
    public Result upTag(TagParams tagParams) {
        Tag tag = new Tag();
        tag.setId(Long.parseLong(tagParams.getId()));
        tag.setTagName(tagParams.getName());
        int i = tagMapper.updateById(tag);
        if(i==0){
            return Result.fail(-998,"failed to update tag");
        }
        rabbitTemplate.convertAndSend(Mq.ART_EXCHANGE,Mq.TAG_DELETE_KEY,tag.getId());
        return Result.success("Successfully update tag");
    }

    @Override
    public List<TagEso> findAllToEso() {
        List<Tag> tags = tagMapper.selectList(null);
        List<TagEso> tagEsos=new ArrayList<>();
        for(Tag tag:tags){
            TagEso tagEso=new TagEso();
            tagEso.setId(tag.getId().toString());
            tagEso.setName(tag.getTagName());
            tagEso.setSuggestion(Arrays.asList(tag.getTagName()));
            tagEsos.add(tagEso);
//            System.out.println(tagEso.getSuggestion());
        }
        return tagEsos;
    }

    @Override
    public TagEso findTgaByIdToEso(Long tagId) {
        Tag tag = tagMapper.selectById(tagId);
        if(tag==null)
            return null;
        TagEso tagEso=new TagEso();
        tagEso.setId(tag.getId().toString());
        tagEso.setName(tag.getTagName());
        tagEso.setSuggestion(Arrays.asList(tag.getTagName()));
//            System.out.println(tagEso.getSuggestion());
        return tagEso;
    }
}
