package com.tmesh.tmeshmall.product.dao;

import com.tmesh.tmeshmall.product.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 * 
 * @author TMesh
 * @email 1009191578@qq.com

 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {
    void updaSpuStatus(@Param("spuId") Long spuId, @Param("code") int code);
}
