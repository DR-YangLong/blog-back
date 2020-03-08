title: 创建Eureka客户端和服务端
date: 2018-01-29 19:40:00
categories: [eureka]
tags: [java]
---

# 构建Eureka服务端和客户端

## 前置需求
> Git 1.7.11.3或更高版本

## 构建步骤
1. 安装最新版[Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)。
2. 从Github获取Eureka最新源码。
> git clone https://github.com/Netflix/eureka.git

3. 在你获取的源码目录中执行构建命令。
> ./gradlew clean build

4. 正确构建之后，你将获得以下文件：
> Eureka服务端程序：./eureka-server/build/libs/eureka-server-XXX.war

> Eureka客户端程序:./eureka-client/build/libs/eureka-client-XXX.jar

> 第三方依赖包：./eureka-server/testlibs/*.jar

