title: JVM参数
date: 2017-04-20 20:09:56
categories: [java]
tags: [java]
---
## 通用参数
|参数|含义|默认值|说明|
|:--:|:--:|:--:|:--:|
|-server|启用server模式|--|启用server模式启动时较慢，编译将在启动时完成，在运行时能获得更好的性能|
|-client|启用client模式|--|启用client模式，jvm不会对编译阶段优化，启动较快，运行时性能较低|
|-Xms|初始堆大小|物理内存的1/64(<1GB>)|默认空余堆内存小于40%时（通过MinHeapFreeRatio参数调整），jvm就会增大堆内存，直到为-Xmx的值。|
|-Xmx|最大堆大小|物理内存的1/4(<1GB)|默认空余堆内存大于70%时（通过MaxHeapFreeRatio参数调整），jvm会减少堆内存，直到-Xms的值。|
|-Xmn|年轻代大小，jdk>=1.4|不设置此值，年轻代默认为堆的1/3|此值的大小是eden+2个survivor的大小|
|-XX:NewRatio|年轻代与年老代的比值倒数|2|设为4，表示年轻代:年老代=1:4，年轻代占堆内存1/(4+1)，如果设置Xms=Xmx且设置了Xmn，该参数不需要设置|
|-XX:SurvivorRatio|1个Survivor与Eden的比值倒数|8|设置为6，则Survivo:Eden=1:6，那么2个Survivor总共占年轻代的2/(6+1+1)大小|
|-XX:PermSize|设置持久代初始大小|物理内存的1/64|JDK1.8之前适用|
|-XX:MaxPermSize|设置持久代最大值|物理内存的1/4|JDK1.8之前适用|
|-Xss|每个线程的栈大小|JDK5以后默认为1M，之前为256K|在相同的物理内存下，减少此值能够生成更多的线程，但不会无限生产，受操作系统限制，大小需要根据具体应用设置|
|-XX:ThreadStackSize|栈深度|根据操作系统不同而不同|设为0表示使用操作系统默认栈深度|
|-XX:LargePageSizeInBytes|内存页大小|--|内存页大小不能设置太大，会影响perm大小|
|-XX:+UseFastAccessorMethods|启用原始类型快速优化|--|--|
|-XX:+DisableExplicitGC|关闭System.gc()|--|关闭后，代码中调用此方法失效|
|-XX:MaxTenuringThreshold|对象最大年龄|15|每经过一次垃圾回收，存活下来的对象年龄+1，达到此值时，对象从年轻代移动到年老代，如果设置为0，则对象直接在年老代中分配，该参数只有在串行GC时有效|
|-XX:+AggressiveOpts|加快编译|--|--|
|-XX:+UseBiasedLocking|启用偏向锁|--|启用锁优化，在锁低竞争程序中，启用偏向锁有助于性能提升，在锁竞争激烈的程序中，启用偏向锁会降低性能|
|-Xnoclassgc|禁用垃圾回收|--|--|
|-XX:SoftRefLRUPolicyMSPerMB|每MB空闲空间中软引用的存活时间|1s|默认单位为秒|
|-XX:PretenureSizeThreshold|对象超过此参数设置的值时直接在年老代分配|0|单位为字节，0时此参数不起作用，且采用Parallel Scavenge GC时无效|
|-XX:TLABWasteTargetPercent|TLAB（Thread-local allocation buffer）占eden区的百分比|1%|--|
|-XX:+CollectGen0First|full GC前是否先进行一次young GC|false|--|  

<!-- more -->
## 辅助信息参数

|参数|含义|默认值|说明|	
|:--:|:--:|:--:|:--:|	
|-XX:+PrintGC|打印GC信息|--|打印为GC简要信息，非稳定参数，已经标记为manageable|	
|-verbose:gc|打印GC信息|--|GC简要信息，上面的稳定版本|	
|-XX:+PrintGCDetails|打印GC详细信息|--|打印GC详细信息|	
|-XX:+PrintGCTimeStamps|输出GC的时间戳（以基准时间的形式）|--|输出形式:–[GC[DefNew: 4416K->0K(4928K), 0.0001897 secs] 4790K->374K(15872K), 0.0002232 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] |	
|-XX:+PrintGCApplicationStoppedTime|打印垃圾回收期间程序暂停的时间。可与上面混合使用|--|输出形式:Total time for which application threads were stopped: 0.0468229 seconds|	
|-XX:+PrintGCApplicationConcurrentTime|打印每次垃圾回收前，程序未中断的执行时间。可与上面混合使用|--|输出形式:Application time: 0.5291524 seconds|	
|-XX:+PrintHeapAtGC|打印GC前后的详细堆栈信息|--|--|	
|-Xloggc:filename|设置打印日志文件|--|设置绝对路径|	
|-XX:+PrintClassHistogram|垃圾回收前打印Histogram情况|--|使用G1收集器时有效|	
|-XX:+PrintTLAB|打印TLAB空间的使用情况|--|--|	
|XX:+PrintTenuringDistribution|打印每次minor GC后新的存活周期的阈值|--|--|	

