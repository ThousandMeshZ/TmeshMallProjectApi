package com.tmesh.common.entity.order;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单项信息
 * 
 * @author: TMesh
 * @email 1009191578@qq.com
 * @date 2023-09-02 22:57:46
 */
@Data
@TableName("oms_order_item")
public class OrderItemEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * order_id
	 */
	private Long orderId;
	/**
	 * order_sn
	 */
	private String orderSn;
	/**
	 * spu_id
	 */
	private Long spuId;
	/**
	 * spu_name
	 */
	private String spuName;
	/**
	 * spu_pic
	 */
	private String spuPic;
	/**
	 * 品牌
	 */
	private String spuBrand;
	/**
	 * 商品分类id
	 */
	private Long categoryId;
	/**
	 * 商品sku编号
	 */
	private Long skuId;
	/**
	 * 商品sku名字
	 */
	private String skuName;
	/**
	 * 商品sku图片
	 */
	private String skuPic;
	/**
	 * 商品sku价格
	 */
	private BigDecimal skuPrice;
	/**
	 * 商品购买的数量
	 */
	private Integer skuQuantity;
	/**
	 * 商品销售属性组合（JSON）
	 * "颜色:星河银","版本:8GB+256GB"
	 */
	private String skuAttrsVals;
	/**
	 * 商品促销分解金额
	 */
	private BigDecimal promotionAmount;
	/**
	 * 优惠券优惠分解金额
	 */
	private BigDecimal couponAmount;
	/**
	 * 积分优惠分解金额
	 */
	private BigDecimal integrationAmount;
	/**
	 * 该商品经过优惠后的分解金额
	 */
	private BigDecimal realAmount;
	/**
	 * 赠送积分
	 */
	private Integer giftIntegration;
	/**
	 * 赠送成长值
	 */
	private Integer giftGrowth;

}
