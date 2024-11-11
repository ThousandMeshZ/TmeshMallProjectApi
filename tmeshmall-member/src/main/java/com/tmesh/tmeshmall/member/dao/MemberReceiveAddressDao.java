package com.tmesh.tmeshmall.member.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tmesh.common.vo.member.MemberReceiveAddressVO;
import com.tmesh.tmeshmall.member.entity.MemberReceiveAddressEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 会员收货地址
 * 
 * @author TMesh
 * @email 1009191578@qq.com
 */
@Mapper
public interface MemberReceiveAddressDao extends BaseMapper<MemberReceiveAddressEntity> {

	public List<MemberReceiveAddressVO> getMemberReceiveAddressByMemberIdForTable(@Param("memberId") Long memberId);

	public MemberReceiveAddressVO getMemberReceiveAddressByIdForTable(@Param("id") Long id);

	public Map<String, String> getAddressInfoFromAllAddress(@Param("id") Long id);
}
