title: ORACLE RAC
date: 2015-06-13 17:09:56
categories: [分布式系统]
tags: [ORACLE,数据库集群]
---

Oracle RAC的优势在于利用多个节点（数据库实例）组成一个数据库，这样在保证了数据库高可用性的情况下更充分的利用了多个主机的性能，而且可以通过增加节点进行性能的扩展。实现Oracle RAC需要解决的关键问题就是多节点进行数据访问时如何保证数据的一致性，Oracle是通过各节点间的私有连接进行内存融合（cache fusion）来保证各节点数据访问的一致性。用一个例子来解释一下内存融合的过程，在存在A、B两个节点的RAC环境中，当A节点使用DML语句（如Update）对一个数据块中的数据进行修改时，A节点实例会到GRD（Global Resource Directory）中查找该数据块的信息，这些信息包括该数据块的Master（第一次读这个数据块的节点），Owner（当前拥有这个数据块的节点），以及数据块在各个节点间的传递记录。A节点如果发现GRD中没有需要读取的数据块的信息，说明该数据块是一个干净的数据块，A节点从磁盘或Buffer Cache中获得该数据块，然后对需要修改的行加锁，进行相应的修改，当然SCN会随之增加。在A完成修改而没有提交或回滚的情况下，如果B节点也需要访问这个数据块修改某些行（假设不同于A修改的行），B同样去到GRD中查找该数据块的信息，当然B发现该数据块的Master为A，Owner也为A，为了保证A的修改不丢失，B需要发信息给A，让A将需要修改的数据块通过私有连接直接从内存中传给B，当然该数据块中包含A的锁信息，这样A节点与B节点间的一次内存的数据传递就是内存融合。Oracle RAC的内存融合也面临一些问题，继续刚刚的例子，如果A又再次请求对该数据块修改或者结束事务（提交或回滚）的时候，又需要从B节点内存中取得数据块，又要发生内存融合，这样在两个节点业务没有合理分割的情况下，数据库繁忙时，大量的内存融合会对数据库性能造成严重的影响。通过对Oracle RAC技术的理解，在实现Oracle RAC架构时的业务分割就成为了保证系统性能的重要手段，业务分割的根本在于使不同的实例不能访问相同的数据块，这样业务分割规则可以小到表的级别（不同的表不会共享一个数据块），大到表空间、Schema的级别。心跳应该用独立的网卡。

 

　　当然，rac本身之保证了数据库服务器的高可用和高性能，所以最好有其他的存储技术来保证存储的高可用，例如raid\vplex等。

　　在距离不太远（几十公里），且速度较快（例如裸光纤）下，延时较小，，米足够多，可以考虑使用oracle rac实现双活或者灾备，RTO RPO=0。
<!-- more -->
附：
1.rac基本理论知识
1.1 Public NIC接入公共网络，private NIC接入私有网络，这是个完全隔离的网络传递的数据只是RAC节点的心跳数据和cache fusion数据。oracle不建议私有网络直接用交叉线连接。

1.2 RAC最重要的是共享存储。数据文件、联机日志、控制文件、参数文件都必须放在共享存储上。现在的存储环境基本上都是基于SAN的，跑FC协议（FC协议封装了SCSI协议）。

1.3 RAC环境需要OS必须版本相同包括小版本、补丁包都必须一致。

1.4 集群件安装在OS之上，负责管理整个集群环境中的硬件资源并为上层的RAC集群提供基础服务。如果把集群看成是一台虚拟的计算机，那么集群件就是这台计算机上的OS，而RAC是运行在其上的一个应用。

10g之前，oracle只针对linux、windows两个OS提供了一个不完善的集群件产品OCM（oracle cluster manager），其它平台需要第三方集群产品比如HACMP、sun cluster。10g开始，10.1版本提供CRS，10.2版            本提供oracle clusterware（10.1的改名）。10.2开始的集群件提供API接口，因此还能够为其它软件提供HA功能。CRS可以和其它集群件产品集成。10g之前oralce只提供对裸设备的支持，所以9i RAC裸设备是常见选择。10后oracle提供OCFS和ASM两种存储方案。

1.5 OEL(oracle enterprise linux)是oracle在RHEL基础上重新开发出的linux。

1.6 需要强调的是，10g的clusterware的vote disk、ocr在目前的版本上还只能创建在裸设备、ocfs上。VIP在clusterware安装过程中创建（调用VIPCA)，不需要手工设置。

1.7 集群的分类：高性能计算集群（用在科学计算）、LB、HA、

1.8 健忘症：集群环境配置文件不是集中存放，每个节点都有一个本地副本，用户修改任何节点的集群配置会将更改同步到其它节点。但这样有一个问题：节点1正常维护需要关闭，然后在节点2修改了配置，然后节点2关闭，启动节点1，由于没有同步，所以节点1启动后，它用的仍然是旧的配置文件，这时就造成配置丢失，这就叫健忘症。RAC的解决方案是OCR。

1.9 脑裂:节点间了解彼此的健康状况是通过心跳机制来实现的。如果仅仅是心跳出了问题，各节点还正常运行，这时每个节点都认为其它节点宕机，自己是集群环境的唯一健在者自己应该获得集群的控制权，因为存储是共享的，这意味着数据灾难。这就叫脑裂。RAC解决脑裂的方法是引入Quorum disk，谁先得到quorum disk这一票谁获胜，另一个节点被踢出集群。

1.10 IO隔离：解决被踢出集群的节点不能操作共享存储上的数据。

1.11 CRS resource：CRS对运行于其上的应用进行监视，并在发生异常时进行重启、切换等干预手段。这些被CRS监控的对象就叫CRS resource。这些resource是在RAC安装过程中自动或手动创建的，并且注册登记到CRS中，以metadata的形式被记录在OCR磁盘上，这些metadata包括resource的名称、如何启动、停止、如何检查健康状况等配置信息。RAC的CRS resource包括GSD、ONS、VIP、database、instance、listener等。

1.12 oracle clusterware的运行环境由两个磁盘文件、若干后台进程及网络元素组成。OCR解决健忘问题，OCR位于共享存储上保存整个群集的配置，无论在哪个节点修改配置都是修改相同的配置文件。配置信息以“key-value的形式保存其中，10g以前这个文件叫server manageability repository（SRVM)。OCR的位置记录在ocr.loc文件中，9i对应的是srvConfig.loc文件。

1.13 能读写OCR disk中内容的节点称OCR master。这个节点的OCR process负责更新本地和其它节点的OCR cache。其它节点想修改OCR需要向本节点的OCR process提出请求，然后本节点的process再向OCR master的OCR process提出请求。

1.14 voting disk主要记录节点成员状态，在出现脑裂时，仲裁哪个partion获得集群的控制权。它的位置可以用这个命令来查看：$crsctl query css votedisk

