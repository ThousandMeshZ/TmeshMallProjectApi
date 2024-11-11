package com.tmesh.common.vo.ware;

import com.tmesh.common.vo.order.OrderItemVO;
import lombok.Data;

import java.util.List;

/**
 * @Description: 锁定库存的vo
 * @Created: with IntelliJ IDEA.
 * @author: TMesh
 **/

@Data
public class WareSkuLockVO {

    /**
     * 订单号
     */
    private String orderSn;

    /** 
     * 需要锁住的所有库存信息 
     */
    private List<OrderItemVO> locks;



}
