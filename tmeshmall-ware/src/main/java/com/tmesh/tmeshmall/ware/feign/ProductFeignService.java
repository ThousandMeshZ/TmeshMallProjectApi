package com.tmesh.tmeshmall.ware.feign;

import com.tmesh.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: TMesh
 **/

@FeignClient(value = "tmeshmall-product")
public interface ProductFeignService {

    /**
     *   /product/skuinfo/info/{skuId}
     *
     *   1)、让所有请求过网关；
     *          1、@FeignClient("tmeshmall-gateway")：给tmeshmall-gateway所在的机器发请求
     *          2、/api/product/skuinfo/info/{skuId}
     *   2）、直接让后台指定服务处理
     *          1、@FeignClient("tmeshmall-product")
     *          2、/product/skuinfo/info/{skuId}
     *
     * @return
     */
    @RequestMapping("/product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);

}
