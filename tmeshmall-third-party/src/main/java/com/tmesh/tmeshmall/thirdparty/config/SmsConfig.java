package com.tmesh.tmeshmall.thirdparty.config;

import com.tmesh.tmeshmall.thirdparty.service.impl.SmsServiceImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 短信配置类
 * @Author: TMesh
 * @Date: 2024/01/27 23:01
 */
@Configuration
public class SmsConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.cloud.alicloud.sms")
    public SmsServiceImpl smsService() {
        return new SmsServiceImpl();
    }

}