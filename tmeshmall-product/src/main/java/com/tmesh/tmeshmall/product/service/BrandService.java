package com.tmesh.tmeshmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tmesh.common.utils.PageUtils;
import com.tmesh.tmeshmall.product.entity.BrandEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌
 *
 * @author TMesh
 * @email 1009191578@qq.com
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

    BrandEntity selectById(Long id, String... selectList);

    List<BrandEntity> getBrandIByIds(List<Long> brandIds);

    void updateDetail(BrandEntity brand);
}

