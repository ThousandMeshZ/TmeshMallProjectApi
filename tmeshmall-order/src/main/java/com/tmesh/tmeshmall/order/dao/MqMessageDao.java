package com.tmesh.tmeshmall.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tmesh.common.entity.order.MqMessageEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * 
 * @author TMesh
 * @email lemon_wan@aliyun.com
 * @date 2021-09-02 22:57:46
 */
@Mapper
public interface MqMessageDao extends BaseMapper<MqMessageEntity> {
	
}
