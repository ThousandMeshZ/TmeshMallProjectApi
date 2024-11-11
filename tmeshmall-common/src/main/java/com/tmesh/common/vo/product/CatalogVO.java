package com.tmesh.common.vo.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 2级分类VO
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CatalogVO implements Serializable {
    private static final long serialVersionUID = -1L;

    private Long parentCid;  // 父分类 ID
    private List<CatalogVO> children = new ArrayList<>();// 子分类集合
    private List<CatalogVO> cj = new ArrayList<>();// 子分类集合
    private Long catId;  // 分类 ID
    private String name;  // 分类 name
    private Integer catLevel; // 层级
    private Integer sort;

}
