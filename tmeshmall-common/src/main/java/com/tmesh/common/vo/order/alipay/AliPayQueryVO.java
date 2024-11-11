package com.tmesh.common.vo.order.alipay;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class AliPayQueryVO {
    
    // 支付宝交易号 必选 string(64)
    private String tradeNo;
    
    // 商家订单号 必选 string(64)
    private String outTradeNo;
    
    // 买家支付宝账号 必选 string(100)
    private String buyerLogonId;
    
    // 交易状态：WAIT_BUYER_PAY（交易创建，等待买家付款）、TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）、TRADE_SUCCESS（交易支付成功）、TRADE_FINISHED（交易结束，不可退款）
    // 必选 string(32)
    private String tradeStatus;

    // 交易的订单金额，单位为元，两位小数。该参数的值为支付时传入的 total_amount 必选price(11)
    private Double totalAmount;
    
    // 交易支付使用的资金渠道。 必选 TradeFundBill[] 只有在签约中指定需要返回资金明细，或者入参的query_options中指定时才返回该字段信息。
    private List<TradeFundBill> fundBillList;
    
    // 买家在支付宝的用户id 必选 string(16) 新商户建议使用buyer_open_id替代该字段。对于新商户，buyer_user_id字段未来计划逐步回收，存量商户可继续使用。如使用buyer_open_id，请确认 应用-开发配置-openid配置管理 已启用。无该配置项，可查看openid配置申请。
    private String buyerUserId;
    
    // 特殊可选 date(32) 本次交易打款给卖家的时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendPayDate;
    
    // 实收金额，单位为元，两位小数。该金额为本笔交易，商户账户能够实际收到的金额 特殊可选 string(11)
    private String receiptAmount;

    // 商户门店编号 特殊可选 string(32)
    private String storeId;
    
    // 商户机具终端编号 特殊可选 string(32)
    private String terminalId;
    
    // 请求交易支付中的商户店铺的名称 特殊可选 string(512)
    private String storeName;
    
    // 买家支付宝用户唯一标识  详情可查看 openid简介 特殊可选 string(128)
    private String buyerOpenId;
    
    // 特殊可选 string(18) 【描述】买家用户类型。CORPORATE:企业用户；PRIVATE:个人用户。
    private String buyerUserType;
            
    // 商家优惠金额 特殊可选 string(11)
    private String mdiscountAmount;
    
    // 平台优惠金额 特殊可选string(11)
    private String discountAmount;
    
    // 交易额外信息，特殊场景下与支付宝约定返回。 特殊可选 string(1024) json格式。【示例值】{"action":"cancel"}
    private String extInfos;
    
    // 买家实付金额，单位为元，两位小数。该金额代表该笔交易买家实际支付的金额，不包含商户折扣等金额 可选 price(11)
    private Double buyerPayAmount;
    
    // 积分支付的金额，单位为元，两位小数。该金额代表该笔交易中用户使用积分支付的金额，比如集分宝或者支付宝实时优惠等 可选 price(11)
    private Double pointAmount;
    
    // 交易中用户支付的可开具发票的金额，单位为元，两位小数。该金额代表该笔交易中可以给用户开具发票的金额 可选 price(11)
    private Double invoiceAmount;

    @Data
    private static class TradeFundBill {
        // 交易使用的资金渠道，详见 支付渠道列表 必选 string(32) 示例值: ALIPAYACCOUNT
        private String fundChannel;

        // 该支付工具类型所使用的金额 必选 price(32)
        private Double amount;

        // 渠道实际付款金额 可选 price(11)
        private Double realAmount;
    }
}
