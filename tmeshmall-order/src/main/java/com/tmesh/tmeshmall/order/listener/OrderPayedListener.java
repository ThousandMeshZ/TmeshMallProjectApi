package com.tmesh.tmeshmall.order.listener;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.tmesh.common.constant.order.PaymentConstant;
import com.tmesh.common.vo.order.PayAsyncVO;
import com.tmesh.common.vo.order.alipay.AliPayAsyncVO;
import com.tmesh.tmeshmall.order.config.AliPayConfig;
import com.tmesh.tmeshmall.order.service.OrderService;
import com.tmesh.tmeshmall.order.service.impl.PayContextStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 订单支付成功监听器
 *
 */
@RestController
public class OrderPayedListener {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private PayContextStrategy payContextStrategy;

    @Autowired
    private AliPayConfig aliPayConfig;
    
    /**
     * 支付宝支付异步通知
     * 只有支付成功会触发
     * @param request
     * @param asyncVo
     */
    @PostMapping(value = "/payed/ali/notify")
    public String handleAliNotify(HttpServletRequest request, AliPayAsyncVO asyncVo) throws AlipayApiException, UnsupportedEncodingException {
        asyncVo.setPayCode(PaymentConstant.PayType.ALI_PAY.getCode());// 封装付款类型
        Boolean result = payContextStrategy.notify(PaymentConstant.PayType.ALI_PAY, request, asyncVo);
        if (result) {
            return "success";// 返回success，支付宝将不再异步回调
        }
        return "error";
    }

    @PostMapping(value = "/payed/notify")
    public String handleAlipayed(AliPayAsyncVO asyncVo, HttpServletRequest request) throws AlipayApiException, UnsupportedEncodingException {
        // 只要收到支付宝的异步通知，返回 success 支付宝便不再通知
        // 获取支付宝POST过来反馈信息
        //TODO 需要验签
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        boolean signVerified = AlipaySignature.rsaCheckV1(params, aliPayConfig.getAlipay_public_key(),
                aliPayConfig.getCharset(), aliPayConfig.getSign_type()); //调用SDK验证签名

        if (signVerified) {
            System.out.println("签名验证成功...");
            //去修改订单状态
            String result = orderService.handlePayResult(asyncVo);
            return result;
        } else {
            System.out.println("签名验证失败...");
            return "error";
        }
    }

    //@PostMapping(value = "/pay/notify")
    //public String asyncNotify(@RequestBody String notifyData) {
    //    //异步通知结果
    //    return orderService.asyncNotify(notifyData);
    //}

}
