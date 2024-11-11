package com.tmesh.tmeshmall.product.web;

import com.tmesh.common.vo.product.SkuItemVO;
import com.tmesh.tmeshmall.product.service.SkuInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: TMesh
 * @createTime: 2024-02-29 11:21
 **/

@Controller
public class ItemController {

    @Resource
    private SkuInfoService skuInfoService;

    /**
     * 展示当前sku的详情
     * @param skuId
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {

        System.out.println("准备查询" + skuId + "详情");

        SkuItemVO vos = skuInfoService.item(skuId);
        
        model.addAttribute("item",vos);
        model.addAttribute("skuId",skuId);

        return "item";
    }
}