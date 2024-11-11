package com.tmesh.common.vo.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
public class CatalogNavVO implements Serializable {
    private static final long serialVersionUID = -1L;

    private Long catId;
    private String name;
    private Long parentCid;
    private Boolean parentB;
    private String link;

    @JsonManagedReference
    private List<CatalogNavVO> peerList;
}
