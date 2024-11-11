package com.tmesh.tmeshmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tmesh.common.utils.PageUtils;
import com.tmesh.common.vo.product.Catalog2VO;
import com.tmesh.common.vo.product.CatalogNavVO;
import com.tmesh.common.vo.product.CatalogVO;
import com.tmesh.tmeshmall.product.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author TMesh
 * @email 1009191578@qq.com

 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    List<CatalogNavVO> listNavWithTree(Long catId);

    List<Long> allChildrenList(Long catId);

    void removeMenuByIds(List<Long> asList);

    /**
     * 找到catelogId的完整路径
     * [父/子/孙]
     * @param catelogId
     * @return
     */
    Long[] findCatelogPath(Long catelogId);

    public void updateCascade(CategoryEntity category);

    /**
     * 查出所有1级分类
     */
    List<CategoryEntity> getLevelOneCategorys();

    Map<String, List<Catalog2VO>> getCatalogJson();

    Map<String, List<Catalog2VO>> getCatalogJson2();

    CategoryEntity selectById(Long id, String[] selectList);

    /**
     * 查询三级分类并封装成Map返回
     * 使用SpringCache注解方式简化缓存设置
     */
    Map<String, List<Catalog2VO>> getCatalogJsonWithSpringCache();

    Map<String, List<CatalogVO>> getCatalogAllJsonWithSpringCache();
}


