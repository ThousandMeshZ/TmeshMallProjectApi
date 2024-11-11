package com.tmesh.tmeshmall.search.controller;

import com.tmesh.common.es.SkuEsModel;
import com.tmesh.common.exception.BizCodeEnum;
import com.tmesh.common.utils.EmptyUtils;
import com.tmesh.common.utils.R;
import com.tmesh.tmeshmall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: TMesh
 * @createTime: 2024-02-18 15:07
 **/


@Slf4j
@RequestMapping(value = "/search/save")
@RestController
public class ElasticSaveController {

    @Autowired
    private ProductSaveService productSaveService;


    /**
     * 上架商品
     *
     * @param skuEsModels
     * @return
     */
    @PostMapping(value = "/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels) {

        boolean status = false;
        try {
            status = productSaveService.productStatusUp(skuEsModels);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("ElasticSaveController 商品上架错误{}", e);
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMessage());
        }

        if (status) {
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMessage());
        } else {
            return R.ok();
        }

    }

    /**
     * 上架商品
     *
     * @param skuIds
     * @return
     */
    @PostMapping(value = "/checkEsUp")
    public R checkEsUp(@RequestBody List<Long> skuIds) {

        List<Long> upSkuIds = new ArrayList<>();
        try {
            upSkuIds = productSaveService.checkEsUp(skuIds);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("ElasticSaveController checkEsUp 商品查询错误{}", e);
            return R.error(BizCodeEnum.PRODUCT_CHECK_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_CHECK_EXCEPTION.getMessage());
        }
        Map<String, Object> result = new HashMap<>();
        result.put("upSkuIds", upSkuIds);
        return R.ok(result);
    }


}
