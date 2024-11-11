package com.tmesh.tmeshmall.product.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.tmesh.common.vo.product.Catalog2VO;
import com.tmesh.common.vo.product.CatalogNavVO;
import com.tmesh.common.vo.product.CatalogVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.tmesh.tmeshmall.product.entity.CategoryEntity;
import com.tmesh.tmeshmall.product.service.CategoryService;
import com.tmesh.common.utils.PageUtils;
import com.tmesh.common.utils.R;



/**
 * 商品三级分类
 *
 * @author TMesh
 * @email 1009191578@qq.com

 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 查出所有分类以及子分类，以树形结构组装起来
     */
    @RequestMapping("/list/tree")
    public R list(){
        List<CategoryEntity> categoryList = categoryService.listWithTree();
        return R.ok().put("data", categoryList);
    }

    @GetMapping(value = "/index/catalog.json")
    @ResponseBody
    public R getCatalogJson() {

//        Map<String, List<Catalog2VO>> catalogJson = categoryService.getCatalogJson();
        Map<String, List<Catalog2VO>> catalogJson = categoryService.getCatalogJsonWithSpringCache();
        return R.ok().put("data", catalogJson);

    }


    @GetMapping(value = "/index/allcatalog.json")
    @ResponseBody
    public R getCatalogAllJson() {

        List<CatalogVO> catalogJson = new ArrayList<>();
        Map<String, List<CatalogVO>> result = categoryService.getCatalogAllJsonWithSpringCache();
        if (result.containsKey("1")) {
            catalogJson = result.get("1");
        }

        return R.ok().put("data", catalogJson);
    }

    /**
     * 查出所有分类以及子分类，以树形结构组装起来
     */
    @GetMapping("/list/nav/{catId}")
    public R navList(@PathVariable("catId") Long catId){
        List<CatalogNavVO> categoryList = categoryService.listNavWithTree(catId);
        return R.ok().put("data", categoryList);
    }

    /**
     * 查出当前分类以及子分类的分类编号
     */
    @GetMapping("/allChildrenList/{catId}")
    public R allChildrenList(@PathVariable("catId") Long catId){
        List<Long> categoryList = categoryService.allChildrenList(catId);
        return R.ok().put("data", categoryList);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    //@RequiresPermissions("product:category:info")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("category", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:category:save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return R.ok();
    }

    @RequestMapping("/update/sort")
    //@RequiresPermissions("product:category:update")
    public R updateSort(@RequestBody CategoryEntity[] category){
        categoryService.updateBatchById(Arrays.asList(category));
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:category:update")
    public R update(@RequestBody CategoryEntity category){
		categoryService.updateById(category);

        return R.ok();
    }

    /**
     * 批量修改
     * @param categorys
     * @return
     */
    @RequestMapping("/update/batch")
    //@RequiresPermissions("product:category:update")
    public R update(@RequestBody CategoryEntity[] categorys){
        categoryService.updateBatchById(Arrays.asList(categorys));
        return R.ok();
    }

    /**
     * 删除
     * @RequestBody:获取请求体，必须发送 post 请求
     * SpringMvc 自动将请求体的数据（json），转换为对应的对象
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:category:delete")
    public R delete(@RequestBody Long[] catIds){

        // 1、检查当前删除的菜单，是否被别的地方引用
//		categoryService.removeByIds(Arrays.asList(catIds));

        categoryService.removeMenuByIds(Arrays.asList(catIds));
        return R.ok();
    }

}
