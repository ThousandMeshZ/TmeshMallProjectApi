package com.tmesh.tmeshmall.order.feign;

import com.alipay.api.AlipayApiException;
import com.tmesh.common.vo.order.PayVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: TMesh
 * @createTime: 2024-01-08 21:14
 **/

@FeignClient("tmeshmall-third-party")
public interface ThridFeignService {

    @GetMapping(value = "/pay",consumes = "application/json")
    String pay(@RequestBody PayVO vo) throws AlipayApiException;

}