1.15 clusterware最重要的三个后台进程：CRSD、CSSD、EVMD。在安装clusterware的最后阶段会要求在每个节点执行root.sh，目的是在/etc/inittab文件中添加三行使每次系统重启时，cluseter也会自动启动。EVMD和CRSD两个进程如果出现异常，系统会自动重启这两个进程。如果CSSD出现异常，系统会立即重启。

OCSSD进程：该进程提供CSS（cluster synchronization service）服务，CSS服务通过多种心跳机制实时监控集群健康状态，提供脑裂保护等基础集群服务功能。心跳机制包含两种：私有网络、voting disk。

两个心跳都有最大时延，对于disk heartbeat，这个时延叫IOT，对于network heartbeat，这个时延叫MC，两个参数都是以秒为单位，缺省IOT大于MC。可以通过以下命令来查看这两个参数：

$crsctl get css disktimeout      $crsctl get css misscount

另外这个进程还用来支持ASM instance和RDBMS instance之间的通信。如果在已经安装了ASM的节点上安装RAC，会遇到一个问题，RAC要求节点只有一个OCSSD进程，并且是运行在$CRS_HOME目录下的。这就需要先停止ASM，并通过$ORACLE_HOME/bin/localconfig.sh delete删除之前的inittab条目。

CRSD进程：实现HA的主要进程，它所提供的服务叫CRS（cluster ready service）它监控资源，并在资源运行异常时进行干预，包括关闭、重启进程或转移服务。默认情况下，CRS会自动尝试重启资源5次，如果还是失败则放弃尝试。

EVMD进程：负责发布CRS产生的各种事件，这些event可以通过两种方式发布给客户--ONS和callout script。另外它还负责CRSD和CSSD两个进程间的通信。

RACGIMON进程：负责检查数据库健康状态，负责service的启动、停止、故障转移，这个进程会建立到数据库的持久连接，定期检查SGA中的特定信息，该信息由PMON进程定时更新。

OPROCD进程：也叫process monitor daemon。在非linux平台且没有使用第三方集群软件时，会看到这个进程，用来检测节点的CPU挂起，如果调度时间超过1.5S，会认为CPU工作异常，会重启节点。在linux平台上时候时利用hangcheck-timer模块来实现IO隔离功能的。

1.16 IP利用的是TCP层超时，VIP利用的是应用层的立即响应。详见P94。

1.17 clusterware的日志体系比较复杂，日志的根目录在$CRS_HOME/log/[node]

1.18 单实例下的并发控制

lock框架包括3个组件：资源、锁、排队机制。前两者是数据结构，后者是使用算法。

资源：由owner、waiter、converter三个成员组成，这是3个指针分别指向lock组成的链表。

锁：当要访问共享资源时，需要锁定资源。如果不能获得资源的访问权，就把lock挂到资源的waiter链表中，如果能获得就挂到lock的owner链表中。

排队机制：lock使用的是enqueue的算法即“先入先出队列”。如进程的锁定不能满足，该进程的lock structure就被加到waiter链表的末端。

waiter和converter排队机制的区别：如果某个操作需要先后两种不同模式的锁，比如先share mode然后exclusive mode，则进程先请求share mode，获得后的lock structure会挂在owner上，当需要exclusive mode锁时，进程必须先释放share mode的锁，然后再次申请exclusive mode的锁，但是可能无法立即获得，这时这个请求就会被挂在converter队列下，converter队列会优先于waiter队列被处理。可以从v$lock看到这些lock信息。

LMODE>0,REQUEST=0   Owner

LMODE=0,REQUEST>0   Waiter（Acquirer）

LMODE>0,REQUEST>0   Converter

对于粗粒度或数量有限的资源使用这种机制还可以，但对于几百G的数据量，如果每条记录都分配一个resourece、lock数据结构，无论从内存需求还是维护开销都是一个噩梦。对于数据记录这种细粒度资源，oracle使用的是行级锁。行级锁其实只是数据块头、数据记录头的一些字段，不会消耗资源。所以它虽然有锁的功能，但无锁的开销。详见书的P102。

latch于lock的对比：latch请求、获得、释放等操作是原子操作，一般几个硬件指令就可以完成。lock相反。进程申请lock没有获得，这个进程会释放CPU资源，也就是进行上下文切换，整个过程较耗资源。如果进程申请latch没有获得，进程不释放CPU资源，而是不断的尝试请求，只有尝试了一定次数之后还不能获得，才释放CPU，这就是latch的spin机制。这时的表现是：CPU利用率非常高，但吞吐量却很低。另外latch使用的是抢占机制，而不是lock使用的排队机制。

1.19 DLM（分布式锁管理）：可以把它想象成一个仲裁，它记录着哪个节点正在用哪种方式操作哪个数据，并负责协调解决节点间的竞争。DLM在不同的阶段有不同的名称，OPS的叫PCM，RAC的叫cache fusion。

Non-Cache Fusion资源：典型的资源包括sharepool的row cache和library cache内容。通过每个节点的LCK0进程来同步。

Cache Fusion资源：buffer cache的数据块。数据块的状态可以从v$BH视图查看。

以上两种资源可以通过v$resource_limit来查看。

1.20 GRD：记录每个数据块在集群间的分布图。每个实例SGA中都是部分GRD，所有实例的GRD构成一个完整的GRD。

1.21 PCM lock：GRD中记录的是PCM lock信息，这种锁有3个属性：Mode、Role、PI。Role这个属性是用来描述脏数据块在集群间分布状况的，有local和global两个取值。有local role的实例可以把数据块写到磁盘不需要联系GRD，由本实例完成即可。拥有local role和X mode的实例要给其它instance发送这个数据块，如果发送的是和磁盘一致的版本，那么本实例任然保持local role。如果发送的是和磁盘不一致的版本，那么本实例的角色就转换成global，同时接收方也是global，代表多个实例拥有脏数据块版本。拥有global role的实例想把数据块写到磁盘，必须要联系GRD，由拥有数据块current版本的实例完成写动作。

PI: past image，代表着这个实例的SGA中是否拥有和磁盘内容不一致的版本，以及版本顺序，并不是代表这个节点是否曾经修改过这个数据块。PI主要能够加速crash recovery的恢复过程。

