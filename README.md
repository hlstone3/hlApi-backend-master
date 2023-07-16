# hlApi项目接口项目



### 项目介绍

一个提供API接口供开发者调用的平台。管理员可以接入并发布接口，统计分析各接口调用情况；用户可以注册登录并调用接口调用，然后可以浏览接口及在线调试，还能使用客户端 SDK 轻松在代码中调用接口。



### 技术选型

#### 前端：

- React 18
- Ant Design Pro 5.x
-  脚手架Ant Design & Procomponents 组件库
- Umi 4 前端框架
- OpenAPI 前端代码生成
- nginx

#### 后端：

- SpringBoot 2.7
- MyBatis Plus + MyBatisX插件
- Spring Boot Starter（SDK 开发）
- API 签名认证（Http 调用）
- Spring Cloud Gateway 微服务网关
- Dubbo 分布式（RPC、Nacos）
- Swagger + Knife4j 接口文档生成
- Hutool、Apache Common Utils、Gson 等工具库

#### 数据存储

- MySQL 数据库
- Redis 内存数据库



### 业务流程

![image-20230715220707774](https://github.com/hlstone3/imgs/blob/master/hlApi/image-20230715220707774.png)

### 业务功能

- 用户登录、注册、注销、权限管理
- 接口创建、删除、编辑、更新
- 接口调用、发布、检索
- 接口上线、下线
- 接口调用次数图表分析
- 接口统计计数，接口调用次数充值

##### API签名认证

![image-20230716092048800](https://github.com/hlstone3/imgs/blob/master/hlApi/image-20230716092048800.png)

#### RPC调用模型

![image-20230716092217472](https://github.com/hlstone3/imgs/blob/master/hlApi/image-20230716092217472.png)

