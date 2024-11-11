package com.tmesh.tmeshmall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tmesh.common.utils.Query;
import com.tmesh.common.utils.R;
import com.tmesh.common.vo.product.*;
import com.tmesh.tmeshmall.product.entity.SkuImagesEntity;
import com.tmesh.tmeshmall.product.entity.SkuInfoEntity;
import com.tmesh.tmeshmall.product.entity.SkuSaleAttrValueEntity;
import com.tmesh.tmeshmall.product.entity.SpuInfoDescEntity;
import com.tmesh.tmeshmall.product.feign.SeckillFeignService;
import com.tmesh.tmeshmall.product.service.*;
import jakarta.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tmesh.common.utils.PageUtils;

import com.tmesh.tmeshmall.product.dao.SkuInfoDao;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Resource
    private SkuImagesService skuImagesService;

    @Resource
    private SpuInfoDescService spuInfoDescService;

    @Resource
    private AttrGroupService attrGroupService;

    @Resource
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private SeckillFeignService seckillFeignService;

    @Resource
    private ThreadPoolExecutor executor;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageCondition(Map<String, Object> params) {

        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key) && !"0".equalsIgnoreCase(key)) {
            queryWrapper.and(qw -> 
                qw.eq("sku_id",key).or().like("sku_name",key)
            );
        }

        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            queryWrapper.eq("catalog_id",catelogId);
        }

        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            queryWrapper.eq("brand_id",brandId);
        }

        String min = (String) params.get("min");
        if (!StringUtils.isEmpty(min)) {
            queryWrapper.ge("price",min);
        }

        String max = (String) params.get("max");

        if (!StringUtils.isEmpty(max)) {
            try {
                BigDecimal bigDecimal = new BigDecimal(max);
                if (bigDecimal.compareTo(BigDecimal.ZERO) == 1) {
                    queryWrapper.le("price",max);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // key:
        // catelogId: 225
        // brandId: 9
        // min: 0
        // max: 0

        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("spu_id", spuId);
        List<SkuInfoEntity> skuInfoEntities = this.list(queryWrapper);

        return skuInfoEntities;
    }

    @Override
    public List<SkuInfoEntity> getSkusBySpuIds(List<Long> spuIds) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("spu_id", spuIds);
        List<SkuInfoEntity> skuInfoEntities = this.list(queryWrapper);

        return skuInfoEntities;
    }


    @Override
    public SkuItemVO item(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemVO result = new SkuItemVO();

        CompletableFuture<com.tmesh.common.entity.product.SkuInfoEntity> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            // 1.获取 sku 基本信息（pms_sku_info）【默认图片、标题、副标题、价格】
            SkuInfoEntity skuInfo = getById(skuId);
            com.tmesh.common.entity.product.SkuInfoEntity commonSkuInfo = new com.tmesh.common.entity.product.SkuInfoEntity();
            BeanUtils.copyProperties(skuInfo, commonSkuInfo);
            List<SkuSaleAttrValueEntity> list = this.skuSaleAttrValueService.getSkuSaleAttrValuesNameAndValueBySkuId(skuId);
            Map<String, String> attrValues =list.stream().collect(Collectors.toMap(SkuSaleAttrValueEntity::getAttrName, SkuSaleAttrValueEntity::getAttrValue));
            commonSkuInfo.setAttrValues(attrValues);
            result.setInfo(commonSkuInfo);
            return commonSkuInfo;
        }, executor);

        CompletableFuture<Void> imagesFuture = CompletableFuture.runAsync(() -> {
            // 2.获取 sku 图片信息（pms_sku_images）
            List<SkuImagesEntity> images = skuImagesService.getImagesBySkuId(skuId);
            List<com.tmesh.common.entity.product.SkuImagesEntity> commonImages = new ArrayList<>();
            BeanUtils.copyProperties(images, commonImages);
            result.setImages(commonImages);
        }, executor);

        CompletableFuture<Void> seckillSkuFuture = CompletableFuture.runAsync(() -> {
            // 3.查询当前商品是否参与秒杀优惠
            R r = seckillFeignService.getSkuSeckilInfo(skuId);
            if (r.getCode() == 0) {
                SeckillSkuVO seckillSku = r.getData(new TypeReference<SeckillSkuVO>() {
                });
                result.setSeckillSku(seckillSku);
            }
        }, executor);

        CompletableFuture<Void> saleAttrFuture = skuInfoFuture.thenAcceptAsync((skuInfo) -> {
            // 4.获取当前 sku 所属 spu 下的所有销售属性组合（pms_sku_info、pms_sku_sale_attr_value）
            List<SkuItemSaleAttrVO> saleAttr = skuSaleAttrValueService.getSaleAttrBySpuId(skuInfo.getSpuId());
            saleAttr.forEach(attr -> {
                String attrName = attr.getAttrName();
                List<AttrValueWithSkuIdVO> attrValues = attr.getAttrValues();
                attrValues.forEach(attrValue -> {
                    if (skuInfo.getAttrValues().containsKey(attrValue.getAttrValue())) {
                        attrValue.setSkuId(skuId);
                    } else {
                        List<String> skuIds = new ArrayList<>(Arrays.stream(attrValue.getSkuIds().split(",")).toList());
                        saleAttr.forEach(attr1 -> {
                           if (!attrName.equals(attr1.getAttrName())) {
                               attr1.getAttrValues().forEach(attrValue1 -> {
                                   if (attrValue1.getAttrValue().equals(skuInfo.getAttrValues().get(attr1.getAttrName()))) {
                                       List<String> list = new ArrayList<>(Arrays.stream(attrValue1.getSkuIds().split(",")).toList());
                                       skuIds.addAll(list);
                                   }
                               });
                           } 
                        });
                        // 使用Collectors.groupingBy来按元素分组，并计算每个元素的出现次数 
                        Map<String, Long> countMap = skuIds.stream().collect(Collectors.groupingBy(str -> str, Collectors.counting()));
                        // 找出出现次数最多的元素  
                        Optional<Map.Entry<String, Long>> mostFrequentElement = countMap.entrySet().stream()
                                .max(Map.Entry.comparingByValue());
                        //获取key
                        String mostId = mostFrequentElement.get().getKey();
                        attrValue.setSkuId(Long.valueOf(mostId));
                    }
                });

            });
            result.setSaleAttr(saleAttr);
        }, executor);

        CompletableFuture<Void> descFuture = skuInfoFuture.thenAcceptAsync((skuInfo) -> {
            // 5.获取 spu 商品介绍（pms_spu_info_desc）【描述图片】
            SpuInfoDescEntity desc = spuInfoDescService.getById(skuInfo.getSpuId());
            com.tmesh.common.entity.product.SpuInfoDescEntity commonDesc = new com.tmesh.common.entity.product.SpuInfoDescEntity();
            BeanUtils.copyProperties(desc, commonDesc);
            result.setDesc(commonDesc);
        }, executor);

        CompletableFuture<Void> groupAttrsFuture = skuInfoFuture.thenAcceptAsync((skuInfo) -> {
            // 6.获取 spu 规格参数信息（pms_product_attr_value、pms_attr_attrgroup_relation、pms_attr_group）
            List<SpuItemAttrGroupVO> groupAttrs = attrGroupService.getAttrGroupWithAttrsBySpuId(skuInfo.getSpuId(), skuInfo.getCatalogId());
            result.setGroupAttrs(groupAttrs);
        }, executor);

        // 等待所有任务都完成
        CompletableFuture.allOf(imagesFuture, saleAttrFuture, descFuture, groupAttrsFuture, seckillSkuFuture).get();

        return result;
    }

    @Override
    public List<SkuInfoEntity> getByIds(Collection<Long> skuIds) {
        return this.baseMapper.selectBatchIds(skuIds);
    }

    @Override
    public boolean uploadPic(SkuInfoEntity skuInfo) {
        UpdateWrapper<SkuInfoEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("sku_id", skuInfo.getSkuId());
        updateWrapper.set("sku_default_img", skuInfo.getSkuDefaultImg());
        boolean update = this.update(updateWrapper);
        return update;
    }

}