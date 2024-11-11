package com.tmesh.tmeshmall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.tmesh.common.constant.ProductConstant;
import com.tmesh.common.es.SkuEsModel;
import com.tmesh.common.to.product.SkuReductionTO;
import com.tmesh.common.to.product.SpuBoundTO;
import com.tmesh.common.utils.EmptyUtils;
import com.tmesh.common.vo.product.*;
import com.tmesh.common.vo.ware.SkuHasStockVO;
import com.tmesh.common.utils.R;
import com.tmesh.tmeshmall.product.entity.*;
import com.tmesh.tmeshmall.product.feign.CouponFeignService;
import com.tmesh.tmeshmall.product.feign.SearchFeignService;
import com.tmesh.tmeshmall.product.feign.WareFeignService;
import com.tmesh.tmeshmall.product.service.*;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tmesh.common.utils.PageUtils;
import com.tmesh.common.utils.Query;

import com.tmesh.tmeshmall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private WareFeignService wareFeignService;

    @Autowired
    private SearchFeignService searchFeignService;
    
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    /**
     *  //TODO：高级部分完善后续
     * @param vo 新增商品
     */
    @GlobalTransactional(rollbackFor = Exception.class)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveSpuInfo(SpuSaveVO vo) {

        //1、保存spu基本信息：pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo,spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);

        //2、保存spu的描述图片：pms_spu_info_desc
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",",decript));
        spuInfoDescService.saveSpuInfoDesc(spuInfoDescEntity);

        //3、保存spu的图片集：pms_spu_images
        List<String> images = vo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(),images);

        //4、保存spu的规格参数：pms_product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity valueEntity = new ProductAttrValueEntity();
            valueEntity.setAttrId(attr.getAttrId());

            //查询attr属性名
            AttrEntity byId = attrService.getById(attr.getAttrId());

            valueEntity.setAttrName(byId.getAttrName());
            valueEntity.setAttrValue(attr.getAttrValues());
            valueEntity.setQuickShow(attr.getShowDesc());
            valueEntity.setSpuId(spuInfoEntity.getId());
            return valueEntity;
        }).toList();
        productAttrValueService.saveProductAttr(collect);


        //5、保存spu的积分信息：tmeshmall_sms--->sms_spu_bounds
        Bounds bounds = vo.getBounds();
        SpuBoundTO spuBoundTo = new SpuBoundTO();
        BeanUtils.copyProperties(bounds,spuBoundTo);
        spuBoundTo.setSpuId(spuInfoEntity.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundTo);

        if (r.getCode() != 0) {
            log.error("远程保存spu积分信息失败");
        }

        //6、保存当前spu对应的所有sku信息：pms_sku_info
        //6、1）、sku的基本信息:pms_sku_info
        List<Skus> skus = vo.getSkus();
        if(skus!=null && !skus.isEmpty()){
            skus.forEach(item->{
                String defaultImg = "";
                for (Images image : item.getImages()) {
                    if(image.getDefaultImg() == 1){
                        defaultImg = image.getImgUrl();
                    }
                }

                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item,skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                skuInfoService.saveSkuInfo(skuInfoEntity);

                Long skuId = skuInfoEntity.getSkuId();

                List<SkuImagesEntity> imagesEntities = item.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(entity -> {
                    //返回true就是需要，false就是剔除
                    return !StringUtils.isEmpty(entity.getImgUrl());
                }).toList();

                //6、2）、sku的图片信息：pms_sku_images
                skuImagesService.saveBatch(imagesEntities);

                //6、3）、sku的销售属性：pms_sku_sale_attr_value
                List<Attr> attr = item.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map(a -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(a, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    return skuSaleAttrValueEntity;
                }).toList();

                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

                //6、4）、sku的优惠、满减等信息：tmeshmall_sms--->sms_sku_ladder、sms_sku_full_reduction、sms_member_price
                SkuReductionTO skuReductionTo = new SkuReductionTO();
                BeanUtils.copyProperties(item,skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(BigDecimal.ZERO) == 1) {
                    R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                    if (r1.getCode() != 0) {
                        log.error("远程保存sku积分信息失败");
                    }
                }
            });
        }

    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {

        this.baseMapper.insert(spuInfoEntity);

    }

    @Override
    public PageUtils queryPageByCondtion(Map<String, Object> params) {

        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and(eq -> 
                eq.eq("id",key).or().like("spu_name",key));
        }

        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
            queryWrapper.eq("publish_status",status);
        }

        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            queryWrapper.eq("brand_id",brandId);
        }

        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            queryWrapper.eq("catalog_id",catelogId);
        }

        IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(params), queryWrapper);

        return new PageUtils(page);
    }

    /**
     * @param spuId 
     */
    @GlobalTransactional(rollbackFor = Exception.class)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void up(Long spuId) {

        //1、查出当前 spuId 对应的所有 sku 信息,品牌的名字
        List<SkuInfoEntity> skuInfoEntities = skuInfoService.getSkusBySpuId(spuId);

        //TODO 4、查出当前 sku 的所有可以被用来检索的规格属性
        List<ProductAttrValueEntity> baseAttrs = productAttrValueService.baseAttrListforspu(spuId);

        List<Long> attrIds = baseAttrs.stream().map(ProductAttrValueEntity::getAttrId).toList();

        List<Long> searchAttrIds = attrService.selectSearchAttrs(attrIds);
        //转换为Set集合
        Set<Long> idSet = searchAttrIds.stream().collect(Collectors.toSet());

        List<SkuEsModel.Attrs> attrsList = baseAttrs.stream().
                filter(item -> idSet.contains(item.getAttrId())).
                map(item -> {
            SkuEsModel.Attrs attrs = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(item, attrs);
            return attrs;
        }).toList();

        List<Long> skuIdList = skuInfoEntities.stream()
                .map(SkuInfoEntity::getSkuId)
                .toList();
        //TODO 1、发送远程调用，库存系统查询是否有库存
        Map<Long, Boolean> stockMap = null;
        try {
            R skuHasStock = wareFeignService.getSkuHasStock(skuIdList);
            //
            TypeReference<List<SkuHasStockVO>> typeReference = new TypeReference<>() {};
            stockMap = skuHasStock.getData(typeReference).stream()
                    .collect(Collectors.toMap(SkuHasStockVO::getSkuId, SkuHasStockVO::getHasStock));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("库存服务查询异常：原因{}",e);
        }

        //2、封装每个 sku 的信息
        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel> collect = skuInfoEntities.stream().map(sku -> {
            //组装需要的数据
            SkuEsModel esModel = new SkuEsModel();
            esModel.setSkuPrice(sku.getPrice());
            esModel.setSkuImg(sku.getSkuDefaultImg());

            //设置库存信息
            if (finalStockMap == null) {
                esModel.setHasStock(true);
            } else {
                esModel.setHasStock(finalStockMap.get(sku.getSkuId()));
            }

            //TODO 2、热度评分。0
            esModel.setHotScore(0L);

            //TODO 3、查询品牌和分类的名字信息
            BrandEntity brandEntity = brandService.getById(sku.getBrandId());
            esModel.setBrandName(brandEntity.getName());
            esModel.setBrandId(brandEntity.getBrandId());
            esModel.setBrandImg(brandEntity.getLogo());

            CategoryEntity categoryEntity = categoryService.getById(sku.getCatalogId());
            esModel.setCatalogId(categoryEntity.getCatId());
            esModel.setCatalogName(categoryEntity.getName());

            //设置检索属性
            esModel.setAttrs(attrsList);

            BeanUtils.copyProperties(sku,esModel);

            return esModel;
        }).toList();

        //TODO 5、将数据发给 es 进行保存：tmeshmall-search
        R r = searchFeignService.productStatusUp(collect);

        if (r.getCode() == 0) {
            //远程调用成功
            //TODO 6、修改当前 spu 的状态
            this.baseMapper.updaSpuStatus(spuId, ProductConstant.ProductStatusEnum.SPU_UP.getCode());
        } else {
            //远程调用失败
            //TODO 7、重复调用？接口幂等性:重试机制
            // Feign调用流程
            /** 
             * 1、构造请求数据，将对象转为json；
             *      RequestTemplate template = buildTemplateFromArrgs.create(argv);
             * 2、发送请求进行执行（执行成功会解码响应数据）
             *      executeAndDecode(template);
             * 3、执行请求会有重试机制
             *      while(true) {
             *          try {
             *              executeAndDecode(template);
             *          } catch() {
             *              try(
             *                  retryer.continueOrPropagate(e);
             *              ) catch() {
             *                  throw ex;
             *              }
             *              continue;
             *          }
             *      }
             * */
        }
    }

    @Override
    public boolean checkEsUp() {
        R r;
        //1、查出所有已上架的 spuId 对应的所有 sku 信息,品牌的名字
        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("publish_status", ProductConstant.ProductStatusEnum.SPU_UP.getCode());
        List<SpuInfoEntity> spuInfoEntities = this.baseMapper.selectList(queryWrapper);
        if (EmptyUtils.isEmpty(spuInfoEntities))
            return true;
        List<SkuInfoEntity> allNoUpSkuInfoEntities = this.skuInfoService.getSkusBySpuIds(spuInfoEntities.stream().map(SpuInfoEntity::getId).toList());
        List<Long> skuIds = allNoUpSkuInfoEntities.stream().map(SkuInfoEntity::getSkuId).toList();
        r = this.searchFeignService.checkEsUp(skuIds);
        List<Long> upSkuIds;
        if (r.getCode() == 0) {
            //远程调用成功
            upSkuIds = r.getData("upSkuIds", new TypeReference<List<Long>>() {});
        } else {
            return false;
        }
        if (upSkuIds == null)
            return false;

        Map<Long, List<SkuInfoEntity>> noUpMap = allNoUpSkuInfoEntities.stream()
                .filter(skuInfoEntity -> !upSkuIds.contains(skuInfoEntity.getSkuId()))
                .collect(Collectors.groupingBy(SkuInfoEntity::getSpuId));
        if (EmptyUtils.isEmpty(noUpMap))
            return true;

        for (SpuInfoEntity spuInfoEntity : spuInfoEntities) {
            Long spuId = spuInfoEntity.getId();
            if (!noUpMap.containsKey(spuId))
                continue;
//            List<SkuInfoEntity> skuInfoEntities = skuInfoService.getSkusBySpuId(spuId);
            List<SkuInfoEntity> skuInfoEntities = noUpMap.get(spuId);
            //TODO 4、查出当前 sku 的所有可以被用来检索的规格属性
            List<ProductAttrValueEntity> baseAttrs = productAttrValueService.baseAttrListforspu(spuId);

            List<Long> attrIds = baseAttrs.stream().map(ProductAttrValueEntity::getAttrId).toList();

            List<Long> searchAttrIds = attrService.selectSearchAttrs(attrIds);
            //转换为Set集合
            Set<Long> idSet = searchAttrIds.stream().collect(Collectors.toSet());

            List<SkuEsModel.Attrs> attrsList = baseAttrs.stream().
                    filter(item -> idSet.contains(item.getAttrId())).
                    map(item -> {
                        SkuEsModel.Attrs attrs = new SkuEsModel.Attrs();
                        BeanUtils.copyProperties(item, attrs);
                        return attrs;
                    }).toList();

            List<Long> skuIdList = skuInfoEntities.stream()
                    .map(SkuInfoEntity::getSkuId)
                    .toList();
            //TODO 1、发送远程调用，库存系统查询是否有库存
            Map<Long, Boolean> stockMap = null;
            try {
                R skuHasStock = wareFeignService.getSkuHasStock(skuIdList);
                //
                TypeReference<List<SkuHasStockVO>> typeReference = new TypeReference<>() {};
                stockMap = skuHasStock.getData(typeReference).stream()
                        .collect(Collectors.toMap(SkuHasStockVO::getSkuId, SkuHasStockVO::getHasStock));
            } catch (Exception e) {
                e.printStackTrace();
                log.error("库存服务查询异常：原因{}",e);
            }

            //2、封装每个 sku 的信息
            Map<Long, Boolean> finalStockMap = stockMap;
            List<SkuEsModel> collect = skuInfoEntities.stream().map(sku -> {
                //组装需要的数据
                SkuEsModel esModel = new SkuEsModel();
                esModel.setSkuPrice(sku.getPrice());
                esModel.setSkuImg(sku.getSkuDefaultImg());

                //设置库存信息
                if (finalStockMap == null) {
                    esModel.setHasStock(true);
                } else {
                    esModel.setHasStock(finalStockMap.get(sku.getSkuId()));
                }

                //TODO 2、热度评分。0
                esModel.setHotScore(0L);

                //TODO 3、查询品牌和分类的名字信息
                BrandEntity brandEntity = brandService.getById(sku.getBrandId());
                esModel.setBrandName(brandEntity.getName());
                esModel.setBrandId(brandEntity.getBrandId());
                esModel.setBrandImg(brandEntity.getLogo());

                CategoryEntity categoryEntity = categoryService.getById(sku.getCatalogId());
                esModel.setCatalogId(categoryEntity.getCatId());
                esModel.setCatalogName(categoryEntity.getName());

                //设置检索属性
                esModel.setAttrs(attrsList);

                BeanUtils.copyProperties(sku,esModel);

                return esModel;
            }).toList();

            //TODO 5、将数据发给 es 进行保存：tmeshmall-search
            r = searchFeignService.productStatusUp(collect);

            if (r.getCode() == 0) {
                //远程调用成功
                //TODO 6、修改当前 spu 的状态
                this.baseMapper.updaSpuStatus(spuId, ProductConstant.ProductStatusEnum.SPU_UP.getCode());
            } else {
                //远程调用失败
                //TODO 7、重复调用？接口幂等性:重试机制
                // Feign调用流程
                /**
                 * 1、构造请求数据，将对象转为json；
                 *      RequestTemplate template = buildTemplateFromArrgs.create(argv);
                 * 2、发送请求进行执行（执行成功会解码响应数据）
                 *      executeAndDecode(template);
                 * 3、执行请求会有重试机制
                 *      while(true) {
                 *          try {
                 *              executeAndDecode(template);
                 *          } catch() {
                 *              try(
                 *                  retryer.continueOrPropagate(e);
                 *              ) catch() {
                 *                  throw ex;
                 *              }
                 *              continue;
                 *          }
                 *      }
                 * */
            }
        }
        return true;
    }

    /**
     * 根据skuId查询spu的信息
     * @param skuId
     * @return
     */
    @Override
    public SpuInfoEntity getSpuInfoBySkuId(Long skuId) {

        //先查询sku表里的数据
        SkuInfoEntity skuInfoEntity = skuInfoService.getById(skuId);

        //获得spuId
        Long spuId = skuInfoEntity.getSpuId();

        //再通过spuId查询spuInfo信息表里的数据
        SpuInfoEntity spuInfoEntity = this.baseMapper.selectById(spuId);

        //查询品牌表的数据获取品牌名
        BrandEntity brandEntity = brandService.getById(spuInfoEntity.getBrandId());
        spuInfoEntity.setBrandName(brandEntity.getName());

        return spuInfoEntity;
    }

}