title: ubuntu编译JDK7
date: 2017-01-07 19:01:56
categories: [linux]
tags: [linux,ubuntu]
---

# ubutu不能正确解析域名解决办法
最简单的办法是，系统设置，网络，找到
## 方法一，修改**/etc/resolv.conf**
添加
~~~ bash
nameserver 223.5.5.5
nameserver 223.6.6.6
nameserver 180.76.76.76
nameserver 202.101.172.35
nameserver 61.153.177.196
nameserver 8.8.8.8
nameserver 8.8.4.4
nameserver 222.172.200.68
nameserver 61.166.150.123
~~~
这个方法重启就会失效。

<!-- more -->
## 方法二，修改**/etc/resolvconf/resolv.conf.d/base**
文件原本是空的，添加DNS服务器地址
~~~bash
nameserver 223.5.5.5
nameserver 223.6.6.6
nameserver 180.76.76.76
nameserver 202.101.172.35
nameserver 61.153.177.196
nameserver 8.8.8.8
nameserver 8.8.4.4
nameserver 222.172.200.68
nameserver 61.166.150.123
~~~
此方法我的17.04版本不起作用。但同样的另一种修改起作用。
~~~bash
cd /etc/resolvconf/resolv.conf.d/
touch tail
vim tail
#新加以下内容
nameserver 223.5.5.5
nameserver 223.6.6.6
nameserver 180.76.76.76
nameserver 202.101.172.35
nameserver 61.153.177.196
nameserver 8.8.8.8
nameserver 8.8.4.4
nameserver 222.172.200.68
nameserver 61.166.150.123

~~~
这样会起作用，但是如果你用dchp的话，就有一个问题，这些dns的优先级会低于dchp获取的dns。

## 方法三，修改dchp文件
~~~bash
vim /etc/dhcp/dhclient.conf
#找到prepend domain-name-servers 127.0.0.1;这行，大概在25行位置。去掉注释符号，修改为：
prepend domain-name-servers 223.5.5.5,223.6.6.6,180.76.76.76,202.101.172.35;
~~~
这种方法保证了自定义dns在dchp之前。

## 方法四，安装dnsmasq
~~~ basn
sudo apt install dnsmasq
sudo gedit /etc/dnsmasq.conf
#修改 #resolv-file=为resolv-file=/etc/resolv.dnsmasq.conf
~~~
如果修改过/etc/resolv.conf，先还原，然后运行
~~~bash
sudo cp /etc/resolv.conf /etc/resolv.dnsmasq.conf
~~~
修改网络里的dns为
> 127.0.0.1

重启dnsmasq
> sudo /etc/init.d/dnsmasq restart