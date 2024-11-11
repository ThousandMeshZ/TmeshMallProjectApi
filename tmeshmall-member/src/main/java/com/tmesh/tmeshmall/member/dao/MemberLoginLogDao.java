package com.tmesh.tmeshmall.member.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tmesh.tmeshmall.member.entity.MemberLoginLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员登录记录
 * 
 * @author TMesh
 * @email 1009191578@qq.com
 */
@Mapper
public interface MemberLoginLogDao extends BaseMapper<MemberLoginLogEntity> {
	
}
