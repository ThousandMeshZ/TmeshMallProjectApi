package com.tmesh.common.to.product;

import com.tmesh.common.vo.product.MemberPrice;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 满件数打折、满金额优惠、会员价格
 */
@Data
public class SkuReductionTO {
    private Long skuId;
    private int fullCount;// 满件数
    private BigDecimal discount;// 折扣
    private int countStatus; // 可叠加优惠
    private BigDecimal fullPrice;// 满金额
    private BigDecimal reducePrice;// 优惠金额
    private int priceStatus; // 可叠加优惠
    private List<MemberPrice> memberPrice;// 会员价格
}
