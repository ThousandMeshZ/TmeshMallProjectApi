package com.tmesh.tmeshmall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.tmesh.common.utils.EmptyUtils;
import com.tmesh.common.vo.product.Catalog2VO;
import com.tmesh.common.vo.product.CatalogNavVO;
import com.tmesh.common.vo.product.CatalogVO;
import com.tmesh.tmeshmall.product.entity.CategoryBrandRelationEntity;
import com.tmesh.tmeshmall.product.service.CategoryBrandRelationService;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tmesh.common.utils.PageUtils;
import com.tmesh.common.utils.Query;

import com.tmesh.tmeshmall.product.dao.CategoryDao;
import com.tmesh.tmeshmall.product.entity.CategoryEntity;
import com.tmesh.tmeshmall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.util.UriEncoder;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    private Map<String,Object> cache = new HashMap<>();

     @Resource
     private CategoryDao categoryDao;

    @Resource
    private CategoryBrandRelationService categoryBrandRelationService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redissonClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {

        //1、查询出所有分类
        List<CategoryEntity> entities = super.baseMapper.selectList(null);

        //2、组装成父子的树形结构

        //2.1)、找到所有一级分类
/*         List<CategoryEntity> levelMenus = entities.stream()
                .filter(e -> e.getParentCid() == 0)
                .map((menu) -> {
                    menu.setChildren(getChildrens(menu, entities));
                    return menu;
                })
                .sorted((menu, menu2) -> {
                    return (menu.getSort() == null ? 0 : menu.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
                })
                .toList(); */
        List<CategoryEntity> levelMenus = entities.stream()
//                .filter(e -> e.getParentCid() == 0)
                .filter(e -> e.getParentCid() == 0)
                .map(menu -> {
                    menu.setChildren(getChildrens(menu, entities));
                    return menu;
                })
                .sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort())))
                .toList();

        return levelMenus;
    }

    @Override
    public List<CatalogNavVO> listNavWithTree(Long catId) {

        List<CatalogNavVO> navMenus = new ArrayList<>();
            getNavChild(catId, navMenus);
        
        return navMenus;
    }

    //递归查找所有菜单的父菜单
    private void getNavChild(Long catId, List<CatalogNavVO> navMenus) {
        List<CategoryEntity> entities = this.categoryDao.getPeerByCatId(catId);

        CatalogNavVO[] currentCatalogNavVOs = {null};
        //2、组装成父子的树形结构
        List<CatalogNavVO> peerNavMenus = new ArrayList<>();
        
        //2.1)、找到所有一级分类
        entities.forEach(categoryEntity -> {

            if (categoryEntity.getCatId().equals(catId)) {
                CatalogNavVO catalogNavVO = new CatalogNavVO();
                BeanUtils.copyProperties(categoryEntity, catalogNavVO);
                currentCatalogNavVOs[0] = catalogNavVO;
                catalogNavVO.setParentB(true);
                String replace = replaceQueryString("catalog3Id", catalogNavVO.getCatId().toString());
                catalogNavVO.setLink("http://search.tmesh.cn/list.html?" + replace);
            } else {
                if (categoryEntity.getParentCid() != 0) {
                    CatalogNavVO catalogNavVO = new CatalogNavVO();
                    BeanUtils.copyProperties(categoryEntity, catalogNavVO);
                    catalogNavVO.setParentB(false);
                    String replace = replaceQueryString("catalog3Id", catalogNavVO.getCatId().toString());
                    catalogNavVO.setLink("http://search.tmesh.cn/list.html?" + replace);
                    peerNavMenus.add(catalogNavVO);
                }
            }
        });
        CatalogNavVO currentCatalogNavVO = currentCatalogNavVOs[0];
        currentCatalogNavVO.setPeerList(peerNavMenus);
        navMenus.add(0,currentCatalogNavVO);
        if (currentCatalogNavVO.getParentCid() != 0) {
            getNavChild(currentCatalogNavVO.getParentCid(), navMenus);
        }
    }


    @Override
    public List<Long> allChildrenList(Long catId) {
        Set<Long> set = new HashSet<>();
        set.add(catId);
        getAllChildrenList(catId, set);
        return set.stream().toList();
    }
    
    public void getAllChildrenList(Long catId, Set<Long> list) {
        List<Long> childrenCatIdList = this.categoryDao.getAllChildrenCatIdList(catId);
        if (EmptyUtils.isEmpty(childrenCatIdList)) {
            return;
        }
        list.addAll(childrenCatIdList);
        childrenCatIdList.forEach(childCatId -> {
            List<Long> childrenCatIdList2 = this.categoryDao.getAllChildrenCatIdList(catId);
            if (EmptyUtils.isNotEmpty(childrenCatIdList2)) {
                list.addAll(childrenCatIdList);
            }
        });
    }
    
    private String replaceQueryString(String key, String value) {
        // 解决编码问题，前端参数使用UTF-8编码了
        String encode = null;
        encode = UriEncoder.encode(value);
//                try {
//                    encode = URLEncoder.encode(attr, "UTF-8");// java将空格转义成了+号
//                    encode = encode.replace("+", "%20");// 浏览器将空格转义成了%20，差异化处理，否则_queryString与encode匹配失败
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
        String replace = key + "=" + encode;
        return replace;
    }
    
    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 检查当前删除的菜单，是否被别的地方引用
        List<CategoryBrandRelationEntity> categoryBrandRelation =
                categoryBrandRelationService.list(new QueryWrapper<CategoryBrandRelationEntity>().in("catelog_id", asList));

        if (categoryBrandRelation.isEmpty()) {
            //逻辑删除
            baseMapper.deleteBatchIds(asList);
        } else {
            throw new RuntimeException("该菜单下面还有属性，无法删除!");
        }
    }


    //递归查找所有菜单的子菜单
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {

/*         List<CategoryEntity> children = all.stream().filter(categoryEntity ->
            categoryEntity.getParentCid().equals(root.getCatId())
        ).map(categoryEntity -> {
            //1、找到子菜单(递归)
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu, menu2) -> {
            //2、菜单的排序
            return (menu.getSort() == null ? 0 : menu.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).toList();*/


    List<CategoryEntity> children = all.stream().filter(categoryEntity ->
                categoryEntity.getParentCid().equals(root.getCatId())
        ).map(categoryEntity -> {
            //1、找到子菜单(递归)
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted(
            //2、菜单的排序
                Comparator.comparingInt(menu -> menu.getSort() == null ? 0 : menu.getSort())
        ).toList();

        return children;

    }

    //[2,29,20]
    @Override
    public Long[] findCatelogPath(Long catelogId) {

        List<Long> parentPath = new ArrayList<>();

        //递归查询是否还有父节点
//        parentPath = findParentPath(catelogId, parentPath);
        findParentPath(catelogId, parentPath);

        //进行一个逆序排列
        Collections.reverse(parentPath);

        return (Long[]) parentPath.toArray(new Long[parentPath.size()]);
    }


    /**
     * 级联更新所有关联的数据
     *
     * @CacheEvict:失效模式
     * @CachePut:双写模式，需要有返回值
     * 1、同时进行多种缓存操作：@Caching
     * 2、指定删除某个分区下的所有数据 @CacheEvict(value = "category", allEntries = true)
     * 3、存储同一类型的数据，都可以指定为同一分区。分区名默认就是缓存的前缀
     * @param category
     */
    // @Caching(evict = {
    //         @CacheEvict(value = "category",key = "'getLevelOneCategorys'"),
    //         @CacheEvict(value = "category",key = "'getCatalogJson'")
    // })
    @CacheEvict(value = "category", allEntries = true)       //删除某个分区下的所有数据
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCascade(CategoryEntity category) {

        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("catalogJson-lock");
        //创建写锁
        RLock rLock = readWriteLock.writeLock();

        try {
            rLock.lock();
            this.baseMapper.updateById(category);
            categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }

        //同时修改缓存中的数据
        //删除缓存,等待下一次主动查询进行更新
    }


    /**
     * 1、每一个需要缓存的数据我们都来指定要放到那个名字的缓存。【缓存的分区(按照业务类型分)】
     * 2、@Cacheable(value = {"category"})
     *      代表当前方法的结果需要缓存。
     *      如果缓存中有，方法都不用调用。
     *      如果缓存中没有，会调用方法。
     *      最后将方法的结果放入缓存。
     * 3、默认行为
     *      1）、如果缓存中有，方法不再调用
     *      2）、key 是默认生成的:缓存的名字::SimpleKey::[](自主生成 key 值)
     *      3）、缓存的 value 值，默认使用 jdk 序列化机制，将序列化的数据存到 redis 中
     *      4）、默认 ttl 时间是 -1：
     *      自定义操作：
     *          1）、指定生成的缓存使用的 key：key 属性指定，接收一个 Spel
     *          2）、指定缓存的数据的存活时间:配置文档中修改存活时间
     *          3）、将数据保存为 json 格式
     * 4、Spring-Cache 的不足之处：
     *  1）、读模式
     *      缓存穿透：查询一个 null 数据。解决方案：缓存空数据
     *      缓存击穿：大量并发进来同时查询一个正好过期的数据。解决方案：加锁 ? 默认是无加锁的;使用 sync = true 来解决击穿问题
     *      缓存雪崩：大量的 key 同时过期。解决：加随机时间。加上过期时间
     *  2)、写模式：（缓存与数据库一致）
     *      1）、读写加锁。
     *      2）、引入 Canal,感知到 MySQL 的更新去更新 Redis
     *      3）、读多写多，直接去数据库查询就行
     *  总结：
     *      常规数据（读多写少，即时性，一致性要求不高的数据，完全可以使用 Spring-Cache）：写模式(只要缓存的数据有过期时间就足够了)
     *      特殊数据：特殊设计
     *  原理：
     *      CacheManager(RedisCacheManager)->Cache(RedisCache)->Cache 负责缓存的读写
     * @return
     */
    @Cacheable(value = {"category"},key = "#root.method.name",sync = true)
    @Override
    public List<CategoryEntity> getLevelOneCategorys() {
        System.out.println("getLevelOneCategorys........");
        long l = System.currentTimeMillis();
        List<CategoryEntity> categoryEntities = this.baseMapper.selectList(
                new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        System.out.println("消耗时间："+ (System.currentTimeMillis() - l));
        return categoryEntities;
    }


    @Cacheable(value = "category",key = "#root.method.name")
    @Override
    public Map<String, List<Catalog2VO>> getCatalogJson() {
        System.out.println("查询了数据库");

        //将数据库的多次查询变为一次
        List<CategoryEntity> selectList = this.baseMapper.selectList(null);

        //1、查出所有分类
        //1、1）查出所有一级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);

        //封装数据
        Map<String, List<Catalog2VO>> parentCid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //1、每一个的一级分类,查到这个一级分类的二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());

            //2、封装上面的结果
            List<Catalog2VO> Catalog2Vos = null;
            if (categoryEntities != null) {
                Catalog2Vos = categoryEntities.stream().map(l2 -> {
                    Catalog2VO Catalog2Vo = new Catalog2VO(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName().toString());

                    //1、找当前二级分类的三级分类封装成vo
                    List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());

                    if (level3Catelog != null) {
                        List<Catalog2VO.Catalog3VO> Catagory3Vos = level3Catelog.stream().map(l3 -> {
                            //2、封装成指定格式
                            Catalog2VO.Catalog3VO Catagory3Vo = new Catalog2VO.Catalog3VO(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());

                            return Catagory3Vo;
                        }).collect(Collectors.toList());
                        Catalog2Vo.setCatalog3List(Catagory3Vos);
                    }

                    return Catalog2Vo;
                }).toList();
            }
            return Catalog2Vos;
        }));

        return parentCid;
    }

    //TODO 产生堆外内存溢出OutOfDirectMemoryError:
    //1)、springboot2.0以后默认使用 lettuce 操作 redis 的客户端，它使用通信
    //2)、lettuce 的 bug 导致 netty 堆外内存溢出   可设置：-Dio.netty.maxDirectMemory
    //解决方案：不能直接使用-Dio.netty.maxDirectMemory 去调大堆外内存
    //1)、升级 lettuce 客户端。      2）、切换使用 jedis
     @Override
    public Map<String, List<Catalog2VO>> getCatalogJson2() {
        //给缓存中放 json 字符串，拿出的 json 字符串，反序列为能用的对象

        /**
         * 1、空结果缓存：解决缓存穿透问题
         * 2、设置过期时间(加随机值)：解决缓存雪崩
         * 3、加锁：解决缓存击穿问题
         */

        //1、加入缓存逻辑,缓存中存的数据是json字符串
        //JSON 跨语言。跨平台兼容。
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        String catalogJson = ops.get("catalogJson");
        if (StringUtils.isEmpty(catalogJson)) {
            System.out.println("缓存不命中...查询数据库...");
            //2、缓存中没有数据，查询数据库
            Map<String, List<Catalog2VO>> catalogJsonFromDb = getCatalogJsonFromDbWithRedissonLock();

            return catalogJsonFromDb;
        }

        System.out.println("缓存命中...直接返回...");
        //转为指定的对象
        Map<String, List<Catalog2VO>> result = JSON.parseObject(catalogJson,new TypeReference<Map<String, List<Catalog2VO>>>(){});

        return result;
    }
    
    @Override
    public CategoryEntity selectById(Long id, String[] selectList) {
        QueryWrapper<CategoryEntity> queryWrapper = new QueryWrapper<>();
        if (EmptyUtils.isNotEmpty(selectList)) {
            queryWrapper.select(selectList);
        }
        if (EmptyUtils.isNotEmpty(id)) {
            queryWrapper.eq("CAT_ID", id);
            return categoryDao.selectOne(queryWrapper);
        }
        return null;
    }

    /**
     * 查询三级分类并封装成Map返回
     * 使用SpringCache注解方式简化缓存设置
     */
    @Cacheable(value = {"category"}, key = "'getCatalogJson'", sync = true)
    @Override
    public Map<String, List<Catalog2VO>> getCatalogJsonWithSpringCache() {
        // 未命中缓存
        // 1.double check，占锁成功需要再次检查缓存（springcache使用本地锁）
        // 查询非空即返回
        String catlogJSON = stringRedisTemplate.opsForValue().get("getCatalogJson");
        if (!StringUtils.isEmpty(catlogJSON)) {
            // 查询成功直接返回不需要查询DB
            Map<String, List<Catalog2VO>> result = JSON.parseObject(catlogJSON, new TypeReference<Map<String, List<Catalog2VO>>>() {
            });
            return result;
        }

        // 2.查询所有分类，按照parentCid分组
        Map<Long, List<CategoryEntity>> categoryMap = baseMapper.selectList(null).stream()
                .collect(Collectors.groupingBy(key -> key.getParentCid()));

        // 3.获取1级分类
        List<CategoryEntity> level1Categorys = categoryMap.get(0L);

        // 4.封装数据
        Map<String, List<Catalog2VO>> result = level1Categorys.stream().collect(Collectors.toMap(key -> key.getCatId().toString(), l1Category -> {
            // 5.查询2级分类，并封装成List<Catalog2VO>
            List<Catalog2VO> catalog2VOS = categoryMap.get(l1Category.getCatId())
                    .stream().map(l2Category -> {
                        // 7.查询3级分类，并封装成List<Catalog3VO>
                        List<Catalog2VO.Catalog3VO> catalog3Vos = categoryMap.get(l2Category.getCatId())
                                .stream().map(l3Category -> {
                                    // 封装3级分类VO
                                    Catalog2VO.Catalog3VO catalog3VO = new Catalog2VO.Catalog3VO(l2Category.getCatId().toString(), l3Category.getCatId().toString(), l3Category.getName());
                                    return catalog3VO;
                                }).collect(Collectors.toList());
                        // 封装2级分类VO返回
                        Catalog2VO catalog2VO = new Catalog2VO(l1Category.getCatId().toString(), catalog3Vos, l2Category.getCatId().toString(), l2Category.getName());
                        return catalog2VO;
                    }).collect(Collectors.toList());
            return catalog2VOS;
        }));
        return result;
    }

    /**
     * 缓存里的数据如何和数据库的数据保持一致？？
     * 缓存数据一致性
     * 1)、双写模式
     * 2)、失效模式
     * @return
     */

    public Map<String, List<Catalog2VO>> getCatalogJsonFromDbWithRedissonLock() {

        //1、占分布式锁。去redis占坑
        //（锁的粒度，越细越快:具体缓存的是某个数据，11号商品） product-11-lock
        //RLock catalogJsonLock = redissonClient.getLock("catalogJson-lock");
        //创建读锁
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("catalogJson-lock");

        RLock rLock = readWriteLock.readLock();

        Map<String, List<Catalog2VO>> dataFromDb = null;
        try {
            rLock.lock();
            //加锁成功...执行业务
            dataFromDb = getDataFromDb();
        } finally {
            rLock.unlock();
        }
        //先去redis查询下保证当前的锁是自己的
        //获取值对比，对比成功删除=原子性 lua脚本解锁
        // String lockValue = stringRedisTemplate.opsForValue().get("lock");
        // if (uuid.equals(lockValue)) {
        //     //删除我自己的锁
        //     stringRedisTemplate.delete("lock");
        // }

        return dataFromDb;

    }


    /**
     * 从数据库查询并封装数据::分布式锁
     * @return
     */
    public Map<String, List<Catalog2VO>> getCatalogJsonFromDbWithRedisLock() {

        //1、占分布式锁。去redis占坑      设置过期时间必须和加锁是同步的，保证原子性（避免死锁）
        String uuid = UUID.randomUUID().toString();
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid,300, TimeUnit.SECONDS);
        if (lock) {
            System.out.println("获取分布式锁成功...");
            Map<String, List<Catalog2VO>> dataFromDb = null;
            try {
                //加锁成功...执行业务
                dataFromDb = getDataFromDb();
            } finally {
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

                //删除锁
                stringRedisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList("lock"), uuid);

            }
            //先去redis查询下保证当前的锁是自己的
            //获取值对比，对比成功删除=原子性 lua脚本解锁
            // String lockValue = stringRedisTemplate.opsForValue().get("lock");
            // if (uuid.equals(lockValue)) {
            //     //删除我自己的锁
            //     stringRedisTemplate.delete("lock");
            // }

            return dataFromDb;
        } else {
            System.out.println("获取分布式锁失败...等待重试...");
            //加锁失败...重试机制
            //休眠一百毫秒
            try { 
                TimeUnit.MILLISECONDS.sleep(100); 
            } catch (InterruptedException e) { 
                e.printStackTrace(); 
            }
            return getCatalogJsonFromDbWithRedisLock();     //自旋的方式
        }
    }

    private Map<String, List<Catalog2VO>> getDataFromDb() {
        //得到锁以后，我们应该再去缓存中确定一次，如果没有才需要继续查询
        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
        if (!StringUtils.isEmpty(catalogJson)) {
            //缓存不为空直接返回
            Map<String, List<Catalog2VO>> result = JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catalog2VO>>>() {
            });

            return result;
        }

        System.out.println("查询了数据库");

        /**
         * 将数据库的多次查询变为一次
         */
        List<CategoryEntity> selectList = this.baseMapper.selectList(null);

        //1、查出所有分类
        //1、1）查出所有一级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);

        //封装数据
        Map<String, List<Catalog2VO>> parentCid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //1、每一个的一级分类,查到这个一级分类的二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());

            //2、封装上面的结果
            List<Catalog2VO> Catalog2Vos = null;
            if (categoryEntities != null) {
                Catalog2Vos = categoryEntities.stream().map(l2 -> {
                    Catalog2VO Catalog2Vo = new Catalog2VO(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName().toString());

                    //1、找当前二级分类的三级分类封装成vo
                    List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());

                    if (level3Catelog != null) {
                        List<Catalog2VO.Catalog3VO> Catagory3Vos = level3Catelog.stream().map(l3 -> {
                            //2、封装成指定格式
                            Catalog2VO.Catalog3VO Catagory3Vo = new Catalog2VO.Catalog3VO(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());

                            return Catagory3Vo;
                        }).collect(Collectors.toList());
                        Catalog2Vo.setCatalog3List(Catagory3Vos);
                    }

                    return Catalog2Vo;
                }).collect(Collectors.toList());
            }

            return Catalog2Vos;
        }));

        //3、将查到的数据放入缓存,将对象转为json
        String valueJson = JSON.toJSONString(parentCid);
        stringRedisTemplate.opsForValue().set("catalogJson", valueJson, 1, TimeUnit.DAYS);

        return parentCid;
    }

    /**
     * 从数据库查询并封装数据::本地锁
     * @return
     */
    public Map<String, List<Catalog2VO>> getCatalogJsonFromDbWithLocalLock() {

        // //如果缓存中有就用缓存的
        // Map<String, List<Catalog2Vo>> catalogJson = (Map<String, List<Catalog2Vo>>) cache.get("catalogJson");
        // if (cache.get("catalogJson") == null) {
        //     //调用业务
        //     //返回数据又放入缓存
        // }

        //只要是同一把锁，就能锁住这个锁的所有线程
        //1、synchronized (this)：SpringBoot所有的组件在容器中都是单例的。
        //TODO 本地锁：synchronized，JUC（Lock),在分布式情况下，想要锁住所有，必须使用分布式锁
        synchronized (this) {

            //得到锁以后，我们应该再去缓存中确定一次，如果没有才需要继续查询
            return getDataFromDb();
        }


    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList,Long parentCid) {
        List<CategoryEntity> categoryEntities = selectList.stream()
                .filter(item -> item.getParentCid().equals(parentCid))
                .toList();
        return categoryEntities;
        // return this.baseMapper.selectList(
        //         new QueryWrapper<CategoryEntity>().eq("parent_cid", parentCid));
    }

