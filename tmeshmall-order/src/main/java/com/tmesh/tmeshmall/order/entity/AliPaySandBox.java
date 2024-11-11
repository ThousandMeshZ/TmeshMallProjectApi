package com.tmesh.tmeshmall.order.entity;

import lombok.Data;

@Data
public class AliPaySandBox {
    private String tradeNo;
    private double totalAmount;
    private String subject;
    private String alipayTradeNo;
}
