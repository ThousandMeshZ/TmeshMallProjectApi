package com.tmesh.tmeshmall.order.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 支付信息表
 * 
 * @author TMesh
 * @email 1009191578@qq.com
 */
@Data
@TableName("oms_payment_info")
public class PaymentInfoEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * 订单号（对外业务号）
	 */
	private String orderSn;
	/**
	 * 订单id
	 */
	private Long orderId;
	/**
	 * 支付宝交易流水号
	 */
	private String alipayTradeNo;
	/**
	 * 支付总金额
	 */
	private BigDecimal totalAmount;
	/**
	 * 交易内容
	 */
	private String subject;
	/**
	 * 支付状态
	 */
	private String paymentStatus;
	/**
	 * 创建时间
	 */
	@TableField(fill = FieldFill.INSERT)
	private Date createTime;
	/**
	 * 确认时间
	 */
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private Date confirmTime;
	/**
	 * 回调内容
	 */
	private String callbackContent;
	/**
	 * 回调时间
	 */
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private Date callbackTime;

}
