package com.tmesh.tmeshmall.member.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @Description: feign拦截器功能
 * @Created: with IntelliJ IDEA.
 * @author: TMesh
 **/

@Configuration
public class TMeshFeignConfig {

    /**
     * 注入拦截器
     * feign调用时根据拦截器构造请求头，封装cookie解决远程调用时无法获取springsession
     */
    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor() {

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                System.out.println("feign远程调用，拦截器封装请求头...RequestInterceptor.apply");
                // 1、使用RequestContextHolder拿到原生请求的请求头信（下文环境保持器）
                // 从ThreadLocal中获取请求头（要保证feign调用与controller请求处在同一线程环境）
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                if (requestAttributes != null) {
                    //老请求
                    HttpServletRequest request = requestAttributes.getRequest();

                    if (request != null) {
                        //2、同步请求头的数据（主要是cookie）
                        //把老请求的cookie值放到新请求上来，进行一个同步
                        String cookie = request.getHeader("Cookie");
                        template.header("Cookie", cookie);
                    }
                }
            }
        };
        return requestInterceptor;
    }

}