//    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
    private void findParentPath(Long catelogId, List<Long> paths) {

        //1、收集当前节点id
        paths.add(catelogId);

        //根据当前分类id查询信息
        CategoryEntity byId = this.getById(catelogId);
        //如果当前不是父分类
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }

//        return paths;
    }

    @Cacheable(value = {"category"}, key = "'getAllCatalogJson'", sync = true)
    @Override
    public Map<String, List<CatalogVO>> getCatalogAllJsonWithSpringCache() {
        // 未命中缓存
        // 1.double check，占锁成功需要再次检查缓存（springcache使用本地锁）
        // 查询非空即返回
        String catlogJSON = stringRedisTemplate.opsForValue().get("getAllCatalogJson");
        if (!StringUtils.isEmpty(catlogJSON)) {
            // 查询成功直接返回不需要查询DB
            Map<String, List<CatalogVO>> result = JSON.parseObject(catlogJSON, new TypeReference<Map<String, List<CatalogVO>>>() {});
            if (result.containsKey("1")) {
                return result;
            }
        }

        //1、查询出所有分类
        List<CategoryEntity> entities = super.baseMapper.selectList(null);

        List<CatalogVO> list = entities.stream().map(category -> {
            CatalogVO catalogVO = new CatalogVO();
            BeanUtils.copyProperties(category, catalogVO);
            return catalogVO;
        }).toList();

        //2、组装成父子的树形结构
        List<CatalogVO> allcategory = list.stream()
                .filter(e -> e.getParentCid() == 0)
                .map(menu -> {
                    menu.setChildren(getChildrens(menu, list));
                    return menu;
                })
                .sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort())))
                .toList();

        Map<String, List<CatalogVO>> map = new HashMap<>();
        map.put("1", allcategory);
        return map;
    }

    private List<CatalogVO> getChildrens(CatalogVO root, List<CatalogVO> all) {

        List<CatalogVO> children = all.stream().filter(category ->
                category.getParentCid().equals(root.getCatId())
        ).map(category -> {
            //1、找到子菜单(递归)
            category.setChildren(getChildrens(category, all));
            return category;
        }).sorted(
                //2、菜单的排序
                Comparator.comparingInt(menu -> menu.getSort() == null ? 0 : menu.getSort())
        ).toList();

        return children;

    }

}