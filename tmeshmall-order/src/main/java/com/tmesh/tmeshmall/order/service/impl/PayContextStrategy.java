package com.tmesh.tmeshmall.order.service.impl;

import com.alipay.api.AlipayApiException;
import com.tmesh.common.constant.order.PaymentConstant;
import com.tmesh.common.utils.SpringUtils;
import com.tmesh.common.vo.order.PayAsyncVO;
import com.tmesh.common.vo.order.PayVO;
import com.tmesh.tmeshmall.order.service.PayStrategy;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 策略类
 */
@Component
public class PayContextStrategy {

    /**
     * 创建支付
     *
     * @param payType        策略类型
     * @param order          订单数据
     * @return
     */
    public String pay(PaymentConstant.PayType payType, PayVO order) throws Exception {
        // 获取实际策略对象
        PayStrategy payStrategy = SpringUtils.getBean(payType.getStrategyBeanId(), PayStrategy.class);
        // 执行具体策略
        return payStrategy.pay(order);
    }

    /**
     * 处理回调
     * @param payType   策略类型
     * @param request   请求
     * @param asyncVo   回参VO
     * @return
     */
    public Boolean notify(PaymentConstant.PayType payType, HttpServletRequest request, PayAsyncVO asyncVo) throws AlipayApiException {
        // 获取实际策略对象
        PayStrategy payStrategy = SpringUtils.getBean(payType.getStrategyBeanId(), PayStrategy.class);
        // 执行具体策略
        return payStrategy.notify(request, asyncVo);
    }
}