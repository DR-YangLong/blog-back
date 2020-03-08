title: Eureka配置
date: 2017-09-22 19:40:00
categories: [eureka]
tags: [java]
---

# 配置Eureka
阅读简介以更好的理解设置的概念。        

Eureka共有2个组件-Eureka Client和Eureka Server。在使用Eureka时，你的架构中通常有2个应用：
* **Application Client**通过Eureka Client请求应用服务端。
* **Application Server**接受客户端的请求并发送响应。

设置涉及以下内容：
* Eureka Server
* Application Client中的Eureka Client
* Application Server中的Eureka Client

*Eureka在AWS环境中和非AWS环境中都能运行。*

>如果在云环境中运行，则需要传入java commandline属性-Deureka.datacenter=cloud，以便Eureka客户端/服务器知道如何初始化AWS云的特定信息。
<!-- more -->
## 配置Eureka Client
### 先决条件
* JDK 1.8或更高
你能够通过以下方式获得Eureka Client程序。请优先选择最高的release版本，因为它有更多的修复。
* 你能够通过<http://search.maven.org/#search%7Cga%7C1%7Ceureka-client>下载。

* 可以通过在依赖文件中添加依赖
```xml
<dependency>
  <groupId>com.netflix.eureka</groupId>
  <artifactId>eureka-client</artifactId>
  <version>1.1.16</version>
 </dependency>
```

* 可以参照<https://github.com/Netflix/eureka/wiki/Building-Eureka-Client-and-Server>进行手动构建。

### 配置
配置Eureka Client最简单的方式是使用配置文件。默认情况下，Eureka Client会在*classpath* 路径下搜索 * eureka-client.properties* 配置文件。进一步的，它会搜索特定环境下的特定配置文件，并覆盖相关配置。环境通常为 *test* 或者 *prod* ，这是通过java commandline *-Deureka.environment*传给eureka client的（值并没有 *.properties* 后缀）。因此，客户端还会搜索 *eureka-client-{test,prod}.properties* 文件。     

你可以在<https://github.com/Netflix/eureka/blob/master/eureka-examples/conf/sample-eureka-client.properties>查看默认配置。如下：
```txt
###Eureka Client configuration for Sample Eureka Client

# see the README in eureka-examples to see an overview of the example set up

# note that for a purely client usage (e.g. only used to get information about other services,
# there is no need for registration. This property applies to the singleton DiscoveryClient so
# if you run a server that is both a service provider and also a service consumer,
# then don't set this property to false.
eureka.registration.enabled=false

## configuration related to reaching the eureka servers
eureka.preferSameZone=true
eureka.shouldUseDns=false
eureka.serviceUrl.default=http://localhost:8080/eureka/v2/

eureka.decoderName=JacksonJson
```

你可以复制并按你自己的需要编辑这些配置，然后将它们放到你项目的class path。如果你因为某些原因改变了自定义配置文件的名称，你可以通过在java commondline中指定 *-Deureka.client.props={配置文件名}*（文件名不带后缀） 来搜索并启用你修改名称后的配置文件。      

配置文件中解释了各个配置项的作用。最小的必须配置项为：
~~~txt
Application Name (eureka.name)
Application Port (eureka.port)
Virtual HostName (eureka.vipAddress)
Eureka Service Urls (eureka.serviceUrls)
~~~

更多的配置项，通过查看<https://github.com/Netflix/eureka/blob/master/eureka-client/src/main/java/com/netflix/appinfo/EurekaInstanceConfig.java https://github.com/Netflix/eureka/blob/master/eureka-client/src/main/java/com/netflix/discovery/EurekaClientConfig.java>可以获得。

## 配置Eureka Server
### 先决条件
* JDK 1.8或更高
* Tomcat 6.0.10或更高

你可以选择以下方式获取Eureka Server程序：
* 参照<https://github.com/Netflix/eureka/wiki/Building-Eureka-Client-and-Server>手动编译WAR包。
* 使用<http://search.maven.org/#search%7Cga%7C1%7Ceureka-server>从MAVEN中央仓库直接下载WAR包。

### 配置
Eureka Server有2个配置：
* Eureka Client配置，就如同之前的。
* Eureka Server配置。

最简单的配置方式就是像之前的Eureka Client一样使用配置文件。首先，按照说明配置和Eureka Server一起运行的Eureka Client。Eureka Server本身会启动一个Eureka Client用以发现其他Eureka Server。因此，你需要首先为Eureka Server配置Eureka Client，就像与其他连接到Eureka Server的客户端一样。Eureka Server将使用它的Eureka Client配置来识别具有相同名称(意即)eureka.name的对等Eureka Server。   

在配置好Eureka Client之后，如果你正在AWS中运行，那么你需要配置Eureka Server。Eureka Server默认会在 *classpath* 目录下寻找 *eureka-server.properties* 配置文集。同样的，可以使用java commandline *-Deureka.environment* 来指定环境，用于读取不同环境下的配置文件。

#### 本地开发配置
当在本地开发运行Eureka时，通常需要等待大约3分钟来完成启动。这是服务器默认的行为，目的是在发现没有其它可用服务节点时，用来搜索其它节点以同步和重试。等待时间可以通过属性设置来减少：
> eureka.numberRegistrySyncRetries=0


#### AWS配置
如果你运行在AWS中，按照<https://github.com/Netflix/eureka/wiki/Deploying-Eureka-Servers-in-EC2>处的说明，需要一些额外的配置。其他高级配置，可以参照此处<https://github.com/Netflix/eureka/blob/master/eureka-core/src/main/java/com/netflix/eureka/EurekaServerConfig.java>。    

如果你是手动构建WAR包，你可以编辑 *eureka-server/conf* 目录下的配置文件，这样构建之前文件将会存放到 WEB-INF/classes 目录下。

如果你下载了WAR包，你可以自行修改包中的 WEB-INF/classes 目录下的配置文件。

运行一个demo应用可以帮助你很好的理解配置属性。

## 版本兼容性
我们在eureka中使用版本语义，并在小版本升级中维护兼容性（即1.X版下服务器和客户端是兼容的）。通常，服务器使用比客户端新的版本是安全的。