title: Collection之List
date: 2016-09-08 17:40:56
categories: [java]
tags: [Collection]
---

# List

## ArrayList
ArrayList由Object数组构成,用int类型的size属性记录数组中实际对象的数量，实现了随机访问接口RandomAccess。默认容量是10，最大容量为Integer.MAX_VALUE-8。使用int modCount记录结构变更（增减数组长度）次数，使用指针迭代ArrayList时，
指针中的expectedModCount初始为modCount，当在迭代中发现expectedModCount和modCount不相等时，认为结构被其他线程改变了，此时将抛出异常。
每次插入、批量新增和删除元素都会对结构作出改变，并对modCount+1，底层通过使用System.arraycopy实现。
``` java
    /*
     * @param      src      the source array.源数组。
     * @param      srcPos   starting position in the source array.要复制的起始位置,包含此位置的元素。
     * @param      dest     the destination array.目标数组。
     * @param      destPos  starting position in the destination data.目标数组放置复制元素的其实位置。
     * @param      length   the number of array elements to be copied.要从源数组复制的元素数量。
     * @exception  IndexOutOfBoundsException  if copying would cause
     *               access of data outside array bounds.
     * @exception  ArrayStoreException  if an element in the <code>src</code>
     *               array could not be stored into the <code>dest</code> array
     *               because of a type mismatch.
     * @exception  NullPointerException if either <code>src</code> or
     *               <code>dest</code> is <code>null</code>.
     */
    public static native void arraycopy(Object src,  int  srcPos,
                                        Object dest, int destPos,
                                        int length);
```
<!-- more -->
ArrayList是**非线程安全**的。由于是数组实现，每次**结构改变都要移动数据**，所以涉及此类的操作性能很低，但却带来了**很好的访问速度**。
``` java
public class ArrayList<E> extends AbstractList<E>
        implements List<E>, RandomAccess, Cloneable, java.io.Serializable
{
    private static final long serialVersionUID = 8683452581122892189L;
    private static final int DEFAULT_CAPACITY = 10;
    private static final Object[] EMPTY_ELEMENTDATA = {};
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};
    transient Object[] elementData; // non-private to simplify nested class access
    private int size;
    protected transient int modCount = 0;
    //...
  }
```



Tips
```
subList(int fromIndex, int toIndex)方法返回一个窗口，在fromIndex（包含），toIndex（不包含）之间，本身元素还是在源ArrayList中，
在返回的SubList上进行的操作会对源ArrayList造成影响。
```

## Vector
Vector,向量，底层和ArrayList一样使用Object数组实现，但在增加元素改变结构时，vector使用**capacityIncrement**因子来增加定长容量——
如果capacityIncrement<=0,则一次性增加容量为原来的2倍。增加容量时，如果按上述方方法仍不够容纳元素，则增加的容量为元素数量。
vector使用**同步方法**的方式保证线程安全，因而其多线程性能不高。
``` java
public class Vector<E>
    extends AbstractList<E>
    implements List<E>, RandomAccess, Cloneable, java.io.Serializable
{
    protected Object[] elementData;
    protected int elementCount;
    protected int capacityIncrement;
    private static final long serialVersionUID = -2767605614048989439L;
    protected transient int modCount = 0;
    //...
}
```


## LinkedList
LinkedList链表，实现了Deque（双端队列）接口，可以作为链表使用，也可以作为队列使用。其由Node构成：
``` java
private static class Node<E> {
        E item;
        Node<E> next;
        Node<E> prev;

        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }
```
本身记录first（头）和last（尾）的Node引用，以及元素数量size。
``` java
public abstract class AbstractList<E> extends AbstractCollection<E> implements List<E>{
  transient int size=0;
  transient Node<E> first;
  transient Node<E> last;
  protected transient int modCount = 0;
//....
}
```

与ArrayList一样，非线程安全，使用modCount记录结构改变次数，迭代时用于判断结构是否被改变，SubList同样是个窗口。
由于使用引用记录顺序，因此LinkedList在结构改变的操作中效率较高，但是在数据读取时由于需要从头遍历，所以效率较低。

## CopyOnWriteArrayList
ArrayList的**线程安全版本**，所有的可变（add、set、remove等）操作都是通过对底层的数组进行一次复制来实现。

### 2个类常见的遍历同时操作的比较：

> ArrayList遍历同时操作：

|迭代方式\操作|list.add|iterator.remove|list.remove|
|:---:|:---:|:---:|:--:|
|foreach方式|报错|--|报错|
|Iterator方式|报错|不报错|报错|

> CopyOnWriteArrayList遍历同时操作：

|迭代方式\操作|list.add|iterator.remove|list.remove|
|:---:|:---:|:---:|:--:|
|foreach方式|不报错|--|不报错|
|Iterator方式|不报错|报错|不报错|

CopyOnWriteArrayList在Iterator使用list执行结构改变的操作不会报错的原因是Iterator创建的时候是返回了一个当前list的快照，这个快照生成的Iterator并不支持那些改变结构的操作，并且由于是快照，所以使用list.add等操作时，操作其实是“另一个”list。

``` java
public class CopyOnWriteArrayList<E>
    implements List<E>, RandomAccess, Cloneable, java.io.Serializable {
    private static final long serialVersionUID = 8673264195747942595L;

    /** The lock protecting all mutators */
    final transient ReentrantLock lock = new ReentrantLock();

    /** The array, accessed only via getArray/setArray. */
    private transient volatile Object[] array;

    /**
     * Gets the array.  Non-private so as to also be accessible
     * from CopyOnWriteArraySet class.
     */
    final Object[] getArray() {
        return array;
    }

    /**
     * Sets the array.
     */
    final void setArray(Object[] a) {
        array = a;
    }
    //.......
    }
```

