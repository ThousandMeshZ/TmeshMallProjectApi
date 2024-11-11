package com.tmesh.common.to.ware;

import lombok.Data;

/**
 * TODO 废弃
 * 库存锁定结果，每一个Item一个结果
 *
 * @author: TMesh
 */
@Data
public class LockStockResultTO {
    private Long skuId;
    private Integer num;
    /**
     * 是否锁定成功
     **/
    private Boolean locked;
}
