package com.tmesh.tmeshmall.search.controller;

import com.alibaba.fastjson.TypeReference;
import com.tmesh.common.utils.R;
import com.tmesh.common.vo.product.Catalog2VO;
import com.tmesh.common.vo.product.CatalogVO;
import com.tmesh.common.vo.search.AttrResponseVo;
import com.tmesh.common.vo.search.SearchParam;
import com.tmesh.common.vo.search.SearchResult;
import com.tmesh.tmeshmall.search.feign.ProductFeignService;
import com.tmesh.tmeshmall.search.service.MallSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: TMesh
 * @createTime: 2024-02-18 15:07
 **/

@Controller
public class SearchController {

    @Autowired
    private MallSearchService mallSearchService;

    @Autowired
    private ProductFeignService productFeignService;

    /**
     * 自动将页面提交过来的所有请求参数封装成我们指定的对象
     * @param param
     * @return
     */
    @GetMapping(value = "/list.html")
    public String listPage(SearchParam param, Model model, HttpServletRequest request) {

        param.set_queryString(request.getQueryString());

        //1、根据传递来的页面的查询参数，去es中检索商品
        SearchResult result = mallSearchService.search(param);

        model.addAttribute("result",result);

        model.addAttribute("list",this.getAllCatalogJson());

        return "list";
    }

    @GetMapping(value = "/index/allcatalog.json")
    @ResponseBody
    public List<CatalogVO> getAllCatalogJson() {
        List<CatalogVO> list = new ArrayList<>();
        R r = productFeignService.allcatalog();
        if (r.getCode() == 0) {
            list = r.getData("data", new TypeReference<List<CatalogVO>>() {
            });
        }
        return list;
    }

}
