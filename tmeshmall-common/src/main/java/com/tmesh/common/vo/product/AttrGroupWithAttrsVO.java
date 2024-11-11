package com.tmesh.common.vo.product;

import lombok.Data;

import java.util.List;

@Data
public class AttrGroupWithAttrsVO {
    /**
     * 分组id
     */
    private Long attrGroupId;
    /**
     * 分组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 分组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;

    private List<Attr> attrs;

}
