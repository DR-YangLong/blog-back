title: Zookeeper第六课
date: 2016-04-18 19:30:00
categories: [zookeeper]
tags: [zk,zookeeper]
---
# Zookeeper详解
## 客户端连接时如何选择连接节点。

> 1. 客户端connectstring：localhost:2181,localhost:2182,localhost:2183
> 2. 通过类client.StaticHostProvider类维护地址列表。
> 3. 通过解析connectstring后，进行地址随机排序，形成最终的地址列表。
> 4. 每次从形成的地址列表中选择第一个地址进行连接，如果连接不上再选择第二个地址，如果当前节点是列表最后一个节点，则重新选择第一个节点，相当于循环选择。
> 5. 通过随机排序，每个zk客户端随机的链接zk服务器节点，分布相对均匀。

## zk会话

### 什么是zk会话
> 1. 会话代表客户端与服务端的连接。
> 2. 底层通讯通过TCP协议进行连接通讯。
> 3. 网络出现抖动或者暂时断网时，并不意味着会话一定断开。
> 4. 会话的对象实现是SessionImpl（此类是SessionTrackerImpl静态内部类），包括4个属性：sessionID，唯一标识一个会话，在zk服务端具备全局唯一性。Timeout：会话超时时间，创建客户端zk对象时传入，服务器会根据最小会话时间和最大会话时间来明确此值是什么（如果介于2者之间，直接使用此值，如果小于最小时间，使用最小时间，如果大于最大时间，使用最大时间89）。Ticktime：下次会话超时时间，与“分桶策略”有关。isClosing：标记一个会话是否已经被关闭，当服务器检测到有会话失效时，就会把此会话标识为已关闭（CLOSE），此状态一直存在直到会话被清除。
> 5. 会话的状态：CONNECTING，CONNECTED，RECONNECTING，RECONNECTED，CLOSE。

<!--more -->
### 会话管理
zk通过SessionTracker接口进行会话管理。
> 1. 服务端通过sessionTracker进行会话创建、管理和清除。
> 2. 此接口提供三个维度进行会话管理：通过sessionID查找session。通过sessionID查找session过期时间。通过一个时间点查询哪些会话会在此时间点过期。

> SessionTrackerImpl的维护属性

```bash
//id获取session
HashMap<Long, SessionImpl> sessionsById = new HashMap<Long, SessionImpl>();
//在某个时间点要失效的会话
HashMap<Long, SessionSet> sessionSets = new HashMap<Long, SessionSet>();
//id查看session过期时间
ConcurrentHashMap<Long, Integer> sessionsWithTimeout;

```
#### 如何检查会话是否失效
zk将所有会话按照失效时间维度进行切分，放入不同的桶中，进行管理，同时冗余到另外的容器中，提供另外2个维度的查询。

> 会话失效时间计算

```bash
约定：把所有的时间按照某个单位进行等份（默认为服务器配置的ticktime）切割，此单位称呼为ExpirationInterval
公式：某次超时时间=（（currentTime+sessiontimeout）/ExpirationInterval+1）*ExpirationInterval
eg:
服务器配置ticktime为2000ms，则ExpirationInterval=2000ms，第一次创建会话时，currenttime=137090700，
此时客户端传入的超时时间是15000ms，则计算公式为：（（137090700+15000）/2000+1）*2000=137107700
```

> 分桶

将超时时间作为key，所有在此时间超时的会话放入一个set中，然后作为value，放入map中。当某个会话由于有操作导致超时时间变化，则将此会话从一个桶移动到另一个桶中。

```bash
会话的激活状态：
当会话下一直有操作，则会话不会失效。
影响会话超时的时间因素：
1.心跳检测，ping命令（当客户端发现在sessionTimeout/3时间范围内没有任何操作命令产生，就会发送一个PING心跳请求）；
2.正常业务操作，比如get或set。
每次业务操作或者心跳检测，都会重新计算超时时间，然后在桶间移动会话。
```

#### 如何高效的检测和清除失效会话

> 检查会话失效

