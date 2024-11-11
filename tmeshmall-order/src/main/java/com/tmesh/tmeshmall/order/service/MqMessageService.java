package com.tmesh.tmeshmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tmesh.common.entity.order.MqMessageEntity;
import com.tmesh.common.utils.PageUtils;

import java.util.Map;

/**
 * 
 *
 * @author TMesh
 * @email 1009191578@qq.com
 * @date 2024-01-02 22:57:46
 */
public interface MqMessageService extends IService<MqMessageEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