AST: DLM使用两个队列跟踪所有的lock请求，并用两个ASTs(异步中断或陷阱）来完成请求的发送和响应。具体的过程见书的P120。

1.22 RAC进程名称和进程提供的服务名称差异很大，不便记忆。造成这种现象是因为进程名称是从OPS时代延续下来的，但是服务名称却已经在RAC中重新设计并命名。

LMSn：负责数据块在实例间的传递，对应的服务叫GCS（global cache service）。进程的数据量通过参数GCS_SERVER_PROCESSES控制，默认是2，取值范围为：0-9。

LMD: 负责在多个实例之间协调对数据块的访问顺序，保证数据的一致性访问。它负责提供GES（global enqueue service）服务。GCS、GES、GRD构成RAC最核心的功能：cache fusion。

LCK：负责non-cache fusion资源的同步访问，每个实例有一个LCK进程。

LMON: 各实例的LMON进程会定期通信，以检查集群中各节点的健康状态，当某个节点出现故障时，负责集群重构、GRD恢复等操作，它提供的服务叫CGS（cluster group services）。LMON可以和下层的clusterware合作也可以单独工作。当LMON检测到实例级别的脑裂时，LMON会通知下层的clusterware，期待clusterware解决脑裂问题，但是RAC并不假设clusterware肯定能够解决问题，因此，LMON不会无尽等待clusterware层的处理结果。如果发生等待超时，LMON会自动触发IMR（instance membership recovery）IMR功能可以看做是oracle在数据库层提供的脑裂、IO隔离机制。LMON主要是借助两种心跳机制来完成健康检测：1、节点间的网络心跳。2、控制文件的磁盘心跳。每个节点的CKPT进程每隔3S更新一次控制文件一个数据块。可以通过x$kcccp看到这个动作。SQL>select inst_id,cphbt from x$kcccp

DIAG: 监控实例的健康状态，并在实例出现运行错误时收集诊断数据记录到alert.log

GSD: 负责从客户端工具，比如srvctl接收用户命令，为用户提供管理接口。

1.23 RAC中的文件。

spfile文件：放在共享存储

redo thread： 每个实例有套redo log，这套redo log叫做一个redo thread。RAC中每个实例要设置thread参数，该参数缺省值时0。如果设置了这个参数，则实例启动时，会用等于该thread的private redo thread。如果用缺省值，实例启动会选择使用public redo thread，并且该实例会以独占的方式使用该redo thread。RAC环境下，redo log group是在整个数据库级别进行编号的，比如实例1有1，2，3三个日志组，那么实例2的日志组就应该从4开始编号。

归档日志：归档日志不必放在共享存储上，每个实例可以在本地存放归档日志，但是如果在单个实例进行备份归档日志或进行介质恢复操作，又要求这个节点能够访问到所有实例的归档日志。因此RAC环境下配置归档日志有多种选择：1、NFS。2、实例间归档。3、ASM。常用第二种方法进行配置。

undo表空间：每个实例都需要一个单独的回滚表空间。

1.24 RAC中的SCN。在单实例环境中只有一个SCN产生器，改变发生的顺序就是SCN的顺序。在RAC下，每个节点都有自己的SCN发生器，必须有某种机制保证这些SCN在时间排序上的精确。RAC中GCS负责维护全局的SCN产生，缺省用的时Lamport SCN生成算法。原理如下：节点间通信内容中都携带SCN，每个节点把接收到的SCN和本机的SCN比，如果本机的SCN小，则调整本机的SCN和接收到的一致。如果节点间通信不多，还会主动定期互相通报，因此节点即使处于idle状态，还是会有一些redo log的产生。另外一种算法是广播算法。原理：每个commit操作后，节点向其它节点广播SCN，虽然会对系统造成负载，但是确保每个节点在commit后都能看到SCN号。10g RAC就是用的广播算法，可以在alert.log中看到。

1.25 当不同的实例请求相同的数据块，这个数据块就需要在实例间进行传递。oracle7的OPS中，这种传递是通过磁盘完成的。实例1必须先把这个块写回磁盘，然后实例2再从磁盘读取这个数据块。oracle8i只能传递没有修改过的数据块，对于修改的数据块还是要通过磁盘传递。9i才使用cache fusion通过私有网络传递数据块。

1.26 RAC和clusterware的交互。RAC集群和节点集群是两个层次的集群，两个集群都有脑裂，IO隔离等问题。这两个集群有各自的故障检测机制，二者之间的机制可以有重叠也可以不同。RAC的集群状态维护是由RAC的LMON进程提供的，这个进程提供了CGS和NM（node management）两个服务。NM是RAC集群和clusterware集群的通信通道，通过它把本节点的资源状态登记到本地的clusterware，进而由后者提供给其它节点的clusterware，NM还要从clusterware获得其它节点的资源状态。

NM组：RAC的每个实例的所有进程是作为一个组注册到clusterware中的，其中LMON进程作为组里的primary member注册并获得Member ID，而其它进程作为这个组的slave Member并以相同的member id注册到clusterware。整个集群的节点成员信息是通过一个位图来维护的，每个节点对应一个bit，0代表节点down，1代表up，整个位图有个有效/无效标志位。这个位图在整个集群中作为一个全局资源被永久记录。当有新的节点加入集群时，该节点需要读取该位图，找到对应的bit，把值从0改变成1，并且把位图的无效标志位置为1，这时虽然位图内容是正确的，但状态是无效的，其它节点发现这个状态位图无效，就会触发集群的重构，达到新的稳态后，再把位图状态置为有效，当集群完成重构后，NM会把这个事件传递给CGS层，CGS负责同步所有节点间的重构。对于实例的正常启动和关闭，该实例的NM会向clusterware进行注册或取消注册，注册过程中，NM同时从clusterware获得集群的其它节点列表，然后NM通知其它节点的NM，最后NM事件发送给CGS层，由CGS层负责协调整个集群的组重构，当CGS完成了重构之后，再通知GCS，GES进行实例重构（GRD层的重构）。对于实例的异常关闭，clusterware、NM就不会知道，这时就需要CGS提供的IMR功能进行感知，然后进行重构。

IMR的重构原理：IMR是由CGS提供的重构机制，用于确认实例之间的连通性、快速的排除故障节点以减少对数据的损害。在这个过程中，每个实例都需要做出投票，投票的内容是它所认为的整个集群现在的状态，然后由一个实例根据这些投票，重新规划出一个新的集群，并把这个投票结果记录到控制文件，其它实例读取这个结果，确认自己是否还属于集群，如果不属于集群，就要自动重启，如果属于集群则参与重构。IMR发现出现脑裂，即集群中出现两个group，这时IMR会先通知CM，然后等待CM去解决这个问题，等待时间是_IMR_SPLITBRAIN_RES_WAIT，缺省600毫秒，超时后IMR自己执行节点排除。在CGS完成节点的重构后，GCS，GES才进行数据层面的重构，也就是crash recovery。

重构触发类型：1,节点的加入或离开，由NM触发。2,网络心跳异常，超时时间默认300S，由_cgs_send_timeout参数控制，由IMR触发。3，控制文件心跳异常，超时时间默认900S，由_controlfile_enqueue_timeout参数控制，由IMR触发。

 

 

2.ASM存储方案

 

2.1red hat as4以后，裸设备已经被linux社区抛弃了，而是通过支持O_DIRECT标识来绕过OS缓冲。10gR2缺省就是用O_DIRECT的方式操作设备的。但是oracle clusterware 10R2的开发没能及时跟上，仍然需要使用裸设备来创建voting disk和OCR。

2.2 ASM中的shared pool有extent map，每100GB需要1MB的extent map，根据这个空间再加上额外的2MB就可以了，ASM SGA的默认值一般能满足要求。

2.3 ASM实例比RDBMS多出两个进程：RBAL和ABRn

RBAL：rebalancer进程，负责规划ASM磁盘组的reblance活动。

ABRn：RBAL的子进程，数量上可以有多个1-9，这组进程负责真正完成reblance活动。

2.4 使用ASM作为存储的RDBMS实例，会多出两个进程：RBAL和ASMB

RBAL:打开每个磁盘组的所有磁盘和数据的rebalance。

ASMB:负责与ASM实例的通信，它先利用diskgroup name从CSS获得管理改diskgroup的ASM实例的连接串，然后建立到ASM的持久连接，两个实例通过这条连接定期交换信息，同时也是一种心跳机制。

RDBMS要想使用ASM作为存储，必须在启动时从ASM实例获得extent map，以后发生磁盘组的维护操作，ASM实例还要把extent map的更新信息通知给RDBMS，这两个实例间的信息交互就是通过ASMB进程完成的。因此ASM实例必须先于数据库实例的启动。

O0nn 01-10：这组实例建立到ASM实例的连接，某些长时间操作比如创建数据文件，RDBMS会通过这些进程向ASM发送信息。

2.5 ASM实例运行不需要任何文件只是表面现象，其实ASM也需要很多文件来保证它的运行，只不过这些文件是oracle内部维护的，对DBA不可见，也不需要DBA的干预。

2.6 ASM实例和RDBMS是1：1的关系，两个实例可以共用一个$ORACLE_HOME。ASM和RDBMS是1：n的关系，则最好为ASM安装单独的ASM_HOME，并和RDBMS的ORACLE_HOME区分开来，这种环境需要使用ASM_HOME下的监听器。

2.7 创建ASM磁盘：首先要让ASM实例发现磁盘，另外要让磁盘分区的属主设成oracle。接下来就是创建ASM磁盘，ASM可以通过两种方式使用磁盘，一是裸设备方式，二是ASMlib方式（允许在块设备上创建ASM，目前oracle只提供了linux下的ASMlib)。

