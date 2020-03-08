title: Zookeeper第一课
date: 2016-04-03 18:01:56
categories: [zookeeper]
tags: [zk,zookeeper]
---

## 一、什么是Zookeeper，作用是什么。
### zookeeper是什么：
简而言之，zookeeper是java语言编写的，属于Hadoop的一个子项目，是一个开源的、高性能的、分布式的、应用协调服务。
### zookeeper能干什么：
zookeeper作为hadoop的子项目，在hadoop中扮演着指挥员的作用，保证集群稳定运行，资源有序调度。
#### zookeeper的作用

##### 集群管理、发布订阅

> 名字服务(NameService) 

``` bash
分布式应用中，通常需要一套完备的命令机制，既能产生唯一的标识，又方便人识别和记忆。 我们知道，每个ZNode都可以由其路径唯一标识，路径本身也比较简洁直观，另外ZNode上还可以存储少量数据，这些都是实现统一的 NameService的基础。下面以在HDFS中实现NameService为例，来说明实现NameService的基本布骤:
目标：通过简单的名字来访问指定的HDFS机群
定义命名规则：这里要做到简洁易记忆。
下面是一种可选的方案： [serviceScheme://][zkCluster]-[clusterName]，比如hdfs://lgprc-example/表示基于 lgprc ZooKeeper集群的用来做example的HDFS集群。
配置DNS映射: 将zkCluster的标识lgprc通过DNS解析到对应的ZooKeeper集群的地址
创建ZNode: 在对应的ZooKeeper上创建/NameService/hdfs/lgprc-example结点，将HDFS的配置文件存储于该结点下
用户程序要访问hdfs://lgprc-example/的HDFS集群，首先通过DNS找到lgprc的ZooKeeper机群的地址，然后在 ZooKeeper的/NameService/hdfs/lgprc-example结点中读取到HDFS的配置，进而根据得到的配置，得到HDFS的实际访问入口
```
> 配置管理

在分布式系统中，有很多节点的配置是一样的，如果每个节点维护一个配置，当改动是需要一个一个的修改，不仅低效而且造成服务可靠性降低，通过zk可以很容易解决这样的问题，配置集中存放在zk的一个node中，所有用到此配置的节点watch此node，当启动时拉取配置，当配置修改时得到通知更新配置。
``` bash
组员管理(Group Membership) 
在典型的Master-Slave结构的分布式系统中，Master需要作为“总管”来管理所有的Slave, 当有Slave加入，或者有Slave宕机，Master都需要感知到这个事情，然后作出对应的调整，以便不影响整个集群对外提供服务。以HBase为 例，HMaster管理了所有的RegionServer，当有新的RegionServer加入的时候，HMaster需要分配一些Region到该 RegionServer上去，让其提供服务；当有RegionServer宕机时，HMaster需要将该RegionServer之前服务的 Region都重新分配到当前正在提供服务的其它RegionServer上，以便不影响客户端的正常访问。下面是这种场景下使用ZooKeeper的基本步骤：
Master在ZooKeeper上创建/service/slaves结点，并设置对该结点的Watcher
每个Slave在启动成功后，创建唯一标识自己的临时性(Ephemeral)结点/service/slaves/${slave_id}，并将自己地址(ip/port)等相关信息写入该结点
Master收到有新子结点加入的通知后，做相应的处理
如果有Slave宕机，由于它所对应的结点是临时性结点，在它的Session超时后，ZooKeeper会自动删除该结点
Master收到有子结点消失的通知，做相应的处理
```

<!--more -->

> 发布订阅 

watch一个节点，节点信息变更会得到通知。

> 分布式锁服务  

> 1. 简单互斥锁(Simple Lock) 
我们知识，在传统的应用程序中，线程、进程的同步，都可以通过操作系统提供的机制来完成。但是在分布式系统中，多个进程之间的同步，操作系统层面就无能为 力了。这时候就需要像ZooKeeper这样的分布式的协调(Coordination)服务来协助完成同步，下面是用ZooKeeper实现简单的互斥 锁的步骤，这个可以和线程间同步的mutex做类比来理解：
多个进程尝试去在指定的目录下去创建一个临时性(Ephemeral)结点 /locks/my_lock
ZooKeeper能保证，只会有一个进程成功创建该结点，创建结点成功的进程就是抢到锁的进程，假设该进程为A
其它进程都对/locks/my_lock进行Watch
当 A进程不再需要锁，可以显式删除/locks/my_lock释放锁；或者是A进程宕机后Session超时，ZooKeeper系统自动删除 /locks/my_lock结点释放锁。此时，其它进程就会收到ZooKeeper的通知，并尝试去创建/locks/my_lock抢锁，如此循环反 复
互斥锁(Simple Lock without Herd Effect) 
上一节的例子中有一个问题，每次抢锁都会有大量的进程去竞争，会造成羊群效应(Herd Effect)，为了解决这个问题，我们可以通过下面的步骤来改进上述过程：
每个进程都在ZooKeeper上创建一个临时的顺序结点(Ephemeral Sequential) /locks/lock_${seq}
${seq}最小的为当前的持锁者(${seq}是ZooKeeper生成的Sequenctial Number)
其它进程都对只watch比它次小的进程对应的结点，比如2 watch 1, 3 watch 2, 以此类推
当前持锁者释放锁后，比它次大的进程就会收到ZooKeeper的通知，它成为新的持锁者，如此循环反复
这里需要补充一点，通常在分布式系统中用ZooKeeper来做Leader Election(选主)就是通过上面的机制来实现的，这里的持锁者就是当前的“主”。