由SessionTracker中一个线程周期性检查会话是否失效。
```bash
线程的检查周期也是ExpirationInterval的倍数。
当某次检查时，如果在此次线程执行的时间点之前还有会话，就说明这些会话都过期了，因为如果会话有业务操作或者心跳，会不断的从key较小的桶中移动到key大的桶中。
检查周期eg：
系统启动时间为100001，ExpirationInterval为2000ms，则基数为100001/2000=50：
第一次检查时间为（50+1）*2000=102000，第二次启动时为102000+2000即（50+2）*2000
以后检查时间总为最小间隔的倍数（源码中为每次+ExpirationInterval）
···


> 会话清除

```bash
1.修改会话状态为close：由于清理过程需要时间，为了避免清除期间会话状态出现变更，先标记，再清除。
2.向所有的集群节点发起会话关闭请求：只有主节点有权限进行会话检测，会话清除。
3.收集跟被清除的会话相关的临时节点
4.向集群节点发出删除临时节点的事务请求
5.集群中的所有节点执行删除临时节点事务
6.从sessionTracker的列表中移除会话
7.关闭会话网络连接，会话连接工厂类为NIOServerCnxnFactory
```

> 会话重连

当客户端与服务端网络连接断开后，客户端会不断的尝试重新连接，当连接上后会话的状态当前有2种情况：
> 1. CONNECTED-服务端会话依然存在
> 2. EXPIRED服务端的会话已经被清除

> 网络断开并不代表会话超时

三个会话的异常状态
> 1. CONNECTION_LOSS：网络闪断导致或者是客户端服务器出现问题导致。此状态下客户端会重新查找地址进行连接，直到连接上。当作某个操作的过程中出现了CONNECTION_LOSS现象，则客户端会接收到Non-Disconnected通知（设置了默认watcher情况下），同时客户端抛出ConnectionLossException异常。当重新连接上后，客户端会收到事件None-SyncConnected通知（设置了默认watcher的情况下）。
> 2. SESSION_EXPIRED：通常发生在CONNECTION_LOSS期间，因为没有网络连接，就不会有操作和心跳，会话就会超时。由于重新接连时间较长，导致服务器关闭了会话，并清除会话，此时会话关联的watcher等数据都会丢失。出现这种情况，客户端需要重新建立zk对象，并且恢复数据（比如注册watcher）。客户端重连时服务端会话处于此状态时会报SessionExpiredException异常。
> 3. SESSION_MOVE：出现CONNECTION_LOSS时，客户端尝试重新连接下一个节点，此时会话从已断开的服务器迁移到了重新连接上的服务器。


## 服务器端数据与存储
zk服务端通过三个类对zk数据模型进行维护和管理。ZKDatabase负责会话，快照，日志，以及树的管理，DataTree负责维护节点树。DataNode则维护数据节点。

### ZKDatabase
![ZKDatabase](/zk/ZKDatabase.png)

* 负责管理zk的所有会话，datatree存储以及事务日志。
* 定时向磁盘写入内存数据快照。
* 当节点启动后，会通过磁盘上的事务日志和快照文件恢复完整的内存数据。


### DataTree
![DataTree](/zk/DataTree.png)

* 整个zk的数据靠DataTree维护，包括数据，目录，权限。
* 数据的领域模型，不包括对外连接的管理。


### DataNode
![DataNode](/zk/DataNode.png)

* 树形结构中的每个节点。
* 节点的领域模型。包括当前节点的父节点，当前节点的子节点列表。

### 日志
> 日志文件

日志文件存储于datalog或者datalogDir配置的目录下。
对应目录下的version-2代表的是日志的格式版本号（代表的是zk格式器的版本）。
日志文件的命名：每个日志文件的大小都是64M。后缀都是16进制格式数字，逐渐增大，其本质是日志文件的第一条zxid。
日志的格式：文件内容为2进制，zk提供工具类LogFormatter解析日志内容。在日志文本中，第一行是日志的格式信息。信息从左到右为事务操作时间、客户端会话id、cxid、zxid、操作类型、节点路径、节点数据内容、acl信息、是否临时节点（F持久T临时）、父节点的子节点版本号

> 日志写入

* zk通过FileTxnLog实现日志管理，使用append方法来添加事务日志。
* 写入过程：1.确定是否有日志文件可写，当第一次创建事务日志文件或者上一个事务日志文件写满后都会关闭这个文件流。2.确定事务日志是否需要扩容，当文件剩余空间不足4kb时，把文件新增64M（新增一个日志文件），用“0”将旧文件剩余空间填充满。3.将事务序列化。4.生成Checksum。5.写入事务日志文件流。6.事务日志刷入磁盘（调用系统fsync接口）。

### 快照
快照的定义：zk在某一时刻的完整数据。
> 快照文件

存储于配置的dataDir目录下。
快照文件的后缀为服务器最新的zxid。
可以通过SnapshotFormatter查看快照文件内容。

> 快照写入

确定是否需要进行数据快照：
* snapCount默认为100000，表示达到这个数量的事务日志（10万条日志后）才开始进行快照。
* 为了避免集群节点同时进行快照，按照如下方式触发快照操作。

>  logCount>(snapCount/2+randRoll) 【randRoll是一个1到snapCount/2之间的随机数】

* 切换事务日志文件：创建新的事务日志文件。
* 创建数据快照异步线程。
* 获取全量数据和会话信息。
* 生成快照数据文件。
* 把数据刷入快照文件。