此类使用**volatile**修饰的Object数组存储对象，因此保证了多线程下读的可见性（一致性），但不保证多线程下写的一致性，写的一致性靠**ReentrantLock**来保证：
> 在所有的add，remove，set等写方法中，此类使用ReentrantLock.lock和ReentrantLock.unlock方法来保证写时的数据一致性。

此类sublist方法使用ReentrantLock保证线程安全，但返回sublist还是原list的一个窗口。

# 队列
队列有FIFO（first in first out）的特性，元素从队尾入从队头出，但是双端队列可以不遵守此规则，根据需要选择入队出队的位置是头还是尾。
Queue/Deque的主要方法：
1. add 插入元素到队列尾部，如果队列满了抛出异常，不会阻塞线程
2. offer 插入元素到队列尾部，如果队列满了，返回false，不会阻塞线程
3. remove 查看队列头元素并出队，空队列抛出异常，不会阻塞线程
4. poll 查看队列头元素并出队，不会阻塞线程
5. element 查看队列头元素，空队列抛出异常
6. peek 查看队列头元素
7. put 插入元素到队尾，没有空间则线程阻塞
8. take 出队，没有元素则线程阻塞

|特点|入队|出队|查看|
|:---:|:---:|:---:|:---:|
|不阻塞，抛异常|add|remove|element|
|不阻塞，不抛异常|offer|poll|peek|
|阻塞|put|take|--|

# Queue

## PriorityQueue
``` java
public class PriorityQueue<E> extends AbstractQueue<E>
    implements java.io.Serializable {
    private static final long serialVersionUID = -7720805057305804111L;
    private static final int DEFAULT_INITIAL_CAPACITY = 11;
    transient Object[] queue; // non-private to simplify nested class access
    private int size = 0;//队列元素的数量<=queue数组的length
    private final Comparator<? super E> comparator;
    transient int modCount = 0;
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    //...
  }
```
优先队列使用Comparator来对元素进行排序，小的在前，大的在后，如果没有制定则按入队顺序。扩容时，如果容量小于64，则扩容为
原来的2倍，如果大于64,则扩容为原来的1.5倍。最大容量为int的最大值。在保证优先顺序时，只保证一个优先级，
并不会一定严格按照元素在Comparator里的大小顺序进行排列存储到数组中（存储的是完全二叉树构成的小顶堆）：
``` java
private void siftUpUsingComparator(int k, E x) {//K是当前队列的size，x是新入队的元素
        while (k > 0) {
            int parent = (k - 1) >>> 1;
            Object e = queue[parent];
            if (comparator.compare(x, (E) e) >= 0)
                break;
            queue[k] = e;
            k = parent;
        }
        queue[k] = x;
    }
```
此方法使用的是和当前queue的长度n[n=(size-1)/2]下标处的元素用Comparator比较，如果返回大于等于0则将新加元素放到n处，并将n处的元素挪到队列尾处。
如果n处的元素还返回小于0,则再次向前以此方法递归换元素的位置。
object数组存储的是完全二叉树结构,可参考此处[PriorityQueue]("http://www.cnblogs.com/CarpenterLee/p/5488070.html")，这个操作其实是完全二叉树的插入操作。      
如果需要按一定的顺序遍历元素，需要先将其转换为Array，并用stream排序。
此类**非线程安全**。
不允许添加null值。

## ArrayBlockingQueue

~~~java
public class ArrayBlockingQueue<E> extends AbstractQueue<E>
        implements BlockingQueue<E>, java.io.Serializable {
    private static final long serialVersionUID = -817911632652898426L;
    /** The queued items */
    final Object[] items;
    /** items index for next take, poll, peek or remove */
    int takeIndex;
    /** items index for next put, offer, or add */
    int putIndex;
    /** Number of elements in the queue */
    int count;

    /** Main lock guarding all access */
    final ReentrantLock lock;
    /** Condition for waiting takes */
    private final Condition notEmpty;
    /** Condition for waiting puts */
    private final Condition notFull;
    transient Itrs itrs = null;

    /**
     * Creates an {@code ArrayBlockingQueue} with the given (fixed)
     * capacity and the specified access policy.
     *
     * @param capacity the capacity of this queue
     * @param fair if {@code true} then queue accesses for threads blocked
     *        on insertion or removal, are processed in FIFO order;
     *        if {@code false} the access order is unspecified.
     * @throws IllegalArgumentException if {@code capacity < 1}
     */
    public ArrayBlockingQueue(int capacity, boolean fair) {
        if (capacity <= 0)
            throw new IllegalArgumentException();
        this.items = new Object[capacity];
        lock = new ReentrantLock(fair);
        notEmpty = lock.newCondition();
        notFull =  lock.newCondition();
    }
}
~~~
基于**数组**实现的**线程安全**的**有界**队列，通过ReentrantLock及其条件，控制共享资源Object数组的操作。
构造函数中fair用于控制唤醒阻塞的访问线程是否按FIFO顺序。
使用takeIndex和putIndex记录将要出队和入队的元素将要放到items数组中的下标，putIndex一直增长，直到和数组长度相等时置为0，
takeIndex从0开始，一直增长，同样到数组长度后重置为0,当出现队列满了后，调用put会阻塞。队列为空时，调用take方法会阻塞。
不允许添加null值。

