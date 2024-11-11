package com.tmesh.tmeshmall.order.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.easysdk.factory.Factory;
import com.tmesh.common.vo.order.alipay.AliPayAsyncVO;
import com.tmesh.tmeshmall.order.config.AliPayConfigSandBox;
import com.tmesh.tmeshmall.order.entity.AliPaySandBox;
import com.tmesh.tmeshmall.order.service.OrderService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("alipay")
@Transactional(rollbackFor = Exception.class)
public class AliPayController {

    @Autowired
    AliPayConfigSandBox aliPayConfigSandBox;

    @Resource
    private OrderService orderService;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @GetMapping("/pay") // &subject=xxx&traceNo=xxx&totalAmount=xxx
    public void pay(AliPaySandBox aliPay, HttpServletResponse httpResponse) throws Exception {
        AlipayClient alipayClient = new DefaultAlipayClient(
                aliPayConfigSandBox.getGatewayUrl(), aliPayConfigSandBox.getAppId(),
                aliPayConfigSandBox.getAppPrivateKey(), aliPayConfigSandBox.getFormat(), 
                aliPayConfigSandBox.getCharset(), aliPayConfigSandBox.getAlipayPublicKey(), 
                aliPayConfigSandBox.getSignType());
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(aliPayConfigSandBox.getNotifyUrl());
        request.setReturnUrl(aliPayConfigSandBox.getReturnUrl());
        request.setBizContent("{\"out_trade_no\":\"" + aliPay.getTradeNo() + "\","
                + "\"total_amount\":\"" + aliPay.getTotalAmount() + "\","
                + "\"subject\":\"" + aliPay.getSubject() + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        String form = "";
        try {
            // 调用SDK生成表单
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            if(response.isSuccess()){
                log.info("【 Ali pay 调用成功 】");
            } else {
                log.info("【 Ali pay 调用失败 】");
            }
            log.info("支付宝支付结束，响应为：{}", JSON.toJSON(response));
            form = response.getBody();
        } catch (Exception e) {
            log.error("【 Ali pay 异常 】", e);
            e.printStackTrace();
        }
        httpResponse.setContentType("text/html;charset=" + aliPayConfigSandBox.getCharset());
        // 直接将完整的表单html输出到页面
        httpResponse.getWriter().write(form);
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();
    }
    @GetMapping("/query")
    public boolean query(AliPaySandBox aliPay, HttpServletResponse httpResponse){
        AlipayClient alipayClient = new DefaultAlipayClient(
                aliPayConfigSandBox.getGatewayUrl(), aliPayConfigSandBox.getAppId(),
                aliPayConfigSandBox.getAppPrivateKey(), aliPayConfigSandBox.getFormat(),
                aliPayConfigSandBox.getCharset(), aliPayConfigSandBox.getAlipayPublicKey(),
                aliPayConfigSandBox.getSignType());
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        //订单id
        bizContent.put("out_trade_no", aliPay.getTradeNo());
        request.setBizContent(bizContent.toString());
        AlipayTradeQueryResponse response = null;
        //和上面方法一样，定义变量存放响应的数据
        String from = "";
        try {
            response = alipayClient.execute(request);
            if(response.isSuccess()){
                log.info("【 Ali query 调用成功 】");
            } else {
                log.info("【 Ali query 调用失败 】");
            }
            log.info("支付宝支付查询，响应为：{}", JSON.toJSON(response));
            from = response.getBody();
            JSONObject jsonObject = JSON.parseObject(from);
            /** 
             * {
             *   "alipay_trade_query_response":{
             *      "msg":"Success",
             *      "code":"10000",
             *      "buyer_user_id":"2088722032442053",
             *      "send_pay_date":"2024-08-27 16:36:22",
             *      "invoice_amount":"0.00",
             *      "out_trade_no":"202408271635555431828350720523546625",
             *      "total_amount":"6599.00",
             *      "buyer_user_type":"PRIVATE",
             *      "trade_status":"TRADE_SUCCESS",
             *      "trade_no":"2024082722001442050503870140",
             *      "buyer_logon_id":"pkc***@sandbox.com",
             *      "receipt_amount":"0.00",
             *      "point_amount":"0.00",
             *      "buyer_pay_amount":"0.00"
             *      },
             *   "sign":"PPlnwESQuLFM4+wxVZ6sM4hyciSOFRSzvTIOsgLuLFGvaN7mA3xG2EitGkN4RFzwdl65t1Idhu3IjpK2IwEdt2YppEmq91QQk3S3tkuFZ8K32YBSd9r9eTogEzXS167EClIjEQXRakDXce+ehCAU39s5UH8cSwBGo+phqYiVpg0lGtidLvtgT6HK+hJmEhFpid82YeJ08+AA+J2jL53ZgUFAKL7DTQlgMesETwyO+IB/Se0ybgUTnkduBdZVU1Ufl1PdhVqRhTQVX7ZRzLytSj006DC0o0/I+BqOHdAJ8Yhn+jDcQYuckHNIOaxQhjdNW1C0SSCjG2TmP6lW+TwJNA=="
             *   }
            *  */
            JSONObject alipayTradeQueryResponse = jsonObject.getJSONObject("alipay_trade_query_response");
            String code = alipayTradeQueryResponse.getString("code");
            if ("10000".equals(code)) {
                // 更新订单未已支付
                AliPayAsyncVO asyncVo = new AliPayAsyncVO();
                asyncVo.setOut_trade_no(alipayTradeQueryResponse.getString("out_trade_no"));
                asyncVo.setTrade_no(alipayTradeQueryResponse.getString("trade_no"));
                asyncVo.setTotal_amount(alipayTradeQueryResponse.getString("total_amount"));
                asyncVo.setSubject(aliPay.getSubject());
                asyncVo.setTrade_status(alipayTradeQueryResponse.getString("trade_status"));
    //            asyncVo.setGmt_payment(alipayTradeQueryResponse.getString("send_pay_date"));
    //          asyncVo.setNotify_time(alipayTradeQueryResponse.getString("notify_time"));
                asyncVo.setBuyer_pay_amount(alipayTradeQueryResponse.getString("buyer_pay_amount"));
                orderService.handlePayResult(asyncVo);
    //          order.setCheckoutTime(alipayTradeQueryResponse.getString("gmt_payment"));
                return true;
            }
        } catch (Exception e) {
            log.error("【 Ali query 异常 】", e);
            e.printStackTrace();
        }
        return false;
    }

    @RequestMapping("/return")
    public String returnUrlMethod(@RequestParam(value = "orderId",required = false) Long orderId,
                                  @RequestParam(value = "memberId",required = false) Long memberId,
                                  HttpServletRequest request, HttpSession session) {
        // 获取支付宝GET过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        try {
            for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
                }
                // 乱码解决，这段代码在出现乱码时使用
                valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
                params.put(name, valueStr);
            }