> 2. 读写锁(Read/Write Lock) 
我们知道，读写锁跟互斥锁相比不同的地方是，它分成了读和写两种模式，多个读可以并发执行，但写和读、写都互斥，不能同时执行行。利用ZooKeeper，在上面的基础上，稍做修改也可以实现传统的读写锁的语义，下面是基本的步骤:
每个进程都在ZooKeeper上创建一个临时的顺序结点(Ephemeral Sequential) /locks/lock_${seq}
${seq}最小的一个或多个结点为当前的持锁者，多个是因为多个读可以并发。需要写锁的进程，Watch比它次小的进程对应的结点，需要读锁的进程，Watch比它小的最后一个写进程对应的结点。当前结点释放锁后，所有Watch该结点的进程都会被通知到，他们成为新的持锁者，如此循环反复同步

> 3. 屏障(Barrier) 
在分布式系统中，屏障是这样一种语义: 客户端需要等待多个进程完成各自的任务，然后才能继续往前进行下一步。下用是用ZooKeeper来实现屏障的基本步骤：
Client在ZooKeeper上创建屏障结点/barrier/my_barrier，并启动执行各个任务的进程
Client通过exist()来Watch /barrier/my_barrier结点
每个任务进程在完成任务后，去检查是否达到指定的条件，如果没达到就啥也不做，如果达到了就把/barrier/my_barrier结点删除
Client收到/barrier/my_barrier被删除的通知，屏障消失，继续下一步任务。

> 4. 双屏障(Double Barrier)
双屏障是这样一种语义: 它可以用来同步一个任务的开始和结束，当有足够多的进程进入屏障后，才开始执行任务；当所有的进程都执行完各自的任务后，屏障才撤销。下面是用ZooKeeper来实现双屏障的基本步骤：
进入屏障：
Client Watch /barrier/ready结点, 通过判断该结点是否存在来决定是否启动任务
每个任务进程进入屏障时创建一个临时结点/barrier/process/${process_id}，然后检查进入屏障的结点数是否达到指定的值，如果达到了指定的值，就创建一个/barrier/ready结点，否则继续等待
Client收到/barrier/ready创建的通知，就启动任务执行过程
离开屏障：
Client Watch /barrier/process，如果其没有子结点，就可以认为任务执行结束，可以离开屏障
每个任务进程执行任务结束后，都需要删除自己对应的结点/barrier/process/${process_id}

> 选主

见互斥锁


