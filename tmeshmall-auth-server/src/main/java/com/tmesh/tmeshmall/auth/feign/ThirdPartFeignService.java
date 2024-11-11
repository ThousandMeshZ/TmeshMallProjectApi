package com.tmesh.tmeshmall.auth.feign;

import com.tmesh.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 第三方服务
 * @Author: TMesh
 * @Date: 2024/01/28 10:40
 */
@FeignClient("tmeshmall-third-party")
public interface ThirdPartFeignService {

    @GetMapping("/sms/sendcode")
    R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code);
}