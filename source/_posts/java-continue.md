title: continue，break和goto
date: 2017-05-16 20:09:56
categories: [java]
tags: [java]
---

# java之continue，break，goto
先说goto，goto是java中保留关键字，实际并未使用，goto在c中很强大，直接中断当前执行流程跳转到标记所在的流程点开始执行，但是这种能力不好掌控。
java中可以使用continue和break加标签达到类似的效果。

## continue
continue在循环内部使用时，中断后面的代码执行，直接开始下一次循环。结合标签使用时，直接执行标签后面的循环代码的下一次循环：           
```java
    public static void main(String[] args) {
        //continue单层循环，output:13579
        for (int j = 0; j < 10; j++) {
            if (j % 2 == 0) continue;//当偶数时跳过后面的执行语句，直接继续increment语句
            //打印j
            System.out.print(j);
        }
        System.out.println("\n===========");
        //等同上面
        outer:
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) continue outer;
            System.out.print(i);
        }
        System.out.println("\n===========");
        outer:
        for (int i = 0; i < 5; i++) {
            //跳到outer处的increment语句执行
            for (int j = i; j < 5; j++) {
                if (j % 2 == 0) continue outer;
                System.out.println("inner:" + j);
            }
            //j的循环每次continue到i++，使得此处永不执行
            System.out.println("outer:" + i);
        }
    }
```

## break
break在循环内部使用时，中断后面代码执行并跳出**当前**循环，执行循环后面的代码，结合标签使用时，表示结束标签后的代码块执行，转而执行后面的代码。
```java
        //output:012
        for (int i = 0; i < 5; i++) {
            if(i>2)break;
            System.out.print(i);
        }

        /**
         * outer:1
         * inner not break:2
         * outer:3
         */
        for (int i = 0; i < 2; ) {
            while (i++<2){
                if(i<2) break;//i<2时结束内层循环
                System.out.println("inner not break:"+i);
            }
            System.out.println("outer:"+i);
        }

                /**
         * break-inner[j]:0
         * break-inner[j]:1
         * break-inner[j]:2
         * break-end
         */
        outer:
        for (int i=0; i <5; i++) {
            for (int j = 0; j <5; j++) {
                if(i==0&&j==3)break outer;//外层循环第一次执行，内层循环第4次执行时，中断标签后的循环代码块
                System.out.println("break-inner[j]:"+j);
            }
            System.out.println("break-outer[i]:"+i);
        }
        System.out.println("break-end");
```

## continue和break结合

```java
         /**
         * level_4[m]-0
         * level_2[k]-0
         */
        level_1:
        for (int j = 0; j <2; j++) {
            level_2:
            for (int k = 0; k <3; k++) {
                if(k==2)break level_1;//k<2时执行level_3
                level_3:
                for (int l = 0; l <2; l++) {
                    if (k==1)continue level_2;//只有k=0时执行了
                    for (int m = 0; m <2; m++) {
                        if(m==1)break level_3;//只有k=0&&m=0时执行了，当m>0时直接结束了level_3语句块
                        System.out.println("level_4[m]-"+m);
                    }
                    System.out.println("level_3[l]-"+l);
                }
                //k=0&&m=0执行，之后循环k=1，触发level_3的continue level_2，
                //直接k=2，然后触发break level_1，结束整段代码
                System.out.println("level_2[k]-"+k);
            }
            System.out.println("level_1[j]-"+j);
        }
```
