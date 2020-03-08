title: Ubuntu下安装JDK
date: 2015-05-13 19:09:56
categories: [java]
tags: [linux,jdk,ubuntu jdk]
---

### 1.下载JDK
&nbsp;&nbsp;&nbsp;&nbsp;从官网[JDK](http://www.sun.com "下载")下载解压放到/usr/lib/jdk/jdk8中
``` bash
sudo gedit /etc/profile
```
末尾添加
``` bash
export JAVA_HOME=/usr/lib/jdk/jdk8
export JRE_HOME=/usr/lib/jdk/jdk8/jre
export CLASSPATH=.:$JAVA_HOME/lib:$JRE_HOME/lib:$CLASSPATH
export PATH=$JAVA_HOME/bin:$JRE_HOME/bin:$JAVA_HOME:$PATH
# for hadoop if exist
export HADOOP_HOME=/usr/local/hadoop
export PATH=$PATH:$HADOOP_HOME/bin
```

配置系统默认jdk，300是一个优先级数值：
```bash
sudo update-alternatives --install /usr/bin/java java /usr/lib/jdk/jdk8/bin/java 300
sudo update-alternatives --install /usr/bin/javac javac /usr/lib/jdk/jdk8/bin/javac 300
sudo update-alternatives --config java 选择安装的jdk
sudo update-alternatives --config javac
```