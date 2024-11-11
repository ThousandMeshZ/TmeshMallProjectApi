package com.tmesh.common.vo.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 2级分类VO
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Catalog2VO implements Serializable {
    private static final long serialVersionUID = -1L;

    private String catalog1Id;  // 1 级父分类 ID
    private List<Catalog3VO> catalog3List;// 3 级子分类集合
    private String id;  // 2 级分类 ID
    private String name;  // 2 级分类 name

    /**
     * 三级分类Vo
     */
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Catalog3VO implements Serializable {
        private static final long serialVersionUID = -1L;

        private String catalog2Id;  // 2 级父分类 ID
        private String id;  // 3 级分类 ID
        private String name;  // 3 级分类 name
    }
}
