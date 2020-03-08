title: Spring无法通过接口注入实现类
date: 2015-06-25 20:09:56
categories: [java]
tags: [java,Spring,spring 依赖注入]
---

# 关于Spring 无法注入接口的实现类，通常报错类似：
```java
Caused by: java.lang.IllegalArgumentException: Cannot convert value of type [xxxx] to required type [xxxxImpl] for property 'xxxx': no matching editors or conversion strategy found
```

## 首先检查aop设置，确认**当前配置文件中**配置了
```java
<aop:aspectj-autoproxy proxy-target-class="true"/>
```

## 其次
重命名你的接口，然后实现类实现的接口也更新为新的接口名称。

# 如果还不能解决，确认依赖jar包完整和其他配置无误。
