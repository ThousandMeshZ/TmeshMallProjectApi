package com.tmesh.tmeshmall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tmesh.common.constant.ObjectConstant;
import com.tmesh.common.utils.PageUtils;
import com.tmesh.common.utils.Query;
import com.tmesh.tmeshmall.member.dao.MemberLevelDao;
import com.tmesh.tmeshmall.member.entity.MemberLevelEntity;
import com.tmesh.tmeshmall.member.service.MemberLevelService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("memberLevelService")
public class MemberLevelServiceImpl extends ServiceImpl<MemberLevelDao, MemberLevelEntity> implements MemberLevelService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberLevelEntity> page = this.page(
                new Query<MemberLevelEntity>().getPage(params),
                new QueryWrapper<MemberLevelEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查询默认等级
     */
    @Override
    public MemberLevelEntity getDefaultLevel() {
        return baseMapper.selectOne(new QueryWrapper<MemberLevelEntity>().eq("default_status", ObjectConstant.BooleanIntEnum.YES.getCode()));
    }
}