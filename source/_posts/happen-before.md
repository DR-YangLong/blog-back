title: Happen-Before
date: 2017-04-14 12:20:56
categories: [java]
tags: [jvm]
---

## 关于happen-before

### [原文]("https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/package-summary.html#MemoryVisibility")
1. Each action in a thread happens-before every action in that thread that comes later in the program's order.   
2. An unlock (synchronized block or method exit) of a monitor happens-before every subsequent lock (synchronized block or method entry) of that same monitor. And because the happens-before relation is transitive, all actions of a thread prior to unlocking happen-before all actions subsequent to any thread locking that monitor.
3. A write to a volatile field happens-before every subsequent read of that same field. Writes and reads of volatile fields have similar memory consistency effects as entering and exiting monitors, but do not entail mutual exclusion locking.
4. A call to start on a thread happens-before any action in the started thread.
5. All actions in a thread happen-before any other thread successfully returns from a join on that thread. 

____
### 整理
1. 一个线程对monitor解锁之前的所有写操作都对下一个对此monitor加锁的线程可见。即在加锁顺序上，上一个对资源加锁的线程所做的所有操作都对下一个加锁的线程可见。
2. 如果线程1对volatile变量进行写操作，那么线程1此时及之前的所有写操作（不仅是volatile变量）对后续读取此volatile变量的线程可见。
3. 线程写入的所有变量，对调用了此线程的jion()方法并成功返回的线程可见。即线程会在join()方法处执行完，且线程写入的变量对调用了此线程join()方法的线程可见。
4. 线程中上一个写操作及之前的所有写操作在该线程执行下一个动作时对该线程可见。就是在一个单独的线程中，按照程序代码时间顺序执行操作。
5. 线程的start方法在线程的任何操作之前。
6. happen before具有传递性，如果存在A>B(>可理解为A happen before B)，B>C，则A>C