2.8 使用裸设备。solaris平台下，系统同时提供对磁盘设备的字符（c）、块（b）方式访问。每个磁盘有两个设备文件名（/dev/dsk/c1t1d1s1;/dev/rdsk/c1t1d1s1),创建ASM直接用这些设备名就可以了，无需额外配置裸设备。AIX也是一样的道理。linux平台比较麻烦，缺省没有提供对磁盘设备的字符访问方式，必须配置rawdevices服务，把块设备绑定到裸设备才行。这里有三种方式来配置。只要区别在于对oracle用户权限处理方法不同。

方式1：#vi /etc/sysconfig/rawdevices 添加裸设备、块设备的绑定条目：/dev/raw/raw30 /dev/sdc1     /dev/raw/raw31 /dev/sdc2   ...  --> #service rawdevices start  --》#chkconfig rawdevices on(系统启动时，自动启动rawdevices服务）  --》#service rawdevices status  --》cd /dev/raw;ll(查看裸设备）  --》#cd /dev/raw;chown oracle:dba raw*  -->在/etc/rc.local或其它脚本中添加改raw设备属性的命令。(因为rawdevices是以root运行的，因此裸设备缺省的owner是root:root)

方式2：#mknod /oradata/system.dbf c 162 1(这里的162，1分别是major device number，minor device number） --》#chown oracle：dba /oradata/system.dbf  -->#vi /etc/sysconfig/rawdevices

/oradata/system.dbf /dev/sdd7   -->#service rawdevices restart 服务重启后会在/dev/raw目录下创建出一个新的裸设备。

方式3：适合在red hat as4使用，这个版本是用UDEV来管理设备，设备启动后的属主可以在文件中配置。前面的步骤跟方式一样，权限的修改步骤如下：#vi /etc/udev/permissions.d/50-udev.permissions  找到raw一节，修改成下面内容：raw/*:oracle:dba:0660  -->#service rawdevices restart   RHEL5 UDEV的工作方式又发生了变化，50-udev.permissions 文件被抛弃，而使用rule文件来配置。

2.9 ASMlib方式。ASMlib是一个由ORACLE定义接口、由存储厂商实现的函数库，目前oralce只提供了linux平台下的实现库。下载ASMlib时要选择和OS内核匹配的版本。安装完成后，配置驱动（#/etc/init.d/oracleasm configure) ,确认配置成功（#lsmod |grep asm;cat /proc/filesystem;df -ha）。创建ASM磁盘（#/etc/init.d/oracleasm createdisk VOL1 /dev/sdb1 ...),这时能在/dev/oracleasm/disks目录下看到createdisks创建的磁盘VOL1。列出创建好的磁盘（#/etc/init.d/oracleasm listdisks),进一步查看每个ASM磁盘对应的物理设备（#/etc/init.d/oracleasm querydisk VOL1)。如果是RAC环境，只需要在一个节点上执行，其它节点执行这个命令就可以扫描的磁盘了，#/etc/init.d/oracleasm scandisks。

2.10 如果完全使用裸设备实现RAC，配置存储的时候有两点需要注意：1、保证LUN在各节点上的顺序一样。2、设备名对应的物理设备不会因为系统的重启发生变化。比如sda、sdb这类名字，到底是a还是b取决于总线对硬件的扫描顺序。RAC环境中一个节点连着两套存储，一套是本地，另一套是通过HBA卡连接的SAN，HBA卡和本地盘的扫描顺序决定着这类名字对应的设备的变化。为了避免这种不一致，要在/etc/mobprobe.conf中添加两行，强迫扫描本地盘，再扫描HBA。（alias scsi_hostadapter1 aic7xxx   alias scsi_hostadapter2 lpfc)不过到了RED HAT AS4默认就是这种顺序了。如果使用ASM就不需要这些配置，ASM磁盘头会有metadata信息可以准确的识别磁盘。但是磁盘名称在所有节点一致仍然是一个好习惯。

2.11 major number，minor number。前者找到设备驱动程序，后者找到设备具体位置。major number，minor number是预先分配好的，比如裸设备的major number是162，SCSI设备的major number是8。SCSI设备的minor number=driver*16 + partition number。SCSI设备的用户空间名是sd driver partition。linux下SCSI磁盘/dev/sda的partition number是0，代表整个磁盘，linux每个磁盘最多有16个分区，其中分区4代表整个扩展分区，可用分区只有15个。

