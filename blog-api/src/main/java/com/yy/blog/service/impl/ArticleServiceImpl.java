package com.yy.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yy.blog.dao.mapper.ArticleBodyMapper;
import com.yy.blog.dao.mapper.ArticleMapper;
import com.yy.blog.dao.mapper.ArticleTagMapper;
import com.yy.blog.dao.mapper.TagMapper;
import com.yy.blog.dao.pojo.*;
import com.yy.blog.service.ArticleService;
import com.yy.blog.service.CategoryService;
import com.yy.blog.service.TagsService;
import com.yy.blog.service.UserService;
import com.yy.blog.vo.*;
import com.yy.blog.vo.params.ArticleParams;
import com.yy.blog.vo.params.PageParams;
import com.yy.blog.vo.params.SaveParams;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleBodyMapper articleBodyMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private ArticleTagMapper articleTagMapper;
    @Autowired
    private TagsService tagsService;
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Result listArticle(PageParams pageParams) {
        Page<Article> page = new Page<>(pageParams.getPage(),pageParams.getPageSize());
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        // 默认按创建时间倒序排序
        queryWrapper.orderByDesc(Article::getCreateDate);
        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);
        List<Article> records=articlePage.getRecords();
        // 转成VO
        List<ArticleVo> articleVoList=copyList(records,true,true);
        return Result.success(articleVoList);

    }

    @Override
    public Result findArticleById(Long articleId) {
        /*
        * 需要先查artcle表，再查artcle_body表
        * */
        Article article=articleMapper.selectById(articleId);
        ArticleVo articleVo=copy(article);
        return Result.success(articleVo);
    }

    @Override
    @Transactional
    public Result publishArticle(ArticleParams articleParam) {
        Article article=new Article();
        String token = articleParam.getToken();
        if(isBlank(token)){
            return Result.fail(-999,"fail to write:no token");
        }
        List<TagVo> tags=articleParam.getTags();
        if(tags==null){
            return Result.fail(-999,"fail to write:no tag");
        }
        Long userId = userService.findUserIdByToken(token);
        article.setAuthorId(userId);

        long random = (long) (Math.random() * 971006);
        boolean isPublish=true;
        long articleId=-1;
        if(articleParam.getArticleId()!=null) {
            isPublish=false;
            articleId = Long.valueOf(articleParam.getArticleId());
        }else{
            articleId = System.currentTimeMillis() + random;
        }

        // 插入 标签表 插入 标签和文章 表
        for(TagVo tag:tags){
            ArticleTag articleTag = new ArticleTag();
            articleTag.setArticleId(articleId);
            if(tag.getId()==null){
                long tagId = System.currentTimeMillis()+random;
                Tag newTag = new Tag();
                newTag.setId(tagId);
                newTag.setTagName(tag.getTagName());
                tagMapper.insert(newTag);
                articleTag.setTagId(tagId);
            }else{
                articleTag.setTagId(Long.parseLong(tag.getId()));
            }
            articleTagMapper.insert(articleTag);
        }

        article.setId(articleId);
        article.setCreateDate(System.currentTimeMillis());
        article.setSummary(articleParam.getSummary());
        article.setTitle(articleParam.getTitle());
        long bodyId = System.currentTimeMillis()+random;
        article.setBodyId(bodyId);
        // 插入文章内容
        ArticleBody articleBody = new ArticleBody();
        articleBody.setArticleId(articleId);
        articleBody.setId(bodyId);
        articleBody.setContent(articleParam.getBody().getContent());
        articleBody.setContentHtml(articleParam.getBody().getContentHtml());
        articleBodyMapper.insert(articleBody);
        // 插入文章表
        article.setCategoryId(Long.parseLong(articleParam.getCategoryId()));
        if(isPublish){
            articleMapper.insert(article);
        }else{
            articleMapper.updateById(article);
        }

        Map<String,String> map=new HashMap<>();
        map.put("articleId",article.getId().toString());
        if(redisTemplate.opsForValue().get(String.valueOf(userId))!=null){
            redisTemplate.delete(String.valueOf(userId));
        }
        return Result.success(map);
    }

    @Override
    public Result saveArticle(SaveParams saveParams) {
        String token = saveParams.getToken();
        if(isBlank(token)){
            return Result.fail(-999,"fail to save:no token");
        }
        Long userId = userService.findUserIdByToken(token);
        ArticleBodyVo articleBodyVo = new ArticleBodyVo();
        articleBodyVo.setContent(saveParams.getContent());
        redisTemplate.opsForValue().set(String.valueOf(userId),articleBodyVo);
        return Result.success(articleBodyVo);
    }

    /**
     * 按传的token查找用户id，如缓存中存在id对应的文档值，返回该值。
     * @param token
     * @return
     */
    @Override
    public Result newArticle(String token) {
        Long userId = userService.findUserIdByToken(token);
        Object obj = redisTemplate.opsForValue().get(String.valueOf(userId));
        if(obj==null){
            return Result.success(null);
        }
        return Result.success(obj);
    }

    public ArticleVo copy(Article article, boolean isAuthor, boolean isTags){
        ArticleVo articleVo = new ArticleVo();
        BeanUtils.copyProperties(article, articleVo);
        articleVo.setId(String.valueOf(article.getId()));
        // 不是所有接口都需要 标签，作者信息
        if (isAuthor) {
            Long authorId=article.getAuthorId();
            UserVo sysUser = userService.findUserVoById(authorId);
            articleVo.setAuthor(sysUser);
        }
        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
        if (isTags){
            Long articleId=article.getId();
            List<TagVo> tags = tagsService.findTagsByArticleId(articleId);
            articleVo.setTags(tags);
        }
        return articleVo;
    }
    /*
    * 文章详情Vo复制
    */
    public ArticleVo copy(Article article){
        ArticleVo articleVo = new ArticleVo();
        BeanUtils.copyProperties(article, articleVo);
        articleVo.setId(String.valueOf(article.getId()));
        // 作者信息，创建时间，标签信息
        Long authorId=article.getAuthorId();
        UserVo sysUser = userService.findUserVoById(authorId);
        articleVo.setAuthor(sysUser);
        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));

        Long articleId=article.getId();
        List<TagVo> tags = tagsService.findTagsByArticleId(articleId);
        articleVo.setTags(tags);
        //文章内容
        Long bodyId = article.getBodyId();
        ArticleBody articleBody = articleBodyMapper.selectById(bodyId);
        ArticleBodyVo articleBodyVo=new ArticleBodyVo();
        articleBodyVo.setContent(articleBody.getContent());
        articleBodyVo.setContentHtml(articleBody.getContentHtml());
        articleVo.setBody(articleBodyVo);
        //文章分类
        Long categoryId=article.getCategoryId();
        articleVo.setCategory(categoryService.findCategoryById(categoryId));
        return articleVo;
    }

    private List<ArticleVo> copyList(List<Article> records,boolean isAuthor,boolean isTags) {
        List<ArticleVo> articleVoList = new ArrayList<>();
        for (Article article : records) {
            ArticleVo articleVo = copy(article,isAuthor,isTags);
            articleVoList.add(articleVo);
        }
        return articleVoList;
    }

}
