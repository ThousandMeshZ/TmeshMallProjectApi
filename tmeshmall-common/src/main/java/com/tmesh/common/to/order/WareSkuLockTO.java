package com.tmesh.common.to.order;

import com.tmesh.common.vo.order.OrderItemVO;
import lombok.Data;

import java.util.List;

/**
 * 锁定库存传输对象
 * 创建订单时封装所有订单项进行锁定
 * @author: TMesh
 */
@Data
public class WareSkuLockTO {
    private String orderSn;
    /** 需要锁住的所有库存信息 **/
    private List<OrderItemVO> locks;
}
