package com.tmesh.common.entity.product;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * sku信息
 * 
 * @author: TMesh
 * @email 1009191578@qq.com
 * @date 2024-09-02 22:58:35
 */
@Data
public class SkuInfoEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * skuId
	 */
	@TableId
	private Long skuId;
	/**
	 * spuId
	 */
	private Long spuId;
	/**
	 * sku 名称
	 */
	private String skuName;
	/**
	 * sku 介绍描述
	 */
	private String skuDesc;
	/**
	 * 所属分类 id
	 */
	private Long catalogId;
	/**
	 * 品牌 id
	 */
	private Long brandId;
	/**
	 * 默认图片
	 */
	private String skuDefaultImg;
	/**
	 * 标题
	 */
	private String skuTitle;
	/**
	 * 副标题
	 */
	private String skuSubtitle;
	/**
	 * 价格
	 */
	private BigDecimal price;
	/**
	 * 销量
	 */
	private Long saleCount;

	private Map<String, String> attrValues;
}
