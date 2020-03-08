title: Zookeeper第五课
date: 2016-04-15 20:30:00
categories: [zookeeper]
tags: [zk,zookeeper]
---

~~~
watcher由于通讯问题，如果是高并发，频繁写入或更新时候，并不是所有的变更通知都会通过watcher感知到，所以最好改用消息中间件来处理。
~~~

# ZAB协议
Zookeeper Atomic Broadcast即原子消息广播协议。

## ZAB协议用在哪些地方
选举过程，数据写入过程。
**zab的核心是定义了那些会改变zk服务器数据状态的事务请求的处理方式。**

> 所有事物请求必须由一个全局唯一的服务器来协调处理，这个服务器被称为leader服务器，余下的服务器则称为follower。leader负责将一个客户端请求转换成一个事务Proposal（提议），并将该Proposal分发给集群中所有的follower。之后leader等待所有follower的反馈，一旦超过半数的follower进行了正确的反馈，那么leader就会再次向所有的follower分发commit消息，要求follower将前一个Proposal进行提交。

<!--more-->
### ZAB协议的三个阶段：
发现（discovery），即选举Leader过程。
同步（synchronization），即选举出Leader后，follower或者observer从leader同步最新数据。
广播，同步完成后，leader接收客户端的新事务请求并进行消息广播，实现数据在集群节点的副本存储

###  zk的选举

#### 角色
> Leader

1、事务求情的**唯一**调度和处理者，保证集群事务处理的顺序性。
2、集群内部各服务器的调度者。

> Follower(Learner)

1、处理客户端非事务请求，**转发事务请求**给Leader。
2、参与事务请求的Proposal的投票。
3、参与Leader选举投票。

> Observer(Learner)

1、处理客户端非事务请求，**转发事务请求**给Leader。
2、不参与**任何形式**的投票，包括选举和事务投票。
3、这个角色的存在是为了提高读性能。

#### 状态
> LOOKING

寻找leader。处于此状态 时，表示当前没有Leader，需要进入选举流程。
> FOLLOWING

跟随者状态，表示当前服务器角色是Follower。
> OBSERVING

观察者状态，表示当前服务器角色是Observer。
>LEADING

领导者状态，表示当前服务器角色是Leader。

> 状态的维护是通过org.zookeeper.quorum.ServerState类进行维护的。

#### 集群通讯

> 通讯协议

基于TCP协议，为了避免重复连接，采取的策略是按照集群中节点的myid数值大小来建立连接：myid大的节点向myid小的节点发起连接，如果当前节点发现发起连接的节点的myid比自己的小时，会关闭连接。
```bash
3个节点，
A[myid=1]、B[myid=2]、C[myid=3]，则C分别向A、B发起连接，B向A发起连接，总共有3个连接。
```

>  缺点，不能发现新节点的加入，新节点加入需要修改配置，重启整个集群节点，成本相对较高

> 通讯端口

多端口通讯。在进行集群**server**配置时，第一个端口是通讯和数据同步端口，默认2888；第二个端口是投票端口，摩恩是3888。客户端端口使用cilentPort配置，默认是2181。

#### 选举算法
各个版本支持协议不同，从3.4.0版本后只支持FastLeaderElection协议。
> 1. LeaderElection：支持UDP协议
> 2. FastLeaderElection：支持UDP，TCP协议
> 3. AuthFastLeaderElection：支持UDP协议

#### 产生选举的情景
> 集群启动时

各节点处于寻找Leader状态，表示当前没有Leader，进入选举流程。
> 崩溃恢复【Leader下线】

Leader宕机，或者因网络原因，导致**过半**节点与Leader心跳中断。

#### 影响称为Leader的因素
> 数据新旧程度

