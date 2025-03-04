package com.tmesh.tmeshmall.ssoclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
@SpringBootApplication
public class TMeshmallTestSsoClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(TMeshmallTestSsoClientApplication.class, args);
    }

}
