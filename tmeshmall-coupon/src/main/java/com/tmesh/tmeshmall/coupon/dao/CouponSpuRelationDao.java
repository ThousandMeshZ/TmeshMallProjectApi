package com.tmesh.tmeshmall.coupon.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tmesh.tmeshmall.coupon.entity.CouponSpuRelationEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券与产品关联
 * 
 * @author TMesh
 * @email 1009191578@qq.com
 */
@Mapper
public interface CouponSpuRelationDao extends BaseMapper<CouponSpuRelationEntity> {
	
}
