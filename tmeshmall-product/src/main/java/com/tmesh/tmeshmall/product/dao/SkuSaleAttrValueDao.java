package com.tmesh.tmeshmall.product.dao;

import com.tmesh.common.vo.product.SkuItemSaleAttrVO;
import com.tmesh.tmeshmall.product.entity.SkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 * 
 * @author TMesh
 * @email 1009191578@qq.com

 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {
    List<SkuItemSaleAttrVO> getSaleAttrBySpuId(@Param("spuId") Long spuId);

    List<String> getSkuSaleAttrValuesAsStringList(@Param("skuId") Long skuId);

    List<SkuSaleAttrValueEntity> getSkuSaleAttrValuesNameAndValueBySkuId(@Param("skuId") Long skuId);

    List<SkuSaleAttrValueEntity> getSkuSaleAttrValuesBySkuId(@Param("skuId") Long skuId);
}