> 此实现可以用于生产者-消费者模式。

## LinkedBlockingQueue
固定长度的基于单向链表结构的队列。
``` java
public class LinkedBlockingQueue<E> extends AbstractQueue<E>
        implements BlockingQueue<E>, java.io.Serializable {
    private static final long serialVersionUID = -6903933977591709194L;
    /**
     * Linked list node class
     */
    static class Node<E> {
        E item;

        /**
         * One of:
         * - the real successor Node
         * - this Node, meaning the successor is head.next
         * - null, meaning there is no successor (this is the last node)
         */
        Node<E> next;

        Node(E x) { item = x; }
    }

    /** The capacity bound, or Integer.MAX_VALUE if none */
    private final int capacity;

    /** Current number of elements */
    private final AtomicInteger count = new AtomicInteger();

    /**
     * Head of linked list.
     * Invariant: head.item == null
     */
    transient Node<E> head;

    /**
     * Tail of linked list.
     * Invariant: last.next == null
     */
    private transient Node<E> last;

    /** Lock held by take, poll, etc */
    private final ReentrantLock takeLock = new ReentrantLock();

    /** Wait queue for waiting takes */
    private final Condition notEmpty = takeLock.newCondition();

    /** Lock held by put, offer, etc */
    private final ReentrantLock putLock = new ReentrantLock();

    /** Wait queue for waiting puts */
    private final Condition notFull = putLock.newCondition();
    }
```
根据构造函数是否传入容量决定是否有界，最大容量为int最大值，超过容量抛出异常或等待一定时间后再抛出异常。通过内部单向链表实现队列，使用2个ReentrantLock控制数据的读取和新增，
由于2个锁相互独立，因此读写互不影响，在多线程情况下并发性能较高。无界的情况下，每次入队生成新的Node对象，出队产生废弃的Node对象，因此需要注意内存溢出。
> ArrayBlockingQueue 性能 > LinkedBlockingQueue

不允许添加null值。空队列包含一个空值Node，队head永远指向这个空值Node（出队时将正数第二个Node值弹出，然后将他的值设为空，变为队头）。

## ConcurrentLinkedQueue

基于链表实现的线程安全的**无界队列**，由于*size方法需要遍历整个容器*，所以调用此方法时性能取决于队列中实际需要遍历的元素数量，并且由于在遍历过程中元素数量可能发生变化，导致得到一个错误的返回值。
不允许添加null值。初始时队列的head和tail指向空值的node。当队列元素为1时，head指向此节点，tail及其next指向一个空值节点，大于1个元素时，tail节点才会指向尾节点。

底层使用volatile保证多线程下可见性，UNSAFE的cas操作保证原子性，做到线程安全。


removeAll,retailAll,containsAll,equals,toArray,isEmpty,size方法并不保证原子性，因而不具有线程安全。

## PriorityBlockingQueue
通过一个ReentrantLock保证线程安全，内部封装PriorityQueue操作实现**无界优先队列**。逻辑上的无界，当无法申请到足够的内存空间时抛出OutOfMemory异常。
```java
public class PriorityBlockingQueue<E> extends AbstractQueue<E>
    implements BlockingQueue<E>, java.io.Serializable {
    private static final long serialVersionUID = 5595510919245408276L;

    /*
     * The implementation uses an array-based binary heap, with public
     * operations protected with a single lock. However, allocation
     * during resizing uses a simple spinlock (used only while not
     * holding main lock) in order to allow takes to operate
     * concurrently with allocation.  This avoids repeated
     * postponement of waiting consumers and consequent element
     * build-up. The need to back away from lock during allocation
     * makes it impossible to simply wrap delegated
     * java.util.PriorityQueue operations within a lock, as was done
     * in a previous version of this class. To maintain
     * interoperability, a plain PriorityQueue is still used during
     * serialization, which maintains compatibility at the expense of
     * transiently doubling overhead.
     */

    /**
     * Default array capacity.
     */
    private static final int DEFAULT_INITIAL_CAPACITY = 11;

    /**
     * The maximum size of array to allocate.
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * Priority queue represented as a balanced binary heap: the two
     * children of queue[n] are queue[2*n+1] and queue[2*(n+1)].  The
     * priority queue is ordered by comparator, or by the elements'
     * natural ordering, if comparator is null: For each node n in the
     * heap and each descendant d of n, n <= d.  The element with the
     * lowest value is in queue[0], assuming the queue is nonempty.
     */
    private transient Object[] queue;

    /**
     * The number of elements in the priority queue.
     */
    private transient int size;

    /**
     * The comparator, or null if priority queue uses elements'
     * natural ordering.
     */
    private transient Comparator<? super E> comparator;

    /**
     * Lock used for all public operations
     */
    private final ReentrantLock lock;

    /**
     * Condition for blocking when empty
     */
    private final Condition notEmpty;

    /**
     * Spinlock for allocation, acquired via CAS.
     */
    private transient volatile int allocationSpinLock;

    /**
     * A plain PriorityQueue used only for serialization,
     * to maintain compatibility with previous versions
     * of this class. Non-null only during serialization/deserialization.
     */
    private PriorityQueue<E> q;
    //...
    }

```
由于有优先级的功能，可以用来做任务调度执行的排序等有顺序要求的功能。


