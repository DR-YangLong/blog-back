title: Shiro集群环境下丢失session
date: 2015-07-07 22:09:56
categories: [java]
tags: [java,shiro,shiro 丢失session]
---

#Shiro在集群环境下会丢失session解决
Shiro在集群时可能会选择Redis，ehcache，membercache等作为集群缓存，存放登陆认证的信息，有时候会存在丢失session的情况，通过分析，shiro自己实现了一个session，如下
```java
public class SimpleSession implements ValidatingSession, Serializable {

    // Serialization reminder:
    // You _MUST_ change this number if you introduce a change to this class
    // that is NOT serialization backwards compatible.  Serialization-compatible
    // changes do not require a change to this number.  If you need to generate
    // a new number in this case, use the JDK's 'serialver' program to generate it.
    private static final long serialVersionUID = -7125642695178165650L;

    //TODO - complete JavaDoc
    private transient static final Logger log = LoggerFactory.getLogger(SimpleSession.class);

    protected static final long MILLIS_PER_SECOND = 1000;
    protected static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;
    protected static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;

    //serialization bitmask fields. DO NOT CHANGE THE ORDER THEY ARE DECLARED!
    static int bitIndexCounter = 0;
    private static final int ID_BIT_MASK = 1 << bitIndexCounter++;
    private static final int START_TIMESTAMP_BIT_MASK = 1 << bitIndexCounter++;
    private static final int STOP_TIMESTAMP_BIT_MASK = 1 << bitIndexCounter++;
    private static final int LAST_ACCESS_TIME_BIT_MASK = 1 << bitIndexCounter++;
    private static final int TIMEOUT_BIT_MASK = 1 << bitIndexCounter++;
    private static final int EXPIRED_BIT_MASK = 1 << bitIndexCounter++;
    private static final int HOST_BIT_MASK = 1 << bitIndexCounter++;
    private static final int ATTRIBUTES_BIT_MASK = 1 << bitIndexCounter++;

    private transient Serializable id;
    private transient Date startTimestamp;
    private transient Date stopTimestamp;
    private transient Date lastAccessTime;
    private transient long timeout;
    private transient boolean expired;
    private transient String host;
    private transient Map<Object, Object> attributes;
```

可以看到，很多属性是**transient**修饰的，也就是说，当我们不使用JDK原生序列化机制时，这些属性不会被序列化，特别是源码注释也说明了这一点，而我们使用的序列化器往往考虑到性能，使用的是ASM或反射机制来实现的序列化，因而在没有是否序列化transient属性的设置时，往往默认不序列化，例如Kryo，已经是一个定死的属性，不序列化transient属性。
那么解决的方式也很简单：
1. 使用兼容原生序列化机制的序列化器。
2. 重新SimpleSession