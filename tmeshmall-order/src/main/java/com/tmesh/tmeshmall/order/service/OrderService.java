package com.tmesh.tmeshmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tmesh.common.to.mq.SeckillOrderTo;
import com.tmesh.common.utils.PageUtils;
import com.tmesh.common.vo.order.OrderConfirmVO;
import com.tmesh.common.vo.order.OrderSubmitVO;
import com.tmesh.common.vo.order.PayVO;
import com.tmesh.common.vo.order.SubmitOrderResponseVO;
import com.tmesh.common.vo.order.alipay.AliPayAsyncVO;
import com.tmesh.tmeshmall.order.entity.OrderEntity;
import com.tmesh.tmeshmall.order.entity.PaymentInfoEntity;

import java.text.ParseException;
import java.util.Map;

/**
 * 订单
 *
 * @author TMesh
 * @email 1009191578@qq.com
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 分页查询订单列表、订单详情
     * @param params
     * @return
     */
    PageUtils queryPageWithItem(Map<String, Object> params);
    

    PageUtils queryPageWithItemForTable(Map<String, Object> params);

    /**
     * 按照订单号获取订单信息
     */
    OrderEntity getOrderByOrderSn(String orderSn);

    /**
     * 获取结算页（confirm.html）VO数据
     */
    OrderConfirmVO OrderConfirmVO() throws Exception;

    /**
     * 创建订单
     */
    SubmitOrderResponseVO submitOrder(OrderSubmitVO vo) throws Exception;

    /**
     * 关闭订单
     */
    void closeOrder(OrderEntity order);

    /**
     * 获取订单支付的详细信息
     */
    PayVO getOrderPay(String orderSn);

    /**
     *支付宝异步通知处理订单数据
     * @param asyncVo
     * @return
     */
    String handlePayResult(AliPayAsyncVO asyncVo);

    /**
     * 处理支付回调
     * @param targetOrderStatus 目标状态
     */
    void handlePayResult(Integer targetOrderStatus, Integer payCode, PaymentInfoEntity paymentInfo);

    /**
     * 微信异步通知处理
     * @param notifyData
     */
    String asyncNotify(String notifyData);

    /**
     * 创建秒杀订单
     * @param order 秒杀订单信息
     */
    void createSeckillOrder(SeckillOrderTo order);
}

