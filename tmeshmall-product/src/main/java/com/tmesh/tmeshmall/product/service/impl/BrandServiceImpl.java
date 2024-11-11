package com.tmesh.tmeshmall.product.service.impl;

import com.tmesh.common.utils.EmptyUtils;
import com.tmesh.tmeshmall.product.service.CategoryBrandRelationService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tmesh.common.utils.PageUtils;
import com.tmesh.common.utils.Query;

import com.tmesh.tmeshmall.product.dao.BrandDao;
import com.tmesh.tmeshmall.product.entity.BrandEntity;
import com.tmesh.tmeshmall.product.service.BrandService;
import org.springframework.transaction.annotation.Transactional;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Resource
    private CategoryBrandRelationService categoryBrandRelationService;
    
    @Autowired
    BrandDao brandDao;
    
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }
    

    @Override
    public BrandEntity selectById(Long id, String... selectList) {
        QueryWrapper<BrandEntity> queryWrapper = new QueryWrapper<>();
        if (EmptyUtils.isNotEmpty(selectList)) {
            queryWrapper.select(selectList);
        }
        if (EmptyUtils.isNotEmpty(id)) {
            queryWrapper.eq("BRAND_ID", id);
            return brandDao.selectOne(queryWrapper);
        }
        return null;
    }

    @Override
    public List<BrandEntity> getBrandIByIds(List<Long> brandIds) {
        return baseMapper.selectList(new QueryWrapper<BrandEntity>().in("brand_id", brandIds));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateDetail(BrandEntity brand) {
        //保证冗余字段的数据一致
        baseMapper.updateById(brand);

        if (!StringUtils.isEmpty(brand.getName())) {
            //同步更新其他关联表中的数据
            categoryBrandRelationService.updateBrand(brand.getBrandId(),brand.getName());

            //TODO 更新其他关联
        }
    }

}