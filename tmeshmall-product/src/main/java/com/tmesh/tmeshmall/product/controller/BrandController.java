package com.tmesh.tmeshmall.product.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tmesh.common.valid.AddGroup;
import com.tmesh.common.valid.UpdateGroup;
import com.tmesh.common.valid.UpdateStatusGroup;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.tmesh.tmeshmall.product.entity.BrandEntity;
import com.tmesh.tmeshmall.product.service.BrandService;
import com.tmesh.common.utils.PageUtils;
import com.tmesh.common.utils.R;


/**
 * 品牌
 *
 * @author TMesh
 * @email 1009191578@qq.com
 */
@RestController
@RequestMapping("/product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:brand:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    //@RequiresPermissions("product:brand:info")
    public R info(@PathVariable("brandId") Long brandId) {
        BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }
    
    @GetMapping("/infos")
    //@RequiresPermissions("product:brand:info")
    public R infos(@PathVariable("brandIds") List<Long> brandIds) {
        List<BrandEntity> brand = brandService.getBrandIByIds(brandIds);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:brand:save")
    public R save(@Validated(AddGroup.class) @RequestBody BrandEntity brand) {
//    public R save(@Valid @RequestBody BrandEntity brand , BindingResult bindingResult ) {
/*         if (bindingResult.hasErrors()) {
            JSONObject result = new JSONObject();
            JSONObject errors = new JSONObject();
            JSONObject data = new JSONObject();
            result.put("timestamp", new Date());
            result.put("data", data);
            data.put("error", "Bad Request");
            data.put("errors", errors);
            //获取效验错误结果
            bindingResult.getFieldErrors().forEach((item) -> {
                //获取到错误提示
                String message = item.getDefaultMessage();
                //获取错误的属性的名字
                String field = item.getField();
                errors.put(field, message);
            });
            return R.error(400, "提交的数据不合法").putAllNew(result);
        } */
        brandService.save(brand);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:brand:update")
    public R update(@Validated(UpdateGroup.class) @RequestBody BrandEntity brand) {
        brandService.updateById(brand);

        return R.ok();
    }

    /**
     * 修改状态
     */
    @RequestMapping("/update/status")
    //@RequiresPermissions("product:brand:update")
    public R updateStatus(@Validated(UpdateStatusGroup.class) @RequestBody BrandEntity brand) {
        brandService.updateById(brand);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:brand:delete")
    public R delete(@RequestBody Long[] brandIds) {
        brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