2.12 配置ASM实例：先介绍几个初始化参数。ASM_POWER_LIMIT:当在磁盘组中添加或删除磁盘时，磁盘组会自动对数据在新旧磁盘间重新分配，从而实现分散IO，这个过程叫再平衡（rebalance）。取值范围0-11。0代表不做rebalance，11代表最快的速度做rebalance，也意味着最严重的性能影响。alter diskgroup dg1 add disk 'a' rebalance power 1 (往磁盘组增加一个磁盘a，并定义rebalance为1。）

ASM_DISKSTRING:定义哪些磁盘可以被ASM使用。使用ASMlib时，需要使用”ORCL:磁盘名“格式。ASM实例也可以使用SPFILE。

无论是否在RAC环境，ASM实例都需要CSS进程，否则会报29701的错误。启动CSS进程的命令如下：/oracle/product/10.2.0/db1/bin/localconfig add

创建磁盘组的操作需要连接到ASM实例中进行，记得创建一个spfile文件。SQL>create diskgroup dg1 external redundancy disk 'ORCL:VOL1','ORCL:VOL2';(创建磁盘组dg1）。

现在RDBMS可以使用ASM的磁盘组了。使用前必须保证ASM实例已经注册到Listener,否则需要手工注册（SQL>alter system register;)。使用ASM的磁盘组中的磁盘很简单（SQL>create tablespace test datafile '+dg1/test.dbf' size 100M;)。RDBMS在运行的时候，ASM实例是无法关闭的，手工关闭也不可能。

 

 

3.RAC维护工具集

 

oracle clusterware命令集的分类：

节点层：olsnodes

网络层：oifcfg

集群层：crsctl ocrcheck ocrdump ocrconfig

应用层：srvctl onsctl crs_stat

oifcfg的4个子命令：iflist;getif;setif;delif    举例：

$oifcfg iflist    --显示网口列表

$oifcfg getif     --查看每个网卡的属性

$oifcfg getif -global dbp   --查看节点dbp的global类型的配置

$oifcfg getif -type public  --查看public类型的网卡配置

$oifcfg getif -type cluster_interconnect

$oifcfg setif -global ten@none/10.0.0.1:public   --添加新的网卡，这个命令并不会检查网卡是否真的存在。

$oifcfg delif -global  --删除接口配置

$oifcfg setif -g global eth0/192.168.12.1:public   --添加接口配置

$oifcfg setif -g global eth1/10.0.0.0:cluster_interconnect

$crsctl check crs   --检查CRS状态

$crsctl check cssd/crsd/evmd    --分别检查三个组件的状态

CRS进程默认随着OS的启动而自动启动，有时出于维护的目的需要停止这个进程，可以用以下命令：

#/oracle/product/oem/crs/bin/crsctl disable/enable crs

这个命令实际上是修改了/etc/oracle/scls_scr/dbp/root/crsstart 这个文件的内容。

启动、停止CRS棧：crsctl start/stop crs

$crsctl get query css votedisk   --查看votedisk磁盘的位置。

$crsctl get css misscount   --查看参数

$crsctl set css miscount 100   --设置参数

CRS由CRS、CSS、EVM3个服务组成，每个服务又是由一系列module组成的。CRSCTL允许对每个module进行跟踪，并把跟踪内容记录到日志中。

$crsctl lsmodules css/crs/evm

#/oracle/product/oem/crs/bin/crsctl debug log css "CSSD:1"   --跟踪CSSD模块。

#more $CRS_HOME/log/dbp/cssd/ocssd.log  --查看跟踪产生的日志。

维护votedisk：可以通过crsctl命令添加votedisk，votedisk使用是的是一种过半数的算法，所以添加votedisk应该添加两个。添加和删除votedisk的操作比较危险，必须停止数据库、停止ASM、停止CRS后操作，并且操作时必须使用-force参数。举例如何添加一个votedisk：

1.#$ORA_CRS_HOME/bin/crsctl add css votedisk /dev/raw/raw1 -force

  #$ORA_CRS_HOME/bin/crsctl add css votedisk /dev/raw/raw2 -force

2.#$ORA_CRS_HOME/bin/crsctl query css votedisk

3.#crsctl start crs  

OCR系列命令：ORACLE会每4个小时对OCR做一个备份，并且保留最后3个备份，以及前一天，前一周的最后一个备份。这个备份由master node的CRSD进程完成，备份的默认位置为：$CRS_HOME/crs/cdata/<cluster_name>目录下，每次备份后，备份文件名会自动更改，最近一次备份叫backup00.ocr。建议DBA除了保存在本地外，还应该在其它地方保存一份。

ocrdump：以ASCII的方式打印出OCR的内容，产生的文件只能用于阅读，不能用于恢复。

$ocrdump -stdout -keyname SYSTEM.css -xml|more  --把SYSTEM.css键的内容以.xml格式打印输出到屏幕。命令的执行过程中，会在$CRS_HOME/log/<nodename>/client目录下产生名为ocrdump_<pid>.log的日志文件，如果命令出现执行问题，可以查看这个日志。

ocrcheck：用于检查OCR内容的一致性，不需要参数。这个命令会产生ocrcheck_pid.log日志文件。

ocrconfig：用于维护OCR磁盘，OCR磁盘最多只能有两个，一个是primary OCR，另一个是Mirror OCR。

$ocrconfig -help   --查看命令帮助

$ocrconfig -showbackup  --查看自动备份，OCR的自动备份在$CRS_HOME/crs/cdata/<cluster_name>目录ixa，可以通过ocrconfig -backuploc <directory_name>命令修改到新目录。

OCR的备份与恢复：oracle推荐在对集群作调整时，比如增加、删除节点前，应该对OCR做一个备份。可以使用export备份到指定文件。如果做了replace或restore等操作，oracle建议使用cluvfy comp ocr -n all命令做一次全面检查。下面举一个OCR备份与恢复的案例：

1.crsctl stop crs

