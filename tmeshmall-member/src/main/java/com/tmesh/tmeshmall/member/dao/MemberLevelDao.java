package com.tmesh.tmeshmall.member.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tmesh.tmeshmall.member.entity.MemberLevelEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员等级
 * 
 * @author TMesh
 * @email 1009191578@qq.com
 */
@Mapper
public interface MemberLevelDao extends BaseMapper<MemberLevelEntity> {

    MemberLevelEntity getDefaultLevel();
}
