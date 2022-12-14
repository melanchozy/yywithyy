package com.yy.blog;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yy.blog.BlogApp;
import com.yy.blog.dao.eso.ArticleEso;
import com.yy.blog.dao.eso.TagEso;
import com.yy.blog.service.ArticleService;
import com.yy.blog.service.TagsService;
import com.yy.blog.utils.EsUtils;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;

import static com.yy.blog.dao.eso.ArticleIndex.MAPPING_TEMPLATE;
import static com.yy.blog.dao.eso.ArticleIndex.TAG_MAPPING_TEMPLATE;


@SpringBootTest(classes = {BlogApp.class})
@RunWith(SpringRunner.class)
public class TestIndex {
    private ObjectMapper mapper = new ObjectMapper();



    private String index="article";

    private RestHighLevelClient client= EsUtils.getclient();
//    @Autowired
//    private RestHighLevelClient client;



    /**
     * ????????????????????????
     * @throws IOException
     */
    @Test
    public void existIndex() throws IOException {
        IndicesClient indices = client.indices();
        GetIndexRequest getIndexRequest=new GetIndexRequest(index);
        boolean exists = indices.exists(getIndexRequest, RequestOptions.DEFAULT);
        System.out.println(exists);

    }
    /**
     * ????????????
     */
    @Test
    public void deleteIndex() throws IOException {
        IndicesClient indices = client.indices();
        DeleteIndexRequest deleteRequest=new DeleteIndexRequest(index);
        AcknowledgedResponse delete = indices.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
    }

    /**
     * ????????????
     * @throws IOException
     */
    @Test
    public void addDoc2() throws IOException {
        ArticleEso articleEso=new ArticleEso();
        articleEso.setId("2");
        String data = JSON.toJSONString(articleEso);
        IndexRequest request=new IndexRequest(index).id(articleEso.getId()).source(data,XContentType.JSON);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println(response.getId());
    }

    /**
     * ???????????????????????????????????????id??????????????????id??????????????????
     */

    @Test
    public void UpdateDoc() throws IOException {
        ArticleEso articleEso = new ArticleEso();
        articleEso.setId("2");

        String data = JSON.toJSONString(articleEso);
        IndexRequest request = new IndexRequest(index).id(articleEso.getId()).source(data, XContentType.JSON);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println(response.getId());
    }
    /**
     * ??????id????????????
     */
    @Test
    public void getDoc() throws IOException {

        //??????????????????????????????
        GetRequest indexRequest=new GetRequest("person","2");

        GetResponse response = client.get(indexRequest, RequestOptions.DEFAULT);
        System.out.println(response.getSourceAsString());
    }

    /**
     * ??????id????????????
     */
    @Test
    public void delDoc() throws IOException {

        //?????????????????????????????????
        DeleteRequest deleteRequest=new DeleteRequest("person","1");

        DeleteResponse response = client.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(response.getId());
    }

    /**
     * ?????????????????????????????????
     */
    @Test
    public void test09() throws IOException {
        //????????????????????????
        DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest("person");
        //??????????????????????????????
        deleteByQueryRequest.setQuery(new TermQueryBuilder("age", 11));
        deleteByQueryRequest.setQuery(new TermQueryBuilder("name", "??????"));

        // ????????????
        deleteByQueryRequest.setSlices(2);
        // ????????????
        deleteByQueryRequest.setTimeout(TimeValue.timeValueMinutes(2));
        BulkByScrollResponse response = client.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
        System.out.println(response.getStatus().getDeleted());
    }
    /**
     * ??????????????????
     */
    @Test
    public void testCreateIndex() throws IOException {
        //1.??????request??????
        CreateIndexRequest request=new CreateIndexRequest(index);
        //2.????????????????????????DSL??????
        request.source(MAPPING_TEMPLATE, XContentType.JSON);
        //3.????????????
        client.indices().create(request, RequestOptions.DEFAULT);
    }
    /**
     * ????????????articles
     */
    @Autowired
    private ArticleService articleService;
    @Test
    public void testBulkRequest() throws IOException {
        //?????????
        List<ArticleEso> allToEso = articleService.findAllToEso();
        BulkRequest request = new BulkRequest();
        for(ArticleEso articleEso:allToEso){
            request.add(new IndexRequest(index).id(articleEso.getId()).source(JSON.toJSONString(articleEso),XContentType.JSON));
        }
        client.bulk(request,RequestOptions.DEFAULT);
    }
    @Test
    public void deleteTagById() {
        try {
            DeleteRequest request = new DeleteRequest("tag", "1659789195436");
            client.delete(request, RequestOptions.DEFAULT);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    @Test
    public void insertTagById() {
        try {
            //?????????
            TagEso toEso = tagsService.findTgaByIdToEso(1659789195436l);
            IndexRequest request = new IndexRequest("tag").id(String.valueOf(1659789195436l));
            request.source(JSON.toJSONString(toEso), XContentType.JSON);
            client.index(request, RequestOptions.DEFAULT);
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    @Test
    public void testCreateIndex1() throws IOException {
        //1.??????request??????
        CreateIndexRequest request=new CreateIndexRequest("tag");
        //2.????????????????????????DSL??????
        request.source(TAG_MAPPING_TEMPLATE, XContentType.JSON);
        //3.????????????
        client.indices().create(request, RequestOptions.DEFAULT);
    }
    /**
     * ????????????tags
     */
    @Autowired
    private TagsService tagsService;
    @Test
    public void testBulkRequest1() throws IOException {
        //?????????
        List<TagEso> allToEso = tagsService.findAllToEso();
        BulkRequest request = new BulkRequest();
        for(TagEso tagEso:allToEso){
            request.add(new IndexRequest("tag").id(tagEso.getId()).source(JSON.toJSONString(tagEso),XContentType.JSON));
        }
        client.bulk(request,RequestOptions.DEFAULT);
    }
}
