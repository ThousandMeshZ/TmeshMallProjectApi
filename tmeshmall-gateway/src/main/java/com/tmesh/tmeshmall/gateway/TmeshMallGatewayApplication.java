package com.tmesh.tmeshmall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/** 
*  1、开启服务的注册发现
*   （配置 nacos 的注册中心地址）
*  
*/
@EnableDiscoveryClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class TmeshMallGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(TmeshMallGatewayApplication.class, args);
    }

}
