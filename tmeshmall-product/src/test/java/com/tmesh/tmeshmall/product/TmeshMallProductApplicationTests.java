package com.tmesh.tmeshmall.product;

import com.tmesh.common.utils.AliUtils;
import com.tmesh.common.vo.product.SkuItemSaleAttrVO;
import com.tmesh.common.vo.product.SpuItemAttrGroupVO;
import com.tmesh.tmeshmall.product.dao.AttrGroupDao;
import com.tmesh.tmeshmall.product.dao.SkuSaleAttrValueDao;
import com.tmesh.tmeshmall.product.entity.BrandEntity;
import com.tmesh.tmeshmall.product.service.BrandService;
import com.tmesh.tmeshmall.product.service.CategoryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
class TmeshMallProductApplicationTests {

    @Resource
    private BrandService brandService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Resource
    private AttrGroupDao attrGroupDao;

    @Resource
    private SkuSaleAttrValueDao skuSaleAttrValueDao;

    @Autowired
    AliUtils aliUtils;
    
    @Test
    void contextLoads() {
        BrandEntity brand = new BrandEntity();
        brand.setBrandId(1L);
        brand.setDescript("话威");
        boolean updated = brandService.updateById(brand);
        System.out.println(updated);
    }
    
    @Test
    void alicloudOssUpload() throws FileNotFoundException {
        // 创建PutObjectRequest对象:参数 bucket 和 文件名
        System.out.println(new AliUtils().getBucket());
        System.out.println(aliUtils.getBucket());
    }

    @Test
    public void test1() {
        List<SkuItemSaleAttrVO> saleAttrBySpuId = skuSaleAttrValueDao.getSaleAttrBySpuId(13L);
        saleAttrBySpuId.forEach(System.out::println);
    }

    @Test
    public void test() {
        List<SpuItemAttrGroupVO> attrGroupWithAttrsBySpuId = attrGroupDao.getAttrGroupWithAttrsBySpuId(130L, 225L);
        attrGroupWithAttrsBySpuId.forEach(System.out::println);
    }

    @Test
    public void testRedisson() {
        System.out.println(redissonClient);
    }

    @Test
    public void testStringRedis() {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();

        //保存
        ops.set("hello","world_" + UUID.randomUUID().toString());
        //查询
        String hello = ops.get("hello");
        System.out.println("之前保存的数据:"+hello);
    }

    @Test
    public void testFindPath() {
        Long[] catelogPath = categoryService.findCatelogPath(225l);

        log.info("完整路径catelogPath={}", Arrays.asList(catelogPath));
    }

    @Test
    public void contextLoads1() {
        System.out.println(Integer.MAX_VALUE);

    }
    
}
