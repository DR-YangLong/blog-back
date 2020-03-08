title: ubuntu编译JDK7
date: 2016-06-30 12:01:56
categories: [java]
tags: [java,jdk]
---

# ubuntu下编译JDK7

## 心里准备
安装会遇到各种问题，需要静下心来解决，很多问题谷歌上的和自己本身的不一样，但总的来说编译完成的时候还是觉得很高兴的。
各种错的参考地址：http://www.centoscn.com/image-text/install/2015/0908/6140.html
后面才发现的= =，之前折腾好久

## 环境介绍
### 操作系统：
```bash
dream@dream:~/jvm/openjdk$ uname -a
Linux dream 4.4.0-28-generic #47-Ubuntu SMP Fri Jun 24 10:09:13 UTC 2016 x86_64 x86_64 x86_64 GNU/Linux
```
ubuntu 64位，16.04LTS
### jdk
因为jdk的编译很多也是java编写的，所以也需要一个基础版本的jdk用来编译它，叫bootsrap jdk。
1. bootstrap jdk：sun的jdk6u45，注意尽量不要使用编译目标的同版本或以后版本，会出一些问题。尽量安装编译目标的readme中的环境来。下载并解压。
2. 目标版本：openjdk7，下载并解压。

<!-- more-->
## 编译所需环境
```bash
sudo apt-get install ccache make gcc g++ ca-certificates ca-certificates-java
sudo apt-get install libX11-dev libxext-dev libxrender-dev libxtst-dev
sudo apt-get install libasound2-dev libcups2-dev libfreetype6-dev
sudo apt-get install ALSA libcups2-dev
```
个人环境不同，有的依赖组件在编译时报错会提示，如果出错提示，再安装即可。
### 编译使用脚本
虽然脚本中有unset jdk环境变量，但是最好还是将自己设置的环境变量注释掉。
> 2个需要注意和修改的地方ALT_BOOTDIR修改为自己的bootsrap jdk根目录，ALT_OUTPUTDIR修改为想要存放编译结果的目录

```bash
#!/usr/bin/env bash
#语言选项，这个必须设置，否则编译好后会出现一个HashTable的NPE错
export LANG=C

#Bootstrap JDK的安装路径。必须设置。
export ALT_BOOTDIR=/home/dream/Downloads/jdk1.6.0_45

#允许自动下载依赖
export ALLOW_DOWNLOADS=true

#并行编译的线程数，设置为和CPU内核数量一致即可
export HOTSPOT_BUILD_JOBS=4
export ALT_PARALLEL_COMPILE_JOBS=4

#比较本次build出来的映像与先前版本的差异。这个对我们来说没有意义，必须设置为false，否则sanity检查会报缺少先前版本JDK的映像。如果有设置dev或者DEV_ONLY=true的话这个不显式设置也行。
export SKIP_COMPARE_IMAGES=true

#使用预编译头文件，不加这个编译会更慢一些
export USE_PRECOMPILED_HEADER=true

#要编译的内容
export BUILD_LANGTOOLS=true
#export BUILD_JAXP=false
#export BUILD_JAXWS=false
#export BUILD_CORBA=false
export BUILD_HOTSPOT=true
export BUILD_JDK=true

#要编译的版本
#export SKIP_DEBUG_BUILD=false
#export SKIP_FASTDEBUG_BUILD=true
#export DEBUG_NAME=debug

#把它设置为false可以避开javaws和浏览器Java插件之类的部分的build。
BUILD_DEPLOY=false

#把它设置为false就不会build出安装包。因为安装包里有些奇怪的依赖，但即便不build出它也已经能得到完整的JDK映像，所以还是别build它好了。
BUILD_INSTALL=false

#编译结果所存放的路径
export ALT_OUTPUTDIR=/home/dream/jvm/build

#这两个环境变量必须去掉，不然会有很诡异的事情发生（我没有具体查过这些“”诡异的事情”，Makefile脚本检查到有这2个变量就会提示警告“）
unset JAVA_HOME
unset CLASSPATH

make 2>&1 | tee $ALT_OUTPUTDIR/build.log
```

将上面的脚本存放到待编译的jdk根目录下，本人的是make.sh文件，执行：
```bash
sudo chmod u+x make.sh
```

### 修改一些文件
由于一些小bug，会导致编译失败。我们修改一些文件。
> 修改日期时间配置文件

```
jdk/src/share/classes/java/util/CurrencyData.properties
535行
TR=TRL;2004-12-31-22-00-00;TRY
修改为当前年份的10年内，eg:
今年2016，那么修改为
TR=TRL;2015-12-31-22-00-00;TRY
这个是根据地区码定的时间，所以最好是把此文件中所有涉及的时间日期按上面的规则修改。
```

> 修改虚拟机make文件

这个会导致编译jvm失败，报Err 2，不支持的操作系统，原因是部支持当前linux内核版本
```
hotspot/make/linux/Makefile
234行
SUPPORTED_OS_VERSION = 2.4% 2.5% 2.6% 3%
为
SUPPORTED_OS_VERSION = 2.4% 2.5% 2.6% 3% 4%
```

### 编译
在编译目标的jdk根目录下执行
```
sh make.sh
```
进行编译。
我的4核4线程的编译结果：
```
#-- Build times ----------
Target all_product_build
Start 2016-06-30 10:36:48
End   2016-06-30 10:49:06
00:01:03 corba
00:02:52 hotspot
00:00:11 jaxp
00:00:15 jaxws
00:07:37 jdk
00:00:20 langtools
00:12:18 TOTAL
```
最后进入编译结果存放的j2sdk-image目录，就是编译的jdk，可以作为完整的jdk使用。测试一下：
进入此目录的bin目录下执行：
```bash
dream@dream:~/jvm/build/j2sdk-image/bin$ ./java -version
openjdk version "1.7.0-internal"
OpenJDK Runtime Environment (build 1.7.0-internal-root_2016_06_30_10_36-b00)
OpenJDK 64-Bit Server VM (build 24.0-b56, mixed mode)
```
显示root是因为我使用的是
> sudo sh make.sh
