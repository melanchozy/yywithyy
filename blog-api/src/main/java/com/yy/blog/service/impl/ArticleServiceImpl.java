package com.yy.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yy.blog.dao.eso.ArticleEso;
import com.yy.blog.dao.mapper.ArticleBodyMapper;
import com.yy.blog.dao.mapper.ArticleMapper;
import com.yy.blog.dao.mapper.ArticleTagMapper;
import com.yy.blog.dao.mapper.TagMapper;
import com.yy.blog.dao.pojo.*;
import com.yy.blog.service.ArticleService;
import com.yy.blog.service.CategoryService;
import com.yy.blog.service.TagsService;
import com.yy.blog.service.UserService;
import com.yy.blog.utils.Mq;
import com.yy.blog.vo.*;
import com.yy.blog.vo.params.ArticleParams;
import com.yy.blog.vo.params.BackArticleParams;
import com.yy.blog.vo.params.PageParams;
import com.yy.blog.vo.params.SaveParams;
import org.joda.time.DateTime;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public Result listArticle(PageParams pageParams) {
        if(redisTemplate.opsForValue().get("indexArticle")!=null){
            return Result.success(redisTemplate.opsForValue().get("indexArticle"));
        }
        Page<Article> page = new Page<>(pageParams.getPage(),pageParams.getPageSize());
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        // ?????????????????????????????????????????????????????????
        queryWrapper.ne(Article::getIsDelete,1)
                .orderByDesc(Article::getWeight)
                .orderByDesc(Article::getCreateDate);
        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);
        List<Article> records=articlePage.getRecords();
        if(records.isEmpty())
            return Result.fail(-998,"article not found");
        // ??????VO
        List<ArticleVo> articleVoList=copyList(records,true,true);
        redisTemplate.opsForValue().set("indexArticle",articleVoList);
        return Result.success(articleVoList);
    }

    @Override
    public Result findArticleById(Long articleId) {
        /*
        * ????????????artcle????????????artcle_body???
        * */
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.ne(Article::getIsDelete,1)
                .eq(Article::getId,articleId);
        Article article=articleMapper.selectOne(queryWrapper);
        if(article==null){
            return Result.fail(-998,"article not found");
        }
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

        // ?????? ????????? ?????? ??????????????? ???
        for(TagVo tag:tags){
            ArticleTag articleTag = new ArticleTag();
            articleTag.setArticleId(articleId);
            LambdaQueryWrapper<Tag> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(Tag::getTagName,tag.getTagName());
            Tag tagtag = tagMapper.selectOne(queryWrapper);
            if(tagtag==null){
                long tagId = System.currentTimeMillis()+random;
                Tag newTag = new Tag();
                newTag.setId(tagId);
                newTag.setTagName(tag.getTagName());
                tagMapper.insert(newTag);
                rabbitTemplate.convertAndSend(Mq.ART_EXCHANGE,Mq.TAG_INSERT_KEY,tagId);
                articleTag.setTagId(tagId);
            }else{
                articleTag.setTagId(tagtag.getId());
            }
            // ???????????????????????????????????????????????????
            if(!isPublish){
                LambdaQueryWrapper<ArticleTag> queryWrapper2=new LambdaQueryWrapper<>();
                queryWrapper2.eq(ArticleTag::getArticleId,articleTag.getArticleId());
                articleTagMapper.delete(queryWrapper2);
            }
            articleTagMapper.insert(articleTag);
        }

        article.setId(articleId);
        article.setCreateDate(System.currentTimeMillis());
        article.setSummary(articleParam.getSummary());
        article.setTitle(articleParam.getTitle());
        long bodyId = System.currentTimeMillis()+random;
        article.setBodyId(bodyId);
        // ??????????????????
        ArticleBody articleBody = new ArticleBody();
        articleBody.setArticleId(articleId);
        articleBody.setId(bodyId);
        articleBody.setContent(articleParam.getBody().getContent());
        articleBody.setContentHtml(articleParam.getBody().getContentHtml());
        articleBodyMapper.insert(articleBody);
        // ???????????????
        article.setCategoryId(Long.parseLong(articleParam.getCategoryId()));
        if(isPublish){
            articleMapper.insert(article);
        }else{
            articleMapper.updateById(article);
        }
        // ????????????--es???????????????
        rabbitTemplate.convertAndSend(Mq.ART_EXCHANGE,Mq.ART_INSERT_KEY,articleId);

        Map<String,String> map=new HashMap<>();
        map.put("articleId",article.getId().toString());
        if(redisTemplate.opsForValue().get(String.valueOf(userId))!=null){
            redisTemplate.delete(String.valueOf(userId));
        }
        if(redisTemplate.opsForValue().get("indexArticle")!=null){
            redisTemplate.delete("indexArticle");
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

    @Override
    public int updateCateByCateId(Long cateId) {
        LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Article::getCategoryId,cateId)
                .set(Article::getCategoryId,10086);
        int update = articleMapper.update(null,updateWrapper);
        return update;
    }

    /**
     * ?????????token????????????id?????????????????????id????????????????????????????????????
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

    @Override
    public Result logicdel(Long articleId) {
        LambdaUpdateWrapper<Article> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper.eq(Article::getId,articleId)
                .set(Article::getIsDelete,1);
        int update = articleMapper.update(null, updateWrapper);
        if(update==0)
            return Result.fail(-998,"Failed to Ldelete article");
        rabbitTemplate.convertAndSend(Mq.ART_EXCHANGE,Mq.ART_DELETE_KEY,articleId);
        return Result.success("Successfully Ldelete article");
    }

    @Override
    public Result reArt(Long articleId) {
        LambdaUpdateWrapper<Article> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper.eq(Article::getId,articleId)
                .set(Article::getIsDelete,0);
        int update = articleMapper.update(null, updateWrapper);
        if(update==0)
            return Result.fail(-998,"Failed to recover article");
        rabbitTemplate.convertAndSend(Mq.ART_EXCHANGE,Mq.ART_INSERT_KEY,articleId);
        return Result.success("Successfully recover article");
    }
    // ??????????????????
    @Override
    @Transactional
    public Result delArt(Long articleId) {
        LambdaQueryWrapper<ArticleTag> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleTag::getArticleId,articleId);
        articleTagMapper.delete(queryWrapper);
        LambdaQueryWrapper<ArticleBody> queryWrapper2=new LambdaQueryWrapper<>();
        queryWrapper2.eq(ArticleBody::getArticleId,articleId);
        articleBodyMapper.delete(queryWrapper2);
        int i = articleMapper.deleteById(articleId);
        if(i==0)
            return Result.fail(-998,"Failed to delete Article");
        rabbitTemplate.convertAndSend(Mq.ART_EXCHANGE,Mq.ART_DELETE_KEY,articleId);
        return Result.success("Successfully delete Article");
    }

    @Override
    public Result allArticle(PageParams pageParams) {
        Page<Article> page = new Page<>(pageParams.getPage(),pageParams.getPageSize());
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getCreateDate)
                .orderByDesc(Article::getWeight);
        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);
        List<Article> articles=articlePage.getRecords();
        if(articles.isEmpty())
            return Result.fail(-998,"article not found");
        return Result.success(articles);
    }

    @Override
    public Result updateArticle(BackArticleParams backArticleParams) {
        LambdaUpdateWrapper<Article> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper.eq(Article::getId,backArticleParams.getId())
                .set(Article::getTitle,backArticleParams.getTitle())
                .set(Article::getSummary,backArticleParams.getSummary())
                .set(Article::getCreateDate,backArticleParams.getCreateDate())
                .set(Article::getCategoryId,backArticleParams.getCategoryId())
                .set(Article::getWeight,backArticleParams.getWeight());
        int update = articleMapper.update(null, updateWrapper);
        if(update==0){
            return Result.fail(-998,"Failed to update article");
        }
        rabbitTemplate.convertAndSend(Mq.ART_EXCHANGE,Mq.ART_INSERT_KEY,backArticleParams.getId());
        return Result.success("Successfully update article");
    }

    @Override
    public List<ArticleEso> findAllToEso() {
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.ne(Article::getIsDelete,true);
        List<Article> articles = articleMapper.selectList(queryWrapper);
//        List<Article> articles = articleMapper.selectList(null);
        List<ArticleEso> aEs = new ArrayList<>();

        for(Article article:articles){
            ArticleEso articleEso = new ArticleEso();
            articleEso.setId(String.valueOf(article.getId()));
            articleEso.setSummary(article.getSummary());
            articleEso.setTitle(article.getTitle());
            articleEso.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
            UserVo userVo = userService.findUserVoById(article.getAuthorId());
            articleEso.setUserVo(JSON.toJSONString(userVo));
            CategoryVo categoryVo=categoryService.findCategoryById(article.getCategoryId());
            articleEso.setCategoryVo(JSON.toJSONString(categoryVo));
            List<TagVo> tags = tagsService.findTagsByArticleId(article.getId());
            articleEso.setTagVo(JSON.toJSONString(tags));
            ArticleBody articleBody = articleBodyMapper.selectById(article.getBodyId());
            if(articleBody.getContent()!=null){
                articleEso.setContent(articleBody.getContent());
            }
            aEs.add(articleEso);
        }
        return aEs;
    }

    @Override
    public ArticleEso findToEso(Long articleId) {
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.ne(Article::getIsDelete,true)
                .eq(Article::getId,articleId);
        Article article = articleMapper.selectOne(queryWrapper);
//        List<Article> articles = articleMapper.selectList(null);
        ArticleEso articleEso = new ArticleEso();
        if(article!=null){
            articleEso.setId(String.valueOf(article.getId()));
            articleEso.setSummary(article.getSummary());
            articleEso.setTitle(article.getTitle());
            articleEso.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
            UserVo userVo = userService.findUserVoById(article.getAuthorId());
            articleEso.setUserVo(JSON.toJSONString(userVo));
            CategoryVo categoryVo=categoryService.findCategoryById(article.getCategoryId());
            articleEso.setCategoryVo(JSON.toJSONString(categoryVo));
            List<TagVo> tags = tagsService.findTagsByArticleId(article.getId());
            articleEso.setTagVo(JSON.toJSONString(tags));
            ArticleBody articleBody = articleBodyMapper.selectById(article.getBodyId());
            if(articleBody.getContent()!=null){
                articleEso.setContent(articleBody.getContent());
            }
        }
        return articleEso;
    }

    public ArticleVo copy(Article article, boolean isAuthor, boolean isTags){
        ArticleVo articleVo = new ArticleVo();
        BeanUtils.copyProperties(article, articleVo);
        articleVo.setId(String.valueOf(article.getId()));
        // ??????????????????????????? ?????????????????????
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
    * ????????????Vo??????
    */
    public ArticleVo copy(Article article){
        ArticleVo articleVo = new ArticleVo();
        BeanUtils.copyProperties(article, articleVo);
        articleVo.setId(String.valueOf(article.getId()));
        // ??????????????????????????????????????????
        Long authorId=article.getAuthorId();
        UserVo sysUser = userService.findUserVoById(authorId);
        articleVo.setAuthor(sysUser);
        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));

        Long articleId=article.getId();
        List<TagVo> tags = tagsService.findTagsByArticleId(articleId);
        articleVo.setTags(tags);
        //????????????
        Long bodyId = article.getBodyId();
        ArticleBody articleBody = articleBodyMapper.selectById(bodyId);
        ArticleBodyVo articleBodyVo=new ArticleBodyVo();
        articleBodyVo.setContent(articleBody.getContent());
        articleBodyVo.setContentHtml(articleBody.getContentHtml());
        articleVo.setBody(articleBodyVo);
        //????????????
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
