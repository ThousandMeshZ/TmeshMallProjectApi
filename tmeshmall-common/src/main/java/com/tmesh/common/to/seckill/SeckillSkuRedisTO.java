package com.tmesh.common.to.seckill;

import com.tmesh.common.vo.seckill.SkuInfoVO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description: 给Redis中存放的skuInfo的信息
 * @Created: with IntelliJ IDEA.
 * @author: TMesh
 * @createTime: 2020-07-09 21:39
 **/

@Data
public class SeckillSkuRedisTO {
    /**
     * 活动 id
     */
    private Long promotionId;
    /**
     * 活动场次 id
     */
    private Long promotionSessionId;
    /**
     * 商品 id
     */
    private Long skuId;
    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;
    /**
     * 秒杀总量
     */
    private Integer seckillCount;
    /**
     * 每人限购数量
     */
    private Integer seckillLimit;
    /**
     * 排序
     */
    private Integer seckillSort;

    /**
     * sku 的详细信息
     */
    private SkuInfoVO skuInfo;

    /**
     * 当前商品秒杀的开始时间
     */
    private Long startTime;

    /**
     * 当前商品秒杀的结束时间
     */
    private Long endTime;

    /**
     * 当前商品秒杀的随机码
     */
    private String randomCode;
}
