package com.tmesh.common.vo.order;

import com.tmesh.common.entity.order.OrderEntity;
import lombok.Data;

/**
 * 提交订单返回结果
 * @author: TMesh
 */
@Data
public class SubmitOrderResponseVO {
    private OrderEntity order;

    /** 错误状态码 **/
    private Integer code;
}
