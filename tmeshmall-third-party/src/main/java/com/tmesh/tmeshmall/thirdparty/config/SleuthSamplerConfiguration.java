package com.tmesh.tmeshmall.thirdparty.config;

import brave.sampler.Sampler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.sleuth.sampler.ProbabilityBasedSampler;
import org.springframework.cloud.sleuth.sampler.SamplerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 解决使用zipkin项目启动死锁问题
 * @Author: TMesh
 * @Date: 2024/1/8 18:50
 */
@Configuration
public class SleuthSamplerConfiguration {

    @Value("${spring.sleuth.sampler.rate}")
    private String probability;

    @Bean
    public Sampler defaultSampler() throws Exception {
        Float f = Float.valueOf(probability);
        SamplerProperties samplerProperties = new SamplerProperties();
        samplerProperties.setProbability(f);
        ProbabilityBasedSampler sampler = new ProbabilityBasedSampler(samplerProperties);
        return sampler;
    }
}