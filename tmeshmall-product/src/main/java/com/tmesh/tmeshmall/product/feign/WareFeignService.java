package com.tmesh.tmeshmall.product.feign;

import com.tmesh.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: TMesh
 **/

@FeignClient("tmeshmall-ware")
public interface WareFeignService {

    /**
     * 1、R设计的时候可以加上泛型 R<T>
     * 2、直接返回我们想要的结果
     * 3、自己封装解析结果
     */
    @PostMapping(value = "/ware/waresku/hasStock")
    R getSkuHasStock(@RequestBody List<Long> skuIds);

}
