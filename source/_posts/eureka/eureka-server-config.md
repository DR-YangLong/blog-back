title: Eureka Server配置
date: 2017-09-26 19:50:00
categories: [eureka]
tags: [java]
---

# Eureka Server配置说明
* {eureka}.awsAccessId: AWSAccessId AWS Access Id，用来绑定到EIP，默认值null
* {eureka}.awsSecretKey: AWSSecretKey 密钥 默认值null
* {eureka}.eipBindRebindRetries: EIPBindRebindRetries 绑定到EIP的重试次数 默认值3
* {eureka}.eipBindRebindRetryIntervalMsWhenUnbound: 单位毫秒 EIPBindingRetryIntervalMsWhenUnbound 检查绑定状态的时间间隔 默认值60 * 1000
* {eureka}.eipBindRebindRetryIntervalMs: 单位毫秒 EIPBindingRetryIntervalMs 绑定稳定状态检查时间间隔 默认值300秒
* {eureka}.enableSelfPreservation: shouldEnableSelfPreservation 是否开启自我保护（当接收到客户端的心跳小于阈值时server会进入自我保护模式）默认值true
* {eureka}.renewalPercentThreshold: renewalPercentThreshold 期待从客户端获取的心跳阈值百分比 默认值0.85
* {eureka}.renewalThresholdUpdateIntervalMs 单位毫秒 renewalThresholdUpdateIntervalMs 更新心跳阈值的时间间隔 默认值15分钟
* {eureka}.peerEurekaNodesUpdateIntervalMs: 单位毫秒 peerEurekaNodesUpdateIntervalMs  集群里eureka节点的变化信息更新的时间间隔，单位为毫秒，默认为10 * 60 * 1000
* {eureka}.enableReplicatedRequestCompression: shouldEnableReplicatedRequestCompression 复制的数据在发送时是否压缩 默认值false
* {eureka}.numberOfReplicationRetries: NumberOfReplicationRetries 集群里服务器尝试复制数据的次数 默认值5
* {eureka}.peerEurekaStatusRefreshTimeIntervalMs: PeerEurekaStatusRefreshTimeIntervalMs 服务器节点状态信息更新的时间间隔 默认值30*1000
* {eureka}.waitTimeInMsWhenSyncEmpty: WaitTimeInMsWhenSyncEmpty 服务器获取不到集群里对等服务器实例时，需要等待的时间 默认值1000 * 60 * 5
<!-- more -->
* {eureka}.peerNodeConnectTimeoutMs: PeerNodeConnectTimeoutMs 链接对等服务器节点的超时时间 默认值200
* {eureka}.peerNodeReadTimeoutMs: PeerNodeReadTimeoutMs 读取对等服务器节点复制数据的超时时间 默认值200
* {eureka}.peerNodeTotalConnections: PeerNodeTotalConnections 允许对等服务器节点连接的总数量 默认值1000
* {eureka}.peerNodeTotalConnectionsPerHost PeerNodeTotalConnectionsPerHost 允许单个对等服务器节点连接的数量 默认值500
* {eureka}.peerNodeConnectionIdleTimeoutSeconds: PeerNodeConnectionIdleTimeoutSeconds http连接允许空闲时间 默认值30
* {eureka}.retentionTimeInMSInDeltaQueue: RetentionTimeInMSInDeltaQueue 客户端保持增量信息的缓存时间 默认值3 * 60 * 1000
* {eureka}.evictionIntervalTimerInMs: EvictionIntervalTimerInMs 过期实例允许重启时间间隔  默认值60 * 1000
* {eureka}.asgQueryTimeoutMs:  ASGQueryTimeoutMs 查询AWS上ASG信息超时时间 默认值300
* {eureka}.asgUpdateIntervalMs: ASGUpdateIntervalMs 从AWS更新ASG信息的时间间隔 默认值5 * 60 * 1000
* {eureka}.asgCacheExpiryTimeoutMs: ASGCacheExpiryTimeoutMs 缓存ASG信息的时间 默认值10 * 60 * 1000
* {eureka}.responseCacheAutoExpirationInSeconds: ResponseCacheAutoExpirationInSeconds 注册信息被更改时，旧信息在缓存中保存的时间 默认值180
* {eureka}.responseCacheUpdateIntervalMs: ResponseCacheUpdateIntervalMs 客户端注册信息列表缓存更新的时间间隔 默认值30 * 1000
* {eureka}.shouldUseReadOnlyResponseCache: shouldUseReadOnlyResponseCache 目前采用的是二级缓存策略，一个是读写高速缓存过期策略，另一个没有过期只有只读缓存，默认为true，表示只读缓存 默认值true
* {eureka}.disableDelta: shouldDisableDelta 增量信息是否提供给客户端 默认值false
* {eureka}.maxIdleThreadAgeInMinutesForStatusReplication: MaxIdleThreadInMinutesAgeForStatusReplication 状态复制线程可以保持存活的空闲时间 默认值10
* {eureka}.minThreadsForStatusReplication: MinThreadsForStatusReplication 被用于状态复制的最小线程数 默认值1
* {eureka}.maxThreadsForStatusReplication: MaxThreadsForStatusReplication 被用于状态复制的最大线程数 默认值1
* {eureka}.maxElementsInStatusReplicationPool: MaxElementsInStatusReplicationPool 可以允许备份的状态复制事件最大数量 默认值10000
* {eureka}.syncWhenTimestampDiffers: shouldSyncWhenTimestampDiffers 当时间变化时是否重新同步  默认值true
* {eureka}.numberRegistrySyncRetries: RegistrySyncRetries 当服务器启动时尝试去获取集群里其他服务器上的注册信息的次数 默认值5
* {eureka}.registrySyncRetryWaitMs: RegistrySyncRetryWaitMs 当服务器启动时获取集群中其他服务器注册信息失败时，重试的时间间隔 默认值 30*1000
* {eureka}.maxElementsInPeerReplicationPool: MaxElementsInPeerReplicationPool 备份复制事件的最大数量 默认值10000
* {eureka}.maxIdleThreadAgeInMinutesForPeerReplication: 单位分钟 MaxIdleThreadAgeInMinutesForPeerReplication 复制线程可以保持存活的空闲时间 默认值15
* {eureka}.minThreadsForPeerReplication: MinThreadsForPeerReplication 被用于复制的最小线程数目 默认值5
* {eureka}.maxThreadsForPeerReplication: MaxThreadsForPeerReplication 被用于复制的最大线程数目 默认值20
* {eureka}.maxTimeForReplication: MaxTimeForReplication 允许复制线程执行的时间 默认值30000
* {eureka}.primeAwsReplicaConnections: shouldPrimeAwsReplicaConnections 检查新节点的复制连接是否启用 默认值true
* {eureka}.disableDeltaForRemoteRegions: shouldDisableDeltaForRemoteRegions 增量信息是否提供给客户端或其他region 默认值false
* {eureka}.remoteRegionConnectTimeoutMs: RemoteRegionConnectTimeoutMs 连接到其他region服务器节点的超时时间 默认值1000
* {eureka}.remoteRegionReadTimeoutMs: RemoteRegionReadTimeoutMs 从其他region服务器节点读取信息的超时时间 默认值1000
* {eureka}.remoteRegionTotalConnections: RemoteRegionTotalConnections 允许连接到其他region服务器节点的总http数量 默认值1000
* {eureka}.remoteRegionTotalConnectionsPerHost: RemoteRegionTotalConnectionsPerHost 允许连接到其他region单个服务器节点的http数量 默认值500
* {eureka}.remoteRegionConnectionIdleTimeoutSeconds: RemoteRegionConnectionIdleTimeoutSeconds 允许连接到其他region服务器节点的http空闲时间 默认值30
* {eureka}.remoteRegion.gzipContent: shouldGZipContentFromRemoteRegion 获取的信息是否在其他region的服务器节点被压缩过 默认值true
* {eureka}.remoteRegionUrlsWithName: RemoteRegionUrlsWithName 其他region的名称地址MAP  默认值null；使用 **,** 分割 **region;url** 组成的数组，
* {eureka}.remoteRegionUrls: RemoteRegionUrls 其他region的url地址列表 默认null，使用 **,** 分割的url地址。
* {eureka}.remoteRegion.{regionName}.appWhiteList: RemoteRegionAppWhitelist 允许其他region连接到此实例的应用白名单 使用 **,** 分割的数组，默认值null，regionName默认值为global。
* {eureka}.remoteRegion.registryFetchIntervalInSeconds: RemoteRegionRegistryFetchInterval 从其他region同步注册信息的时间间隔 默认值30
* {eureka}.remoteRegion.fetchThreadPoolSize: RemoteRegionFetchThreadPoolSize 用于执行和其他region同步注册信息的线程池大小 默认值20
* {eureka}.remoteRegion.trustStoreFileName: RemoteRegionTrustStore 信任文件存储位置 默认值""
* {eureka}.remoteRegion.trustStorePassword: RemoteRegionTrustStorePassword 信任文件存储密码 默认值changeit
* {eureka}.remoteRegion.disable.transparent.fallback: disableTransparentFallbackToOtherRegion 在本region中没有provider运行时，是否回退到其他region 默认值false
* {eureka}.shouldBatchReplication: shouldBatchReplication 是否为复制进行批量处理 默认值false
* {eureka}.auth.shouldLogIdentityHeaders: shouldLogIdentityHeaders 服务器是否应该登录clientAuthHeaders 默认值true
* {eureka}.jsonCodecName: JsonCodecName json编码器类名 默认值null
* {eureka}.xmlCodecName: XmlCodecName  xml编码器类名 默认值null
* {eureka}.route53BindRebindRetries: Route53BindRebindRetries 尝试绑定到Route53的次数 默认值3
* {eureka}.route53BindRebindRetryIntervalMs: Route53BindingRetryIntervalMs 检查绑定状态的时间间隔 默认值5 * 60 * 1000
* {eureka}.route53DomainTTL: Route53DomainTTL Route53的TTL值 默认值301
* {eureka}.awsBindingStrategy: BindingStrategy 绑定策略 默认值EIP
* {eureka}.experimental.{name}: Experimental 试验特性配置 默认值null
* {eureka}.minAvailableInstancesForPeerReplication: HealthStatusMinNumberOfAvailablePeers 当多少个实例运行时判定集群健康状态 默认值-1

