//package com.tmesh.tmeshmall.product.configuration;
//
//import com.tmesh.common.utils.AliUtils;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class AliUtilsConfiguration {
//
//    @Value("${spring.cloud.alicloud.oss.endpoint}")
//    private String endpoint;
//
//    @Value("${spring.cloud.alicloud.oss.bucket}")
//    private String bucket;
//
//    @Value("${spring.cloud.alicloud.access-key}")
//    private String accessKeyId;
//
//    @Value("${spring.cloud.alicloud.secret-key}")
//    private String accessKeySecret;
//    
//    @Bean(name = "aliUtils")
//    public AliUtils getInstance() {
//        AliUtils aliUtils = new AliUtils();
//        aliUtils.setEndpoint(endpoint);
//        aliUtils.setBucket(bucket);
//        aliUtils.setAccessKeyID(accessKeyId);
//        aliUtils.setAccessKeySecret(accessKeySecret);
//        return aliUtils;
//    }
//
//}