## 二、Zookeeper安装和运行
### Windows下安装运行zookeeper
<span id="windows">[前往查看linux下安装运行](#linux)</span>

#### 安装
到[官网]('http://zookeeper.apache.org/releases.html#download' '点击前往')下载zookeeper。解压。本机使用3.5.1版本。
安装JDK，**此处注意JDK安装不用使用默认目录并且安装的目录不能有空格的目录名**否则Zookeeper不能正确识别JAVA_HOME。会报以下错误
> JAVA_HOME is incorrectly set.

这个错误在zkEnv.cmd中抛出。
JDK安装好后，需要设置环境变量。此处安装在**D:\Java\jdk**
>1. JAVA_HOME:D:\Java\jdk
>2. CLASS_PATH:%JAVA_HOME%\lib
>3. Path:%JAVA_HOME%\bin\java.exe

注意Path设置时最好带上java.exe。
可选的环境变量为Zookeeper的环境变量设置，此处略。
验证：
> java -version

配置配置文件：
> 复制zookeeper目录conf下zoo_sample.cfg改名为zoo.cfg

#### 运行
打开zookeeper目录下的bin目录，运行**zkServer.cmd**即可启动服务端。输出类似信息：
``` cmd
C:\WINDOWS\system32>call D:\Java\jdk\bin\java "-Dzookeeper.log.dir=E:\zookeeper-3.5.1-alpha\bin\..\logs" "-Dzookeeper.root.logger=INFO,CONSOLE" "-Dzookeeper.log.file=zookeeper-lenovo-server-LENOVO-PC.log" "-XX:+HeapDumpOnOutOfMemoryError" "-XX:OnOutOfMemoryError=cmd /c taskkill /pid %%p /t /f" -cp "E:\zookeeper-3.5.1-alpha\bin\..\build\classes;E:\zookeeper-3.5.1-alpha\bin\..\build\lib\*;E:\zookeeper-3.5.1-alpha\bin\..\*;E:\zookeeper-3.5.1-alpha\bin\..\lib\*;E:\zookeeper-3.5.1-alpha\bin\..\conf" org.apache.zookeeper.server.quorum.QuorumPeerMain "E:\zookeeper-3.5.1-alpha\bin\..\conf\zoo.cfg"
2015-12-16 09:16:40,031 [myid:] - INFO  [main:QuorumPeerConfig@114] - Reading configuration from: E:\zookeeper-3.5.1-alpha\bin\..\conf\zoo.cfg
2015-12-16 09:16:40,046 [myid:] - INFO  [main:QuorumPeerConfig@316] - clientPortAddress is 0.0.0.0/0.0.0.0:2181
2015-12-16 09:16:40,046 [myid:] - INFO  [main:QuorumPeerConfig@320] - secureClientPort is not set
2015-12-16 09:16:40,046 [myid:] - INFO  [main:DatadirCleanupManager@78] - autopurge.snapRetainCount set to 3
2015-12-16 09:16:40,046 [myid:] - INFO  [main:DatadirCleanupManager@79] - autopurge.purgeInterval set to 0
2015-12-16 09:16:40,046 [myid:] - INFO  [main:DatadirCleanupManager@101] - Purge task is not scheduled.
2015-12-16 09:16:40,046 [myid:] - WARN  [main:QuorumPeerMain@122] - Either no config or no quorum defined in config, running  in standalone mode
2015-12-16 09:16:40,093 [myid:] - INFO  [main:QuorumPeerConfig@114] - Reading configuration from: E:\zookeeper-3.5.1-alpha\bin\..\conf\zoo.cfg
2015-12-16 09:16:40,093 [myid:] - INFO  [main:QuorumPeerConfig@316] - clientPortAddress is 0.0.0.0/0.0.0.0:2181
2015-12-16 09:16:40,093 [myid:] - INFO  [main:QuorumPeerConfig@320] - secureClientPort is not set
2015-12-16 09:16:40,109 [myid:] - INFO  [main:ZooKeeperServerMain@113] - Starting server
2015-12-16 09:16:49,234 [myid:] - INFO  [main:Environment@109] - Server environment:zookeeper.version=3.5.1-alpha-1693007, built on 07/28/2015 07:19 GMT
2015-12-16 09:16:49,234 [myid:] - INFO  [main:Environment@109] - Server environment:host.name=lenovo-PC
2015-12-16 09:16:49,234 [myid:] - INFO  [main:Environment@109] - Server environment:java.version=1.8.0_65
2015-12-16 09:16:49,234 [myid:] - INFO  [main:Environment@109] - Server environment:java.vendor=Oracle Corporation
2015-12-16 09:16:49,234 [myid:] - INFO  [main:Environment@109] - Server environment:java.home=D:\Java\jdk\jre
```
之后运行**zkCli.cmd**即可运行客户端并链接上zookeeper服务端。
输出类似信息：
``` cmd
WatchedEvent state:SyncConnected type:None path:null
[zk: localhost:2181(CONNECTED) 0]
```

### Linux下安装运行zookeeper
> 本机环境ubuntu 32位desktop

<span id="linux">[前往查看windows下安装运行](#windows)</span>

#### 安装
下载zookeeper，jdk。分别解压到/usr/local，/usr/lib，重命名。
``` bash
cd /home/docker/Downloads/

sudo tar -zxvf jdk-8u65-linux-i586.tar.gz -C /usr/lib

sudo tar -zxvf zookeeper-3.4.7.tar.gz -C /usr/local/

cd /usr/lib

sudo mv jdk1.8.0_65 jdk

cd /usr/loacl

sudo mv zookeeper-3.4.7 zookeeper

sudo gedit /etc/profile
```
编辑环境变量：
``` bash
export JAVA_HOME=/usr/lib/jdk
export ZOOKEEPER_HOME=/usr/local/zookeeper
export JRE_HOME=/usr/lib/jdk/jre
export CLASSPATH=.:$JAVA_HOME/lib:$JRE_HOME/lib:$ZOOKEEPER_HOME/lib:$CLASSPATH
export PATH=$JAVA_HOME/bin:$ZOOKEEPER_HOME/bin:$JRE_HOME/bin:$JAVA_HOME:$PATH
```
**此处注意，一定要使环境变量立即生效**
> source /etc/profile

验证：
> java -version

配置zookeeper配置文件，切换目录到zookeeper/conf下
> cp zoo_sample.cfg zoo.cfg

#### 运行
服务端：
由于要创建目录，需要root权限：
> sudo su;

``` bash
root@ubuntu:/usr/local/zookeeper/conf# cd ../bin
root@ubuntu:/usr/local/zookeeper/bin# ./zkServer.sh
ZooKeeper JMX enabled by default
Using config: /usr/local/zookeeper/bin/../conf/zoo.cfg
Usage: ./zkServer.sh {start|start-foreground|stop|restart|status|upgrade|print-cmd}
root@ubuntu:/usr/local/zookeeper/bin# ./zkServer.sh start
ZooKeeper JMX enabled by default
Using config: /usr/local/zookeeper/bin/../conf/zoo.cfg
Starting zookeeper ... STARTED
```
使用客户端链接：
``` bash
root@ubuntu:/usr/local/zookeeper/bin#./zkCli.sh
```

## 关于报错
windows下一般配置有错或是目录有空格之类的。
linux下可能是环境变量没有立即生效的。本机测试，在普通用户下使用 source /etc/profile后切换到root用户，启动出错，原因是root用户下环境变量没起作用，再用一遍source /etc/profile后启动成功。

输出日志可以查看zookeeper目录下bin目录里的zookeeper.out文件。

## 三、结构
### 总体结构
    zk是树形结构的，类似xml结构。每一个节点可以存储信息，可以有子节点。
#### 节点模型
``` bash
czxid – The zxid of the change that caused this znode to be created.
mzxid – The zxid of the change that last modified this znode.
ctime – The time in milliseconds from epoch when this znode was created.
mtime – The time in milliseconds from epoch when this znode was last modified.
version – The number of changes to the data of this znode.
cversion – The number of changes to the children of this znode.
aversion – The number of changes to the ACL of this znode.
ephemeralOwner – The session id of the owner of this znode if the znode is an ephemeral node. If it is not an ephemeral node, it will be zero.
dataLength – The length of the data field of this znode.
numChildren – The number of children of this znode.
cZxid = 0x3//节点id，创建时指定，创建时事务id
ctime = Thu Dec 17 21:04:51 CST 2015//创建时间
mZxid = 0x1b//节点更新id，每次更新节点，id就会改变。更新时事务ID
mtime = Thu Dec 17 21:31:00 CST 2015//更新时间
pZxid = 0x6//父节点id
cversion = 3//子节点节点数变化次数，删除，创建子节点会使数字增加。子节点版本
dataVersion = 1//数据版本，每次set值会使数据版本增加
aclVersion = 0//权限版本
ephemeralOwner = 0x0//如果是临时节点，这是此节点对应的sessionId
dataLength = 3//本节点数据长度
numChildren = 3//子节点数
```

## 四、zoo.cfg配置项说明
> 1. tickTime：心跳时间，毫秒为单位。
> 2. initLimit：这个配置项是用来配置 Zookeeper 接受客户端（这里所说的客户端不是用户连接 Zookeeper服务器的客户端，而是 Zookeeper 服务器集群中连接到 Leader 的 Follower 服务器）初始化连接时最长能忍受多少个心跳时间间隔数。当已经超过 10 个心跳的时间（也就是 tickTime）长度后 Zookeeper 服务器还没有收到客户端的返回信息，那么表明这个客户端连接失败。总的时间长度就是 10*2000=20 秒。
> 3. syncLimit：这个配置项标识 Leader 与 Follower 之间发送消息，请求和应答时间长度，最长不能超过多少个 tickTime 的时间长度，总的时间长度就是 5*2000=10 秒。
> 4. dataDir：存储内存中数据库快照的位置。
> 5. clientPort：监听客户端连接的端口
> 6. server.A=B：C：D：其中 A 是一个数字，表示这个是第几号服务器，范围是1-255；B 是这个服务器的 ip 地址；C 表示的是这个服务器与集群中的 Leader 服务器交换信息的端口；D 表示的是万一集群中的 Leader 服务器挂了，需要一个端口来重新进行选举，选出一个新的 Leader，而这个端口就是用来执行选举时服务器相互通信的端口。如果是伪集群的配置方式，由于 B 都是一样，所以不同的 Zookeeper 实例通信端口号不能一样，所以要给它们分配不同的端口号。
集群时需要在dataDir目录下创建myid文件，将内容设置为上⑥中的A值，用来标识不同的服务器。