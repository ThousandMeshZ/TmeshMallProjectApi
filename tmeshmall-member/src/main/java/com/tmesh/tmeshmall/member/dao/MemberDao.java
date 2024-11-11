package com.tmesh.tmeshmall.member.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tmesh.tmeshmall.member.entity.MemberEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author TMesh
 * @email 1009191578@qq.com
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
