package com.yy.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.yy.blog.dao.eso.ArticleEso;
import com.yy.blog.dao.eso.TagEso;
import com.yy.blog.service.ArticleService;
import com.yy.blog.service.SearchService;
import com.yy.blog.service.TagsService;
import com.yy.blog.vo.SearchVo;
import com.yy.blog.vo.params.SearchParams;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private TagsService tagsService;

    @Override
    public SearchVo search(SearchParams searchParams) {
        try {
            SearchRequest request = new SearchRequest("article");
            String key = searchParams.getKey();
            if(StringUtils.isBlank(key)){
                return null;
            }else{
                request.source().query(QueryBuilders.multiMatchQuery(key,"summary","content","title"));
            }
            int page = searchParams.getPage();
            int size = searchParams.getSize();
            if(page<1||size<1)
                page=1;
            size=10;
            request.source().from((page-1)*size).size(size);
            SearchResponse response=client.search(request, RequestOptions.DEFAULT);
            SearchVo searchVo = handleEsResponse(response);
            return searchVo;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteById(Long id) {
        try {
            DeleteRequest request = new DeleteRequest("article", id.toString());
            client.delete(request, RequestOptions.DEFAULT);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void insertById(Long id) {
        try {
            ArticleEso toEso = articleService.findToEso(id);
            IndexRequest request = new IndexRequest("article").id(String.valueOf(id));
            request.source(JSON.toJSONString(toEso), XContentType.JSON);
            client.index(request, RequestOptions.DEFAULT);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getTags(String key) {
        try {
            SearchRequest request = new SearchRequest("tag");
            request.source().suggest(new SuggestBuilder().addSuggestion(
                    "suggestions",
                    SuggestBuilders.completionSuggestion("suggestion")
                            .prefix(key)
                            .skipDuplicates(true)
                            .size(5)
            ));
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            Suggest suggest = response.getSuggest();
            CompletionSuggestion suggestions = suggest.getSuggestion("suggestions");
            List<CompletionSuggestion.Entry.Option> options = suggestions.getOptions();
            List<String> list = new ArrayList<>(options.size());
            for (Suggest.Suggestion.Entry.Option option : options) {
                String text = option.getText().toString();
                list.add(text);
            }
            return list;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 更新整个标签索引库
     */
    @Override
    public void updateTagIndex() {
        try {
            //先查询
            List<TagEso> allToEso = tagsService.findAllToEso();
            BulkRequest request = new BulkRequest();
            for (TagEso tagEso : allToEso) {
                request.add(new IndexRequest("tag").id(tagEso.getId()).source(JSON.toJSONString(tagEso), XContentType.JSON));
            }
            client.bulk(request, RequestOptions.DEFAULT);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    /**
     * 更新整个文章索引库
     */
    @Override
    public void updateArticleIndex() {
        try{
            //先查询
            List<ArticleEso> allToEso = articleService.findAllToEso();
            BulkRequest request = new BulkRequest();
            for(ArticleEso articleEso:allToEso){
                request.add(new IndexRequest("article").id(articleEso.getId()).source(JSON.toJSONString(articleEso),XContentType.JSON));
            }
            client.bulk(request,RequestOptions.DEFAULT);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void insertTagById(Long tagId) {
        try {
            //先查询
            TagEso toEso = tagsService.findTgaByIdToEso(tagId);
            IndexRequest request = new IndexRequest("tag").id(String.valueOf(tagId));
            request.source(JSON.toJSONString(toEso), XContentType.JSON);
            client.index(request, RequestOptions.DEFAULT);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void deleteTagById(Long id) {
        try {
            DeleteRequest request = new DeleteRequest("tag", id.toString());
            client.delete(request, RequestOptions.DEFAULT);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    // 解析es返回结果
    public SearchVo handleEsResponse(SearchResponse response){
        SearchHits searchHits=response.getHits();
        long total =searchHits.getTotalHits().value;
        System.out.println("共搜索到"+total+"条数据");

        SearchHit[] hits=searchHits.getHits();
        List<ArticleEso> articleEsoList =new ArrayList<>();
        for(SearchHit hit:hits){
            String json =hit.getSourceAsString();
            ArticleEso articleEso= JSON.parseObject(json,ArticleEso.class);
            //获取高亮结果
//            Map<String, HighlightField> highlightFields=hit.getHighlightFields();
//            if(!CollectionUtils.isEmpty(highlightFields)){
//                HighlightField highlightField=highlightFields.get("name");
//                if(highlightField!=null){
//                    String name=highlightField.getFragments()[0].string();
//                    articleEso.setTitle(name);
//                }
//            }
            articleEsoList.add(articleEso);
        }
        return new SearchVo(total,articleEsoList);
    }
}