## SynchronousQueue
无数据缓冲队列，每个put操作必须等待一个take操作，反之一样。队列没有实际容量，不能迭代。不允许使用null。提供公平策略，使put和take线程组按FIFO顺序put和take。volatile保证可见性，使用UNSAFE类进行CAS操作，**线程安全**。入队线程在没有put或take线程等待的时候，通过LockSupport调用UNSAFE的park方法挂起，在可以执行的时候调用unpark方法恢复。
> 方法和注意事项：

1. 此类Iterator直接返回Collections.emptyIterator()。hasNext永远返回false，next抛出NoSuchElementException异常。
2. peek永远返回null。
3. put线程调用put方法后一直等待有其他线程将其put的元素取走或已经有排队的线程，直接将元素取走。
4. offer方法会立即返回，不同的是如果没有等待的线程，返回false，如果已经有等待的线程，等待线程取走元素，offer方法返回true。此方法提供一个有等待时间的重载方法，允许offer线程等待一段时间后再返回。
5. take取出元素（即从put的线程获取元素），取不到就阻塞直到有其他线程put元素。
6. poll从put线程获取元素，此方法立即返回，只有恰好有其他线程put或offer元素或已经有put线程在等待时，才会获得元素，否则返回null。此方法提供一个有等待时间的重载方法，允许poll线程等待一段时间后再返回。
7. isEmpty永远返回true。
8. remainingCapacity永远返回0。
9. remove、removeAll、contains永远返回false。

SynchronousQueue内部通过不同的数据结构实现公平策略。
```java
public SynchronousQueue() {
        this(false);
    }

public SynchronousQueue(boolean fair) {
        transferer = fair ? new TransferQueue<E>() : new TransferStack<E>();
    }
```
非公平模式下使用栈结构，公平模式下使用队列结构。
### 核心方法为transfer以及node实现

#### queue模式
```java
 E transfer(E e, boolean timed, long nanos) {
            /* Basic algorithm is to loop trying to take either of
             * two actions:
             *
             * 1. 如果队列是空的或新入队的线程与等待线程队列的操作相同（都是put线程或者都是          
             * take线程），则尝试将此线程加入到等待队列中，等待有相反操作的线程到来或取消等待（被中断），操作成功会返回node的item值。
             *
             * 2. 如果队列不为空，并且入队的线程为等待线程队列的相反操作线程，尝试执行CAS操作，并将第* 一个入队的线程弹出，并返回put线程的item值。
             *
             * 在上面的操作中，每次操作成功都会尝试变更队列的head和tail的位置。
             *
             * 在循环一开始，检查head或tail为null,如果为null则重置循环. 
             */
            QNode s = null; // constructed/reused as needed
            boolean isData = (e != null);

            for (;;) {
                QNode t = tail;
                QNode h = head;
                if (t == null || h == null)         // saw uninitialized value
                    continue;                       // spin

                if (h == t || t.isData == isData) { // empty or same-mode
                    QNode tn = t.next;
                    if (t != tail)                  // inconsistent read
                        continue;
                    if (tn != null) {               // lagging tail
                        advanceTail(t, tn);
                        continue;
                    }
                    if (timed && nanos <= 0)        // can't wait
                        return null;
                    if (s == null)
                        s = new QNode(e, isData);
                    if (!t.casNext(null, s))        // failed to link in
                        continue;

                    advanceTail(t, s);              // swing tail and wait
                    Object x = awaitFulfill(s, e, timed, nanos);
                    if (x == s) {                   // wait was cancelled
                        clean(t, s);
                        return null;
                    }

                    if (!s.isOffList()) {           // not already unlinked
                        advanceHead(t, s);          // unlink if head
                        if (x != null)              // and forget fields
                            s.item = s;
                        s.waiter = null;
                    }
                    return (x != null) ? (E)x : e;

                } else {                            // complementary-mode
                    QNode m = h.next;               // node to fulfill
                    if (t != tail || m == null || h != head)
                        continue;                   // inconsistent read

                    Object x = m.item;
                    if (isData == (x != null) ||    // m already fulfilled
                        x == m ||                   // m cancelled
                        !m.casItem(x, e)) {         // lost CAS
                        advanceHead(h, m);          // dequeue and retry
                        continue;
                    }

                    advanceHead(h, m);              // successfully fulfilled
                    LockSupport.unpark(m.waiter);
                    return (x != null) ? (E)x : e;
                }
            }
        }
```

```java
static final class QNode {
            volatile QNode next;          // next node in queue
            volatile Object item;         // put的值，CAS'ed to or from null
            volatile Thread waiter;       // 调用put或take的线程，to control park/unpark
            final boolean isData; //是否有值，有为put操作，没有为take操作，区分互补的2种操作线程

            QNode(Object item, boolean isData) {
                this.item = item;
                this.isData = isData;
            }

            boolean casNext(QNode cmp, QNode val) {
                return next == cmp &&
                    UNSAFE.compareAndSwapObject(this, nextOffset, cmp, val);
            }

            boolean casItem(Object cmp, Object val) {
                return item == cmp &&
                    UNSAFE.compareAndSwapObject(this, itemOffset, cmp, val);
            }

            /**
             * Tries to cancel by CAS'ing ref to this as item.
             */
            void tryCancel(Object cmp) {
                UNSAFE.compareAndSwapObject(this, itemOffset, cmp, this);
            }

            boolean isCancelled() {
                return item == this;
            }

            /**
             * Returns true if this node is known to be off the queue
             * because its next pointer has been forgotten due to
             * an advanceHead operation.
             */
            boolean isOffList() {
                return next == this;
            }

            // Unsafe mechanics
            private static final sun.misc.Unsafe UNSAFE;
            private static final long itemOffset;
            private static final long nextOffset;

            static {
                try {
                    UNSAFE = sun.misc.Unsafe.getUnsafe();
                    Class<?> k = QNode.class;
                    itemOffset = UNSAFE.objectFieldOffset
                        (k.getDeclaredField("item"));
                    nextOffset = UNSAFE.objectFieldOffset
                        (k.getDeclaredField("next"));
                } catch (Exception e) {
                    throw new Error(e);
                }
            }
        }
```

