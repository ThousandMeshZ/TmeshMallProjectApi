package com.tmesh.tmeshmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tmesh.common.utils.PageUtils;
import com.tmesh.common.vo.product.SkuItemSaleAttrVO;
import com.tmesh.tmeshmall.product.entity.SkuSaleAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author TMesh
 * @email 1009191578@qq.com

 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuItemSaleAttrVO> getSaleAttrBySpuId(Long spuId);

    List<String> getSkuSaleAttrValuesAsStringList(Long skuId);

    List<SkuSaleAttrValueEntity> getSkuSaleAttrValuesBySkuId(Long skuId);
    
    List<SkuSaleAttrValueEntity> getSkuSaleAttrValuesNameAndValueBySkuId(Long skuId);
    
}

