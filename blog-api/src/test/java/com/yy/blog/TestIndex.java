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
     * 查询索引是否存在
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
     * 删除索引
     */
    @Test
    public void deleteIndex() throws IOException {
        IndicesClient indices = client.indices();
        DeleteIndexRequest deleteRequest=new DeleteIndexRequest(index);
        AcknowledgedResponse delete = indices.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
    }

    /**
     * 添加文档
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
     * 修改文档：添加文档时，如果id存在则修改，id不存在则添加
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
     * 根据id查询文档
     */
    @Test
    public void getDoc() throws IOException {

        //设置查询的索引、文档
        GetRequest indexRequest=new GetRequest("person","2");

        GetResponse response = client.get(indexRequest, RequestOptions.DEFAULT);
        System.out.println(response.getSourceAsString());
    }

    /**
     * 根据id删除文档
     */
    @Test
    public void delDoc() throws IOException {

        //设置要删除的索引、文档
        DeleteRequest deleteRequest=new DeleteRequest("person","1");

        DeleteResponse response = client.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(response.getId());
    }

    /**
     * 删除满足给定条件的文档
     */
    @Test
    public void test09() throws IOException {
        //设置要删除的索引
        DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest("person");
        //设置条件，可设置多个
        deleteByQueryRequest.setQuery(new TermQueryBuilder("age", 11));
        deleteByQueryRequest.setQuery(new TermQueryBuilder("name", "张三"));

        // 设置并行
        deleteByQueryRequest.setSlices(2);
        // 设置超时
        deleteByQueryRequest.setTimeout(TimeValue.timeValueMinutes(2));
        BulkByScrollResponse response = client.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
        System.out.println(response.getStatus().getDeleted());
    }
    /**
     * 测试新建索引
     */
    @Test
    public void testCreateIndex() throws IOException {
        //1.创建request对象
        CreateIndexRequest request=new CreateIndexRequest(index);
        //2.准备请求的参数：DSL语句
        request.source(MAPPING_TEMPLATE, XContentType.JSON);
        //3.发送请求
        client.indices().create(request, RequestOptions.DEFAULT);
    }
    /**
     * 批量添加articles
     */
    @Autowired
    private ArticleService articleService;
    @Test
    public void testBulkRequest() throws IOException {
        //先查询
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
            //先查询
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
        //1.创建request对象
        CreateIndexRequest request=new CreateIndexRequest("tag");
        //2.准备请求的参数：DSL语句
        request.source(TAG_MAPPING_TEMPLATE, XContentType.JSON);
        //3.发送请求
        client.indices().create(request, RequestOptions.DEFAULT);
    }
    /**
     * 批量添加tags
     */
    @Autowired
    private TagsService tagsService;
    @Test
    public void testBulkRequest1() throws IOException {
        //先查询
        List<TagEso> allToEso = tagsService.findAllToEso();
        BulkRequest request = new BulkRequest();
        for(TagEso tagEso:allToEso){
            request.add(new IndexRequest("tag").id(tagEso.getId()).source(JSON.toJSONString(tagEso),XContentType.JSON));
        }
        client.bulk(request,RequestOptions.DEFAULT);
    }
}
