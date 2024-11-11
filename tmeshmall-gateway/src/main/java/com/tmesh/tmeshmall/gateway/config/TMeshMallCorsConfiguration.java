package com.tmesh.tmeshmall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;

@Configuration
public class TMeshMallCorsConfiguration {

    @Bean
    public CorsWebFilter corsFilter() {
        // 1、配置跨域
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedHeader("*");        // #允许访问的头信息,*表示全部
        corsConfiguration.addAllowedMethod("*");        // 允许提交请求的方法类型，*表示全部允许
        corsConfiguration.addAllowedOriginPattern("*"); // #允许向该服务器提交请求的URI，*表示全部允许，在SpringMVC中，如果设成*，会自动转成当前请求头中的Origin
        corsConfiguration.setAllowCredentials(true);    // 允许cookies跨域
        corsConfiguration.setMaxAge(18000L);            // 预检请求的缓存时间（秒），即在这个时间段里，对于相同的跨域请求不会再预检了

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsWebFilter(source);
    }
}