2.ocrconfig -export /oracle/ocr.exp  (注意这里要用root用户导出）

3.crsctl start crs

4.crsctl check crs

5.dd if=/dev/zero of=/dev/raw/raw1 bs=1024 count=102400  (故意破坏ocr内容）

6.ocrcheck   --检查会失败。

7./backup/install_medir/clusterware/cluvfy/runcluvfy.sh comp ocr -n all   --检查一致性也失败。

8.ocrconfig -import /oracle/ocr.exp   --恢复OCR内容。

9.再次用刚才的两个工具进行检查。

10.crsctl start crs

 

移动OCR的位置：（/dev/raw/raw1移到/dev/raw/raw31)

1.ocrconfig -showbackup /  ocrconfig -export /tmp/ocrexp -s online   --查看OCR是否有备份，如果没有执行一次导出做备份。

2.ocrcheck   /  ocrconfig -replace ocrmirror  /dev/raw/raw21    --查看当前OCR的位置，发现只有一个primary ocr，没有mirror ocr，所以不能直接改变OCR的位置，需要添加镜像再修改OCR位置。

3.ocrcheck  --验证一下是否添加成功。

4.ocrconfig -replace ocr /dev/raw/raw31   --改变位置。

5.检查/etc/oracle/ocr.loc文件。系统默认会修改这个文件，如果没有更改，需要手工修改相应的条目。

 

crs_stat  --查看CRS维护的所有资源的运行状态，包括2个GSD,ONS,VIP,ASM INSTANCE,LISTENER,RDBMS INSTANCE和1个database。使用-v -p参数可以获得更详细的信息。 -ls选项可以获得每个资源的权限定义。

 

onsctl  --这个命令用于管理配置ONS（oracle notification service），ONS是oracle clusterware实现FAN(fast application notification）Event push模型的基础。传统模型中，客户端需要定期检索服务器来判断服务端状态，本质上是一个PULL模型。10g引入了一种全新的PUSH机制-FAN，当服务端发生某些事件时，服务器会主动的通知客户端这种变化，这样客户端能尽早知道服务端的变化，而这种机制就是依赖ONS实现的。在使用这个命令之前，需要先配置ONS服务。

ONS配置内容：配置文件在$CRS_HOME/opmn/conf/ons.config,注意这个文件中的nodes和useocr这两个参数。这两个参数共同决定了本机ONS daemon要和哪些远程节点上的ONS daemon进行通信。如果useocr=ON，说明信息保存在OCR中，如果是OFF说明信息取nodes中的配置。对于单实例而言，要把useocr设置成OFF。看几个配置的例子：

useocr=off

nodes=dbs：6200，dbp：6200

（节点信息从nodes参数读取，本机的ONS要和这dbs，dbp两个节点上的6200端口通信）

useocr=on

（使用OCR时，这个信息是保存在DATABASE.ONS_HOSTS这个键下。可以把这个键从OCR导出来：ocrdump -xml abc.xml -keyname DATABASE.ONS_HOSTS)。

可以直接编辑这个配置文件来配置ONS，如果使用了OCR则可以通过racgons命令进行配置。举个例子：

racgons add_config dbs:6200 dbp:6200  --添加配置

racgons remove_config dbs:6200 dbp:6200  --删除配置

ONS进程运行，并不代表ONS正常工作，需要使用ping命令来确认。比如ps -ef|grep ons可以看到ONS进程正常运行，但onsctl ping看到ons is not running...,启动ONS服务：onsctl start 再次确认ONS服务状态，已经ok。 $onsctl debug  --查看详细信息。

 

srvctl --RAC维护中最常用的命令，也是最复杂的命令。

查看配置：

$srvctl config database   --显示在OCR中注册的所有数据库

$srvctl config database -d a  --查看数据库a的配置

$srvctl config database -d a -a --查看数据库a更详细的配置

$srvctl config nodeapps -n dbs  --返回节点名、实例、$ORACLE_HOME

$srvctl config nodeapps -n dbs -a  --查看VIP配置

$srvctl config nodeapps -n dbs -g  --查看GSD

$srvctl config nodeapps -n dbs -s  --查看ONS

$srvctl config nodeapps -n dbs -l  --查看listener

$srvctl config listener -n dbs   --查看监听器的名称

$srvctl config asm -n dbp   --查看ASM实例名和$ORACLE_HOME

$srvctl config service -d test -a  --查看数据库的所有service配置

$srvctl add database -d abc -o $ORACLE_HOME  --添加数据库

$srvctl add instance -d abc -n dbs -i abc2  --添加实例

$srvctl add service -d abc -s abcservice -r abc1 -a abc2 -P BASIC  --添加服务

 

配置数据库随CRS的启动而自动启动：

srvctl enable/disable database -d test   --启动/关闭数据库的自动启动特性

srvctl config database -d test -a  --确认配置是否成功

srvctl enable/disable instance -d test -i abc1  --开启/关闭某个实例的自动启动

srvctl disable service -d test -s abcservice -i abc1  --禁止服务在某个实例上运行

 

$srvctl remove service -d test -s abcservice  --删除service

$srvctl remove instance -d test -i abc1  --删除abc1

$srvctl remove database -d test  --删除数据库

remove命令删除的只是对象在OCR中的定义信息，对象本身不会被删除。

 

$srvctl start database -d test   --启动数据库。

$srvctl start database -d test -i abc1 -o mount  --启动实例abc1到mount状态。

$srvctl stop instance -d test -i abc1 -o immediate

$srvctl stop instance -d test -i abc1 -o abort

$srvctl start service -d test -s abcservice -i abc1

$srvctl status service -d test -v

 

跟踪srvctl：10g中跟踪srvctl，只需要设置SRVM_TRACE=true这个OS环境变量即可，这个命令的所有函数调用会输出到屏幕上。

 

一个恢复案例（OCR磁盘和votedisk磁盘全部破坏，并且没有备份）：

1.crsctl stop crs  --停止所有节点的clusterware stack

2.$CRS_HOME/install/rootdelete.sh   --分别在每个节点执行这个脚本。

3.$CRS_HOME/install/rootdeinstall.sh  --只需要在一个节点上执行即可。

4.$CRS_HOME/root.sh  --在和步骤3同一个节点执行这个脚本。

5.$CRS_HOME/root.sh  --在其它节点执行这个脚本。

6.用netca命令重新配置监听器，确认注册到了clusterware中：crs_stat -t -v

7.srvctl add asm -n dbs -i +ASM1 -o /oracle/product/database

  srvctl add asm -n dbp -i +ASM2 -o /oracle/product/database

8.srvctl start asm -n dbs

  srvctl start asm -n dbp(这里出现了ORA-27550：的错误，这个问题是因为RAC无法确认使用哪个网卡作为private interconncect，所以可以通过在两个ASM实例的pile中添加以下参数解决这个问题。

+ASM1.cluster_interconnects='10.0.0.8'

+ASM2.cluster_interconnects='10.0.0.9'

重启ASM，问题得到解决)

9.srvctl add database -d test -o /oracle/product/database

10.srvctl add instance -d test -i abc1 -n dbs

   srvctl add instance -d test -i abc2 -n dbp

11.srvctl modify instance -d test -i abc1 -s +ASM1   --修改实例和ASM实例的依赖关系

   srvctl modify instance -d test -i abc2 -s +ASM2

12.srvctl start database -d test(启动过程又出错，跟ASM问题相同，解决办法类似，修改database参数即可，如下：

SQL>alter system set cluster_interconnect='10.0.0.8' scope=spfile sid='abc1';

SQL>alter system set cluster_interconnect='10.0.0.9' scope=spfile sid='abc2';)

srvctl start database -d test  --重启数据库，操作成功。

 

 

 

4.HA和LB

 

HA=MTTF/(MTTF+MTTR)  MTTF=平均故障间隔时间   MTTR=平均修复时间

10g RAC failover的分类：client-side connect time failover;TAF;service-side TAF

注意：不要在listner.ora中设置GLOBAL_DB_NAME，这个参数会禁用connect_time failover和transparent application failover

 

client-side connect time failover:用户端tnsname中配置了多个地址，用户发起连接请求时，先尝试第一个地址，如果连接失败尝试第二个地址，直到遍历所有地址。它的特点是只在发起连接的时候才去感知节点故障，一旦连接建好后，节点故障不会处理，客户端的表现就是会话断开，用户程序必须重新建立连接。在tnsnames.ora添加FAILOVER=ON条目即可实现此功能，系统默认就能实现这种功能。

 

TAF：如果某个实例发生故障，连接到这个实例上的用户就会被自动迁移到其它的健康实例上。迁移对应用程序而言是透明的，但用户未提交的事务会回滚。TAF的配置也很简单，只要在tnsnames.ora添加FAILOVER_MODE配置项，这个条目有4个子项目需要定义。METHOD=BASIC/PRECONNECT  (BASIC:感知节点故障时才创建到其它实例的连接。 PRECONNECT:在最初建立连接时就同时建立到所有实例的连接，这样切换速度就快)。  TYPE=session/select(session:对于select语句切换后需要重新执行查询语句。 select：对于select语句切换后在新的几点继续返回剩下的记录。两种方式对未提交的事务都自动回滚）。

DELAY和RETRIES表示重试间隔时间和重试次数。

 

service-side TAF：直接在服务器上修改配置，无需修改客户端的tnsnames.ora文件。它通过结合service在数据库里保存FAIL_MODE的配置，把所有的TAF配置保存在数据字典中，省去了客户端的配置工作。

service-side TAF比TAF多出了一个instance role，所谓实例角色，就是有多个instance参与一个service时，可以配置优先使用哪个instance为用户提供服务，用户共有两种可选角色：

PREFERRED:首选实例  AVAILABLE:后备实例。要想实现这个功能必须配置services（DBCA，手工方式（srvctl)都可以配置）。使用srvctl这个工具时，命令只更新OCR中的配置，不会更新数据字典和监听器中的信息，因此还要用dbms_service包来更新数据字典。无论使用DBCA还是srvctl命令来配置service，都无法配置TAF的type、delay、retries3个属性。必须使用dbms_services包来修改这些属性。10g配置了service-side TAF，客户端甚至都不需要tnsnames.ora文件。10g提供新连接方法：easy connect naming methods。连接串格式如下：username/password@[//]host[:port][/service_name]

 

oracle clusterware HA框架：这里强调的是oracle clusterware是一款独立的集群件产品，不只是针对RAC，另外oracle也提供了API，用户可以进行二次开发实现更丰富的功能。详见书的P210。

 

LOADBALANCE:10g RAC提供两种手段实现分散负载：纯技术的分散负载;面向业务的的分散负载。

纯技术的分散负载有两种实现方法：客户端均衡;服务器端均衡

客户端均衡：oracle8实现的方法，使用随机算法把连接请求分散到各个实例，这种分配方法没有考虑每个节点的真实负载。

服务器端均衡：oracle9引进的方法。负载均衡的实现依赖于listener收集的负载信息，PMON进程会收集系统的负载信息，然后登记到listener中，最少1分钟，最多10分钟PMON要做一次更新，节点负载越高，更新频率越高。如果listener关闭，PMON会每隔1秒钟检查listener是否重启。除了自动定时更新任务外，用户也可以使用alter system register命令手工进行这个过程。整个过程可以在listener日志看到。我们也可以使用1025事件跟踪PMON进程，来查看注册的内容。PMON不仅会向本地注册，还可以向其它节点上的listener注册，但到底向何处注册，是由remote_listeners决定的，参数值是一个tnsnames项。

客户端均衡和服务器端均衡不是互拆的，两者可以一起工作。配置LB时有点需要注意：需要将各个实例的listener.ora文件中去掉缺省产生的sid_list_listener_name条目，这样才能保证listener获得的信息都是动态注册的，而不是从文件中读取的静态信息。

 

利用service分散负载：通过把应用按照功能模块进行划分成service，进而把每个service固定在某些RAC节点上。（cache fusion减少了）

 

 

 

5.备份

 

flash recovery area：闪回恢复区可以集中存放所有与恢复有关的文件，这些文件包括以下几类：

控制文件（创建db时使用了闪回恢复区，会自动在这里创建一个控制文件的copy）

控制文件和spfile的自动备份

备份集backup set文件

image copy文件

归档日志，log_archive_dest_10会自动指向flash recovery area

闪回日志（闪回数据库需要这种功能）

10g中的闪回功能家族中，只有闪回数据库和闪回恢复区有关系（闪回日志必须放在闪回恢复区），其它的没有直接关系。

10g中的v$logfile,v$control_file,v$datafile_copy,v$backup_piece,v$archived_log 这些视图中也增加了

is_reconvery_dest_file列，代表该文件是否放在recovery area中。

RMAN>select name,is_recovery_dest_file from v$archived_log;

 

配置闪回区：RAC环境下的配置，要保证每个节点的配置值都相同。可以在线修改，立即生效：

SQL>alter system set db_recovery_file_dest='+DISKA' scope=both;

SQL>alter system set db_recover­y_file_dest_size='5g' scope=both sid='*';

使用ASM作为闪回区，只能指定到diskgroup级别，而不能指定到目录，ASM存储管理是采用OMF方式，每个数据库会被分配到指定目录diskgroup/instance_name。

 

闪回区的监控：当空间使用率达到90%，会自动触发删除，如果没有空间可以释放，并且使用空间超过85%，会记录一条warning日志，如果超过97%会记录一条critical warning日志，这些日志内容可以从DBA_OUTSTANDING_ALERTS视图查看。闪回区的使用情况可以通过v$recovery_file_dest来进行监控。

 

RMAN使用方法：

1.批处理方法：把命令写入文本文件。cat back

run {

backup database;

}

通过cmdfile指定命令文件，使用log指定日志文件：

$rman target / cmdfile=back log=back.log

 

2.脚本方式：需要恢复目录，脚本分local和global两种。

local：连接到target db和catalog db

RMAN>create script full_bakcup

{

backup database plus archivelog

delete obsolete;

}

 

global：需要用global关键字

RMAN>create global script global_full_backup

{

backup database plus archivelog;

delete obsolete;

}

 

使用脚本： RMAN>run { execute script full_bakcup；}

 

备份格式：image copy只能在磁盘上进行，backup set是一种压缩格式，RMAN能跳过空数据块，备份的时候还可以额外压缩，但image copy比backup set的restore速度快，尽管它占用较多的空间。

RMAN的备份保留策略： RMAN>configure retention policy to recovery window of 7 days;

                    RMAN>configure retention policy to redundancy 2;

RMAN>report obsolete;

RMAN>report obsolete recovery window of 7 days;

RMAN>report obsolete redundancy 2;

RMAN>show retention policy;

RMAN>delete obsolete;

RMAN>delete obsolete redundancy 2;

RMAN>delete obsolete recovery window of 4 days;

RMAN>configure retention policy to none;

 

RMAN只能对数据文件进行增量备份，控制文件、日志文件不能增量备份。增量备份能够捕获nologging操作的数据变化，而这些操作不会被记录到日志上。

backup as copy db_file_name_convert=('+data/wxxrzxm','/backup/test') database;   --路径的转变。

如果想把ASM上的数据文件备份到ASM上，上述方法可能会出错，因为ASM是使用OMF方式管理数据文件的。

 

增量备份：oracle10g只允许0和1两级了，0级相当于全备，但不能作为0级使用。

种类：RMAN>backup incremental level 1 database;   --差异增量

      RMAN>backup incremental level 1 cumulative database;  --累加增量

举个例子：

RMAN>run {

     backup as copy db_file_name_convert=('+DATA/wxxrzm','/backup/test') incremental level 0 database tag 'full_backup';

    }

RMAN>run {

     backup incremental level 1 CUMULATIVE for recover of copy with tag 'full_backup' database;

     recover copy of database with tag 'full_backup';

     }

增量备份为了获得要备份的数据块，必须对数据文件中的所有数据块进行遍历，效率不高。因此oracle提供了一个特殊的文件叫block change tracking file，每当数据块发生变化时，相关信息同时记录到这个文件中。

SQL>alter database enable/disable block change tracking;   --启用关闭该功能。

SQL>select * from v$block_change_tracking;  --查看文件的位置。

SQL>alter database enable block change tracking using file 'path'   --手工指定文件位置。

启用这个特征后oracle会启动一个ctwr进程，它负责跟踪数据变化。

 

RMAN>backup duration 2:00 database;  --希望在2小时内完成备份，如果无法完成，整个任务都会出错返回，产生的备份文件也不可用。

RMAN>backup duration 2:00 partial database filesperset 1;  --保证产生的每个备份文件都对应一个数据文件。完成的备份保留。

RMAN>backup duration 0:01 minimize load database;

RMAN>backup duration 0:01 minimize time database;

 

恢复命令：10G，restore命令增加了一个preview子命令。举例如下：

$rman / target

RMAN>spool log to test.log

RMAN>restore datafile 1 preview;

RMAN>restore database preview summary;

RMAN>spool log off;

 

RMAN>list backup;

RMAN>list copy;

RMAN>crosscheck copy;

RMAN>delete expired copy;  --list显示的信息是从控制文件获得，如果用rm命令删除copy这个动作不会同步到控制文件，这会造成不一致。

 

v$rman_output:查看每个备份任务的日志。  v$rman_status：查看备份任务的完成情况。

 

RAC的备份：RAC的备份与单实例备份有两点需要注意：1.RMAN连接集群中的某个实例即可。 2.备份归档时，必须保证在备份实例上能够访问所有实例的归档日志，否则会报错。

SQL>alter system set log_archive_dest_state_2=defer scope=both sid='*';

注意10g里增加的新参数log_archive_local_first参数，10g以前本地和远程的归档都完成后，联机日志文件才能被重用。这个参数设置为true后，oracle先进行本地归档，然后同时远程传递和使用联机日志。

 

 

 

6.恢复

 

修改数据块之前，代表本次修改操作的redo记录必须先要被保存下来，然后才能修改数据记录。旧的日志被覆盖前需要完成两件事：1，检查点必须完成。2，完成归档。

SCN的种类：系统检查点SCN--记录在控制文件中（v$database可以看到）。  数据文件检查点SCN--记录在控制文件中（v$datafile)。 数据文件的启动、终止SCN--数据文件头记录启动scn（v$datafile_header)，控制文件记录数据文件的终止scn。这两个SCN用来确认数据文件是否需要恢复。数据库运行中，所有数据文件的终止SCN都是null，正常关闭数据库后数据文件的终止scn会被设置成启动scn，异常关闭数据库后，终止scn来不及修改还是null。

