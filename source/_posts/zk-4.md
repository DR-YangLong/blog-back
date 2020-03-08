title: Zookeeper第四课
date: 2016-04-07 19:30:00
categories: [zookeeper]
tags: [zk,zookeeper]
---

# 权限控制 ACL(access control list)
zk的权限控制是用过固定的字符串模式来表示的。

## 权限组成
~~~ bash
[scheme]:[id]:[permission]
~~~
“[  ]”中是字符串变量。
> scheme：验证策略，shceme有：world，auth，digest，ip，super。
> id：权限被赋予的对象，不同的scheme有不同的id。
> permission：权限组合，为：c[创建]，r[读]，d[删除]，w[写]，a[权限控制]中的一个或多个组成。如：crd。

~~~
acl权限没有继承关系，仅仅会针对当前节点。
~~~
<!--more -->
## ACL组合
zk通过字符串组合确定访问客户端对zk节点的操作权限。
### world
与world组合的id只有一个，就是anyone，代表任何人，permission字符串随意组合。
eg:
~~~
#任何人拥有创建、读取和写入的权限
world:anyone:crw
~~~

### digest
指定账号密码访问。
~~~ bash
#服务器端指定acl，其中username和password为字符串
#注意设置的时候需要SHA1然后BASE64编码以后才能使用
#代表客户端为此账号和密码匹配的用户拥护所有权限
digest:{BASE64(SHA1(<username:password>))}:crdwa
#客户端设置digest用户
addauth digest <username:password>
~~~

### auth
auth的本质为digest，表示给认证通过的所有用户设置权限，通过
**addauth digest <username>:<password>**添加，通过此方式添加的用户，当节点权限被修改时，所有用户权限都被修改。新创建的用户和密码组需要重新调用setAcl才会添加。
~~~ bash
#服务端，添加认证信息
addauth digest <username>:<password>
#服务端，设置权限信息
setAcl [nodePath] auth:<username>:<password>:crwd

#客户端，设置认证信息
addauth digest <username:password>
~~~

### ip
指定的ip或ip地址段拥有权限。
~~~ bash
#192.168.1.2拥有创建读取权限
ip:192.168.1.2:cr
#192.168.1.2到192.168.1.16拥有读和写权限
ip:192.168.2/16:rw
~~~

### super
有权限操作任何节点，用于zk管理和维护。
此scheme只能在服务端启动时添加虚拟机参数的形式使用。
在zkServer脚本中添加：
~~~ bash
SUPER_ACL=“-Dzookeeper.DigestAuthenticationProvider.superDigest=super:gG7s8t3oDEtIqF6DM9LlI/R+9Ss=”
~~~
客户端添加super用户：
~~~ bash
addauth digest super:super
~~~
可以看到，其实也是digest策略。

## 设置或获取ACL信息
设置使用
~~~ bash
setAcl [nodePath] [ACL组合]
~~~
获取权限信息使用
~~~ bash
getAcl [nodePath]
~~~
