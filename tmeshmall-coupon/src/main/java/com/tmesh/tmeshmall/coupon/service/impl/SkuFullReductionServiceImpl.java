package com.tmesh.tmeshmall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tmesh.common.to.product.SkuReductionTO;
import com.tmesh.common.utils.PageUtils;
import com.tmesh.common.utils.Query;
import com.tmesh.common.vo.product.MemberPrice;
import com.tmesh.tmeshmall.coupon.dao.SkuFullReductionDao;
import com.tmesh.tmeshmall.coupon.entity.MemberPriceEntity;
import com.tmesh.tmeshmall.coupon.entity.SkuFullReductionEntity;
import com.tmesh.tmeshmall.coupon.entity.SkuLadderEntity;
import com.tmesh.tmeshmall.coupon.service.MemberPriceService;
import com.tmesh.tmeshmall.coupon.service.SkuFullReductionService;
import com.tmesh.tmeshmall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    private SkuLadderService skuLadderService;

    @Autowired
    private MemberPriceService memberPriceService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        QueryWrapper<SkuFullReductionEntity> queryWrapper = new QueryWrapper<SkuFullReductionEntity>();

        String key = (String) params.get("key");

        if (!StringUtils.isEmpty(key)) {
            queryWrapper.eq("id",key).or().eq("sku_id",key);
        }

        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    /**
     * 新增满减信息（发布商品）
     */
    @Override
    public void saveSkuReduction(SkuReductionTO reductionTo) {
        // 1.sku的打折（买几件打几折）sms_sku_ladder【剔除满减信息为0的】
        //1、1）、sku的优惠、满减等信息：tmeshmall_sms--->sms_sku_ladder、sms_sku_full_reduction、sms_member_price
        SkuLadderEntity skuLadder = new SkuLadderEntity();
        BeanUtils.copyProperties(reductionTo, skuLadder);
        skuLadder.setAddOther(reductionTo.getCountStatus());
        if (reductionTo.getFullCount() > 0) {
            skuLadderService.save(skuLadder);
        }

        // 2.满减信息（满多少减多少）sms_sku_full_reduction【剔除满减信息为0的】
        SkuFullReductionEntity skuFullReduction = new SkuFullReductionEntity();
        BeanUtils.copyProperties(reductionTo, skuFullReduction);
        skuFullReduction.setAddOther(reductionTo.getPriceStatus());
        if (reductionTo.getFullPrice().compareTo(BigDecimal.ZERO) == 1) {
            this.save(skuFullReduction);
        }

        // 3.会员价格：sms_member_price【剔除会员价格为0的数据】
        List<MemberPrice> memberPrices = reductionTo.getMemberPrice();
        List<MemberPriceEntity> memberPriceEntities = memberPrices.stream().
                filter(memberPriceVo -> memberPriceVo.getPrice().compareTo(BigDecimal.ZERO) == 1).
                map(memberPriceVo -> {
                    MemberPriceEntity memberPrice = new MemberPriceEntity();
                    memberPrice.setSkuId(reductionTo.getSkuId());
                    memberPrice.setMemberLevelId(memberPriceVo.getId());
                    memberPrice.setMemberLevelName(memberPriceVo.getName());
                    memberPrice.setMemberPrice(memberPriceVo.getPrice());
                    memberPrice.setAddOther(1);

                    return memberPrice;
                }).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(memberPriceEntities)) {
            memberPriceService.saveBatch(memberPriceEntities);
        }
    }

}