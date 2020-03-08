title: 快速排序
date: 2018-03-11 19:40:56
categories: [java]
tags: [java]
---

```java
import java.util.Arrays;

import static java.lang.System.out;

/**
 * functional describe:快速排序算法
 *
 * @author DR.YangLong [410357434@163.com]
 * @version 1.0    2017/5/16 14:15
 */
public class QuickSort {

    //找中轴线
    public static int midIndex(int[] a, int i, int j) {
        int l = i, r = j;//获取排序的区间
        int x = a[i];//第一个数作为基数
        while (l < r) {
            while (l < r && a[r] >= x) r--;//从后向前找到第一个比基准数小的数
            if (l < r) {
                a[l] = a[r];//将小的数填到基准数左边
                l++;//向后移动一个位置
            }
            while (l < r && a[l] <= x) l++;//从前向后找到比基准数大的数
            if (l < r) {
                a[r] = a[l];
                r--;//向前移动一个位置
            }
        }
        a[l] = x;//循环退出时i=j位中间位置
        return l;
    }

    //排序
    static void sort(int[] a, int l, int r) {
        if (l < r) {
            int midIndex = midIndex(a, l, r);
            sort(a, l, midIndex - 1);
            sort(a, midIndex + 1, r);
        }
    }

    /**
     * 合并版本
     * @param a 待排序数组
     * @param left 排序数组起始位置
     * @param right 排序数组结束位置
     */
    public static void quickSort(int[] a, final int left, final int right) {
        if (null != a && a.length > 0 && left >= 0 && right > left && right < a.length) {
            int i = left, j = right;
            int x = a[left];//中间数，空出i的位置
            while (i < j) {
                while (i < j && a[j] >= x) j--;//从后向前找，直到找到第一个比基数小的数，停止
                if (i < j) {
                    a[i++] = a[j];//填到i的位置，并且下轮填到i的下一个位置，此时j的位置空出
                }
                while (i < j && a[i] <= x) i++;//从前向后找，直到找到第一个比基数大的数，停止
                if (i < j) {
                    a[j--] = a[i];//将大的数放到j的位置，并且下轮填到j的前一个位置，此时i的位置空出
                }
            }
            a[i] = x;//循环结束，找到中间位置i，将中间数填入
            quickSort(a, left, i-1);//对左边部分排序
            quickSort(a, i+1, right);//对右边部分排序
        }
    }

    public static void main(String[] args) {
        int[] a = {1, 2, 3, 7, 1, 6, 7, 8, 4, 3, 3, 9};
        sort(a, 0, a.length - 1);
        Arrays.stream(a).forEach(out::print);
        System.out.println("\n==================");
        int []b = {1, 2, 3, 7, 1, 6, 7, 8, 4, 3, 3, 9};
        quickSort(b,0,b.length-1);
        Arrays.stream(b).forEach(out::print);
    }
}
```