package com.tmesh.common.vo.search;

import com.tmesh.common.es.SkuEsModel;
import com.tmesh.common.vo.product.CatalogNavVO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SearchResult {

    private List<SkuEsModel> product;// es 检索到的所有商品信息

    /**
     * 分页信息
     */
    private Integer pageNum;// 当前页码
    private Long total;// 总记录数
    private Integer totalPages;// 总页码
    private List<Integer> pageNavs;// 导航页码[1、2、3、4、5]

    private List<BrandVo> brands;// 当前查询到的结果所有涉及到的品牌
    private List<CatalogVo> catalogs;// 当前查询到的结果所有涉及到的分类
    /**
     * attrs=1_anzhuo&attrs=5_其他:1080P
     */
    private List<AttrVo> attrs = new ArrayList<>();// 当前查询到的结果所有涉及到的属性【符合检索条件的，可检索的属性】


    // ============================以上是要返回的数据====================================

    // 面包屑导航数据
    private List<NavVo> navs = new ArrayList<>();
    private List<CatalogNavVO> catalogNavs = new ArrayList<>();

    // 封装筛选条件中的属性id集合【用于面包屑，选择属性后出现在面包屑中，下面的属性栏则隐藏】
    // 该字段是提供前端用的
    private List<Long> attrIds = new ArrayList<>();


    /**
     * 面包屑导航VO
     */
    @Data
    public static class NavVo {
        private String navName;// 属性名
        private String navValue;// 属性值
        private String link;// 回退地址（删除该面包屑筛选条件回退地址）
    }

    @Data
    public static class BrandVo {
        private Long brandId;// 品牌 id
        private String brandName;// 品牌名
        private String brandImg;//  品牌图片
    }

    @Data
    public static class CatalogVo {
        private Long catalogId;// 分类 id
        private String catalogName;// 分类名
    }

    @Data
    public static class CatalogVo1 {
        private Long catalogId;// 分类 id
        private String catalogName;// 分类名
        private List<CatalogVo> allCatalog = new ArrayList<>(); // 同级分类
    }
    
    @Data
    public static class CatalogVo2 {
        private Long catalogId;// 分类 id
        private String catalogName;// 分类名
        private List<CatalogVo> allCatalog = new ArrayList<>(); // 同级分类
    }
    
    @Data
    public static class CatalogVo3 {
        private Long catalogId;// 分类 id
        private String catalogName;// 分类名
        private List<CatalogVo> allCatalog = new ArrayList<>(); // 同级分类
    }

    @Data
    public static class AttrVo {
        private Long attrId;// 允许检索的 属性Id
        private String attrName;// 允许检索的 属性名
        private List<String> attrValue;// 属性值【多个】
    }
}
