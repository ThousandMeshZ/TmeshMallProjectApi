package com.tmesh.common.vo.product;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class SpuItemAttrGroupVO {
    private String groupName;
    /** 
     * 属性列表
     * */
    private List<Attr> attrs;
}
