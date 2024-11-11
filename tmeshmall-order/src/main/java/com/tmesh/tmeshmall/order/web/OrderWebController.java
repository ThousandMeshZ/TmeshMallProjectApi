package com.tmesh.tmeshmall.order.web;

import com.tmesh.common.exception.NoStockException;
import com.tmesh.common.exception.VerifyPriceException;
import com.tmesh.common.vo.order.OrderConfirmVO;
import com.tmesh.common.vo.order.OrderSubmitVO;
import com.tmesh.common.vo.order.SubmitOrderResponseVO;
import com.tmesh.common.annotation.TokenVerify;
import com.tmesh.tmeshmall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;

/**
 * @Author: TMesh
 * @Date: 2021/12/20 21:59
 */
@Controller
public class OrderWebController {

    @Autowired
    private OrderService orderService;

    /**
     * 跳转结算页
     * 购物车页cart.html点击去结算跳转confirm.html结算页
     */
    @GetMapping(value = "/toTrade")
    public String toTrade(Model model, HttpServletRequest request) throws ExecutionException, InterruptedException {
        // 查询结算页VO
        OrderConfirmVO confirmVo = null;
        try {
            confirmVo = orderService.OrderConfirmVO();
            model.addAttribute("confirmOrderData", confirmVo);
            // 跳转结算页
            return "confirm";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "toTrade";
    }

    /**
     * 创建订单
     * 创建成功，跳转订单支付页
     * 创建失败，跳转结算页
     * 无需提交要购买的商品，提交订单时会实时查询最新的购物车商品选中数据提交
     */
    @TokenVerify
    @PostMapping(value = "/submitOrder")
    public String submitOrder(OrderSubmitVO vo, Model model, RedirectAttributes attributes) {
        try {
            SubmitOrderResponseVO responseVo = orderService.submitOrder(vo);
            if (responseVo.getCode() == 0) {
                //成功
                model.addAttribute("submitOrderResp", responseVo);// 封装VO订单数据，供页面解析[订单号、应付金额]
                return "pay";
            } else {
                String msg = "下单失败";
                switch (responseVo.getCode()) {
                    case 1: msg += "令牌订单信息过期，请刷新再次提交"; break;
                    case 2: msg += "订单商品价格发生变化，请确认后再次提交"; break;
                    case 3: msg += "库存锁定失败，商品库存不足"; break;
                }
                attributes.addFlashAttribute("msg",msg);
                return "redirect:http://order.tmesh.cn/toTrade";
            }
            // 创建订单成功，跳转收银台

        } catch (Exception e) {
            e.printStackTrace();
            // 下单失败回到订单结算页
            if (e instanceof VerifyPriceException) {
                String message = ((VerifyPriceException) e).getMessage();
                attributes.addFlashAttribute("msg", "下单失败" + message);
            } else if (e instanceof NoStockException) {
                String message = ((NoStockException) e).getMessage();
                attributes.addFlashAttribute("msg", "下单失败" + message);
            }
            return "redirect:http://order.tmesh.cn/toTrade";
        }
    }
}