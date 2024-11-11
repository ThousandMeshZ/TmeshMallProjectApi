package com.tmesh.tmeshmall.thirdparty.service;

/**
 * 短信服务
 * @Author: TMesh
 * @Date: 2024/01/27 22:58
 */
public interface SmsService {

    /**
     * 发送短信验证码
     * @param phone 电话号码
     * @param code  验证码
     */
    public Boolean sendCode(String phone, String code);

}