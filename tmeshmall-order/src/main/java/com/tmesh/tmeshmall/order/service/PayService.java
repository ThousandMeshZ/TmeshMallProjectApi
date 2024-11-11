package com.tmesh.tmeshmall.order.service;

import com.alipay.api.AlipayApiException;
import com.tmesh.common.vo.order.PayAsyncVO;
import com.tmesh.common.vo.order.PayVO;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @Author: TMesh
 * @Date: 2024/1/5 17:17
 */
public interface PayService {

    /**
     * 创建支付
     * @param order 订单详情
     */
    public String pay(PayVO order) throws AlipayApiException;

    /**
     * 验签
     * @param request 回参
     */
    Boolean verify(HttpServletRequest request) throws AlipayApiException;

    /**
     * 处理支付异步回调
     * @param asyncVo
     */
    void handlePayResult(PayAsyncVO asyncVo);
}