> GC日志分析

```txt
 [GC (Allocation Failure) 13817.377: [ParNew: 587356K->21969K(652288K), 0.0280778 secs] 815480K->250094K(1479680K), 0.0281542 secs] [Times: user=0.06 sys=0.00, real=0.03 secs] 
GC (Allocation Failure) ：引起GC的原因。
[ParNew: 587356K->21969K(652288K), 0.0280778 secs]：GC的类型，ParNew为新生代垃圾收集器，说明是Minor GC，587356K为GC前新生代内存使用量，21969K为GC后新生代内存使用用，(652288K)为新生代总内存大小。
815480K->250094K(1479680K)：815480K为Minor GC前堆内存总使用量，250094K为GC后堆内存总是用量，(1479680K)为堆总内存大小。
时间为GC耗时。
[Times: user=0.06 sys=0.00, real=0.03 secs]：user为程序耗时，是参与GC的所有cpu（cpu核心）耗时总和，sys为系统（内核）耗时，real为GC真正耗时

[Full GC (System.gc()) 13952.460: [CMS: 228124K->185622K(827392K), 1.9256164 secs] 350869K->185622K(1479680K), [Metaspace: 313411K->313411K(557056K)], 1.9271636 secs] [Times: user=0.91 sys=0.97, real=1.93 secs] 
使用System.gc()进行FullGC，年老代使用CMS垃圾收集器，GC前年老代使用内存为228124K，GC后使用185622K，年老代总内存827392K，整个堆GC前使用350869K，GC后使用185622K，总共内存为1479680K，Metaspace GC前使用313411K，GC后使用313411K，整个Metaspace内存大小为557056K。
```

## 垃圾收集器参数
-XX:+UseSerialGC：使用Serial+Serial Old收集器组合。
-XX:+UseParNewGC：使用ParNew+Serial Old收集器组合。
-XX:+UseParallelGC：使用Parallel Scavenge+Serial Old收集器组合。
-XX:+UseParallelOldGC：使用Parallel Scavenge+Parallel Old收集器组合。
-XX:+UseConcMarkSweepGC：使用ParNew+CMS+Serial Old收集器组合，Serial Old收集器作为CMS出现“Concurrent Mode Failure”时的后备收集器。

### 使用Parallel Scavenge垃圾收集器可用的参数：
> -XX:MaxGCPauseMillis：必须设为>0的整数，垃圾收集最大停顿时间。

> -XX:GCTimeRatio：垃圾收集器运行时间和用户程序运行时间比值倒数，如95，则允许5%的GC时间。

> -XX:UseAdaptiveSizePolicy：功能开关，设置此参数后，不需要配置新生代大小，Eden和Survivor比例，对象分代年龄参数，JVM动态调整堆中各个区域的大小以及对象分代年龄，提供合适的GC停顿时间。

### 使用CMS垃圾收集器参数：
> -XX:+UseCMSInitiatingOccupancyOnly：使JVM一直使用手动设置的JVM参数，不自动优化调节。

> -XX:+CMSParallelInitialMarkEnabled：初始标记阶段使用多线程并行。

> -XX:+CMSScavengeBeforeRemark：在进行重新标记之前，先进行一次young gc，这个参数可以防止年轻代中引用了年老代中的对象，导致这些年老代对象无法回收，例如ArrayList动态扩容时，ArrayList中的数组存在跨代引用，导致OOM。

> -XX:+CMSParallelRemarkEnabled：在重新标记时使用多线程并行。

> -XX:CMSInitiatingOccupancyFraction：设置CMS收集器在老年代空间被使用多少后出发垃圾收集，默认值为68%。有效设置此值能减少“Concurrent Mode Failure”发生概率，基本公式：(Tenured-Eden-Survivor)/Tenured。

> -XX:+UseCMSCompactAtFullCollection：由于CMS收集器会产生碎片，此参数设置表示CMS收集器顶不住要进行Full GC时开启内存碎片整理，默认是开启的。

> -XX:+CMSFullGCBeforeCompaction：设置CMS收集器在进行若干次垃圾收集后再进行一次内存碎片整理过程，通常与UseCMSCompactAtFullCollection参数一起使用，降低Full GC发生概率。

### 所有并行并发收集器适用参数
-XX:ParallelGCThreads：用于垃圾收集的线程数量，一般设置为CPU核心数。