每个实例对应的联机日志就是一个redo thread。rac环境下每个实例都需要自己的联机日志，也就是每个实例都有自己的redo thread，所以在rac下添加日志时必须指定线程号：

SQL>alter database add logfile thread 1 group 5('/oracle/oradata/redo5') size 50m;

日志切换触发检查点，检查点启动DBWR把data buffer cache中的dirty block写入磁盘，当该联机日志所覆盖的操作都被同步到数据时，这个联机日志文件就可以被重用了。但是dirty blcok在磁盘上不是连续分布的，所以日志切换要等待DBWR写完，这就会导致用户进程必须长时间的等待。oracle 8开始出现了增量检查点的算法。执行检查点时，只在控制文件中记录当时的检查点SCN，然后DBWR在后台进程进行写操作，每隔3s，DBWR会在控制文件中更新checkpoint progress record，代表工作进展情况，而用户进程继续前台操作，不受DBWR的影响。

记住RAC下的联机日志必须放在共享存储上，因为恢复时必须把所有实例的联机日志都合并，把redo log record按照SCN排序。

crash recovery：RAC下某个实例发生了crash后在其它实例上进行的recovery。在执行crash recovery时，故障节点被IO fencing，即故障节点不能对共享数据进行操作。

PCM-lock：用来描述数据块的buffer copy在不同实例间的分布情况。它有三个属性：MODE,ROLE,PAST IMAGE。具体参见书的P270。可以根据其它节点上的PCM-lock推断出哪些数据块需要恢复。关于crash recovery的过程详见p273。

