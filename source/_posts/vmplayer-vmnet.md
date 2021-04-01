title: Failed to build vmnet. Failed to execute the build Command
date: 2016-12-21 10:00
categories: [linux]
tags: [linux,ubuntu]
---

# Kernel 4.9+无法安装VMPlayer vmnet问题解决
今天一早升级线上服务器的open ssh，然后升级了下自己的ubuntu，采用的是apt update+upgrade命令。
之后悲剧的发现vmplayer跪了，用度娘找了下，就只有一篇有价值的文章，说在国外论坛看到的，下载了个patch文件。
后来看了看，不适用，最后用手机热点翻墙（公司2M小水管，国内打开网页速度都感人），终于在http://rglinuxtech.com/?p=1838
看到了问题所在和解决方案，在这里重复下解决方案。其他内核版本也可以参照此方法解决。

## 日志异常

```bash
I125: Setting destination path for vmnet to "/lib/modules/4.9.0-11-generic/misc/vmnet.ko".
I125: Extracting the vmnet source from "/usr/lib/vmware/modules/source/vmnet.tar".
I125: Successfully extracted the vmnet source.
I125: Building module with command "/usr/bin/make -j4 -C /tmp/modconfig-Zu8VZj/vmnet-only auto-build HEADER_DIR=/lib/modules/4.9.0-11-generic/build/include CC=/usr/bin/gcc IS_GCC_3=no"
W115: Failed to build vmnet.  Failed to execute the build command.
```
关键点就是**4.9.0-11-generic**刚升级的内核，太新了，vm无法识别。

<!-- more -->
## 解决方法
首先从日志看到是内核版本的问题，导致了vmnet编译安装失败。接着按论坛上的解决方法来解决这个问题。

```bash
cd /usr/lib/vmware/modules/source
```

```bash
sudo tar -xf vmnet.tar
sudo tar -xf vmmon.tar
```

```bash
cd vmnet-only
sudo gedit userif.c
```
在**113行**左右找到
```c
#if LINUX_VERSION_CODE >= KERNEL_VERSION(4, 6, 0)
    retval = get_user_pages(addr, 1, 1, 0, &page, NULL);
#else
    retval = get_user_pages(current, current->mm, addr,
                1, 1, 0, &page, NULL);
#endif
```
把它替换为
```c
#if LINUX_VERSION_CODE >= KERNEL_VERSION(4, 9, 0)
     retval = get_user_pages(addr, 1, 0, &page, NULL);
#else
#if LINUX_VERSION_CODE >= KERNEL_VERSION(4, 6, 0)
     retval = get_user_pages(addr, 1, 1, 0, &page, NULL);
#else
     retval = get_user_pages(current, current->mm, addr,
                 1, 1, 0, &page, NULL);
#endif
#endif
```
接着修改另外一个文件
```bash
cd ../vmmon-only/linux
sudo gedit hostif.c
```
在**1165行**左右，找到
```c
#if LINUX_VERSION_CODE >= KERNEL_VERSION(4, 6, 0)
   retval = get_user_pages((unsigned long)uvAddr, numPages, 0, 0, ppages, NULL);
#else
   retval = get_user_pages(current, current->mm, (unsigned long)uvAddr,
                           numPages, 0, 0, ppages, NULL);
#endif
```
把它替换为
```c
#if LINUX_VERSION_CODE >= KERNEL_VERSION(4, 9, 0)
   retval = get_user_pages((unsigned long)uvAddr, numPages, 0, ppages, NULL);
#else
#if LINUX_VERSION_CODE >= KERNEL_VERSION(4, 6, 0)
   retval = get_user_pages((unsigned long)uvAddr, numPages, 0, 0, ppages, NULL);
#else
   retval = get_user_pages(current, current->mm, (unsigned long)uvAddr,
                           numPages, 0, 0, ppages, NULL);
#endif
#endif
```
完成2个文件的修改，基本就完成了工作。接着要将修改后的文件打包。切换目录
```bash
cd /usr/lib/vmware/modules/source
```
备份原文件
```bash
sudo mv vmnet.tar vmnet.tar.back
sudo mv vmmon.tar vmmon.tar.back
```
将修改后的文件打包：
```bash
sudo tar -cf vmnet.tar vmnet-only
sudo tar -cf vmmon.tar vmmon-only
sudo rm -rf vmnet-only
sudo rm -rf vmmon-only
```
最后重新启动即可安装vmnet，injoy!
