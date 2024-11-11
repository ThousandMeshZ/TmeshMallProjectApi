package com.tmesh.tmeshmall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tmesh.common.utils.DateUtils;
import com.tmesh.common.utils.PageUtils;
import com.tmesh.common.utils.Query;
import com.tmesh.tmeshmall.coupon.dao.SeckillSessionDao;
import com.tmesh.tmeshmall.coupon.entity.SeckillSessionEntity;
import com.tmesh.tmeshmall.coupon.entity.SeckillSkuRelationEntity;
import com.tmesh.tmeshmall.coupon.service.SeckillSessionService;
import com.tmesh.tmeshmall.coupon.service.SeckillSkuRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {

    @Autowired
    private SeckillSkuRelationService seckillSkuRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        QueryWrapper<SeckillSessionEntity> queryWrapper = new QueryWrapper<>();

        String key = (String) params.get("key");

        if (!StringUtils.isEmpty(key)) {
            queryWrapper.eq("id",key);
        }

        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    /**
     * 查询最近三天需要秒杀的场次+商品
     */
    @Override
    public List<SeckillSessionEntity> getLates3DaySession() {
        // 计算最近三天起止时间
        String startTime = DateUtils.currentStartTime();// 当天00:00:00
        String endTime = DateUtils.getTimeByOfferset(2);// 后天23:59:59

        // 查询起止时间内的秒杀场次
        List<SeckillSessionEntity> sessions = baseMapper.selectList(new QueryWrapper<SeckillSessionEntity>()
                .between("start_time", startTime, endTime));

        // 组合秒杀关联的商品信息
        if (!CollectionUtils.isEmpty(sessions)) {
            // 组合场次ID
            List<Long> sessionIds = sessions.stream().map(SeckillSessionEntity::getId).collect(Collectors.toList());
            // 查询秒杀场次关联商品信息
            Map<Long, List<SeckillSkuRelationEntity>> skuMap = seckillSkuRelationService
                    .list(new QueryWrapper<SeckillSkuRelationEntity>().in("promotion_session_id", sessionIds))
                    .stream().collect(Collectors.groupingBy(SeckillSkuRelationEntity::getPromotionSessionId));
            sessions.forEach(session -> session.setRelationSkus(skuMap.get(session.getId())));
        }
        return sessions;
    }

    /**
     * 当前时间
     * @return
     */
    private String startTime() {
        LocalDate now = LocalDate.now();
        LocalTime min = LocalTime.MIN;
        LocalDateTime start = LocalDateTime.of(now, min);

        //格式化时间
        String startFormat = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return startFormat;
    }

    /**
     * 结束时间
     * @return
     */
    private String endTime() {
        LocalDate now = LocalDate.now();
        LocalDate plus = now.plusDays(2);
        LocalTime max = LocalTime.MAX;
        LocalDateTime end = LocalDateTime.of(plus, max);

        //格式化时间
        String endFormat = end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return endFormat;
    }

    public static void main(String[] args) {
        // LocalDate now = LocalDate.now();
        // LocalDate plus = now.plusDays(2);
        // LocalDateTime now1 = LocalDateTime.now();
        // LocalTime now2 = LocalTime.now();
        //
        // LocalTime max = LocalTime.MAX;
        // LocalTime min = LocalTime.MIN;
        //
        // LocalDateTime start = LocalDateTime.of(now, min);
        // LocalDateTime end = LocalDateTime.of(plus, max);
        //
        // System.out.println(now);
        // System.out.println(now1);
        // System.out.println(now2);
        // System.out.println(plus);
        //
        // System.out.println(start);
        // System.out.println(end);

        // System.out.println(startTime());
        // System.out.println(endTime());
    }

}