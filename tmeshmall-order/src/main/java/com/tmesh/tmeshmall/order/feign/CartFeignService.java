package com.tmesh.tmeshmall.order.feign;

import com.tmesh.common.vo.order.OrderItemVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 购物车系统
 */
@FeignClient("tmeshmall-cart")
public interface CartFeignService {

    /**
     * 查询当前用户购物车选中的商品项
     */
    @GetMapping(value = "/currentUserCartItems")
    List<OrderItemVO> getCurrentCartItems(@RequestParam(value = "id", required = false, defaultValue = "0") Long id);

    @GetMapping(value = "/cart/deleteItem")
    boolean deleteItem(@RequestParam("userId") Long userId, @RequestParam("skuId") Integer skuId);
    
    @GetMapping(value = "/cart/deleteItems")
    boolean deleteItems(@RequestParam("userId") Long userId, @RequestParam("skuIds") List<String> skuIds);
}
