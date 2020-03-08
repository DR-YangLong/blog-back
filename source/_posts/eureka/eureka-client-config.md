title: Eureka客户端配置
date: 2017-09-25 19:50:00
categories: [eureka]
tags: [java]
---

# Eureka客户端配置说明

> EurekaInstanceConfig 

* instanceId:client唯一标识，将此实例从相同appName的集群中区分开来。
* appName:注册到eureka server的服务名，相同appName的实例组成集群。
* appGroupName:实例所在群组名，用于划分服务。
* instanceEnabledOnit:实例启动后是否立即注册到eureka server，因为有的实例启动后需要处理一些额外的事情才能对外提供服务。
* nonSecurePort:非安全端口。
* securePort:安全端口。
* nonSecurePortEnabled:是否启用非安全端口。
* securePortEnabled:是否启用安全端口。
<!-- more -->
* leaseRenewalIntervalInSeconds:向eureka server发送心跳的时间间隔，秒，如果在leaseExpirationDurationInSeconds规定的时间窗口内，server没有收到过心跳，将会把此实例从列表中移除，此示例将无法获得流量，注意如果此示例实现了HealthCheckCallback，那么既使心跳正常，是否对外提供服务还是由回调接口返回值决定。
* leaseExpirationDurationInSeconds:心跳时间窗口，秒，在距离上次收到心跳的时间间隔超过窗口期还未收到新的心跳，则server认为实例已下线。此值设置最小必须超过leaseRenewalIntervalInSeconds的值。
* virtualHostName:虚拟主机名。用于客户端发现此实例。
* secureVirtualHostName:安全虚拟主机名。用于客户端通过安全虚拟主机名发现此实例。
* ASGName:AWS autoscaling group name。
* hostName:关联此实例的主机名，真实的用于其他实例对此实例发起调用的名称。
* metadataMap:元数据表，存储此实例相关联的自定义key-value值并发送到eureka server，此元表能够被其他实例获取到。
* dataCenterInfo: 实例在AWS的哪个数据中心。
* ipAddress:此实例的ip地址。
* statusPageUrlPath:状态页自定义访问链接。
* StatusPageUrl:状态也访问链接。
* homePageUrlPath:自定义首页访问链接。
* homePageUrl:首页访问链接。
* healthCheckUrlPath:自定义健康状态访问链接。
* healthCheckUrl:健康状态访问链接。
* secureHealthCheckUrl:安全健康状态访问链接。
* nameSpace:命名空间，使用此命名空间查找响应的属性。
* defaultAddressResolutionOrder:AWS中实例的地址信息数组，包括publicHostname， publicIp，privateHostname， privateIp。

> EurekaClientConfig 
* registryFetchIntervalSeconds: 从eureka server获取注册信息的间隔，单位秒。
* instanceInfoReplicationIntervalSeconds: 上报当前节点的信息到eureka server的间隔，单位秒
* initialInstanceInfoReplicationIntervalSeconds: 初始化后多少秒将当前节点的信息上报到eureka server
* eurekaServiceUrlPollIntervalSeconds: 获取eureka server url地址改变的时间间隔，单位秒。server端会修改此值。
* proxyHost: 代理主机，可选。
* proxyPort: 代理端口，可选。
* proxyUserName: 代理用户名。
* proxyPassword: 代理用户密码。
* shouldGZipContent: 传输内容是否压缩过。
* eurekaServerReadTimeoutSeconds： 当读取某个eureka server时的超时时间。
* EurekaServerConnectTimeoutSeconds： 连接某个eureka server时的超时时间。
* backupRegistryImpl: BackupRegistry的实现类，备份注册信息。
* eurekaServerTotalConnections: 允许当前实例向所有eureka server发起的连接总数。
* eurekaServerTotalConnectionsPerHost: 允许当前实例向单个eureka server发起的连接总数。
* eurekaServerURLContext: 当eureka server列表为DNS查询获得时，使用此值构造URL连接eureka server。当shouldUseDnsForFetchingServiceUrls设置为true时，eureka client通过DNS获取eureka server的改变。
* eurekaServerPort: 使用DNS获取server时，需要指定eureka server的端口用来连接。
* eurekaServerDNSName: DNS的名称，用来查询eureka server列表。
* shouldUseDnsForFetchingServiceUrls: 是否使用DNS的方式来查询eureka server列表。
* shouldRegisterWithEureka: 当前实例是否注册到eureka server，以使其他实例可以发现它。
* shouldUnregisterOnShutdown: 当前实例关闭时是否在eureka server上注销。
* shouldPreferSameZoneEureka: 是否使用相同zone的eureka server。
* allowRedirects: 是否允许eureka server将当前实例的请求进行重定向转发。
* shouldLogDeltaDiff: 当解析服务器返回信息失败时，输出对比日志。
* shouldDisableDelta： 是否开启对比。
* fetchRegistryForRemoteRegions: 逗号分隔的region列表，当前实例将从region列表中获取可用的zone。
* region: AWS中，当前实例所在的region。
* availabilityZones: 可用的zone列表，数组。
* eurekaServerServiceUrls： eureka server URL列表。
* shouldFilterOnlyUpInstances: 是否过滤掉挂起状态的实例。
* eurekaConnectionIdleTimeoutSeconds: 当前实例在连接eureka server时http关闭前能够保持的空闲时间。超过空闲时间http连接将会关闭。
* shouldFetchRegistry： 当前实例是否同步eureka server的注册信息。
* registryRefreshSingleVipAddress: 从单个VIP获取注册信息，格式 name:port，如果为null则不获取。
* heartbeatExecutorThreadPoolSize: 心跳线程池大小。
* heartbeatExecutorExponentialBackOffBound: 心跳重试延期最大因子。
* cacheRefreshExecutorThreadPoolSize: 缓存信息刷新的线程池大小。
* cacheRefreshExecutorExponentialBackOffBound: 缓存刷新重试延期最大因子。
* dollarReplacement: $符号的替代字符。
* EscapeCharReplacementL: _的替代字符。
* shouldOnDemandUpdateStatusChange： 是否将当前实例的状态上报给eureka server。
* encoderName:编码类名称，为空使用默认。
* dncoderName:解码类名称，为空使用默认。
* experimental: 试验特性配置名称，此配置为了避免开发新特性时造成API污染。