只有拥有最新数据的节点才能有机会成为Leader。
通过事务id（zxid）的大小来表示数据的新旧，越大代表数据越新。
```bash
zxid的构成：
总长度64位，高32位代表主进程周期，低32位代表事务递增计数器。
1.主进程周期
也叫epoch。代表选举的轮次，每进行一次选举，主进程周期加一。比较数据新旧的时候，先比较epoch的大小。
2.事务单调递增计数器 每次选举完成后，重置事务计数器为0。
···

> myid

集群启动时，在data目录下配置文件中的myid代表节点在集群中的编号。zk节点数据一样新时，myid越大成为Leader的机会越大。当集群中已有Leader时，新加入的节点不会影响原来的集群。

> 投票数量

只有得到集群中**多半**的投票，才能成为Leader。多半即指n/2+1，n为集群中节点的数量。

#### 初次启动的选举
情景：有3个新配置的zk节点，将用他们组成集群，对应myid分别为1,2,3。顺序启动。
选举流程：
> 1. 启动myid为1的节点，此时zxid为0，只有一个节点，没法选举Leader。
> 2. 启动myid为2的节点，此时zxid为0，集群已经有2个节点，zxid相同，此节点myid为最大，此节点成为Leader。
> 3. 启动myid为3的节点，因为已经有主节点，3加入集群，但2仍然还是Leader。

#### 运行过程中选举
情景：3个节点server[1、2、3]，此时2为Leader，并且2宕机。
选举流程：
> 1. 变更状态。与leader心跳断开后，其他节点状态变更为LOOKING。
> 2. 每个节点发出一个投自己的投票：生成投票信息（myid，zxid）。假定：server1为(1,123)，server3为(2,122)；server1发给server3，server3发给server1。
> 3. 接收投票。
> 4. 投票处理：server3收到server1投票信息，因为server1的zxid比它自己的大，所以server3修改自己的投票信息为(1,123)，再次发起投票，发送给server1。server1收到server3的投票，因为123大于122，因此不再发起投票。
> 5.统计投票：server3统计——自己收到的投票（包括自己投出去的）中，(1,123)2票。server1统计——自己收到的投票（包括自己投的）中，（1,123）是2票。
> 6. 修改服务器状态：server3选出leader为server1，因此自己进入following状态，成为follower。server1选出的leader是server1，即自己，因此自己进入leading状态，成为新leader。


#### 同步
选主之后，由于各节点信息可能不一致，需要同步信息，使集群中各节点信息保持一致。
> 1. 同步时机：当leader完成选举后，follower需要与新的leader同步数据。
> 2. Leader同步准备：一、leader告诉其他follower当前最新数据时什么即zxid（leader构建一个newleader包，包中包含当前最大zxid，发送给所有的follower和observer）。二、leader给每个follower创建一个线程LearnerHandler来负责处理每个follower的数据同步请求，同事主线程开始阻塞，只有超过一半的follower同步完成，同步过程才完成，leader才能成为正真的leader，解除阻塞。
> 3. Follower同步准备：一、选举完成后，尝试与leader建立同步连接，如果一段时间没有连接上就报错，重新回到选举状态。二、向Leader放FOLLOWERINFO封包，带上follower自己最大的zxid。
> 4. Leader同步初始化：minCommittedLog——最小的事物日志id，即zxid（没有被快照存储的日志文件的第一条，每次快照存储完，会重新生成一个事务日志文件）。maxCommittedLog——事务日志中最大的事务，即zxid。
> 5. leader根据follower的情况不同，采取不同的算法进行数据同步。

~~~bash
同步算法：
1、直接差异化同步（DIFF同步）：
2、仅回滚同步（TRUNC），即删除多余的事务日志，比如原来的主机宕机后又重新加入，可能存在它自己写入提交但是别的节点还没来得及提交的。
3、先回滚再差异化同步（TRUNC+DIFF同步）
4、全量同步（SNAP同步）
算法举例：
场景一：
follower最后的事务zxid称作peerLastZxid
如果minCommittedLog<peerLastZxid<maxCommittedLog
同步方案：
直接差异化同步。leader给follower发送DIFF指令，意思是：进入差异化数据同步阶段，leader会把proposal同步给follower。实际同步工程会先发数据修改proposal，然后再发送COMMIT指令数据包。
~~~
场景二：
~~~ bash
leader A完成一个实物，但还没通知其他节点，自己当机，集群选出新的leader C，
A恢复后向C发起follower info消息，C发现A上有自己没有的事务，先让A TRUNC，然后再DIFF同步。
~~~
场景三：
~~~ bash
A为follower，当机很长时间，此时的如果leader的minCommittedLog比A的peerLastZxid还要大，则采用全量同步。
~~~
可以看到，同步方案有时单独使用，有时组合使用，看具体的情景。
#### 广播
集群数据同步完成（过半节点与leader同步完成）后，就可以对外提供服务，进入广播阶段。
> 1. 当leader接收到客户端新的事务请求后，会生成对应的事务proposal，并根据zxid的顺序向所有的follower发起提案，即发起proposal。
> 2. 当follower收到leader的事务proposal时，根据接收的先后顺序处理这些proposal。
> 3. 当leader收到follower针对这个事务proposal过半的ack消息后，则发起事务提交proposal。
> 4. follower收到leader发出的commit proposal后，记录事务提交，并把数据更新应用到内存数据库。

~~~bash
由于只是过半ack，集群即确认成功，所以可能存在某时刻某些节点上的数据不是最新的。如果业务上需要确定读取到的数据时最新的，那么需要在读取之前调用sync方法进行集群内节点的数据同步。
~~~
