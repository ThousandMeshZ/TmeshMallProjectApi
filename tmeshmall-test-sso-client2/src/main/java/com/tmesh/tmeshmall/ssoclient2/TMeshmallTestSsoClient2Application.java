package com.tmesh.tmeshmall.ssoclient2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
@SpringBootApplication
public class TMeshmallTestSsoClient2Application {

	public static void main(String[] args) {
		SpringApplication.run(TMeshmallTestSsoClient2Application.class, args);
	}

}
