package com.tmesh.tmeshmall.product.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.tmesh.tmeshmall.product.entity.SkuInfoEntity;
import com.tmesh.tmeshmall.product.service.SkuInfoService;
import com.tmesh.common.utils.PageUtils;
import com.tmesh.common.utils.R;



/**
 * sku信息
 *
 * @author TMesh
 * @email 1009191578@qq.com

 */
@RestController
@RequestMapping("product/skuinfo")
public class SkuInfoController {
    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * 根据skuId查询当前商品的价格
     * @param skuId
     * @return
     */
    @GetMapping(value = "/{skuId}/price")
    public BigDecimal getPrice(@PathVariable("skuId") Long skuId) {

        //获取当前商品的信息
        SkuInfoEntity skuInfo = skuInfoService.getById(skuId);

        //获取商品的价格
        BigDecimal price = skuInfo.getPrice();

        return price;
    }
    
    /**
     * 列表
     */
//    @RequestMapping("/list")
//    //@RequiresPermissions("product:skuinfo:list")
//    public R list(@RequestParam Map<String, Object> params){
//        PageUtils page = skuInfoService.queryPage(params);
//
//        return R.ok().put("page", page);
//    }

    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = skuInfoService.queryPageCondition(params);

        return R.ok().put("page", page);
    }



    /**
     * 信息
     */
    @RequestMapping("/info/{skuId}")
    //@RequiresPermissions("product:skuinfo:info")
    public R info(@PathVariable("skuId") Long skuId){
		SkuInfoEntity skuInfo = skuInfoService.getById(skuId);

        return R.ok().put("skuInfo", skuInfo);
    }

    /**
     * 查询商品集合
     */
    @PostMapping("/infos")
    public R infos(@RequestBody List<Long> skuIds) {
        List<SkuInfoEntity> skuInfos = skuInfoService.getByIds(skuIds);
        return R.ok().setData(skuInfos);
    }

    /**
     * 获取商品价格信息
     */
    @PostMapping("/info/sku/price")
    public Map<Long, BigDecimal> getPrice(@RequestBody Collection<Long> skuIds) {
        return skuInfoService.getByIds(skuIds).stream()
                .collect(Collectors.toMap(SkuInfoEntity::getSkuId, SkuInfoEntity::getPrice));
    }


    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:skuinfo:save")
    public R save(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.save(skuInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:skuinfo:update")
    public R update(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.updateById(skuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:skuinfo:delete")
    public R delete(@RequestBody Long[] skuIds){
		skuInfoService.removeByIds(Arrays.asList(skuIds));

        return R.ok();
    }

    /**
     * 修改图片
     */
    @RequestMapping("/uploadPic")
    //@RequiresPermissions("product:skuinfo:delete")
    public R uploadPic(@RequestBody SkuInfoEntity skuInfo){
        boolean b = skuInfoService.uploadPic(skuInfo);
        if (b) {
            return R.ok();
        }
        return R.error("修改图片失败");
    }

}
