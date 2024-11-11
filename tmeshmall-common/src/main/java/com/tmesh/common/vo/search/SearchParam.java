package com.tmesh.common.vo.search;

import lombok.Data;

import java.util.List;

/**
 * @Description: 封装页面所有可能传递过来的查询条件
 * @Created: with IntelliJ IDEA.
 * @author: TMesh
 * @createTime: 2024-02-13 14:17
 **/

/**
 * 封装页面所有可能传递过来的查询条件
 * 三种点击搜索的方式
 * 1、点击搜索：keyword   【skuTitle】
 * 2、点击分类：传catalog3Id【catalogld】
 * 3、选择筛选条件
 *      1、全文检索: keyword【skuTitle】
 *      2、排序: saleCount_asc【销量】、hotScore_asc【综合排序：热度评分】、skuPrice_asc【价格】
 *      3、过滤: hasStock、skuPrice区间、brandld、catalog3ld、attrs
 *      4、聚合: attrs
 *          attrs=2_5寸 传参格式，所以直接for循环split("_")就可以得到attrId与attrValue
 *          attrs=1_白色:蓝色       然后值split(":")得到各项值attrValue
 *
 * 封装页面所有可能传递过来的查询条件
 * 
 * catalog3Id=255&keyword=小米&sort=saleCount_asc&hasStock=0/1&brandId=1&brandId=2
 * 好多的过滤条件
 * hasStock(是否有货)、skuPrice区间、brandId、catalog3Id、attrs
 */
@Data
public class SearchParam {
    /**
     * 页面传递过来的全文匹配关键字
     */
    private String keyword;

    /**
     * 品牌 id,可以多选  brandId=1&brandId=2
     */
    private List<Long> brandId;

    /**
     * 三级分类 id
     */
    private Long catalog3Id;

    /**
     * 排序条件：
     * sort=salecount_desc/asc
     * sort=skuPrice_desc/asc
     * sort=hotscore_desc/asc
     */
    private String sort;
    
    /**
     * 是否显示有货，默认显示所有，null == 1会NullPoint异常  0/1  hasStock=0/1
     */
    private Integer hasStock;

    /**
     * 价格区间查询   skuPrice=1_500/_500/500_
     */
    private String skuPrice;

    /**
     * 按照属性进行筛选 三级分类id+属性值 attrs=2_5寸:6寸
     */
    private List<String> attrs;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 原生的所有查询条件（来自url的请求参数），用于构建面包屑
     */
    private String _queryString;


}
