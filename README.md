# 商城简介
对于谷粒商城的自己重编写，由 java8 + Springboot2 改成 java17 + Springboot3，并根据自己的需求添加其他功能，目前并未全部完成。

采用现阶段流行技术来实现，采用前后端分离 + 前后端不分离 编写。

使用 阿里云服务器 + frp 转发到自己的虚拟机，使用 jenkins 自动部署（原计划使用 kubernetes 部署，由于虚拟机之间网络问题，最终没使用）所有数据库 和 中间件都使用Docker 容器化部署，采用了 java17 + Springboot3

[api接口文档](http://tmesh.cn/apidoc)

### 项目介绍

mall（商城） 项目是一套电商项目，包括前台商城系统以及后台管理系统，基于 SpringCloud + SpringCloudAlibaba + MyBatis-Plus 实现，采用 Docker 容器化部署。

- 前台商城系统
  - 用户登录
  - 注册
  - 商品搜索
  - 商品详情
  - 购物车
  - 下订单流程
  - 秒杀活动
  - 其他模块

- 后台管理系统
  - 系统管理
  - 商品系统
  - 优惠营销
  - 库存系统
  - 订单系统
  - 用户系统
  - 内容管理
  - 其他模块

### 组织结构

```
mall
├── renren-generator -- 人人开源项目的代码生成器
├── renren-fast -- 人人开源项目后台管理系统
├── mall-common -- 工具类及通用代码
├── mall-auth-server -- 认证中心（社交登录、OAuth2.0、单点登录）
├── mall-cart -- 购物车服务
├── mall-coupon -- 优惠卷服务
├── mall-gateway -- 统一配置网关
├── mall-order -- 订单服务
├── mall-product -- 商品服务
├── mall-search -- 检索服务
├── mall-seckill -- 秒杀服务
├── mall-third-party -- 第三方服务
├── mall-ware -- 仓储服务
└── mall-member -- 会员服务
```

### 技术选型

**技术**

|             技术              |           说明           |
| :---------------------------: | :----------------------: |
|             jdk17             |           java           |
|         SpringBoot 3          |       容器+MVC框架       |
|     SpringCloud 2022.0.4      |        微服务架构        |
| SpringCloudAlibaba 2022.0.0.0 |        一系列组件        |
|            MySql 8            |       mysql数据库        |
|             Redis             |        缓存数据库        |
|      MyBatis-Plus 3.5.3       |         ORM框架          |
|          renren-fast          | 人人开源项目后台管理系统 |
|       renren-generator        | 人人开源项目的代码生成器 |
|      Elasticsearch 7.6.2      |         搜索引擎         |
|           RabbitMQ            |         消息队列         |
|         Springsession         |        分布式缓存        |
|           Redisson            |         分布式锁         |
|            Docker             |       应用容器引擎       |
|          Aliyun OSS           |        对象云存储        |
|            Jenkins            |        自动化部署        |
|   Vue 2    |  前端框架  |
|  Element   | 前端UI框架 |
| thymeleaf  |  模板引擎  |
| node.js 12 | 服务端的js |

### 开发环境

|     工具      | 版本号 |
| :-----------: | :----: |
|      JDK      |   17   |
|     Mysql     |  5.8   |
|     Redis     | Redis  |
| Elasticsearch | 7.6.2  |
|    Kibana     | 7.6.2  |
|   RabbitMQ    | 3.8.5  |
|     Nginx     | 1.1.6  |

