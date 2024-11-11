package com.tmesh.tmeshmall.seckill.service;

import com.tmesh.common.to.seckill.SeckillSkuRedisTO;

import java.util.List;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: TMesh
 * @createTime: 2024-01-09 19:29
 **/
public interface SeckillService {

    /**
     * 上架三天需要秒杀的商品
     */
    void uploadSeckillSkuLatest3Days();

    List<SeckillSkuRedisTO> getCurrentSeckillSkus();

    /**
     * 根据skuId查询商品是否参加秒杀活动
     * @param skuId
     * @return
     */
    SeckillSkuRedisTO getSkuSeckilInfo(Long skuId);

    /**
     * 当前商品进行秒杀（秒杀开始）
     * @param killId
     * @param key
     * @param num
     * @return
     */
    String kill(String killId, String key, Integer num) throws InterruptedException;
}
