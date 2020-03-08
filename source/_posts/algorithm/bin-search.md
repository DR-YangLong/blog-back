title: 二分查找
date: 2017-06-05 19:40:56
categories: [java]
tags: [java]
---

# 二分查找
在看Map源码的时候看到了红黑树，去了解了之后发现水太深了，去捡起大学时候的数据结构与算法恶补，顺便做下笔记。学无止境啊。

## 原理
二分查找依赖于一个有序列表，当查找一个元素A时，先用此元素和有序列表中点的元素B比较大小：           
如果A<B则说明需要查找的元素A位于B元素的左侧子序列中。            
如果A>B则说明需要查找的元素A位于B元素的右侧子序列中。               
然后根据情况，分别再用A和上面确定的子序列的中点元素比较，再次得到A的位于的子序列，重复直至找到A。          
可以看到，因为每次查找都会剔除剩余序列的一半元素，因而性能为log(n)。

<!-- more -->
## 实现
参照了维基百科的代码，搬运过来。

```java
public class BinarySearch {

    //递归实现
    static int binary_search_recursion(int arr[], int start, int end, int khey) {
        if (start > end)
            return -1;//递归基
        int mid = start + (end - start) / 2; //直接平均可能会溢出，所以用此算法，与线段中点坐标公式【（start+end）/2】结果相同
        if (arr[mid] > khey)
            return binary_search_recursion(arr, start, mid - 1, khey);
        if (arr[mid] < khey)
            return binary_search_recursion(arr, mid + 1, end, khey);
        return mid; //不大不小，=
    }

    //循环实现
    static int binary_search_loop(int arr[], int start, int end, int khey) {
        int mid;
        while (start <= end) {
            mid = start + (end - start)/2; //直接平均可能溢出，所以用此算法
            if (arr[mid] < khey)
                start = mid + 1;
            else if (arr[mid] > khey)
                end = mid - 1;
            else
                return mid;
        }
        return -1;
    }

    public static void main(String[] args) {
        int[] a={1,2,3,4,6,7,8,9,10};
        int mid=binary_search_recursion(a,0,a.length-1,2);
        int mid1=binary_search_loop(a,0,a.length-1,2);
        System.out.println(mid);
        System.out.println(mid1);
    }
}
```