            System.out.println(params);//查看参数都有哪些
            // 这是我自己放的数据，所以要删除，否则支付宝会校验失败
            params.remove("orderId");
            params.remove("memberId");
            /**
             * {
             *  "charset":"utf-8",
             *  "out_trade_no":"202408271710010241828359299901087746",
             *  "method":"alipay.trade.page.pay.return",
             *  "total_amount":"6599.00",
             *  "sign":"IpqYfRbal2i9iL6nLw6z4IzuB1BPO6J9iCNttseSJoQ13BFHFw3gwF1hv+uIogkOX0QJ565Cle7pIGr4BishZ89A14Zk03++o5bbYDVfJ/dfviwmoKONdHfrRqDet5ulK+YkFFD/gXrDBrvHKO2XjadTfCuteCnCEvXqp2o/OW3k7EsRphGGSzkQT/PsuUExOk3uFXlGm0DEOU645Jl2t5DEHce86/tWHauc4EeVEjER0npCiB2ZF1FXZtljaF7J/r4xG+elBgAhNVNCW8X/dB5UPGXOSphNLARW7w4yftQhTC/Yl3nvgawZTUwGYVcM/DpDYce1XYXxOvOYKiT0Pw==",
             *  "trade_no":"2024082722001442050503872693",
             *  "auth_app_id":"9021000135657613",
             *  "version":"1.0",
             *  "app_id":"9021000135657613",
             *  "sign_type":"RSA2",
             *  "seller_id":"2088721032462106",
             *  "timestamp":"2024-08-27 17:10:42"}
             * */
            log.info("支付宝支付同步，响应为：{}", JSON.toJSON(params));
            //验证签名（支付宝公钥）
            boolean signVerified = AlipaySignature.rsaCheckV1(params,
                    aliPayConfigSandBox.getAlipayPublicKey(), aliPayConfigSandBox.getCharset(),
                    aliPayConfigSandBox.getSignType()); // 调用SDK验证签名
            //验证签名通过
            if(signVerified){
                log.info("【 Ali return 验证签名通过 】");
                // 商户订单号
                String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
                // 支付宝交易流水号
                String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
                // 付款金额
                float money = Float.parseFloat(new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8"));

                log.info("商户订单号: " + out_trade_no);
                log.info("支付宝交易号: " + trade_no);
                log.info("付款金额: " + money);
                log.info("用户: " + memberId);
                //在这里编写自己的业务代码（对数据库的操作）
                /* AliPayAsyncVO asyncVo = new AliPayAsyncVO();
                asyncVo.setOut_trade_no(params.get("out_trade_no"));
                asyncVo.setTrade_no(params.get("trade_no"));
                asyncVo.setTotal_amount(params.get("buyer_pay_amount"));
                asyncVo.setSubject(params.get("subject"));
                asyncVo.setTrade_status(params.get("trade_status"));
                asyncVo.setBuyer_id(params.get("buyer_id"));
                asyncVo.setGmt_payment(params.get("gmt_payment"));
                Date notifyTime = sdf.parse(params.get("notify_time"));
                asyncVo.setNotify_time(notifyTime);
                asyncVo.setBuyer_pay_amount(params.get("buyer_pay_amount"));
                orderService.handlePayResult(asyncVo); */
//              order.setCheckoutTime(params.get("gmt_payment"));
                // 支付成功，修改订单状态，添加支付信息
                //成功
                return "true";
            }else{
                log.info("【 Ali return 验证签名失败 】");
                //失败
                return "false";
            }
        } catch (Exception e) {
            log.error("【 Ali return 异常 】", e);
            e.printStackTrace();
        }
        return "false";
    }
    
    @PostMapping("/notify")  // 注意这里必须是POST接口
    public String payNotify(HttpServletRequest request) throws Exception {
        if (request.getParameter("trade_status").equals("TRADE_SUCCESS")) {
            System.out.println("=========支付宝异步回调========");

            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()) {
                params.put(name, request.getParameter(name));
                System.out.println(name + " = " + request.getParameter(name));
            }
            
            log.info("支付宝支付异步，响应为：{}", JSON.toJSON(params));
            /**
             * {
             *  "gmt_create":"2024-08-27 16:33:27",
             *  "charset":"utf-8",
             *  "gmt_payment":"2024-08-27 16:33:34",
             *  "notify_time":"2024-08-27 16:31:01",
             *  "subject":"202408271632480331828349934062186498",
             *  "sign":"d7Y203ziBE9nZxMzEu68PivTKNOHWJe4FFoVah+slidZu0y+LwRRK1rpqeoVFI1KiO+rHOVtYPAQVj9brzWJx0RzeWdQNt6v0Vi2RgR769DgLvYYhK2hbxszsqcqxuCCFGEIH3dArY+h8TP7KYy3yi686Nivv4+mBJvNByJKOgAIjpZY14o2bbq24jbKtnobNFebkGvqgsnG8v1asIQLqOkAYPe8NRhGCA9149nNfT4zj/azThAMfB5jv8nRF7WjOjEVRMGfxQHSw64oA0zVJ/ORa0xUSAXfhCBS/teecnSMp8xaLJYKEVB6GOYRsFnIrKEk1XerJPvAm+Kpi4vfKA==",
             *  "buyer_id":"2088722032442053",
             *  "invoice_amount":"6599.00",
             *  "version":"1.0",
             *  "notify_id":"2024082701222163334142050504034395",
             *  "fund_bill_list":"[
             *      {
             *          "amount":"6599.00",
             *          "fundChannel":"ALIPAYACCOUNT"
             *      }
             *  ]",
             *  "notify_type":"trade_status_sync",
             *  "out_trade_no":"202408271632480331828349934062186498",
             *  "total_amount":"6599.00",
             *  "trade_status":"TRADE_SUCCESS",
             *  "trade_no":"2024082722001442050503871309",
             *  "auth_app_id":"9021000135657613",
             *  "receipt_amount":"6599.00",
             *  "point_amount":"0.00",
             *  "buyer_pay_amount":"6599.00",
             *  "app_id":"9021000135657613",
             *  "sign_type":"RSA2",
             *  "seller_id":"2088721032462106"}
             */
            // 支付宝验签
            if (Factory.Payment.Common().verifyNotify(params)) {
                log.info("【 Ali notify 验证签名通过 】");
                // 验签通过
                String tradeNo = params.get("out_trade_no");
                String gmtPayment = params.get("gmt_payment");
                String alipayTradeNo = params.get("trade_no");
                log.info("交易名称: " + params.get("subject"));
                log.info("交易状态: " + params.get("trade_status"));
                log.info("支付宝交易凭证号: " + params.get("trade_no"));
                log.info("商户订单号: " + params.get("out_trade_no"));
                log.info("交易金额: " + params.get("total_amount"));
                log.info("买家在支付宝唯一id: " + params.get("buyer_id"));
                log.info("买家付款时间: " + params.get("gmt_payment"));
                log.info("买家付款金额: " + params.get("buyer_pay_amount"));
                // 更新订单未已支付
                AliPayAsyncVO asyncVo = new AliPayAsyncVO();
                asyncVo.setOut_trade_no(params.get("out_trade_no"));
                asyncVo.setTrade_no(params.get("trade_no"));
                asyncVo.setTotal_amount(params.get("buyer_pay_amount"));
                asyncVo.setSubject(params.get("subject"));
                asyncVo.setTrade_status(params.get("trade_status"));
                asyncVo.setBuyer_id(params.get("buyer_id"));
                asyncVo.setGmt_payment(params.get("gmt_payment"));
                Date notifyTime = sdf.parse(params.get("notify_time"));
                asyncVo.setNotify_time(notifyTime);
                asyncVo.setBuyer_pay_amount(params.get("buyer_pay_amount"));
                orderService.handlePayResult(asyncVo);
//                order.setCheckoutTime(params.get("gmt_payment"));
            } else {
                log.info("【 Ali notify 验证签名失败 】");
            }
        }
        return "success";
    }
}