online block recovery：某个用户进程修改数据时异常死掉，导致data buffer数据不一致，这时就会触发online block recovery动作。这个动作找到一个恢复起点（最近的past image）应用联机日志进行恢复。

 

完全恢复的一个特殊案例：数据结构改变后的恢复

先做一个全备份，然后添加一个数据文件，并创建一个表空间，同时增加一些数据。--》关闭数据库，模拟灾难。删除刚才新建的数据文件。--》启动数据库，因为删除的数据文件没有备份，所以restore，recover的方法都不行。 --》只能用alter database create datafile重建数据文件（无需指定数据文件所属的表空间，也不用指定数据文件大小，因为这些属性在控制文件都有记录） --》在ASM里要注意，你指定建的数据文件名可能跟控制文件记录的数据文件名不同。这时需要将控制文件记录的数据文件名rename成ASM里记录的数据文件名。SQL>alter database rename file '' to ''  -->再次recover datafile就可以了。

 

ASM也可以像目录一样操作：

$export ORACLE_SID=+ASM1

$asmcmd -p

ASMCMD[+] >cd +DATA2/DB/controlfile/

>ls

>rm ...

 

不完全恢复的一个案例：

1.RMAN>backup database;   --先做一个全备份。

2.abort数据库，进ASM删除数据文件、联机日志、控制文件。

3.SQL>startup nomount;

4.RMAN>restore controlfile from '自动备份‘

5.RMAN>sql 'alter database mount';

6.RMAN>restore database;  --恢复过程中，数据文件被自动改名，并且改动被同步到控制文件中。

7.确定恢复终点。控制文件记录的归档文件（v$archived_log)和磁盘上的归档文件数量不一样，有4个文件时备份之后产生的。

8.RMAN>catalog archivelog '归档’;   --将备份后产生的归档登记到控制文件。

9.SQL>recover database using backup controlfile until cancel;  --执行不完全恢复。如果使用RMAN工具，需要使用set until提前指定恢复终点：

RMAN>run {

set until sequence 64 thread 2;

restore database;

recover database;

}

10.RMAN>sql 'alter database open resetlogs';

11.$srvctl start database -d test   --打开其它实例。