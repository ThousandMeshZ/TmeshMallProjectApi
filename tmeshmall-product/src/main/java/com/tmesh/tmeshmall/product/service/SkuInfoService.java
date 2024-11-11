package com.tmesh.tmeshmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tmesh.common.utils.PageUtils;
import com.tmesh.common.vo.product.SkuItemVO;
import com.tmesh.tmeshmall.product.entity.SkuInfoEntity;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * sku信息
 *
 * @author TMesh
 * @email 1009191578@qq.com

 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 新增sku信息
     */
    void saveSkuInfo(SkuInfoEntity skuInfoEntity);

    /**
     * sku检索
     */
    PageUtils queryPageCondition(Map<String, Object> params);

    /**
     * 查询spuId对应的所有sku信息
     */
    List<SkuInfoEntity> getSkusBySpuId(Long spuId);

    /**
     * 查询所有spuId对应的所有sku信息
     */
    List<SkuInfoEntity> getSkusBySpuIds(List<Long> spuIds);

    /**
     * 查询skuId商品信息，封装VO返回
     */
    SkuItemVO item(Long skuId) throws ExecutionException, InterruptedException;
    
    /**
     * 根据集合查询
     */
    List<SkuInfoEntity> getByIds(Collection<Long> skuIds);

    boolean uploadPic(SkuInfoEntity skuInfo);
}

