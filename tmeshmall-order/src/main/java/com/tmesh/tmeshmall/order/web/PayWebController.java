package com.tmesh.tmeshmall.order.web;

import com.alipay.api.AlipayApiException;
import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.service.BestPayService;
import com.tmesh.common.constant.order.PaymentConstant;
import com.tmesh.common.vo.order.PayVO;
import com.tmesh.tmeshmall.order.config.AliPayConfig;
import com.tmesh.tmeshmall.order.entity.OrderEntity;
import com.tmesh.tmeshmall.order.service.impl.OrderServiceImpl;
import com.tmesh.tmeshmall.order.service.impl.PayContextStrategy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.lly835.bestpay.enums.BestPayTypeEnum.WXPAY_NATIVE;

/**
 * @author: TMesh
 */
@Slf4j
@Controller
public class PayWebController {

    @Autowired
    private OrderServiceImpl orderService;
    @Autowired
    private PayContextStrategy payContextStrategy;
    @Autowired
    private AliPayConfig aliPayConfig;

    @Autowired
    private BestPayService bestPayService;

    @Resource
    private WxPayConfig wxPayConfig;

    /**
     * 创建支付
     * 返回text/html页面
     * @param orderSn       订单号
     * @param payCode          支付类型
     * @param businessCode  业务类型
     */
    @ResponseBody
    @GetMapping(value = "/html/pay", produces = "text/html")
    public String htmlPayOrder(@RequestParam(value = "orderSn", required = false) String orderSn,
                               @RequestParam(value = "payCode", required = true) Integer payCode,
                               @RequestParam(value = "businessCode", required = true) Integer businessCode) throws Exception {
        // 获取支付类型
        PaymentConstant.PayType payType = PaymentConstant.PayType.getByCode(payCode);
        // 获取业务类型
        PaymentConstant.PayBusinessDetailType businessDetailType = PaymentConstant.PayBusinessDetailType.getByCodeAndBusinessCode(
                payType.getCode(), businessCode);

        // 获取订单信息，构造参数
        PayVO order = orderService.getOrderPay(orderSn);
        order.setNotify_url(PaymentConstant.SYSTEM_URL + businessDetailType.getNotifyUrl());// 封装异步回调地址
        order.setReturn_url(businessDetailType.getReturnUrl());// 封装同步回调地址

        // 请求策略方法
        String html = payContextStrategy.pay(payType, order);
        return html;
    }

    /**
     * 用户下单:支付宝支付
     * 1、让支付页让浏览器展示
     * 2、支付成功以后，跳转到用户的订单列表页
     * @param orderSn
     * @return
     * @throws AlipayApiException
     */
    @ResponseBody
    @GetMapping(value = "/aliPayOrder",produces = "text/html")
    public String aliPayOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {

        PayVO payVo = orderService.getOrderPay(orderSn);
        String pay = aliPayConfig.pay(payVo);
        System.out.println(pay);
        return pay;
    }


    /**
     * 微信支付
     * @param orderSn
     * @return
     */
    @GetMapping(value = "/weixinPayOrder")
    public String weixinPayOrder(@RequestParam("orderSn") String orderSn, Model model) {

        OrderEntity orderInfo = orderService.getOrderByOrderSn(orderSn);

        if (orderInfo == null) {
            throw new RuntimeException("订单不存在");
        }

        PayRequest request = new PayRequest();
        request.setOrderName("4559066-最好的支付sdk");
        request.setOrderId(orderInfo.getOrderSn());
        request.setOrderAmount(0.01);
        request.setPayTypeEnum(WXPAY_NATIVE);

        PayResponse payResponse = bestPayService.pay(request);
        payResponse.setOrderId(orderInfo.getOrderSn());
        log.info("发起支付 response={}", payResponse);

        //传入前台的二维码路径生成支付二维码
        model.addAttribute("codeUrl",payResponse.getCodeUrl());
        model.addAttribute("orderId",payResponse.getOrderId());
        model.addAttribute("returnUrl",wxPayConfig.getReturnUrl());

        return "createForWxNative";
    }


    //根据订单号查询订单状态的API
    @GetMapping(value = "/queryByOrderId")
    @ResponseBody
    public OrderEntity queryByOrderId(@RequestParam("orderId") String orderId) {
        log.info("查询支付记录...");
        return orderService.getOrderByOrderSn(orderId);
    }
}
