package com.tmesh.tmeshmall.cart.controller;

import com.tmesh.common.vo.cart.CartItemVO;
import com.tmesh.tmeshmall.cart.service.CartService;
import com.tmesh.tmeshmall.cart.service.impl.CartServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 购物车信息控制器
 * @Author: TMesh
 * @Date: 2023/12/8 23:54
 */
@RestController
public class CartInfoController {
    @Autowired
    CartService cartService;

    /**
     * 获取当前用户的购物车所有选中的商品项
     *  1.从redis中获取所有选中的商品项
     *  2.获取mysql最新的商品价格信息，替换redis中的价格信息
     */
    @GetMapping(value = "/currentUserCartItems")
    public List<CartItemVO> getCurrentCartItems(@RequestParam(value = "id", required = false, defaultValue = "0") Long id) {
        return cartService.getUserCartItems(id);
    }

    @GetMapping(value = "/cart/deleteItem")
    @ResponseBody
    public boolean deleteItem(@RequestParam("userId") Long userId, @RequestParam("skuId") Integer skuId) {
        Long delete = cartService.deleteIdCartInfo(userId, skuId);
        if (delete == 0) {
            return false;
        }
        return true;
    }

    @GetMapping(value = "/cart/deleteItems")
    @ResponseBody
    public boolean deleteItems(@RequestParam("userId") Long userId, @RequestParam("skuIds") List<String> skuIds) {
        Long delete = cartService.deleteIdCartInfo(userId, skuIds);
        if (delete == 0) {
            return false;
        }
        return true;
    }
}