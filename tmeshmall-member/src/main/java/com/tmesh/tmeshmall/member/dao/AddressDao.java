package com.tmesh.tmeshmall.member.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tmesh.tmeshmall.member.entity.AddressEntity;
import com.tmesh.tmeshmall.member.entity.MemberEntity;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 会员
 * 
 * @author TMesh
 * @email 1009191578@qq.com
 */
@Mapper
public interface AddressDao extends BaseMapper<AddressEntity> {

    List<Map<String, Object>> getAllAddress();

    List<Map<String, Object>> getAddressByParent(@Param("parentId") Long parentId);
}
