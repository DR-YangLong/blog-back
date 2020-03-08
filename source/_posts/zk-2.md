title: Zookeeper第二课
date: 2016-04-04 19:00:56
categories: [zookeeper]
tags: [zk,zookeeper]
---

# WIN下zookeeper单机集群
版本：Release 3.5.1-alpha 点击下载：http://mirror.reverse.net/pub/apache/zookeeper/zookeeper-3.5.1-alpha/zookeeper-3.5.1-alpha.tar.gz

## 说明
zookeeper单机集群和集群类似，只不过需要修改zk数据交换端口，选举端口和客户端链接端口
## 一、配置zoo.cfg
将下载的文件解压，先如下配置
~~~ bash
tickTime=2000
initLimit=10
syncLimit=5
dataDir=d:\\tmp\\zookeeperA
clientPort=2181
~~~
到d盘创建相应目录，到zk的bin目录下执行zkServer.cmd，先确保单机没问题(如果报错，一般是JDK安装目录带了空格，重装JDK到没有空格的目录然后修改环境变量即可)，成功后关闭zk，并复制zk整个目录，复制2份。然后分别如下修改：
<!-- more -->

> 1：

~~~bash
tickTime=2000
initLimit=10
syncLimit=5
dataDir=d:\\tmp\\zookeeperA
dataLogDir=d:\\tmp\\zookeeperA\\log
clientPort=2181
server.1=localhost:2888:3888
server.2=localhost:2889:3889
server.3=localhost:2890:3890
~~~

> 2：

~~~bash
tickTime=2000
initLimit=10
syncLimit=5
dataDir=d:\\tmp\\zookeeperB
dataLogDir=d:\\tmp\\zookeeperB\\log
clientPort=2182
server.1=localhost:2888:3888
server.2=localhost:2889:3889
server.3=localhost:2890:3890
~~~

> 3：

~~~bash
tickTime=2000
initLimit=10
syncLimit=5
dataDir=d:\\tmp\\zookeeperC
dataLogDir=d:\\tmp\\zookeeperC\\log
clientPort=2183
server.1=localhost:2888:3888
server.2=localhost:2889:3889
server.3=localhost:2890:3890
~~~

## 二、创建myid文件
按照配置中
> dataDir=d:\\tmp\\zookeeper[X]

在相应目录中创建剩下2个zk的数据目录，然后到**zookeeper[X]**目录中创建不带后缀名的文件，分别
> A下的填入1，B下的填入2，C下的填入3

然后保存。

## 三、启动并验证
#### 运行
分别到3个zk的bin目录下依次(不一定按ABC的顺序)运行zkServer.cmd(如果报配置不可用，IP:端口不可到达，请注意删除配置文件中端口后的空格等不可见的特殊字符)。

#### 验证
启动其中一个zk的bin目录下的zkCli.cmd，此处为zkA的，执行：
> create /zkcluster "data"

创建一个节点。之后可以使用命令行或者配置参数连接另外一个zk，此处为zkB的：
> get /zkcluster

如看到
> "data"

则说明集群成功。
