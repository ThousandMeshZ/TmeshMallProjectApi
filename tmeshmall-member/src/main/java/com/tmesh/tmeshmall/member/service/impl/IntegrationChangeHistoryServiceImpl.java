package com.tmesh.tmeshmall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tmesh.common.utils.PageUtils;
import com.tmesh.common.utils.Query;
import com.tmesh.tmeshmall.member.dao.IntegrationChangeHistoryDao;
import com.tmesh.tmeshmall.member.entity.IntegrationChangeHistoryEntity;
import com.tmesh.tmeshmall.member.service.IntegrationChangeHistoryService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("integrationChangeHistoryService")
public class IntegrationChangeHistoryServiceImpl extends ServiceImpl<IntegrationChangeHistoryDao, IntegrationChangeHistoryEntity> implements IntegrationChangeHistoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<IntegrationChangeHistoryEntity> page = this.page(
                new Query<IntegrationChangeHistoryEntity>().getPage(params),
                new QueryWrapper<IntegrationChangeHistoryEntity>()
        );

        return new PageUtils(page);
    }

}