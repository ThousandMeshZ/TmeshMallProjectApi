package com.tmesh.tmeshmall.product.dao;

import com.tmesh.tmeshmall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品三级分类
 * 
 * @author TMesh
 * @email 1009191578@qq.com

 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
    List<CategoryEntity> getPeerByCatId(Long catId);
	
    List<Long> getAllChildrenCatIdList(Long catId);

    List<CategoryEntity> getCatalogListByParentCid(Long parentCid);
}
