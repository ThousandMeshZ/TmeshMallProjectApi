package com.tmesh.tmeshmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tmesh.common.utils.PageUtils;
import com.tmesh.tmeshmall.ware.entity.WareOrderTaskDetailEntity;

import java.util.Map;

/**
 * 库存工作单
 *
 * @author TMesh
 * @email 1009191578@qq.com
 */
public interface WareOrderTaskDetailService extends IService<WareOrderTaskDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

