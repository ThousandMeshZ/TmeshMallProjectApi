package com.tmesh.tmeshmall.thirdparty.configuration;

import com.alibaba.nacos.common.utils.ConcurrentHashSet;
import com.tmesh.tmeshmall.thirdparty.common.AliUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Configuration
public class AliUtilsConfiguration {

    @Value("${spring.cloud.alicloud.oss.endpoint}")
    private String endpoint;
    
    @Value("${spring.cloud.alicloud.oss.point}")
    private String point;

    @Value("${spring.cloud.alicloud.oss.bucket}")
    private String bucket;

    @Value("${spring.cloud.alicloud.oss.callback-url:}")
    private String callbackUrl;
    
    @Value("${spring.cloud.alicloud.access-key}")
    private String accessKeyId;

    @Value("${spring.cloud.alicloud.secret-key}")
    private String accessKeySecret;
    
    @Bean(name = "aliUtils")
    public AliUtils getInstance() throws IllegalAccessException {
        Field[] fields = AliUtilsConfiguration.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            System.out.println(field.getName() + " : " + field.get(this));
        }
        Set<String> buckets = Arrays.stream(fields).filter(field -> field.getName().endsWith("bucket")).map(field -> {
            try {
                return field.get(this).toString();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toSet());
        AliUtils aliUtils = new AliUtils();
        aliUtils.setEndpoint(endpoint);
        aliUtils.setPoint(point);
        aliUtils.setBuckets(buckets);
        aliUtils.setAccessKeyId(accessKeyId);
        aliUtils.setAccessKeySecret(accessKeySecret);
        aliUtils.setCallbackUrl(callbackUrl);
        return aliUtils;
    }

}
