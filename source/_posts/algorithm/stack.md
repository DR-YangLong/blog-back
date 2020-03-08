title: 基于数组的stack结构实现
date: 2018-03-11 19:40:56
categories: [java]
tags: [java]
---

# 基于数组的stack结构实现

## 接口定义

```java
public interface Stack {
    //栈大小
    int getSize();
    //入栈
    void push(Object o);
    //出栈
    Object pop();
    //是否为空
    boolean isEmpty();
    //查看栈顶端元素，不删除
    Object top();
}
```
<!-- more -->
## 异常定义

```java
public class StackEmptyException extends RuntimeException{
    public StackEmptyException(String message) {
        super(message);
    }
}

public class StackOverFlowException extends RuntimeException {
    public StackOverFlowException(String message) {
        super(message);
    }
}
```

## 实现
```java
public class ArrayStack implements Stack{
    //默认数组长度
    private static final int DEFAULT_CAPACITY=1024;
    //栈顶位置
    private int top_position=-1;
    //栈数组
    private Object[] stack;
    //实际数组长度
    private int capacity;

    public ArrayStack() {
        this(DEFAULT_CAPACITY);
    }

    public ArrayStack(int capacity) {
        this.capacity = capacity;
        stack=new Object[capacity];
    }

    @Override
    public int getSize() {
        return top_position+1;
    }

    @Override
    public void push(Object o) {
        if(getSize()<capacity){
            stack[++top_position]=o;
        }else {
            throw new StackOverFlowException("栈溢出");
        }
    }

    @Override
    public Object pop() {
        if(!isEmpty()){
            Object element=stack[top_position];
            stack[top_position--]=null;
            return element;
        }else {
            throw  new StackEmptyException("空栈");
        }
    }

    @Override
    public boolean isEmpty() {
        return top_position<0;
    }

    @Override
    public Object top() {
        if(!isEmpty()){
            return stack[top_position];
        }else {
            throw  new StackEmptyException("空栈");
        }
    }
}
```