package com.tmesh.common.vo.ware;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: TMesh
 **/

@Data
public class MergeVO {

    private Long purchaseId;// 采购单ID
    private List<Long> items;// 采购需求ID

}
