package com.tmesh.tmeshmall.order.service.impl;

import com.tmesh.common.entity.order.MqMessageEntity;
import com.tmesh.common.utils.PageUtils;
import com.tmesh.common.utils.Query;
import com.tmesh.tmeshmall.order.dao.MqMessageDao;
import com.tmesh.tmeshmall.order.service.MqMessageService;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


@Service("mqMessageService")
public class MqMessageServiceImpl extends ServiceImpl<MqMessageDao, MqMessageEntity> implements MqMessageService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MqMessageEntity> page = this.page(
                new Query<MqMessageEntity>().getPage(params),
                new QueryWrapper<MqMessageEntity>()
        );

        return new PageUtils(page);
    }

}