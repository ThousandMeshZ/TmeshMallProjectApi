package com.tmesh.tmeshmall.thirdparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class TmeshMallThirdPartyApplication {

    public static void main(String[] args) {
        SpringApplication.run(TmeshMallThirdPartyApplication.class, args);
    }

}
