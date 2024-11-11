package com.tmesh.tmeshmall.cart.feign;

import com.tmesh.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Author: TMesh
 * @Date: 2023/12/7 22:18
 */
@FeignClient("tmeshmall-product")
public interface ProductFeignService {

    /**
     * 根据skuId查询sku信息
     */
    @RequestMapping("/product/skuinfo/info/{skuId}")
    R getInfo(@PathVariable("skuId") Long skuId);

    /**
     * 根据skuId查询pms_sku_sale_attr_value表中的信息查询销售属性值
     * attrName:attrValue
     */
    @GetMapping("/product/skusaleattrvalue/stringList/{skuId}")
    public List<String> getSkuSaleAttrValues(@PathVariable("skuId") Long skuId);

    /**
     * 根据skuId查询当前商品的最新价格
     */
    @PostMapping("/product/skuinfo/info/sku/price")
    public Map<Long, BigDecimal> getPrice(@RequestBody Collection<Long> skuIds);
}