### stack模式
```java
E transfer(E e, boolean timed, long nanos) {
            /*
             * Basic algorithm is to loop trying one of three actions:
             *
             * 1. If apparently empty or already containing nodes of same
             *    mode, try to push node on stack and wait for a match,
             *    returning it, or null if cancelled.
             *
             * 2. If apparently containing node of complementary mode,
             *    try to push a fulfilling node on to stack, match
             *    with corresponding waiting node, pop both from
             *    stack, and return matched item. The matching or
             *    unlinking might not actually be necessary because of
             *    other threads performing action 3:
             *
             * 3. If top of stack already holds another fulfilling node,
             *    help it out by doing its match and/or pop
             *    operations, and then continue. The code for helping
             *    is essentially the same as for fulfilling, except
             *    that it doesn't return the item.
             */

            SNode s = null; // constructed/reused as needed
            int mode = (e == null) ? REQUEST : DATA;

            for (;;) {
                SNode h = head;
                if (h == null || h.mode == mode) {  // empty or same-mode
                    if (timed && nanos <= 0) {      // can't wait
                        if (h != null && h.isCancelled())
                            casHead(h, h.next);     // pop cancelled node
                        else
                            return null;
                    } else if (casHead(h, s = snode(s, e, h, mode))) {
                        SNode m = awaitFulfill(s, timed, nanos);
                        if (m == s) {               // wait was cancelled
                            clean(s);
                            return null;
                        }
                        if ((h = head) != null && h.next == s)
                            casHead(h, s.next);     // help s's fulfiller
                        return (E) ((mode == REQUEST) ? m.item : s.item);
                    }
                } else if (!isFulfilling(h.mode)) { // try to fulfill
                    if (h.isCancelled())            // already cancelled
                        casHead(h, h.next);         // pop and retry
                    else if (casHead(h, s=snode(s, e, h, FULFILLING|mode))) {
                        for (;;) { // loop until matched or waiters disappear
                            SNode m = s.next;       // m is s's match
                            if (m == null) {        // all waiters are gone
                                casHead(s, null);   // pop fulfill node
                                s = null;           // use new node next time
                                break;              // restart main loop
                            }
                            SNode mn = m.next;
                            if (m.tryMatch(s)) {
                                casHead(s, mn);     // pop both s and m
                                return (E) ((mode == REQUEST) ? m.item : s.item);
                            } else                  // lost match
                                s.casNext(m, mn);   // help unlink
                        }
                    }
                } else {                            // help a fulfiller
                    SNode m = h.next;               // m is h's match
                    if (m == null)                  // waiter is gone
                        casHead(h, null);           // pop fulfilling node
                    else {
                        SNode mn = m.next;
                        if (m.tryMatch(h))          // help match
                            casHead(h, mn);         // pop both h and m
                        else                        // lost match
                            h.casNext(m, mn);       // help unlink
                    }
                }
            }
        }
```
```java
static final class SNode {
            volatile SNode next;        // next node in stack
            volatile SNode match;       // the node matched to this
            volatile Thread waiter;     // to control park/unpark
            Object item;                // data; or null for REQUESTs
            int mode;
            // Note: item and mode fields don't need to be volatile
            // since they are always written before, and read after,
            // other volatile/atomic operations.

            SNode(Object item) {
                this.item = item;
            }

            boolean casNext(SNode cmp, SNode val) {
                return cmp == next &&
                    UNSAFE.compareAndSwapObject(this, nextOffset, cmp, val);
            }

            /**
             * Tries to match node s to this node, if so, waking up thread.
             * Fulfillers call tryMatch to identify their waiters.
             * Waiters block until they have been matched.
             *
             * @param s the node to match
             * @return true if successfully matched to s
             */
            boolean tryMatch(SNode s) {
                if (match == null &&
                    UNSAFE.compareAndSwapObject(this, matchOffset, null, s)) {
                    Thread w = waiter;
                    if (w != null) {    // waiters need at most one unpark
                        waiter = null;
                        LockSupport.unpark(w);
                    }
                    return true;
                }
                return match == s;
            }

            /**
             * Tries to cancel a wait by matching node to itself.
             */
            void tryCancel() {
                UNSAFE.compareAndSwapObject(this, matchOffset, null, this);
            }

            boolean isCancelled() {
                return match == this;
            }

            // Unsafe mechanics
            private static final sun.misc.Unsafe UNSAFE;
            private static final long matchOffset;
            private static final long nextOffset;

            static {
                try {
                    UNSAFE = sun.misc.Unsafe.getUnsafe();
                    Class<?> k = SNode.class;
                    matchOffset = UNSAFE.objectFieldOffset
                        (k.getDeclaredField("match"));
                    nextOffset = UNSAFE.objectFieldOffset
                        (k.getDeclaredField("next"));
                } catch (Exception e) {
                    throw new Error(e);
                }
            }
        }
```

