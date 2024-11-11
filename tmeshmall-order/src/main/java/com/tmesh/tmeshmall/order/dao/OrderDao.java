package com.tmesh.tmeshmall.order.dao;

import com.tmesh.tmeshmall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单
 * 
 * @author TMesh
 * @email 1009191578@qq.com
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

    /**
     * 修改订单状态
     * @param orderSn   订单号
     * @param code      订单状态
     * @param payType   支付类型
     */
    void updateOrderStatus(@Param("orderSn") String orderSn, 
                           @Param("code") Integer code, 
                           @Param("payType") Integer payType);
}