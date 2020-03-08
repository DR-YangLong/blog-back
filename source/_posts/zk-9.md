title: Zookeeper第九课
date: 2016-05-10 19:40:00
categories: [zookeeper]
tags: [zk,zookeeper]
---

# Zookeeper运维管理

## 日志配置
zk的日志使用log4j日志组件。
默认日志输出目录：
> 1. winwos为安装目录下，zookeeper.log
> 2. linux为安装目录下bin/zookeeper.out

zk默认不开启日志文件输出。
日志相关属性配置在**conf/log4j.properties**中配置。日志文件输出位置必须在环境设置脚本**zkEnv**中进行设置：set ZOO_LOG_DIR=/usr/log。或者通过JVM启动参数指定：-Dzookeeper.log.dir=/usr/log

## zk其他属性配置
配置方式：
1. 通过java的JVM系统参数指定（在zkServer脚本中配置，3.4.8版本在140行：nohup "$JAVA" **"-Dzookeeper.log.dir=${ZOO_LOG_DIR}”** JVM参数可以加在这个地方）：-Djava.library.path
2. 通过zk的配置文件zoo,conf配置。

<!--more -->
> 配置说明：

| 参数名 |作用 | 说明 |
| ------------- |:---------------:| ---------------------:|
| dataLogDir | 配置事务日志文件存储目录|1. 不支持JVM系统属性配置。2. 默认值为dataDir的值。3. 高并发下，将会产生大量的事务日志和快照，如果log的目录和data的目录是同一磁盘，将会有IO瓶颈，因此最好配置到不同磁盘上，提高IO性能。|
| snapCount|两次快照间隔的事务日志条数|1.  事务日志条数达到这个数目时，就要触发数据快照。2.  默认值为100000。3.  仅支持系统属性配置方式。|
| preAllocSize|事务日志文件预分配的磁盘空间大小|1. 仅支持系统属性配置，zookeeper.preAllocSize。2. 默认值65535,即64M。3.  此参数与snapCount有关，snapCount越大，就需要分配越大的值。|
| minSessionTimout、maxSessionTimeout|服务器端会话失效的时间边界控制 | 1. 不支持系统属性配置。2. 默认为ticktime的2到20倍。3. 当客户端传递过来的超时间不在这2个参数之间时，最小取min最大取max。|
|maxClientCnxns|从socket层限制客户端与单台服务器的并发连接数|1. 不支持系统属性配置，默认值60,0表示不限制。2. 以IP地址为粒度控制。3. 只能控制单台机器，不能控制总连接。|
|Jute.maxbuffer|配置单个节点最大的数据大小|1. 仅支持系统属性方式配置，默认10M，单位字节。2. zk上存储的数据不宜过多，主要是考虑到多节点写入性能。3. 需要在服务器端和客户端都配置才生效。|
|Autopurge.snapRetainCount| 自动清理快照和事务日志时需要保留的文件数|1. 不支持系统属性配置，默认值为3,可以不用配置。2. 最小值。 3. 避免磁盘损坏后不能回复数据。|
|Autopurge.purgeInterval|自动清理快照和事务的周期|1.不支持系统属性配置，默认值0,表示不开启自动清理。2.与Autopurge.snapRetainCount属性一起配合使用。3.配置为负数也表示不清理。|
|fysnc.waringthresholdms|事务日志刷性到磁盘的报警阀值|1.支持系统属性，默认值为1000ms。2.如果fsync的操作超过此时间就会在日志中打印报警日志。|
|forceSync|日志提交时是否强制刷磁盘|1.默认为true。仅支持系统属性配置：zookeeper.forceSync.3.如果设置为no,可以提升写入性能，但是会有数据丢失风险。|
|cnxTimeout|选举过程中，服务器之间创建tcp连接的超时时间|1.默认值为5000ms，仅支持系统属性配置：zookeeper.cnxTimeout。|

## 四字命令
> 定义

长度为4个英文字母的管理命令。
> 使用方式

1. telnet:telnet ip port 然后执行[需要执行的命令]
2. nc:echo [需要执行命令]|nc ip port

linux下大多使用nc来进行维护和监控。
~~~bash
nc是一个简单、可靠的网络工具，可通过TCP或UDP协议传输读写数据。
~~~

> conf