### 总结
操作的线程为互补线程，通过isData区分，true为put，false为take，生成2种对应的node，操作时，如果等待线程是空的，线程挂起，进入等待线程组，后面的线程入队时，判断与已经等待的线程是不是同一种操作，如果是，挂起进入等待线程组，如果是相反的互补操作，则执行transfar操作，根据策略不同，唤醒一个在等待的线程，返回数据，并将唤醒的线程移出等待线程组，并更新SynchronousQueue的头和尾。

> 可用于线程间通讯。需要严格遵守生产者消费者模式的场景。Executors.newCachedThreadPool()任务队列就使用SynchronousQueue。

## LinkedTransferQueue
类似SynchronousQueue，但实现的是公平模式下的SynchronousQueue且**具有实际的容量的无界阻塞队列**，put或take线程遵守FIFO。原理与SynchronousQueue一样，LinkedTransferQueue维护一个等待线程组，这个线程组类型全为take或者全为put，当与其互补的一个线程到来后，立即唤醒等待的第一个线程，完成数据的传递，并出队，当与其类型相同的一个线程到来后，生成一个新的节点入队。

> 与SynchronousQueue不同，LinkedTransferQueue实现了Collection和Iterator的接口，有实际返回值，且可以使用put，

> 和ConcurrentLinkedQueue一样，removeAll,retailAll,containsAll,equals,toArray,isEmpty,size方法并不保证原子性，因而不具有线程安全,并且size方法需要遍历整个队列，时间取决于队列中节点数量。

重要的方法：
1. transfer(E e)：若当前存在一个正在等待获取的消费者线程，即立刻移交之；否则，会插入当前元素e到队列尾部，并且等待进入阻塞状态，到有消费者线程取走该元素。
2. tryTransfer(E e)：若当前存在一个正在等待获取的消费者线程（使用take()或者poll()函数），使用该方法会即刻转移/传输对象元素e；若不存在，则返回false，并且不进入队列。这是一个不阻塞的操作。
3. tryTransfer(E e, long timeout, TimeUnit unit)：若当前存在一个正在等待获取的消费者线程，会立即传输给它;否则将插入元素e到队列尾部，并且等待被消费者线程获取消费掉；若在指定的时间内元素e无法被消费者线程获取，则返回false，同时该元素被移除。
4. hasWaitingConsumer()：判断是否存在消费者线程。
5. getWaitingConsumerCount()：获取所有等待获取元素的消费线程数量。

**transfer方法保证了队列中新加入节点之前的节点都被消费过。**
### 核心方法
```java
/**
     * Implements all queuing methods. See above for explanation.
     *
     * @param e the item or null for take
     * @param haveData true if this is a put, else a take
     * @param how NOW, ASYNC, SYNC, or TIMED
     * @param nanos timeout in nanosecs, used only if mode is TIMED
     * @return an item if matched, else e
     * @throws NullPointerException if haveData mode but e is null
     */
    private E xfer(E e, boolean haveData, int how, long nanos) {
        if (haveData && (e == null))//操作类型和数据是否匹配，匹配才往下执行
            throw new NullPointerException();
        Node s = null;                        // the node to append, if needed

        retry:
        for (;;) {                            // restart on append race

            for (Node h = head, p = h; p != null;) { // find & match first node
                boolean isData = p.isData;
                Object item = p.item;
                if (item != p && (item != null) == isData) { // unmatched
                    if (isData == haveData)   // 操作类型相同，中断互补查找
                        break;
                    if (p.casItem(item, e)) { // 操作类型不同，使用cas操作尝试将找到的节点操作状态变更，如果成功则继续
                        for (Node q = p; q != h;) {
                            Node n = q.next;  // update by 2 unless singleton
                            if (head == h && casHead(h, n == null ? q : n)) {
                                h.forgetNext();
                                break;
                            }                 // advance and retry
                            if ((h = head)   == null ||
                                (q = h.next) == null || !q.isMatched())
                                break;        // unless slack < 2
                        }
                        LockSupport.unpark(p.waiter);
                        return LinkedTransferQueue.<E>cast(item);
                    }
                }
                //进行目标节点后移和头节点修正
                Node n = p.next;
                p = (p != n) ? n : (h = head); // Use head if p offlist
            }

            if (how != NOW) {                 //没有找到互补节点的操作，将调用线程和数据生成新node入队
                if (s == null)
                    s = new Node(e, haveData);
                Node pred = tryAppend(s, haveData);
                if (pred == null)
                    continue retry;           // lost race vs opposite mode
                if (how != ASYNC)
                    return awaitMatch(s, pred, e, (how == TIMED), nanos);
            }
            return e; // not waiting
        }
    }
```

## DelayQueue
基于PriorityQueue实现的以过期时间作为优先排序的线程安全无界阻塞队列，不允许存放null值，且存放元素需要实现Delayed接口。
```java
public class DelayQueue<E extends Delayed> extends AbstractQueue<E>
    implements BlockingQueue<E> {

    private final transient ReentrantLock lock = new ReentrantLock();
    private final PriorityQueue<E> q = new PriorityQueue<E>();

    /**
     *领导追随者模式的leader线程，take和带有超时时间的poll方法执行时，如果队首为 *null或队首超时时间没到，则设置当前线程为leader并在available上一直wait或   *wait剩下的超时时间，直到收到信号继续执行或过了
     *wait时间，继续尝试获取并移除队首
     */
    private Thread leader = null;

    /**
     * Condition signalled when a newer element becomes available
     * at the head of the queue or a new thread may need to
     * become leader.
     */
    private final Condition available = lock.newCondition();
    }
```
队首元素为超时元素，超时元素通过take/poll方法中通过死循环结合线程等待来判断，由于是使用ReentrantLock封装的优先队列（在priorityQueue的方法调用外使用lock），元素在入队时已经调整了存储的二叉树结构，并且在每次take/off后都会进行结构调整，所以保证了每次取到的队首都是最近要过期的。


