title: Eureka客户端和服务端的通讯
date: 2018-01-31 19:20:00
categories: [eureka]
tags: [java]
---

# Eureka客户端和服务端的通讯
使用Eureka的第一步是进行Eureka client的初始化。如果你是在AWS中使用，可以使用以下方式进行初始化：
使用1.1.153版本时，客户端可以和governator/guice一其使用，详细请看[示例]("https://github.com/Netflix/eureka/blob/master/eureka-examples/src/main/java/com/netflix/eureka/ExampleEurekaGovernatedService.java")。
在1.1.153版本之前，你可以使用以下方式进行客户端初始化：
```java
DiscoveryManager.getInstance().initComponent(
                new CloudInstanceConfig(),
                new DefaultEurekaClientConfig());
```
如果你是在其他数据中心使用，则使用以下方式：
```java
DiscoveryManager.getInstance().initComponent(
                new MyDataCenterInstanceConfig(),
                new DefaultEurekaClientConfig());
```
Eureka Client查找并使用*eureka-client.properties*，相关信息看第二章客户端配置。
<!-- more -->
## 实例的状态（About Instance Statuses）
默认的，Eureka client开始时处于**STARTING**状态，在此期间，实例能够处理一些程序特定的初始化操作，这处于实例能够对外提供服务之前。
程序在之后将实例状态转为**UP**，表明实例可以对外提供服务。
```java
ApplicationInfoManager.getInstance().setInstanceStatus(InstanceStatus.UP)
```
程序也能注册健康检查的回调接口，通过此接口可以随时改变实例状态到**DOWN**。
在NETFLIX，还是用一个**OUT_OF_SERVICE**的状态，用于将实例从流量中摘除。在新版本出现问题时，使用它对版本进行回退非常有用。所有的应用为新版本创建一个新ASG并将流量转发到这些新的ASG。当出现问题时，回退版本仅仅是设置所有的有问题版本所在实例的状态为**OUT_OF_SERVICE**。

## 客户端操作（Eureka Client Operations）
AWS云环境下，Eureka client首先尝试连接在相同zone的Eureka Server进行所有操作，如果连接不上，则连接其他zone中的server。
应用能够通过使用Eureka client返回的信息来进行负载均衡。以下是一个示例：
```java
InstanceInfo nextServerInfo = DiscoveryManager.getInstance()
                .getDiscoveryClient()
                .getNextServerFromEureka(vipAddress, false);

        Socket s = new Socket();
        int serverPort = nextServerInfo.getPort();
        try {
            s.connect(new InetSocketAddress(nextServerInfo.getHostName(),
                    serverPort));
        } catch (IOException e) {
            System.err.println("Could not connect to the server :"
                    + nextServerInfo.getHostName() + " at port " + serverPort);
        }
```
如果基本的轮询（round-robin）负载均衡算法不能满足你的要求，你可以通过API接口自行实现。在AWS中，确保失败后进行重试并且超时时间尽量低，因为在Eureka server会因返回时中断导致实例不存在的情况。
还有非常重要的一点，Eureka client会清空空闲时间超过30秒的与server建立的HTTP链接。这是AWS的防火墙策略导致的。

## Eureka 客户端和服务端进行以下操作

### 注册（register）
Eureka client注册正在运行的实例相关信息到Eureka server。在AWS中，实例的信息通过URL http://169.254.169.254/latest/metadata进行注册。注册发生在第一次心跳时（30秒后）。

### 续租（Renew）
Eureka client需要每隔30秒通过心跳进行续租。续租向Eureka server表明本实例仍然存活。如果server在90秒内没有收到续租信息，它将未收到续租的实例从注册表中移除。建议不要更改更新间隔，因为server使用该信息来确定client与server通信是否存在广泛的传播问题。

### 注册信息获取（Fetch Registry）
Eureka client从server获取注册信息并缓存在本地。之后，client使用注册信息发现其他的服务。注册信息会周期性的更新（每隔30秒），并且是进行增量更新。增量信息在server保存的时间更长（大约3分钟），因此在更新时可能会返回相同的信息。eureka client会自动处理重复的信息。
获取增量信息后，eureka client将通过比较server返回的实例数量进行一致性校验，如果发现不一致，整个注册信息将重新获取。Eureka server缓存经过压缩的增量信息，所有注册的实例和每个应用通过相同的方式解压这些信息。信息支持JSON/XML格式。Eureka client使用jersey apache客户端获取进过压缩的JSON格式数据。

### 注销（Cancel）
Eureka client在关闭时向server发出一个注销请求。这把实例从server的注册表中移除，实际上就是将实例从流量中摘除。
这个动作发生在Eureka client关闭并且应用需要确保在关闭时调用了以下代码：
```java
DiscoveryManager.getInstance().shutdownComponent()
```

### 延迟（Time Lag）
Eureka client的所有操作都会花费一些时间才能被server使用，随后才会被其他client获取。这是因为eureka server缓存的注册信息有一定的更新间隔，同样的client获取信息也有间隔。因此，最高有2分钟的延迟，所有的client才能收到变更。

### 通讯机制（Communication mechanism）
默认的，eureka client通过Jersey和XStream一起，使用JSON格式数据同server进行通讯。如果有需要，你可以通过覆盖默认实现，使用自己的通讯机制。