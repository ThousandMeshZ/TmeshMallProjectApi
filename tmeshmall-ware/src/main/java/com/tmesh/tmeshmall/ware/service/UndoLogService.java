package com.tmesh.tmeshmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tmesh.common.utils.PageUtils;
import com.tmesh.tmeshmall.ware.entity.UndoLogEntity;

import java.util.Map;

/**
 * 
 *
 * @author TMesh
 * @email 1009191578@qq.com
 */
public interface UndoLogService extends IService<UndoLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

