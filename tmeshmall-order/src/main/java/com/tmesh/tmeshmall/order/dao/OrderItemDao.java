package com.tmesh.tmeshmall.order.dao;

import com.tmesh.tmeshmall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 * 
 * @author TMesh
 * @email 1009191578@qq.com
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {
	
}
