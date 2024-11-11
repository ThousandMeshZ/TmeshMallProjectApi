package com.tmesh.tmeshmall.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.service.BestPayService;
import com.tmesh.common.constant.ObjectConstant;
import com.tmesh.common.constant.order.OrderConstant;
import com.tmesh.common.constant.order.PayConstant;
import com.tmesh.common.to.mq.SeckillOrderTo;
import com.tmesh.common.to.order.OrderTO;
import com.tmesh.common.utils.EmptyUtils;
import com.tmesh.common.vo.order.alipay.AliPayAsyncVO;
import com.tmesh.common.vo.ware.WareSkuLockVO;
import com.tmesh.tmeshmall.order.entity.OrderEntity;
import com.tmesh.common.exception.NoStockException;
import com.tmesh.common.exception.VerifyPriceException;
import com.tmesh.common.to.order.OrderCreateTO;
import com.tmesh.common.to.order.SpuInfoTO;
import com.tmesh.common.to.order.WareSkuLockTO;
import com.tmesh.common.to.ware.SkuHasStockTO;
import com.tmesh.common.utils.R;
import com.tmesh.common.vo.auth.MemberResponseVO;
import com.tmesh.common.vo.order.*;
import com.tmesh.tmeshmall.order.entity.OrderItemEntity;
import com.tmesh.tmeshmall.order.entity.PaymentInfoEntity;
import com.tmesh.tmeshmall.order.feign.CartFeignService;
import com.tmesh.tmeshmall.order.feign.MemberFeignService;
import com.tmesh.tmeshmall.order.feign.ProductFeignService;
import com.tmesh.tmeshmall.order.feign.WmsFeignService;
import com.tmesh.tmeshmall.order.interceptor.LoginUserInterceptor;
import com.tmesh.tmeshmall.order.service.PaymentInfoService;
import com.tmesh.tmeshmall.order.utils.TokenUtil;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tmesh.common.utils.PageUtils;
import com.tmesh.common.utils.Query;

import com.tmesh.tmeshmall.order.dao.OrderDao;
import com.tmesh.tmeshmall.order.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static com.tmesh.common.constant.cart.CartConstant.CART_PREFIX;
import static com.tmesh.common.constant.order.OrderConstant.USER_ORDER_TOKEN_PREFIX;