此命令用于输出基本的配置信息，也可以查看一些运行时参数。
~~~bash
echo conf |nc localhost 2181;

结果：

~~~
> cons

此命令用于输出当前客户端所有连接的详细信息，包括客户端ip，会话id等。
~~~bash
echo cons |nc localhost 2181

结果：

~~~

> crst

此命令用于重置客户端的连接统计信息。
~~~ bash
echo crst |nc localhost 2181

结果：

~~~

> dump

* 此命令用于输出当前集群的所有的会话信息，包括会话id以及临时节点等信息。
* 如果当前节点是leader节点，则还会输出会话的超时时间。

~~~ bash
echo dump |nc localhost 2181

结果：

~~~


> envi

此命令用于输出运行时环境信息。
~~~ bash
echo envi |nc localhost 2181

结果：

~~~

> ruok

此命令用于输出当前zk服务器运行是否正常，注意仅仅代表2181端口和此命令的执行是否正常，并不能完全代表zk运行正常，如需要确定，使用stat命令。
~~~ bash
echo ruok |nc localhost 2181

结果：

~~~

> stat

此命令用于获取服务端的运行状态：zk的版本、打包信息、运行时角色、集群的数据节点等。

~~~ bash
echo stat |nc localhost 2181

结果：

~~~

> srvr

此命令与stat功能类似，但不输出连接信息。
~~~bash
echo srvr |nc localhost 2181

结果：

~~~

> srst

此命令用于重置服务器统计信息。
~~~ bash
echo srst |nc localhost 2181

结果：

~~~

> wchs

此命令用于输出当前服务器上管理的watcher的概要信息，通过zk构造器创建的默认watcher不在此统计范围。
~~~ bash
echo wchs |nc localhost 2181

结果：

~~~

> wchc

此命令用于输出当前服务器上管理的watcher的详细信息，以会话为组，通过zk构造创建的默认watcher不在此统计范围。
~~~ bash
echo wchc |nc localhost 2181

结果：

~~~

> wchp

此命令与wchc类似，但是以节点路径分组，默认的watcher不在统计范围内。

~~~ bash
echo wchp |nc localhost 2181

结果：

~~~

> mntr

此命令与stat类似，但是比stat更详细，包括请求的延时情况，服务器内存数据库大小，集群同步情况等信息都会显示。
~~~ bash
echo mntr |nc localhost 2181

结果：

~~~

## 性能优化
* zk基于java开发实现，数据全量储存在内存中，因此，调整JVM内存是优化点之一，具体需要根据业务情况来定。也就是JVM调优。
* IO优化：将事务日志与快照存储的路径设在不同的磁盘上，提供IOPS，最后将事务日志设在单独挂载磁盘上，可以考虑SSD。
* 加大linux系统的文件句柄数和用户线程数，通过ulimit可以查看当前配置。
* 业务并发高时，可以创建多于1个的客户端会话；可以不同的业务模块采用不同的客户端实例。
* 利用zk进行业务并发时，尽量通过良好的设计减少资源消耗，比如控制好watcher的数量。
* 节点数量，在写少，读多的应用场景中，采用多一点的节点会提升整体的读并发性能。
* 节点数据量最好比默认的10M还小。
* 带宽尽量高，可以通过网络监控查看带宽是否是瓶颈。

## 扩容
> 停机

增加相应的节点即可。

> 不停机

* 增加新的节点，id一定要比原来集群的大。
* 新增节点启动后会加入集群并同步数据。
* 当用mntr命令查看新的节点数据已经同步成功后做下面的操作。
* 按照之前的id的顺序依次关闭zk实例，然后修改配置，启动实例。

## 容灾

> 单机房容灾

单机房的容灾zk本身的集群机制就能很好的支持。

> 多机房容灾

由于过半投票机制，zk不支持双机房的容灾，比如是5节点，分为2和3,当3这个机房出现故障，2就不能选举成功。因此，多机房容灾主要是考虑三机房情况。跨机房的网络延迟较大，做这个容灾要避免大量写的应用场景。

> 客户端设置

为了避免服务器地址变化影响客户端，客户端尽量采用域名的方式。

## 监控指标

> zookeeper事务日志

* 磁盘IO。
* 可以开启事务日志自动清理：autopurge.snapRetainCount，autopurge.purgeInterval=24

> 连接数
> 注册的watcher数量
> zk事件通知的延时大小
