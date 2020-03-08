title: Zookeeper第四课-watcher
date: 2016-04-10 20:15:00
categories: [zookeeper]
tags: [zk,zookeeper]
---

# Zookeeper的watcher
在zookeeper中，有个很重要的概念就是watcher，可以看做是swing中的listener，只要你注册了watcher，那么当zk节点被操作时，你就可以通过watcher感知到。也可以理解为事件回调接口。
客户端通过
> public class ZooKeeper{...}

进行远程连接和维护结构。

## zookeeper watcher接口
~~~
graph TD
A["interface Watcher"] -->|innerClass|B["interface Event"]
B -->|innerClass|D["enum KeeperState"]
B -->|innerClass|C["enum EventType"]
~~~
<!-- more -->
### Watcher接口
watcher只有一个方法
> abstract public void process(WatchedEvent event);

~~~ java
public class WatchedEvent {
    final private KeeperState keeperState;
    final private EventType eventType;
    private String path;
    ...
    }
~~~

### 客户端watcher
通过Zookeeper的构造函数注册watcher：
> 1. public ZooKeeper(String connectString, int sessionTimeout, Watcher watcher)
> 2. ZooKeeper(String connectString, int sessionTimeout, Watcher watcher,boolean canBeReadOnly)
> 3. public ZooKeeper(String connectString, int sessionTimeout, Watcher watcher,long sessionId, byte[] sessionPasswd)
> 4. public ZooKeeper(String connectString, int sessionTimeout, Watcher watcher,long sessionId, byte[] sessionPasswd, boolean canBeReadOnly)

wather注册后，服务器端会维护watcher，一旦watcher监听的节点被操作，将会发通知给客户端，客户端接收到通知后，会调用watcher的**process**方法。

> zk的watcher只会起一次作用，一旦接收到通知调用后，这个watcher就失效了，如果还要监听此节点，需要重新注册watcher

### 服务端watcher维护
客户端注册watcher后，服务端将会维护已经注册到自己这端的watcher。
服务端会根据watcher监听的节点是否是父节点，将watcher分成2类。如果此节点没有子节点，那么只会监听本节点的变更，如果有子节点，那么会同时监听子节点和本节点的变更。
在服务端，watcher的维护分2个维度，1个是path到watcher，一个是watcher到path。
> private final HashMap<String, HashSet<Watcher>> watchTable =
        new HashMap<String, HashSet<Watcher>>();

> private final HashMap<Watcher, HashSet<String>> watch2Paths =
        new HashMap<Watcher, HashSet<String>>();

### Watcher实现
zk服务端节点被操作，对应不同的EventType。服务端和客户端通过不同的数值代码表示不同的事件：
~~~ java
case -1: return EventType.None;
case  1: return EventType.NodeCreated;
case  2: return EventType.NodeDeleted;
case  3: return EventType.NodeDataChanged;
case  4: return EventType.NodeChildrenChanged;
~~~
另外zk同样通过不同的状态码表示此时客户端与服务端的状态：
~~~ java
case   -1: return KeeperState.Unknown;
case    0: return KeeperState.Disconnected;
case    1: return KeeperState.NoSyncConnected;
case    3: return KeeperState.SyncConnected;
case    4: return KeeperState.AuthFailed;
case    5: return KeeperState.ConnectedReadOnly;
case    6: return KeeperState.SaslAuthenticated;
case -112: return KeeperState.Expired;
~~~
当用户自定义实现watcher接口后，在监听的节点变更后，方法
> void process(WatchedEvent event)

会被调用，用户在此方法中可以根据**事件类型（EventType）** 和 **状态类型（KeeperState）** 来判断需要进行什么样的业务逻辑。