@Slf4j
@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    // 提交订单共享提交数据
    private ThreadLocal<OrderSubmitVO> confirmVoThreadLocal = new ThreadLocal<>();

    @Autowired
    MemberFeignService memberFeignService;
    @Autowired
    CartFeignService cartFeignService;
    @Autowired
    WmsFeignService wmsFeignService;
    @Autowired
    ProductFeignService productFeignService;
    @Autowired
    OrderItemServiceImpl orderItemService;
    @Autowired
    PaymentInfoService paymentInfoService;

    @Autowired
    BestPayService bestPayService;
    @Autowired
    ThreadPoolExecutor executor;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    TokenUtil tokenUtil;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 分页查询订单列表、订单详情
     */
    @Override
    public PageUtils queryPageWithItem(Map<String, Object> params) {
        // 获取登录用户
        MemberResponseVO member = LoginUserInterceptor.loginUser.get();

        // 查询订单
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
                        .eq("member_id", member.getId())
                        .orderByDesc("create_time"));
        // 查询订单项
        List<String> orderSns = page.getRecords().stream().map(order -> {
                    String receiverAddress = (EmptyUtils.isNotEmpty(order.getReceiverProvince()) && EmptyUtils.isNotEmpty(order.getReceiverCity()) && EmptyUtils.isNotEmpty(order.getReceiverRegion())) ? order.getReceiverProvince() + "\\" + order.getReceiverCity() + "\\" + order.getReceiverRegion() + "\\" + order.getReceiverDetailAddress() :
                            (EmptyUtils.isNotEmpty(order.getReceiverProvince()) && EmptyUtils.isNotEmpty(order.getReceiverCity()) && EmptyUtils.isEmpty(order.getReceiverRegion())) ? order.getReceiverProvince() + "\\" + order.getReceiverCity()  + "\\" + order.getReceiverDetailAddress() :
                                    (EmptyUtils.isNotEmpty(order.getReceiverProvince()) && EmptyUtils.isEmpty(order.getReceiverCity()) && EmptyUtils.isEmpty(order.getReceiverRegion())) ? order.getReceiverProvince()  + "\\" + order.getReceiverDetailAddress() :
                                            (EmptyUtils.isEmpty(order.getReceiverProvince()) && EmptyUtils.isEmpty(order.getReceiverCity()) && EmptyUtils.isEmpty(order.getReceiverRegion())) ? order.getReceiverDetailAddress() :
                                                    "";
                    order.setReceiverAddress(receiverAddress);
                    return order.getOrderSn();
                }
        ).collect(Collectors.toList());
        QueryWrapper<OrderItemEntity> queryWrapper = new QueryWrapper<>();
        Map<String, List<OrderItemEntity>> itemMap = new HashMap<>();
        if (EmptyUtils.isNotEmpty(orderSns)) {
            queryWrapper.in("order_sn", orderSns);
            List<OrderItemEntity> list = orderItemService.list();
            itemMap = list.stream().collect(Collectors.groupingBy(OrderItemEntity::getOrderSn));
        }

        // 遍历封装订单项
        Map<String, List<OrderItemEntity>> finalItemMap = itemMap;
        page.getRecords().forEach(order -> {
            if (EmptyUtils.isNotEmpty(finalItemMap.get(order.getOrderSn()))) {
                order.setSkuName(finalItemMap.get(order.getOrderSn()).get(0).getSkuName());
                order.setSkuPic(finalItemMap.get(order.getOrderSn()).get(0).getSkuPic());
            }
            order.setOrderItemEntityList(finalItemMap.get(order.getOrderSn()));
        });
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageWithItemForTable(Map<String, Object> params) {
        // 获取登录用户
        MemberResponseVO member = LoginUserInterceptor.loginUser.get();

        // 查询订单
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
                        .eq("member_id", member.getId())
                        .orderByDesc("create_time"));
        // 查询订单项
        List<String> orderSns = page.getRecords().stream().map(order -> {
            String receiverAddress = (EmptyUtils.isNotEmpty(order.getReceiverProvince()) && EmptyUtils.isNotEmpty(order.getReceiverCity()) && EmptyUtils.isNotEmpty(order.getReceiverRegion())) ? order.getReceiverProvince() + "\\" + order.getReceiverCity() + "\\" + order.getReceiverRegion() + "\\" + order.getReceiverDetailAddress() :
                    (EmptyUtils.isNotEmpty(order.getReceiverProvince()) && EmptyUtils.isNotEmpty(order.getReceiverCity()) && EmptyUtils.isEmpty(order.getReceiverRegion())) ? order.getReceiverProvince() + "\\" + order.getReceiverCity()  + "\\" + order.getReceiverDetailAddress() :
                            (EmptyUtils.isNotEmpty(order.getReceiverProvince()) && EmptyUtils.isEmpty(order.getReceiverCity()) && EmptyUtils.isEmpty(order.getReceiverRegion())) ? order.getReceiverProvince()  + "\\" + order.getReceiverDetailAddress() :
                                    (EmptyUtils.isEmpty(order.getReceiverProvince()) && EmptyUtils.isEmpty(order.getReceiverCity()) && EmptyUtils.isEmpty(order.getReceiverRegion())) ? order.getReceiverDetailAddress() : 
                                            "";
            order.setReceiverAddress(receiverAddress);
            return order.getOrderSn();
            }
        ).collect(Collectors.toList());
        QueryWrapper<OrderItemEntity> queryWrapper = new QueryWrapper<>();
        Map<String, List<OrderItemEntity>> itemMap = new HashMap<>();
        if (EmptyUtils.isNotEmpty(orderSns)) {
            queryWrapper.in("order_sn", orderSns);
            List<OrderItemEntity> list = orderItemService.list();
            itemMap = list.stream().collect(Collectors.groupingBy(OrderItemEntity::getOrderSn));
        }

        // 遍历封装订单项
        Map<String, List<OrderItemEntity>> finalItemMap = itemMap;
        page.getRecords().forEach(order -> {
            if (EmptyUtils.isNotEmpty(finalItemMap.get(order.getOrderSn()))) {
                order.setSkuName(finalItemMap.get(order.getOrderSn()).get(0).getSkuName());
                order.setSkuPic(finalItemMap.get(order.getOrderSn()).get(0).getSkuPic());
            }
            order.setOrderItemEntityList(finalItemMap.get(order.getOrderSn()));
        });

        return new PageUtils(page);
    }

    /**
     * 获取订单详情
     */
    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {
        return getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
    }

    /**
     * 获取结算页（confirm.html）VO数据
     */
    @Override
    public OrderConfirmVO OrderConfirmVO() throws Exception {
        OrderConfirmVO result = new OrderConfirmVO();
        // 获取当前用户
        MemberResponseVO member = LoginUserInterceptor.loginUser.get();

        // 获取当前线程上下文环境器
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        CompletableFuture<Void> addressFuture = CompletableFuture.runAsync(() -> {
            // 1.查询封装当前用户收货列表
            // 同步上下文环境器，解决异步无法从 ThreadLocal 获取 RequestAttributes
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<MemberAddressVO> address = memberFeignService.getAddress(member.getId());
            result.setMemberAddressVos(address);
        }, executor);

        CompletableFuture<Void> cartFuture = CompletableFuture.runAsync(() -> {
            // 2.查询购物车所有选中的商品
            // 同步上下文环境器，解决异步无法从 ThreadLocal 获取 RequestAttributes
            RequestContextHolder.setRequestAttributes(requestAttributes);
            // 请求头应该放入 TMESHSESSION（feign 请求会根据 requestInterceptors 构建请求头）
            List<OrderItemVO> items = cartFeignService.getCurrentCartItems(member.getId());
            result.setItems(items);
        }, executor).thenRunAsync(() -> {
            // 3.批量查询库存（有货/无货）
            List<Long> skuIds = result.getItems().stream().map(item -> item.getSkuId()).collect(Collectors.toList());
            R skuHasStock = wmsFeignService.getSkuHasStock(skuIds);
            List<SkuHasStockTO> skuHasStocks = skuHasStock.getData(new TypeReference<List<SkuHasStockTO>>() {
            });
            Map<Long, Boolean> stocks = skuHasStocks.stream().collect(Collectors.toMap(key -> key.getSkuId(), val -> val.getHasStock()));
            result.setStocks(stocks);
        });

        // 4.查询用户积分
        Integer integration = member.getIntegration();// 积分
        result.setIntegration(integration);

        // 5.金额数据自动计算

        // 6.防重令牌
        String token = tokenUtil.createToken();
        result.setUniqueToken(token);

        // 阻塞等待所有异步任务返回
        CompletableFuture.allOf(addressFuture, cartFuture).get();

        return result;
    }

    /**
     * 创建订单
     * GlobalTransactional：seata分布式事务，不适合高并发场景（默认基于AT实现）
     *
     * @param vo 收货地址、发票信息、使用的优惠券、备注、应付总额、令牌
     */
    @GlobalTransactional
    @Transactional
    @Override
    public SubmitOrderResponseVO submitOrder(OrderSubmitVO vo) throws Exception {

        confirmVoThreadLocal.set(vo);

        SubmitOrderResponseVO responseVo = new SubmitOrderResponseVO();
        //去创建、下订单、验令牌、验价格、锁定库存...

        //获取当前用户登录的信息
        MemberResponseVO memberResponseVo = LoginUserInterceptor.loginUser.get();
        responseVo.setCode(0);

        //1、验证令牌是否合法【令牌的对比和删除必须保证原子性】
        /* String script = "local redisKeys = redis.call('keys', KEYS[1]..'*'); for iter, value in ipairs(redisKeys) do if value == KEYS[1]..ARGV[1] then return redis.call('del', value );end;end;return 0";
        String orderToken = vo.getUniqueToken();

        //通过 lure 脚本原子验证令牌和删除令牌
        Long result = (Long) redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class),
                Arrays.asList(TokenUtil.IDEMPOTENT_TOKEN_PREFIX),
                orderToken); */
        Long result = 1L;
        if (result == 0L) {
            //令牌验证失败
            responseVo.setCode(1);
            return responseVo;
        } else {
            //令牌验证成功
            //1、创建订单、订单项等信息
            OrderCreateTO order = createOrder();

            //2、验证价格
            BigDecimal payAmount = order.getOrder().getPayAmount();
            BigDecimal payPrice = vo.getPayPrice();

            if (Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01) {
                //金额对比
                //TODO 3、保存订单
                saveOrder(order);

                //4、库存锁定,只要有异常，回滚订单数据
                //订单号、所有订单项信息(skuId, skuNum, skuName)
                
                WareSkuLockVO lockVo = new WareSkuLockVO();
                lockVo.setOrderSn(order.getOrder().getOrderSn());

                //获取出要锁定的商品数据信息
                List<OrderItemVO> orderItemVos = order.getOrderItems().stream().map((item) -> {
                    OrderItemVO orderItemVo = new OrderItemVO();
                    orderItemVo.setSkuId(item.getSkuId());
                    orderItemVo.setCount(item.getSkuQuantity());
                    orderItemVo.setTitle(item.getSkuName());
                    return orderItemVo;
                }).collect(Collectors.toList());
                lockVo.setLocks(orderItemVos);
                //TODO 4、远程所库存
                //库存成功了，但是网络原因超时了，订单回滚，库存不滚。

                
                //TODO 调用远程锁定库存的方法
                //出现的问题：扣减库存成功了，但是由于网络原因超时，出现异常，导致订单事务回滚，库存事务不回滚(解决方案：seata)
                //为了保证高并发，不推荐使用seata，因为是加锁，并行化，提升不了效率,可以发消息给库存服务

                // TODO 5.远程扣减积分
                R r = wmsFeignService.orderLockStock(lockVo);
                if (r.getCode() == 0) {
                    //锁定成功
                    responseVo.setOrder(order.getOrder());
                    // int i = 10/0;

                    //TODO 订单创建成功，发送消息给 MQ
                    // 6.发送创建订单到延时队列
                    rabbitTemplate.convertAndSend("order-event-exchange","order.create.order",order.getOrder());

                    //删除购物车里的数据
                    List<String> skuIds = order.getOrderItems().stream().map(item -> String.valueOf(item.getSkuId())).
                            collect(Collectors.toList());
                    cartFeignService.deleteItems(memberResponseVo.getId(), skuIds);
                    redisTemplate.delete(CART_PREFIX+memberResponseVo.getId());
                    return responseVo;
                } else {
                    //锁定失败
                    String msg = (String) r.get("msg");
                    throw new NoStockException(msg);
                    // responseVo.setCode(3);
                    // return responseVo;
                }

            } else {
                responseVo.setCode(2);
                return responseVo;
            }
        }
    }

    /**
     * 封装订单实体类对象
     * 订单 + 订单项
     */
    private OrderCreateTO createOrder() throws Exception {
        OrderCreateTO result = new OrderCreateTO();// 订单
        // 1.生成订单号
        String orderSn = IdWorker.getTimeId();
        // 2.生成订单实体对象
        com.tmesh.common.entity.order.OrderEntity orderEntity = buildOrder(orderSn);
        // 3.生成订单项实体对象
        List<com.tmesh.common.entity.order.OrderItemEntity> orderItemEntities = buildOrderItems(orderSn);
        // 4.汇总封装（封装订单价格[订单项价格之和]、封装订单积分、成长值[订单项积分、成长值之和]）
        summaryFillOrder(orderEntity, orderItemEntities);

        // 5.封装TO返回
        result.setOrder(orderEntity);
        result.setOrderItems(orderItemEntities);
        result.setFare(orderEntity.getFreightAmount());
        result.setPayPrice(orderEntity.getPayAmount());// 设置应付金额
        return result;
    }

    /**
     * 生成订单实体对象
     *
     * @param orderSn 订单号
     */
    private com.tmesh.common.entity.order.OrderEntity buildOrder(String orderSn) {
        com.tmesh.common.entity.order.OrderEntity orderEntity = new com.tmesh.common.entity.order.OrderEntity();// 订单实体类
        // 1.封装会员ID
        MemberResponseVO member = LoginUserInterceptor.loginUser.get();// 拦截器获取登录信息
        orderEntity.setMemberId(member.getId());
        // 2.封装订单号
        orderEntity.setOrderSn(orderSn);
        // 3.封装运费
        OrderSubmitVO orderSubmitVO = confirmVoThreadLocal.get();
        R fare = wmsFeignService.getFare(orderSubmitVO.getAddrId());// 获取地址
        FareVO fareVO = fare.getData(new TypeReference<FareVO>() {
        });
        orderEntity.setFreightAmount(fareVO.getFare());
        // 4.封装收货地址信息
        orderEntity.setReceiverName(fareVO.getAddress().getName());// 收货人名字
        orderEntity.setReceiverPhone(fareVO.getAddress().getPhone());// 收货人电话
        orderEntity.setReceiverProvince(fareVO.getAddress().getProvince());// 省
        orderEntity.setReceiverCity(fareVO.getAddress().getCity());// 市
        orderEntity.setReceiverRegion(fareVO.getAddress().getRegion());// 区
        orderEntity.setReceiverDetailAddress(fareVO.getAddress().getDetailAddress());// 详细地址
        orderEntity.setReceiverPostCode(fareVO.getAddress().getPostCode());// 收货人邮编
        // 5.封装订单状态信息
        orderEntity.setStatus(OrderConstant.OrderStatusEnum.CREATE_NEW.getCode());
        // 6.设置自动确认时间
        orderEntity.setAutoConfirmDay(OrderConstant.autoConfirmDay);// 7天
        // 7.设置未删除状态
        orderEntity.setDeleteStatus(ObjectConstant.BooleanIntEnum.NO.getCode());
        // 8.设置时间
        Date now = new Date();
        orderEntity.setCreateTime(now);
        orderEntity.setModifyTime(now);
        return orderEntity;
    }

    /**
     * 生成订单项实体对象
     * 购物车每项选中商品产生一个订单项
     */
    private List<com.tmesh.common.entity.order.OrderItemEntity> buildOrderItems(String orderSn) throws Exception {
        // 封装订单项（最后确定的价格，不会再改变）
        MemberResponseVO member = LoginUserInterceptor.loginUser.get();
        List<OrderItemVO> currentCartItems = cartFeignService.getCurrentCartItems(member.getId());// 获取当前用户购物车所有商品
        if (!CollectionUtils.isEmpty(currentCartItems)) {
            // 遍历购物车商品，循环构建每个订单项
            List<com.tmesh.common.entity.order.OrderItemEntity> itemEntities = currentCartItems.stream()
                    .filter(cartItem -> cartItem.getCheck())
                    .map(cartItem -> buildOrderItem(orderSn, cartItem))
                    .collect(Collectors.toList());
            return itemEntities;
        } else {
            throw new Exception();
        }
    }

    /**
     * 生成单个订单项实体对象
     */
    private com.tmesh.common.entity.order.OrderItemEntity buildOrderItem(String orderSn, OrderItemVO cartItem) {
        com.tmesh.common.entity.order.OrderItemEntity itemEntity = new com.tmesh.common.entity.order.OrderItemEntity();
        // 1.封装订单号
        itemEntity.setOrderSn(orderSn);
        // 2.封装SPU信息
        R spuInfo = productFeignService.getSpuInfoBySkuId(cartItem.getSkuId());// 查询SPU信息
        SpuInfoTO spuInfoTO = spuInfo.getData(new TypeReference<SpuInfoTO>() {
        });
        itemEntity.setSpuId(spuInfoTO.getId());
        itemEntity.setSpuName(spuInfoTO.getSpuName());
        itemEntity.setSpuBrand(spuInfoTO.getSpuName());
        itemEntity.setCategoryId(spuInfoTO.getCatalogId());
        // 3.封装SKU信息
        itemEntity.setSkuId(cartItem.getSkuId());
        itemEntity.setSkuName(cartItem.getTitle());
        itemEntity.setSkuPic(cartItem.getImage());// 商品sku图片
        itemEntity.setSkuPrice(cartItem.getPrice());// 这个是最新价格，购物车模块查询数据库得到
        itemEntity.setSkuQuantity(cartItem.getCount());// 当前商品数量
        String skuAttrsVals = String.join(";", cartItem.getSkuAttrValues());
        itemEntity.setSkuAttrsVals(skuAttrsVals);// 商品销售属性组合["颜色:星河银","版本:8GB+256GB"]
        // 4.优惠信息【不做】

        // 5.积分信息
        int num = cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount())).intValue();// 分值=单价*数量
        itemEntity.setGiftGrowth(num);// 成长值
        itemEntity.setGiftIntegration(num);// 积分

        // 6.价格信息
        itemEntity.setPromotionAmount(BigDecimal.ZERO);// 促销金额
        itemEntity.setCouponAmount(BigDecimal.ZERO);// 优惠券金额
        itemEntity.setIntegrationAmount(BigDecimal.ZERO);// 积分优惠金额
        BigDecimal realAmount = itemEntity.getSkuPrice().multiply(new BigDecimal(itemEntity.getSkuQuantity()))
                .subtract(itemEntity.getPromotionAmount())
                .subtract(itemEntity.getCouponAmount())
                .subtract(itemEntity.getIntegrationAmount());
        itemEntity.setRealAmount(realAmount);// 实际金额，减去所有优惠金额
        return itemEntity;
    }

    /**
     * 汇总封装订单
     * 1.计算订单总金额
     * 2.汇总积分、成长值
     * 3.汇总应付总额 = 订单总金额 + 运费
     *
     * @param orderEntity       订单
     * @param orderItemEntities 订单项
     */
    private void summaryFillOrder(com.tmesh.common.entity.order.OrderEntity orderEntity, List<com.tmesh.common.entity.order.OrderItemEntity> orderItemEntities) {
        // 1.订单总额、促销总金额、优惠券总金额、积分优惠总金额
        BigDecimal total = new BigDecimal(0);
        BigDecimal coupon = new BigDecimal(0);
        BigDecimal promotion = new BigDecimal(0);
        BigDecimal integration = new BigDecimal(0);
        // 2.积分、成长值
        Integer giftIntegration = 0;
        Integer giftGrowth = 0;
        for (com.tmesh.common.entity.order.OrderItemEntity itemEntity : orderItemEntities) {
            total = total.add(itemEntity.getRealAmount());// 订单总额
            coupon = coupon.add(itemEntity.getCouponAmount());// 促销总金额
            promotion = promotion.add(itemEntity.getPromotionAmount());// 优惠券总金额
            integration = integration.add(itemEntity.getIntegrationAmount());// 积分优惠总金额
            giftIntegration = giftIntegration + itemEntity.getGiftIntegration();// 积分
            giftGrowth = giftGrowth + itemEntity.getGiftGrowth();// 成长值
        }
        orderEntity.setTotalAmount(total);
        orderEntity.setCouponAmount(coupon);
        orderEntity.setPromotionAmount(promotion);
        orderEntity.setIntegrationAmount(integration);
        orderEntity.setIntegration(giftIntegration);// 积分
        orderEntity.setGrowth(giftGrowth);// 成长值

        // 3.应付总额
        orderEntity.setPayAmount(orderEntity.getTotalAmount().add(orderEntity.getFreightAmount()));// 订单总额 +　运费
    }

    /**
     * 保存订单
     * 将封装生成的订单对象 + 订单项对象持久化到DB
     *
     * @param order
     */
    private void saveOrder(OrderCreateTO order) {
        // 1.持久化订单对象
        OrderEntity orderEntity = new OrderEntity();
        BeanUtils.copyProperties(order.getOrder(), orderEntity);
        save(orderEntity);
        order.getOrder().setId(orderEntity.getId());

        // 2.持久化订单项对象
        order.getOrderItems().forEach(item -> item.setOrderId(order.getOrder().getId()));
        List<OrderItemEntity> itemEntities = new ArrayList<>();
        for (com.tmesh.common.entity.order.OrderItemEntity itemEntity : order.getOrderItems()) {
            itemEntity.setOrderId(orderEntity.getId());
            OrderItemEntity orderItem = new OrderItemEntity();
            BeanUtils.copyProperties(itemEntity, orderItem);
            itemEntities.add(orderItem);
        }
//        boolean batch = orderItemService.saveOrUpdateBatch(itemEntities);
        boolean batch = orderItemService.saveBatch(itemEntities);
        BeanUtils.copyProperties(itemEntities, order.getOrderItems());
    }

    /**
     * 关闭订单
     */
    @Override
    public void closeOrder(OrderEntity order) {
        OrderEntity _order = this.getById(order.getId());
        if (_order == null) {
            _order = this.getOrderByOrderSn(order.getOrderSn());
        }
        if (_order == null) {
            OrderTO orderTO = new OrderTO();
            BeanUtils.copyProperties(order, orderTO);
            rabbitTemplate.convertAndSend("order-event-exchange", "order.release.other", orderTO);
            log.error("订单不存在，" + JSON.toJSONString(order));
            return;
        }
        if (OrderConstant.OrderStatusEnum.CREATE_NEW.getCode().equals(_order.getStatus())) {
            // 待付款状态允许关单
            OrderEntity temp = new OrderEntity();
            temp.setId(_order.getId());
            temp.setStatus(OrderConstant.OrderStatusEnum.CANCLED.getCode());
            this.updateById(temp);

            try {
                // 发送消息给MQ
                OrderTO orderTO = new OrderTO();
                BeanUtils.copyProperties(_order, orderTO);
                //TODO 持久化消息到 mq_message 表中，并设置消息状态为 3-已抵达（保存日志记录）
                rabbitTemplate.convertAndSend("order-event-exchange", "order.release.other", orderTO);
            } catch (Exception e) {
                e.printStackTrace();
                // TODO 消息为抵达 Broker，修改 mq_message 消息状态为 2-错误抵达
            }
        }
    }

    /**
     * 获取订单支付的详细信息
     *
     * @param orderSn 订单号
     */
    @Override
    public PayVO getOrderPay(String orderSn) {
        // 查询订单
        OrderEntity order = this.getOrderByOrderSn(orderSn);
        // 查询所有订单项
        OrderItemEntity item = orderItemService.list(new QueryWrapper<OrderItemEntity>()
                .eq("order_sn", orderSn)).get(0);
        PayVO result = new PayVO();
        BigDecimal amount = order.getPayAmount().setScale(2, BigDecimal.ROUND_UP);// 总金额
        result.setTotal_amount(amount.toString());
        result.setOut_trade_no(orderSn);
        result.setSubject(item.getSkuName());
        result.setBody(item.getSkuAttrsVals());
        return result;
    }


    /**
     * 处理支付宝的支付结果
     * @param asyncVo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String handlePayResult(AliPayAsyncVO asyncVo) {

        //保存交易流水信息
        PaymentInfoEntity paymentInfo = paymentInfoService.getByOrderSn(asyncVo.getOut_trade_no());
        if (EmptyUtils.isEmpty(paymentInfo)) {
            paymentInfo = new PaymentInfoEntity();
            paymentInfo.setOrderSn(asyncVo.getOut_trade_no());
            paymentInfo.setAlipayTradeNo(asyncVo.getTrade_no());
            paymentInfo.setTotalAmount(new BigDecimal(asyncVo.getBuyer_pay_amount()));
            paymentInfo.setSubject(asyncVo.getBody());
            paymentInfo.setPaymentStatus(asyncVo.getTrade_status());
            paymentInfo.setCreateTime(new Date());
        } else {
            if (EmptyUtils.isNotEmpty(asyncVo.getNotify_time())) {
                paymentInfo.setCallbackTime(asyncVo.getNotify_time());
            }
            this.paymentInfoService.saveOrUpdate(paymentInfo);
            return "success"; 
        }
        //添加到数据库中
        this.paymentInfoService.saveOrUpdate(paymentInfo);
        
        //修改订单状态
        //获取当前状态
        String tradeStatus = asyncVo.getTrade_status();

        if (tradeStatus.equals("TRADE_SUCCESS") || tradeStatus.equals("TRADE_FINISHED")) {
            //支付成功状态
            String orderSn = asyncVo.getOut_trade_no(); //获取订单号
            this.updateOrderStatus(orderSn, OrderConstant.OrderStatusEnum.PAYED.getCode(), PayConstant.ALIPAY);
        }

        return "success";
    }

    /**
     * 处理支付回调
     *
     * @param targetOrderStatus 目标状态
     */
    @Override
    public void handlePayResult(Integer targetOrderStatus, Integer payCode, PaymentInfoEntity paymentInfo) {
        // 保存交易流水信息
        paymentInfoService.save(paymentInfo);

        // 修改订单状态
        if (OrderConstant.OrderStatusEnum.PAYED.getCode().equals(targetOrderStatus)) {
            // 支付成功状态
            String orderSn = paymentInfo.getOrderSn();
            baseMapper.updateOrderStatus(orderSn, targetOrderStatus, payCode);
        }
    }

    /**
     * 微信异步通知结果
     * @param notifyData
     * @return
     */
    @Override
    public String asyncNotify(String notifyData) {

        //签名效验
        PayResponse payResponse = bestPayService.asyncNotify(notifyData);
        log.info("payResponse={}",payResponse);

        //2.金额效验（从数据库查订单）
        OrderEntity orderEntity = this.getOrderByOrderSn(payResponse.getOrderId());

        //如果查询出来的数据是null的话
        //比较严重(正常情况下是不会发生的)发出告警：钉钉、短信
        if (orderEntity == null) {
            //TODO 发出告警，钉钉，短信
            throw new RuntimeException("通过订单编号查询出来的结果是null");
        }

        //判断订单状态状态是否为已支付或者是已取消,如果不是订单状态不是已支付状态
        Integer status = orderEntity.getStatus();
        if (status.equals(OrderConstant.OrderStatusEnum.PAYED.getCode()) || status.equals(OrderConstant.OrderStatusEnum.CANCLED.getCode())) {
            throw new RuntimeException("该订单已失效,orderNo=" + payResponse.getOrderId());
        }

        /*//判断金额是否一致,Double类型比较大小，精度问题不好控制
        if (orderEntity.getPayAmount().compareTo(BigDecimal.valueOf(payResponse.getOrderAmount())) != 0) {
            //TODO 告警
            throw new RuntimeException("异步通知中的金额和数据库里的不一致,orderNo=" + payResponse.getOrderId());
        }*/

        //3.修改订单支付状态
        //支付成功状态
        String orderSn = orderEntity.getOrderSn();
        this.updateOrderStatus(orderSn, OrderConstant.OrderStatusEnum.PAYED.getCode(), PayConstant.WXPAY);

        //4.告诉微信不要再重复通知了
        return "<xml>\n" +
                "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                "</xml>";
    }

    /**
     * 修改订单状态
     * @param orderSn
     * @param code
     */
    private void updateOrderStatus(String orderSn, Integer code,Integer payType) {

        this.baseMapper.updateOrderStatus(orderSn,code,payType);
    }

    /**
     * 创建秒杀订单
     * @param order 秒杀订单信息
     */
    @Override
    public void createSeckillOrder(SeckillOrderTo order) {
        // TODO 保存订单信息
        // 1.创建订单
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(order.getOrderSn());
        orderEntity.setMemberId(order.getMemberId());
        orderEntity.setCreateTime(new Date());
        BigDecimal totalPrice = order.getSeckillPrice().multiply(BigDecimal.valueOf(order.getNum()));// 应付总额
        orderEntity.setTotalAmount(totalPrice);// 订单总额
        orderEntity.setPayAmount(totalPrice);// 应付总额
        orderEntity.setStatus(OrderConstant.OrderStatusEnum.CREATE_NEW.getCode());
        // 保存订单
        this.save(orderEntity);

        // TODO 保存订单信息
        // 2.创建订单项信息
        OrderItemEntity orderItem = new OrderItemEntity();
        orderItem.setOrderSn(order.getOrderSn());
        orderItem.setRealAmount(totalPrice);
        orderItem.setSkuQuantity(order.getNum());
        
        // TODO 获取当前 sku 的详细信息进行设置 productFeignService.getSpuInfoBySkuId
        // 保存商品的spu信息
        R r = productFeignService.getSpuInfoBySkuId(order.getSkuId());
        SpuInfoTO spuInfo = r.getData(new TypeReference<SpuInfoTO>() {
        });
        orderItem.setSpuId(spuInfo.getId());
        orderItem.setSpuName(spuInfo.getSpuName());
        orderItem.setSpuBrand(spuInfo.getBrandName());
        orderItem.setCategoryId(spuInfo.getCatalogId());
        // 保存订单项数据
        orderItemService.save(orderItem);
    }
}