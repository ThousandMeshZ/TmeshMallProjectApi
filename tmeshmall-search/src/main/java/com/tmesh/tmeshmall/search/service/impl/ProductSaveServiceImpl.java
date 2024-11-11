package com.tmesh.tmeshmall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.tmesh.common.es.SkuEsModel;
import com.tmesh.common.utils.EmptyUtils;
import com.tmesh.common.vo.search.SearchResult;
import com.tmesh.tmeshmall.search.config.TMeshmallElasticSearchConfig;
import com.tmesh.tmeshmall.search.constant.EsConstant;
import com.tmesh.tmeshmall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: TMesh
 * @createTime: 2024-02-06 16:54
 **/

@Slf4j
@Service("productSaveService")
public class ProductSaveServiceImpl implements ProductSaveService {

    @Autowired
    private RestHighLevelClient esRestClient;

    private List<SkuEsModel> searchEsBySkuIds(List<Long> skuIds, String[] include, String[] exclude) throws IOException {

        List<SkuEsModel> esModels = new ArrayList<>();

        if (EmptyUtils.isEmpty(skuIds))
            return esModels;

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        /**
         * 模糊匹配，过滤（按照属性，分类，品牌，价格区间，库存）
         */
        //1. 构建 bool-query
        BoolQueryBuilder boolQueryBuilder=new BoolQueryBuilder();
        // in的效果 传单个参数就是完全匹配
        boolQueryBuilder.must(QueryBuilders.termsQuery("skuId", skuIds));

        searchSourceBuilder.size(skuIds.size());
        searchSourceBuilder.query(boolQueryBuilder);

        if (include == null)
            include = new String[]{};
        if (exclude == null)
            exclude = new String[]{};
        searchSourceBuilder.fetchSource(include, exclude);

        log.debug("构建的DSL语句 {}",searchSourceBuilder.toString());
        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, searchSourceBuilder);
        //2、执行检索请求
        SearchResponse response = esRestClient.search(searchRequest, TMeshmallElasticSearchConfig.COMMON_OPTIONS);

        //3、分析响应数据，封装成我们需要的格式
        //1、返回的所有查询到的商品
        SearchHits hits = response.getHits();
        //遍历所有商品信息
        if (hits.getHits() != null && hits.getHits().length > 0) {
            for (SearchHit hit : hits.getHits()) {
                String sourceAsString = hit.getSourceAsString();
                SkuEsModel esModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
                esModels.add(esModel);
            }
        }
        return esModels;
    }
    
    @Override
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {
        //1.在 ES 中建立索引，建立号映射关系（doc/json/product-mapping.json）

        //2. 在 ES 中保存这些数据
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel skuEsModel : skuEsModels) {
            //构造保存请求
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(skuEsModel.getSkuId().toString());
            String jsonString = JSON.toJSONString(skuEsModel);
            indexRequest.source(jsonString, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }

        BulkResponse bulk = esRestClient.bulk(bulkRequest, TMeshmallElasticSearchConfig.COMMON_OPTIONS);

        //TODO 如果批量错误
        boolean hasFailures = bulk.hasFailures();

        List<String> collect = Arrays.stream(bulk.getItems()).map(BulkItemResponse::getId).toList();

        log.info("商品上架完成：{}",collect);

        return hasFailures;
    }

//    @Override
    public boolean productStatusUp1(List<SkuEsModel> skuEsModels) throws IOException {
        //1.在 ES 中建立索引，建立号映射关系（doc/json/product-mapping.json）

        //2. 在 ES 中保存这些数据
        List<SkuEsModel> esSkuModels = searchEsBySkuIds(skuEsModels.stream().map(SkuEsModel::getSkuId)
                .toList(), null, null);
        List<SkuEsModel> noUpSkuModels = skuEsModels.stream()
                .filter(skuEsModel ->
                                esSkuModels.stream().noneMatch(model -> model.getSkuId().equals(skuEsModel.getSkuId()))
                        ).toList();

        boolean hasFailures = true;
        List<String> collect = new ArrayList<>();

        if (EmptyUtils.isNotEmpty(noUpSkuModels)) {
            BulkRequest bulkRequest = new BulkRequest();
            for (SkuEsModel skuEsModel : noUpSkuModels) {
                //构造保存请求
                IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
                indexRequest.id(skuEsModel.getSkuId().toString());
                String jsonString = JSON.toJSONString(skuEsModel);
                indexRequest.source(jsonString, XContentType.JSON);
                bulkRequest.add(indexRequest);
            }

            BulkResponse bulk = esRestClient.bulk(bulkRequest, TMeshmallElasticSearchConfig.COMMON_OPTIONS);

            //TODO 如果批量错误
            hasFailures = bulk.hasFailures();

            collect = Arrays.stream(bulk.getItems()).map(BulkItemResponse::getId).toList();
        }

        log.info("商品上架完成：{}",collect);
        return hasFailures;
    }

    @Override
    public List<Long> checkEsUp(List<Long> skuIds) throws IOException {
        List<SkuEsModel> esSkuModels = searchEsBySkuIds(skuIds, new String[]{"skuId"}, null);
        List<Long> upSkuIds = skuIds.stream().filter(skuId ->
                esSkuModels.stream().anyMatch(model -> model.getSkuId().equals(skuId))
        ).toList();
        return upSkuIds;
    }
}
