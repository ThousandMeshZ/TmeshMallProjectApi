package com.tmesh.tmeshmall.product.service.impl;

import com.tmesh.common.vo.product.Attr;
import com.tmesh.common.vo.product.AttrGroupWithAttrsVO;
import com.tmesh.common.vo.product.SpuItemAttrGroupVO;
import com.tmesh.tmeshmall.product.entity.AttrEntity;
import com.tmesh.tmeshmall.product.service.AttrService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tmesh.common.utils.PageUtils;
import com.tmesh.common.utils.Query;

import com.tmesh.tmeshmall.product.dao.AttrGroupDao;
import com.tmesh.tmeshmall.product.entity.AttrGroupEntity;
import com.tmesh.tmeshmall.product.service.AttrGroupService;

import org.apache.commons.lang.StringUtils;

@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Resource
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {

        //select * from pms_attr_group where catelog_id=? and (attr_group_id=key or attr_group_name like %key%)
        String key = (String) params.get("key");

        //构造QueryWrapper
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();

        if (!StringUtils.isEmpty(key)) {
            wrapper.and(obj -> obj.eq("attr_group_id", key).or().like("attr_group_name", key));
        }

        //如果传过来的三级分类id为0，就查询所有数据
        if (catelogId == 0) {
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    wrapper
            );
            return new PageUtils(page);
        } else {
            wrapper.eq("catelog_id", catelogId);
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
            return new PageUtils(page);
        }
    }

    /**
     * 根据分类id查询出所有的分组以及这些组里面的属性
     *
     * @param catelogId
     * @return
     */
    @Override
    public List<AttrGroupWithAttrsVO> getAttrGroupWithAttrsByCatelogId(Long catelogId) {

        //1、查询分组信息
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));

        //2、查询所有属性
        List<AttrGroupWithAttrsVO> collect = attrGroupEntities.stream().map(group -> {
            AttrGroupWithAttrsVO AttrGroupWithAttrsVO = new AttrGroupWithAttrsVO();
            BeanUtils.copyProperties(group, AttrGroupWithAttrsVO);

            List<AttrEntity> attrs = attrService.getRelationAttr(AttrGroupWithAttrsVO.getAttrGroupId());
            List<Attr> commonAttrs = attrs.stream().map(attrEntity -> {
                Attr commonAttr = new Attr();
                BeanUtils.copyProperties(attrEntity, commonAttr);
                return commonAttr;
            }).toList();

            AttrGroupWithAttrsVO.setAttrs(commonAttrs);

            return AttrGroupWithAttrsVO;
        }).toList();

        return collect;
    }

    @Override
    public List<SpuItemAttrGroupVO> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId) {

        //1、查出当前spu对应的所有属性的分组信息以及当前分组下的所有属性对应的值
        AttrGroupDao baseMapper = this.getBaseMapper();
        List<SpuItemAttrGroupVO> vos = baseMapper.getAttrGroupWithAttrsBySpuId(spuId, catalogId);

        return vos;
    }
}
