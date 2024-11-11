package com.tmesh.tmeshmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tmesh.common.utils.PageUtils;
import com.tmesh.tmeshmall.product.entity.SkuImagesEntity;

import java.util.List;
import java.util.Map;

/**
 * sku图片
 *
 * @author TMesh
 * @email 1009191578@qq.com

 */
public interface SkuImagesService extends IService<SkuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuImagesEntity> getImagesBySkuId(Long skuId);
}

