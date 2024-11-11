package com.tmesh.tmeshmall.search.service;

import com.tmesh.common.vo.search.SearchParam;
import com.tmesh.common.vo.search.SearchResult;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: TMesh
 * @createTime: 2024-02-18 15:07
 **/
public interface MallSearchService {

    /**
     * @param param 检索的所有参数
     * @return  返回检索的结果，里面包含页面需要的所有信息
     */
    SearchResult search(SearchParam param);
}
