package com.tmesh.tmeshmall.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/** 
* 1、想要远程调用别的服务
*   1）、引入 open-feign
*   2）、编写一个接口，告诉 SpringCloud 这个接口需要调用远程服务
*       1、声明接口的每个方法都是调用哪个远程服务的哪个请求
*   3）、开启远程调用功能
*  */
/**
 * 1、spring-session依赖
 * 2、spring-session配置
 * 3、LoginInterceptor拦截器
 */
// 开启session
@EnableRedisHttpSession
@MapperScan("com.tmesh.tmeshmall.member.dao")
@EnableFeignClients(basePackages = "com.tmesh.tmeshmall.member.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class TmeshMallMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(TmeshMallMemberApplication.class, args);
    }

}
