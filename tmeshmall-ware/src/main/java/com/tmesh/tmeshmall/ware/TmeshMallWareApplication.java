package com.tmesh.tmeshmall.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

// 开启rabbit
@EnableRabbit
// 开启feign
@EnableFeignClients(basePackages = "com.tmesh.tmeshmall.ware.feign")
// 开启服务注册功能
@EnableDiscoveryClient
@MapperScan("com.tmesh.tmeshmall.ware.dao")
@SpringBootApplication
public class TmeshMallWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(TmeshMallWareApplication.class, args);
    }

}
