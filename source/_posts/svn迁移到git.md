title: SVN迁移到Git
date: 2019-06-16 20:09:56
categories: [java]
tags: [java]
---

# svn迁移到git
为什么要从svn迁移到git？    
svn适合项目管理，将项目产出（包括交付代码）在svn上进行管理，有细粒度的权限控制，但不适合代码管理，svn分支和tag太重，使用svn不容易做到开发流程管控，而且极容易出现代码相互覆盖，git配合git flow可以很容易做到代码开发流程管控。

## 如何从svn迁移到git
1. git svn命令，git提供了此命令，使使用svn可以使用git命令。
2. 将svn迁出到本地使用git初始化为git仓库。
3. 在git服务器新建仓库。
4. 将本地代码推送到远程git仓库。

### 安装git-svn
CentOS:
> yum install -y git-svn

MacOSX:
> brew install git subversion

其他操作系统自理。
<!-- more -->

### git远程准备
去要迁移的svn代码库，看代码库有多少个svn账号使用此代码库，新建svn用户到git用户的映射文件，格式如下
> [svn用户名]=[git用户名][<git用户email>]

中括号为变量标识符，一个文件如下user.txt：
```txt
zhang_san=张三<zhang_san@leapmotor.com>
yang_long=杨龙<yang_long@leapmotor.com>
```

如果git服务端没有svn的用户，可以先建用户。

### 检出svn代码库
使用git命令检出svn代码库。

#### 从标准svn代码库检出
SVN仓库使用标准的了/trunk, /branches和/tags的目录结构,并没有使用其他目录。     
检出命令：
> git svn clone --stdlayout --authors-file=<userfile> <svn-repo>/<project> <git-repo-name>

例子：
> git svn clone --stdlayout --authors-file=userinfo.txt https://svn.example.com/web web

#### 从非标准svn代码库检出
SVN仓库目录不标准。     
检出命令：      
> git svn clone --trunk=/trunk --branches=<branche_n> --branches=<branche_n> --tags=<branche_n> --authors-file=<userfile> <svn-repo>/<project> <git-repo-name>

例子：      
> git svn clone --trunk=/trunk --branches=/branches --branches=/bugfixes --branches=/releases --tags=/tags --authors-file=userinfo.txt https://svn.example.com/web web

### 处理svn分支
列出当前分支
> git branch

将svn的tag分支转换为git的tag
> git tag <tagname> <svn tag branche>

删除已经转换好的svn分支
> git branch -r -d <svn tag branch>

### 本地代码推送到git远程仓库
在本地代码库根目录执行
> git init

> git remote add origin <git地址>

> git push origin master --tags 