package com.tmesh.tmeshmall.product.service.impl;

import com.tmesh.common.vo.product.SkuItemSaleAttrVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tmesh.common.utils.PageUtils;
import com.tmesh.common.utils.Query;

import com.tmesh.tmeshmall.product.dao.SkuSaleAttrValueDao;
import com.tmesh.tmeshmall.product.entity.SkuSaleAttrValueEntity;
import com.tmesh.tmeshmall.product.service.SkuSaleAttrValueService;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuItemSaleAttrVO> getSaleAttrBySpuId(Long spuId) {

        SkuSaleAttrValueDao baseMapper = this.getBaseMapper();
        List<SkuItemSaleAttrVO> saleAttrVos = baseMapper.getSaleAttrBySpuId(spuId);

        return saleAttrVos;
    }

    @Override
    public List<String> getSkuSaleAttrValuesAsStringList(Long skuId) {

        SkuSaleAttrValueDao baseMapper = this.baseMapper;
        List<String> stringList = baseMapper.getSkuSaleAttrValuesAsStringList(skuId);

        return stringList;
    }

    @Override
    public List<SkuSaleAttrValueEntity> getSkuSaleAttrValuesBySkuId(Long skuId) {

        SkuSaleAttrValueDao baseMapper = this.baseMapper;
        List<SkuSaleAttrValueEntity> list = baseMapper.getSkuSaleAttrValuesBySkuId(skuId);

        return list;
    }

    @Override
    public List<SkuSaleAttrValueEntity> getSkuSaleAttrValuesNameAndValueBySkuId(Long skuId) {

        SkuSaleAttrValueDao baseMapper = this.baseMapper;
        List<SkuSaleAttrValueEntity> list = baseMapper.getSkuSaleAttrValuesNameAndValueBySkuId(skuId);

        return list;
    }

}