package com.tmesh.tmeshmall.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/** 
*   1、 如何使用 nacos 作为配置中心统一管理配置
*       1）、引入依赖
*           <dependency>
*               <groupId>com.alibaba.cloud</groupId>
*               <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
*           </dependency>
*       2）、创建一个 bootstrap.properties 文件
*           spring.application.name=tmeshmall-coupon（应用名）
*           spring.cloud.nacos.config.server-addr=127.0.0.1:8848（nacos 地址）
*       3）、需要给配置中心默认添加一个叫 数据集（Data Id）tmeshmall-coupon.properties 默认规则：应用名.properties
*       4）、给 应用名.properties 添加任何配置
*       5）、动态获取配置
*           @RefreshScope：动态获取并刷新配置
*           @value("${配置项的名}")：获取配置
*           如果配置中心和当前应用的配置文件中都配置了相同项，优先使用配置中心的配置。
*   2、细节
*       1）、命名空间，配置隔离；
*           默认 public（保留空间）：默认新增的所有配置都在 public 空间
*           1、开发、测试、生产：利用命令空间来做环境隔离
*              注意：在 bootstrap.properties 配置上，需要使用哪个命名空间下的配置。
*              spring.cloud.nacos.config.namespace=21549e63-c43e-473b-b3a5-7d469cd2fd59（命名空间的 uuid）
*           2、每个微服务之间相互隔离配置，每个微服务都创建自己的命名空间，只加载自己命名空间下的所有配置 
*           
*       2）、配置集：所有配置的集合
*       
*       3）、配置集 ID：类似文件名
*           Data ID 类似文件名
*       
*       4）、配置分组
*           默认所有的配置集都属于，DEFAULT_GROUP：
*           
*  每个微服务创建自己的命名空间，使用配置分组区分环境，dev、test、prop
*  
*  3、同时加载多个配置集
*       1）、微服务任何配置信息，在任何配置文件都可以放在配置中心中
*       2）、只需要在 bootstrap.properties 中说明加载配置中心中的哪些配置文件
*       3）、@value，@ConfigurationProperties 等 SpringBoot 中任何方法从配置文件回去值，都能使用。
 *          配置中心有的，优先使用配置中心中的。
*  */

@MapperScan("com.tmesh.tmeshmall.coupon.dao")
@EnableDiscoveryClient
@SpringBootApplication
public class TmeshMallCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(TmeshMallCouponApplication.class, args);
    }

}