# Deque

## ArrayDeque
*基于可变数组实现的无界双端队列（最大为int最大值）*，数组用于存储入队的元素，通过下标标记出队头和队尾，不能存放null元素，非线程安全。下列方法时间复杂度随存放元素的多少呈线性增长（因为方法内对数组进行遍历）：
> remove、removeFirstOccurrence、removeLastOccurrence、contains、iterator.remove() 以及批量操作

同ArrayList一样，iterator迭代时，如果不是通过iterator对结构做出改变，会使得并发的iterator和for遍历操作抛出ConcurrentModificationException，且此异常并不保证一定抛出，因此不能依赖此异常的抛出做任何业务上的逻辑处理。    
ArrayDeque每次扩容后的新容量为原来的2倍。内部elements数组在实现队列时，队头从数组**下标高位**开始，队尾从数组**下标低位**开始。head下标的有元素，获取是直接获取下标对应元素，tail小标没有元素，每次获取时需要执行tail-1获取下标，再取元素。当head==tail+1或head-1==tail时，数组进行扩容。
ArrayDeque可用于堆栈和队列。合理的使用情况下，性能优于Stack和LinkedList。

### addFirst==offerFirst==push
```java
public void addFirst(E e) {
        if (e == null)
            throw new NullPointerException();
        //head从高位开始，每次从头入队
        elements[head = (head - 1) & (elements.length - 1)] = e;
        if (head == tail)
            doubleCapacity();
    }
```

### addLast==offerLast==add
```java
public void addLast(E e) {
        if (e == null)
            throw new NullPointerException();
        elements[tail] = e;
        //每次tail存放后会检查下次入队是否会使head==tail，如果会就扩容，也就是说每次从尾部入队至少会保证有一个位置是空的，使从头部入队不会覆盖元素
        //并且更新tail=tail+1，即真实的tail在数组中的位置为tail-1，tail处并没有元素
        if ( (tail = (tail + 1) & (elements.length - 1)) == head)
            doubleCapacity();
    }
```

### 扩容
```java
private void doubleCapacity() {
        assert head == tail;
        int p = head;
        int n = elements.length;
        int r = n - p; // number of elements to the right of p
        int newCapacity = n << 1;
        if (newCapacity < 0)
            throw new IllegalStateException("Sorry, deque too big");
        Object[] a = new Object[newCapacity];
        //将原来数组从head到数组结束的元素拷贝到新数组
        System.arraycopy(elements, p, a, 0, r);
        //将原来数组从tail倒数到下标0处的元素拷贝到新数组
        System.arraycopy(elements, 0, a, r, p);
        //原来的队列是分离的，首尾在物理上并不保持由首到尾的关系，copy后，在新的数组靠前的位置形成物理上由首到尾关系的队列，相当于原来tail所领导的元素划拨给了head领导
        elements = a;
        head = 0;//下次head位置会变到新数组的末尾
        tail = n;//修正尾部
    }
```

### size
```java
public int size() {
        //tail-head为负数，&运算后得到正确的容量，相当于elements.length-|(tail - head)|
        return (tail - head) & (elements.length - 1);
    }
```

### pollFirst=removeFirst==pop==remove==poll
```java
public E pollFirst() {
        int h = head;
        @SuppressWarnings("unchecked")
        E result = (E) elements[h];
        // Element is null if deque empty
        if (result == null)
            return null;
        elements[h] = null;     // Must null out slot
        head = (h + 1) & (elements.length - 1);
        return result;
    }
```
### pollLast==removeLast
```java
public E pollLast() {
        int t = (tail - 1) & (elements.length - 1);
        @SuppressWarnings("unchecked")
        E result = (E) elements[t];
        if (result == null)
            return null;
        elements[t] = null;
        tail = t;
        return result;
    }
```

## LinkedBlockingDeque
基于双链表结构，可指定容量的双端阻塞队列。容量不指定时，取int的最大值。不超过容量的情况下，每次入队时生成新的节点。
大部分的操作时间复杂度都是O(1)，但一部分操作需要遍历整个队列，时间复杂度O(n)，随队列中元素数量增长而线性增长。这些方法包括：

> remove、removeFirstOccurrence、removeLastOccurrence、contains、iterator.remove()

注意此类并**没有提供批量操作**（被注释去掉）。     
使用一个ReentrantLock及2个Conditon进行并发控制。
> ReentrantLock：入队和出队操作并发时线程安全。
> Conditon：队列为空时阻塞读取操作，队列满时阻塞入队操作。

此类提供的迭代器是弱一致性模型迭代器，在多线程并发情况下，使用迭代器可能出现数据不一致且不会抛出异常的情况。

