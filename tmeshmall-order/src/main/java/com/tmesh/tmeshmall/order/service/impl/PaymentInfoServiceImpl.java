package com.tmesh.tmeshmall.order.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tmesh.common.utils.PageUtils;
import com.tmesh.common.utils.Query;

import com.tmesh.tmeshmall.order.dao.PaymentInfoDao;
import com.tmesh.tmeshmall.order.entity.PaymentInfoEntity;
import com.tmesh.tmeshmall.order.service.PaymentInfoService;


@Service("paymentInfoService")
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoDao, PaymentInfoEntity> implements PaymentInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PaymentInfoEntity> page = this.page(
                new Query<PaymentInfoEntity>().getPage(params),
                new QueryWrapper<PaymentInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PaymentInfoEntity getByOrderSn(String orderSn) {
        QueryWrapper<PaymentInfoEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_sn", orderSn);
        return this.getOne(queryWrapper);
    }

}