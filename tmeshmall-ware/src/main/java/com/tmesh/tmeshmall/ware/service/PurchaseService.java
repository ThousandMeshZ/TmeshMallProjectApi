package com.tmesh.tmeshmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tmesh.common.utils.PageUtils;
import com.tmesh.common.vo.ware.MergeVO;
import com.tmesh.common.vo.ware.PurchaseDoneVO;
import com.tmesh.tmeshmall.ware.entity.PurchaseEntity;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author TMesh
 * @email 1009191578@qq.com
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查询未领取的采购单
     * @param params
     * @return
     */
    PageUtils queryPageUnreceive(Map<String, Object> params);

    /**
     * 合并采购需求
     * @param mergeVo
     */
    void mergePurchase(MergeVO mergeVo);

    /**
     * 领取采购单
     * @param ids
     */
    void received(List<Long> ids);

    /**
     * 完成采购单
     * @param doneVo
     */
    void done(PurchaseDoneVO doneVo);
}

