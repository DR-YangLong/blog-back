title: 合并排序
date: 2018-03-11 19:40:56
categories: [java]
tags: [java]
---

# 合并排序
将数列不停的拆分，直到只剩下一个元素，此时必然有序，然后将拆分的部分两两合并，最后成为一个有序数列。

## 实现

```java
import java.util.Comparator;
import java.util.LinkedList;

import static java.lang.System.out;

/**
 * functional describe:基于队列结构的归并排序算法（队列可换成数组，一般示例为数组）
 *
 * @author DR.YangLong [410357434@163.com]
 * @version 1.0    2017/8/8
 */
public class MergeSort<T> {
    private Comparator<T> comparator;

    //拆分，将s不断的拆分，直至拆分成一个元素的queue，则此时是有序的，然后合并
    public void sort(LinkedList<T> s) {
        int size = s.size();
        if (1 >= size) return;//递归基
        LinkedList<T> s1 = new LinkedList<>();
        LinkedList<T> s2 = new LinkedList<>();
        while (!s.isEmpty()) {//将s拆分到s1和s2
            s1.addLast(s.removeFirst());
            if (!s.isEmpty()) s2.addLast(s.removeFirst());
        }
        //递归拆分
        sort(s1);
        sort(s2);
        //合并单个元素的queue
        merge(s, s1, s2);
    }

    //合并，将2个有序queue合并成一个有序queue，使用2个待合并的queue长度为条件进行循环。
    private void merge(LinkedList<T> container, LinkedList<T> s1, LinkedList<T> s2) {
        while (!s1.isEmpty() || !s2.isEmpty()) {//只要其中一个不为空
            T e;//s1和s2中取出的较小元素
            if (s1.isEmpty()) {//s1已经取光，从s2中取
                e = s2.removeFirst();
            } else if (s2.isEmpty()) {//s2已经取光，从s1中取
                e = s1.removeFirst();
            } else if (comparator.compare(s1.getFirst(), s2.getFirst()) > 0) {//如果s1中第一个大于s2中第一个，则e取s2中第一个
                e = s2.removeFirst();
            } else {//s1中第一个小于s2中第一个，e取s1第一个
                e = s1.removeFirst();
            }
            //将e放入目标容器队尾
            container.addLast(e);
        }
    }

    public MergeSort(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public static void main(String[] args) {
        MergeSort<Integer> sort = new MergeSort<>((o1, o2) -> {
            if (o1 < o2) return -1;
            if (o1 > o2) return 1;
            return 0;
        });
        LinkedList<Integer> list=new LinkedList<>();
        list.add(0);
        list.add(8);
        list.add(6);
        list.add(9);
        list.add(7);
        list.add(2);
        list.add(4);
        list.add(3);
        list.add(5);
        list.add(1);
        sort.sort(list);
        list.forEach(out::print);
    }
}
```