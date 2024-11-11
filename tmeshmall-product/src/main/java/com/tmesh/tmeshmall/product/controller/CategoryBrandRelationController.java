package com.tmesh.tmeshmall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tmesh.common.vo.product.BrandVO;
import com.tmesh.tmeshmall.product.entity.BrandEntity;
import com.tmesh.tmeshmall.product.service.BrandService;
import com.tmesh.tmeshmall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.tmesh.tmeshmall.product.entity.CategoryBrandRelationEntity;
import com.tmesh.tmeshmall.product.service.CategoryBrandRelationService;
import com.tmesh.common.utils.R;



/**
 * 品牌分类关联
 *
 * @author TMesh
 * @email 1009191578@qq.com
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;
    
    @Autowired
    private BrandService brandService;
    
    @Autowired
    private CategoryService categoryService;

    /**
     * 列表
     */
    @GetMapping(value = "/catelog/list")
    //@RequiresPermissions("product:categorybrandrelation:list")
    public R catelogList(@RequestParam Map<String, Object> params,@RequestParam("brandId") Long brandId){

        List<CategoryBrandRelationEntity> data = categoryBrandRelationService.
                list(new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id",brandId));

        return R.ok().put("data", data);
    }

    /**
     * /product/categorybrandrelation/brands/list
     * 1、Controller：处理请求，接收和效验数据
     * 2、Service接收Controller传来的数据，进行业务处理
     * 3、Controller接收Service处理完的数据，封装页面指定的vo
     */
    @GetMapping(value = "/brands/list")
    public R relationBransList(@RequestParam(value = "catId",required = true) Long catId) {

        List<BrandEntity> vos = categoryBrandRelationService.getBrandsByCatId(catId);

        List<BrandVO> collect = vos.stream().map(item -> {
            BrandVO brandVo = new BrandVO();
            brandVo.setBrandId(item.getBrandId());
            brandVo.setBrandName(item.getName());
            return brandVo;
        }).collect(Collectors.toList());

        return R.ok().put("data",collect);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("product:categorybrandrelation:info")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:categorybrandrelation:save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
        categoryBrandRelation.setBrandName(
                brandService.selectById(categoryBrandRelation.getBrandId(), new String[]{"name"})
                        .getName());
        categoryBrandRelation.setCatelogName(
                categoryService.selectById(categoryBrandRelation.getCatelogId(), new String[]{"name"})
                        .getName());
        categoryBrandRelationService.save(categoryBrandRelation);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:categorybrandrelation:update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
        categoryBrandRelation.setBrandName(
                brandService.selectById(categoryBrandRelation.getBrandId(), new String[]{"name"})
                        .getName());
        categoryBrandRelation.setCatelogName(
                categoryService.selectById(categoryBrandRelation.getCatelogId(), new String[]{"name"})
                        .getName());
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:categorybrandrelation:delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
