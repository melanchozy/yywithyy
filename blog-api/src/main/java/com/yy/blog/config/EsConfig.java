package com.yy.blog.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EsConfig {
    @Value("${elasticsearch.url}")
    private String url;
    @Value("${elasticsearch.port}")
    private Integer port;
    @Value("${elasticsearch.username}")
    private String name;
    @Value("${elasticsearch.password}")
    private String psw;
    @Bean
    public RestHighLevelClient client(){
        RestClientBuilder builder = RestClient.builder(
                new HttpHost(url,port,"http"));
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider .setCredentials(AuthScope.ANY,new UsernamePasswordCredentials(name,psw));
        builder.setHttpClientConfigCallback(f->f.setDefaultCredentialsProvider(credentialsProvider));
        RestHighLevelClient client = new RestHighLevelClient (builder);
        return client;
    }
}