# 配置文件属性对照
{eureka}代表eureka为变量，是启动时通过java命令行参数传入的，eureka是默认值。{region}和{zone}为配置时自定义的变参region和zone。

* {eureka}.client.refresh.interval: 单位秒，registryFetchIntervalSeconds 默认值30秒
* {eureka}.appinfo.replicate.interval: 单位秒，instanceInfoReplicationIntervalSeconds 默认值40秒
* {eureka}.serviceUrlPollIntervalMs: 单位毫秒 EurekaServiceUrlPollIntervalSeconds 默认值300秒
* {eureka}.eurekaServer.proxyHost: proxyHost
* {eureka}.eurekaServer.proxyPort: proxyPort
* {eureka}.eurekaServer.proxyUserName: proxyUserName
* {eureka}.eurekaServer.proxyPassword: proxyPassword
* {eureka}.eurekaServer.gzipContent: shouldGZipContent 默认true
* {eureka}.eurekaServer.readTimeout: 单位秒，eurekaServerReadTimeoutSeconds 默认值8秒
* {eureka}.eurekaServer.connectTimeout: 单位秒，eurekaServerConnectTimeoutSeconds 默认值5秒
* {eureka}.backupregistry: 类名，backupRegistryImpl 默认为null
* {eureka}.eurekaServer.maxTotalConnections: 整数，eurekaServerTotalConnections，默认200
* {eureka}.eurekaServer.maxConnectionsPerHost: 整数，eurekaServerTotalConnectionsPerHost 默认50
* {eureka}.eurekaServer.context: eurekaServerURLContext,如果为null，则取{eureka}.context，如果还是null，则为null。
* {eureka}.eurekaServer.port: eurekaServerPort,如果为null，则取{eureka}.port，如果还是null，则取null。
* {eureka}.eurekaServer.domainName: EurekaServerDNSName，如果为null，则取eureka.domainName，如果还是null,则为null。
* {eureka}.shouldUseDns: shouldUseDnsForFetchingServiceUrls 默认值为false
* {eureka}.registration.enabled: shouldRegisterWithEureka 默认值为true
* {eureka}.shouldUnregisterOnShutdown: shouldUnregisterOnShutdown 默认值为true
* {eureka}.preferSameZone: shouldPreferSameZoneEureka 默认值为false
* {eureka}.allowRedirects: allowRedirects 默认值为false
* {eureka}.printDeltaFullDiff: shouldLogDeltaDiff 默认值为false
* {eureka}.disableDelta: shouldDisableDelta 默认值为false
* {eureka}.fetchRemoteRegionsRegistry: fetchRegistryForRemoteRegions 默认null
* {eureka}.region: region(如果namespace是自定义的，先读取自定义namespace.region，读取为null才读取eureka.region) 默认值us-east-1
* {eureka}.{region}.availabilityZones: availabilityZones 使用“,”分割，默认值defaultZone。
* {eureka}.serviceUrl.{zone}:  eurekaServerServiceUrls 如果为空，使用{eureka}.serviceUrl.default的值，使用“,”分割，2个都为null则为null。
* {eureka}.shouldFilterOnlyUpInstances: shouldFilterOnlyUpInstances 默认值true
* {eureka}.eurekaserver.connectionIdleTimeoutInSeconds:  单位秒，eurekaConnectionIdleTimeoutSeconds 默认值30秒
* {eureka}.shouldFetchRegistry: shouldFetchRegistry 默认值true
* {eureka}.registryRefreshSingleVipAddress: RegistryRefreshSingleVipAddress 默认值null
* {eureka}.client.heartbeat.threadPoolSize: 整数 heartbeatExecutorThreadPoolSize 默认值5
* {eureka}.client.heartbeat.exponentialBackOffBound: 整数 heartbeatExecutorExponentialBackOffBound 默认值10
* {eureka}.client.cacheRefresh.threadPoolSize: 整数 cacheRefreshExecutorThreadPoolSize 默认值5
* {eureka}.client.cacheRefresh.exponentialBackOffBound: 整数 cacheRefreshExecutorExponentialBackOffBound 默认值10
* {eureka}.dollarReplacement: 字符串 dollarReplacement 默认值“_-”
* {eureka}.escapeCharReplacement: 字符串 escapeCharReplacement 默认值“__”
* {eureka}.shouldOnDemandUpdateStatusChange: shouldOnDemandUpdateStatusChange 默认值true
* {eureka}.encoderName: encoderName 默认值null
* {eureka}.decoderName: decoderName 默认值null
* {eureka}.clientDataAccept：clientDataAccept 默认full
* {eureka}.experimental.{name}: experimental 默认null