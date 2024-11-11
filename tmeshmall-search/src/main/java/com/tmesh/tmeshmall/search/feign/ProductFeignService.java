package com.tmesh.tmeshmall.search.feign;

import com.tmesh.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: TMesh
 * @createTime: 2024-02-18 15:07
 **/

@FeignClient("tmeshmall-product")
public interface ProductFeignService {

    @GetMapping("/product/category/index/allcatalog.json")
    public R allcatalog();

    @GetMapping("/product/attr/info/{attrId}")
    public R attrInfo(@PathVariable("attrId") Long attrId);

    @GetMapping("/product/attr/infos/")
    public R brandsInfo(@PathVariable("brandIds") List<Long> brandIds);

    @GetMapping("/product/category/list/nav/{catId}")
    public R navList(@PathVariable("catId") Long catId);

    @GetMapping("/product/category/allChildrenList/{catId}")
    public R allChildrenList(@PathVariable("catId") Long catId);

}
