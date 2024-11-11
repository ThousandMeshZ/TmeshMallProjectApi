package com.tmesh.tmeshmall.cart.service;

import com.tmesh.common.vo.cart.CartItemVO;
import com.tmesh.common.vo.cart.CartVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 购物车
 * @Author: TMesh
 * @Date: 2023/12/4 23:53
 */
public interface CartService {

    /**
     * 添加sku商品到购物车
     */
    CartItemVO addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    /**
     * 根据skuId获取购物车商品信息
     */
    CartItemVO getCartItem(Long skuId);

    /**
     * 获取购物车列表
     */
    CartVO getCart() throws ExecutionException, InterruptedException;

    /**
     * 清空购物车的数据
     */
    void clearCart(String cartKey);

    /**
     * 更改购物车商品选中状态
     */
    void checkItem(Long skuId, Integer check);

    /**
     * 改变商品数量
     */
    void changeItemCount(Long skuId, Integer num);

    /**
     * 删除购物项
     */
    Long deleteIdCartInfo(Integer skuId);
    
    Long deleteIdCartInfo(Long userId, Integer skuId);
    
    Long deleteIdCartInfo(Long userId, List<String> skuIds);

    /**
     * 获取当前用户的购物车所有选中的商品项
     *  1.从redis中获取所有选中的商品项
     *  2.获取mysql最新的商品价格信息，替换redis中的价格信息
     */
    List<CartItemVO> getUserCartItems(Long id);
}