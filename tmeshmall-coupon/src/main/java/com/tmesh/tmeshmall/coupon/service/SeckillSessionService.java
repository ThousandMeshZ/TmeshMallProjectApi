package com.tmesh.tmeshmall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tmesh.common.utils.PageUtils;
import com.tmesh.tmeshmall.coupon.entity.SeckillSessionEntity;

import java.util.List;
import java.util.Map;

/**
 * 秒杀活动场次
 *
 * @author TMesh
 * @email 1009191578@qq.com

 */
public interface SeckillSessionService extends IService<SeckillSessionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查询最近三天需要秒杀的场次
     */
    List<SeckillSessionEntity> getLates3DaySession();
}

