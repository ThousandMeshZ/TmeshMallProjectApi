package com.tmesh.common.vo.product;

import lombok.Data;

@Data
public class AttrValueWithSkuIdVO {
    /**
     * 属性属性值
     * */
    private String attrValue;
    private String skuIds;


    /**
     * 选择对应属性商品id
     */
    private Long skuId;
}

