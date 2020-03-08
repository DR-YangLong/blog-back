title: 求斐波那契数列第N个数
date: 2018-03-11 19:40:56
categories: [java]
tags: [java]
---

# 求斐波那契数列第N个数
斐波那契数列的数学公式：
* F(0)=0
* F(1)=1
* F(n)=F(n-1)+F(n-2)  {n>=2}

后一个数等于它前两个数的和。

前13个为:
> 0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233

```java
/**
 * functional describe:1,1,2,3,5,8...f(n) = f(n-1)+ f(n-2)
 *
 * @author DR.YangLong [410357434@163.com]
 * @version 1.0    2017/6/15
 */
public class Fibonacci {
    /***
     * 递归实现方式
     * @param n 第n位
     * @return 第n位的数值
     */
    public static int fibonacci(int n) {
        if (n <= 2) {
            return 1;
        } else {
            return fibonacci(n - 1) + fibonacci(n - 2);
        }
    }


    /**
     * 递推实现方式
     * @param n 第n位
     * @return 第n位的数值
     */
    public static int fibonacciLoop(int n) {
        if (n <= 2) {
            return 1;
        }
        int n1 = 1, n2 = 1, sn = 0;
        for (int i = 0; i < n - 2; i++) {
            sn = n1 + n2;
            n1 = n2;
            n2 = sn;
        }
        return sn;
    }

    public static void main(String[] args) {
        //1、1、2、3、5、8、13、21、34、55、89、144、233
        System.out.println(fibonacci(5));
        System.out.println(fibonacciLoop(5));
    }

}
```