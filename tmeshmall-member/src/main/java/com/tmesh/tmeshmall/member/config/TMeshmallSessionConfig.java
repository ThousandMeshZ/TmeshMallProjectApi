package com.tmesh.tmeshmall.member.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * @Description: springSession配置类
 * @Created: with IntelliJ IDEA.
 * @author: TMesh
 **/

@Configuration
public class TMeshmallSessionConfig {

    @Bean
    public CookieSerializer cookieSerializer() {

        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();

        //放大作用域
        cookieSerializer.setDomainName("tmesh.cn");
        cookieSerializer.setCookieName("TMESHSESSION");

        return cookieSerializer;
    }


    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        // 指定session序列化到redis的序列化器
//        return new Jackson2JsonRedisSerializer<Object>(Object.class);// 无法保存对象类型，反序列化后默认使用Map封装
        return new GenericJackson2JsonRedisSerializer();
    }

}