```java
public class LinkedBlockingDeque<E>
    extends AbstractQueue<E>
    implements BlockingDeque<E>, java.io.Serializable {
    private static final long serialVersionUID = -387911632671998426L;

    /** Doubly-linked list node class */
    static final class Node<E> {
        /**
         * The item, or null if this node has been removed.
         */
        E item;

        /**
         * One of:
         * - the real predecessor Node
         * - this Node, meaning the predecessor is tail
         * - null, meaning there is no predecessor
         */
        Node<E> prev;

        /**
         * One of:
         * - the real successor Node
         * - this Node, meaning the successor is head
         * - null, meaning there is no successor
         */
        Node<E> next;

        Node(E x) {
            item = x;
        }
    }

    /**
     * Pointer to first node.
     * Invariant: (first == null && last == null) ||
     *            (first.prev == null && first.item != null)
     */
    transient Node<E> first;

    /**
     * Pointer to last node.
     * Invariant: (first == null && last == null) ||
     *            (last.next == null && last.item != null)
     */
    transient Node<E> last;

    /** 队列中的元素数量 */
    private transient int count;

    /** 队列容量 */
    private final int capacity;

    /** 并发控制锁 */
    final ReentrantLock lock = new ReentrantLock();
    
    /** 队列不为空条件，take类方法在此条件阻塞 */
    private final Condition notEmpty = lock.newCondition();
    
    /** 队列不满条件，put类方法在此条件阻塞 */
    private final Condition notFull = lock.newCondition();
    //...
    }
```



## ConcurrentLinkedDeque
基于双链表的无界无阻塞线程安全双端队列。不能添加null元素。
此类提供的迭代器是弱一致性模型迭代器，在多线程并发情况下，使用迭代器可能出现数据不一致且不会抛出异常的情况。
addAll,ermoveAll,retainAll,containsAll,equals,toArray操作不是原子性操作，因而不具有线程安全。
size方法由于需要遍历这个链表，性能较低，复杂度为O(n)。使用CAS实现无锁算法（lock-free）。


```java
public class ConcurrentLinkedDeque<E>
    extends AbstractCollection<E>
    implements Deque<E>, java.io.Serializable {

    private static final long serialVersionUID = 876323262645176354L;
    //队头
    private transient volatile Node<E> head;
    //队尾
    private transient volatile Node<E> tail;

    private static final Node<Object> PREV_TERMINATOR, NEXT_TERMINATOR;

    @SuppressWarnings("unchecked")
    Node<E> prevTerminator() {
        return (Node<E>) PREV_TERMINATOR;
    }

    @SuppressWarnings("unchecked")
    Node<E> nextTerminator() {
        return (Node<E>) NEXT_TERMINATOR;
    }

    static final class Node<E> {
        volatile Node<E> prev;
        volatile E item;
        volatile Node<E> next;

        Node() {  // default constructor for NEXT_TERMINATOR, PREV_TERMINATOR
        }

        /**
         * Constructs a new node.  Uses relaxed write because item can
         * only be seen after publication via casNext or casPrev.
         */
        Node(E item) {
            UNSAFE.putObject(this, itemOffset, item);
        }

        boolean casItem(E cmp, E val) {
            return UNSAFE.compareAndSwapObject(this, itemOffset, cmp, val);
        }

        void lazySetNext(Node<E> val) {
            UNSAFE.putOrderedObject(this, nextOffset, val);
        }

        boolean casNext(Node<E> cmp, Node<E> val) {
            return UNSAFE.compareAndSwapObject(this, nextOffset, cmp, val);
        }

        void lazySetPrev(Node<E> val) {
            UNSAFE.putOrderedObject(this, prevOffset, val);
        }

        boolean casPrev(Node<E> cmp, Node<E> val) {
            return UNSAFE.compareAndSwapObject(this, prevOffset, cmp, val);
        }

        // Unsafe mechanics
        private static final sun.misc.Unsafe UNSAFE;
        private static final long prevOffset;
        private static final long itemOffset;
        private static final long nextOffset;

        static {
            try {
                UNSAFE = sun.misc.Unsafe.getUnsafe();
                Class<?> k = Node.class;
                prevOffset = UNSAFE.objectFieldOffset
                    (k.getDeclaredField("prev"));
                itemOffset = UNSAFE.objectFieldOffset
                    (k.getDeclaredField("item"));
                nextOffset = UNSAFE.objectFieldOffset
                    (k.getDeclaredField("next"));
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }
    //...
    public ConcurrentLinkedDeque() {
        head = tail = new Node<E>(null);
    }

    private static final sun.misc.Unsafe UNSAFE;
    private static final long headOffset;
    private static final long tailOffset;
    static {
        PREV_TERMINATOR = new Node<Object>();
        PREV_TERMINATOR.next = PREV_TERMINATOR;
        NEXT_TERMINATOR = new Node<Object>();
        NEXT_TERMINATOR.prev = NEXT_TERMINATOR;
        try {
            UNSAFE = sun.misc.Unsafe.getUnsafe();
            Class<?> k = ConcurrentLinkedDeque.class;
            headOffset = UNSAFE.objectFieldOffset
                (k.getDeclaredField("head"));
            tailOffset = UNSAFE.objectFieldOffset
                (k.getDeclaredField("tail"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}
```
初始时，head和tail同时指向空值节点，如果第一个元素从队头入队时，新生成一个节点，head（tail）节点prev指向新节点，如果第一个元素从队尾入队时，新生成一个节点，head(tail)的next指向新节点。如果元素一开始从队头入队（addFirst），之后当有元素从队尾入队，从队头入队的元素和从队尾入队的元素之间会有空值节点，仅当第二从队尾入队操作结束，才会消除空值节点。如果元素一开始从队尾入队，然后又元素从队头入队，2种操作的元素之间会一直存在一个空值节点。

## LinkedList
见LinkedList