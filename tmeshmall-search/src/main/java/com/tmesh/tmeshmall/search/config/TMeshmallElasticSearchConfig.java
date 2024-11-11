package com.tmesh.tmeshmall.search.config;

import lombok.Data;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: TMesh
 * @createTime: 2024-02-18 15:07
 **/
@Configuration
public class TMeshmallElasticSearchConfig {
    public static final RequestOptions COMMON_OPTIONS;

    @Value("${elasticsearch.host}")
    private String esHost;

    @Value("${elasticsearch.port-http}")
    private Integer esPortHttp;

    @Value("${elasticsearch.port-tcp}")
    private Integer esPortTcp;
    
    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        // builder.addHeader("Authorization", "Bearer " + TOKEN);
        // builder.setHttpAsyncResponseConsumerFactory(
        //         new HttpAsyncResponseConsumerFactory
        //                 .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }

    // @Bean
    // public RestHighLevelClient esRestClient(){
    //     RestHighLevelClient client = new RestHighLevelClient(
    //             RestClient.builder(new HttpHost(esHost, esPortTcp "http")));
    //     return  client;
    // }

    @Bean
    public RestHighLevelClient esRestClient() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(esHost, esPortHttp, "http")
                )
        );
        return client;
    }
}
