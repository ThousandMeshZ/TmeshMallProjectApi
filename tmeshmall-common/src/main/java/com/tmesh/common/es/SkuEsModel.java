package com.tmesh.common.es;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: TMesh
 **/

/**
 *  "properties": {
 *      "skuId": {
 * 	        "type": "long"
 *      },
 *      "spuId": {
 * 	        "type": "long"
 *      },
 *      "skuTitle": {
 * 	        "type": "text",
 * 	        "analyzer": "ik_smart"
 *      },
 *      "skuPrice": {
 * 	        "type": "keyword"
 *      },
 *      "skuImg": {
 * 	        "type": "keyword",
 * 	        "index": false,
 * 	        "doc_values": false
 *      },
 *      "saleCount": {
 * 	       "type": "long"
 *      },
 *      "hosStock": {
 * 	       "type": "boolean"
 *      },
 *      "hotScore": {
 * 	       "type": "long"
 *      },
 *      "brandId": {
 * 	       "type": "long"
 *      },
 *      "catalogId": {
 * 	       "type": "long"
 *      },
 *      "brandName": {
 * 	       "type": "keyword",
 * 	       "index": false,
 * 	       "doc_values": false
 *      },
 *      "brandImg": {
 * 	       "type": "keyword",
 * 	       "index": false,
 * 	       "doc_values": false
 *      },
 *      "catalogName": {
 * 	        "type": "keyword",
 * 	        "index": false,
 * 	        "doc_values": false
 *      },
 *      "attrs": {
 * 	        "type": "nested",
 * 	        "properties": {
 * 	            "attrId": {
 * 		            "type": "long"
* 	            },
 * 	            "attrName": {
 * 		            "type": "keyword",
 * 		            "index": false,
 * 		            "doc_values": false
 * 	            },
 * 	            "attrValue": {
 * 		            "type": "keyword"
 * 	            }
 * 	        }
 *      }
 * }
 * */
@Data
public class SkuEsModel {

    /**
     * skuId
     * */
    private Long skuId;

    /**
     * spuId
     * */
    private Long spuId;

    /**
     * 标题
     */
    private String skuTitle;

    /**
     * 商品 sku 价格
     */
    private BigDecimal skuPrice;

    /**
     * 商品图片
     */
    private String skuImg;

    /**
     * 销量
     */
    private Long saleCount;

    /**
     * 是否有货[True-无货，False-有货]
     * */
    private Boolean hasStock;

    /**
     * 热度评分
     * */
    private Long hotScore;

    /**
     * 品牌 id
     */
    private Long brandId;

    /**
     * 所属分类 id
     */
    private Long catalogId;

    /**
     * 品牌名
     */
    private String brandName;

    /**
     * 品牌图片
     */
    private String brandImg;

    /**
     * 所属分类名
     * */
    private String catalogName;

    /**
     * 属性列表
     */
    private List<Attrs> attrs;

    @Data
    public static class Attrs {

        /**
         * 属性 id
         */
        private Long attrId;
        
        /**
         * 属性名
         */
        private String attrName;

        /**
         * 属性值[用，分隔]
         */
        private String attrValue;

    }


}
