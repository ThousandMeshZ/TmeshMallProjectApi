package com.tmesh.tmeshmall.product.feign;

import com.tmesh.common.es.SkuEsModel;
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

@FeignClient("tmeshmall-search")
public interface SearchFeignService {

    @PostMapping(value = "/search/save/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels);

    @PostMapping(value = "/search/save/checkEsUp")
    public R checkEsUp(@RequestBody List<Long> skuIds);

}
