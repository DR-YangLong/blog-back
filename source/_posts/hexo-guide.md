title: HEXO初使用
date: 2015-6-05 17:40:56
categories: [hexo]
tags: [博客,hexo,hexo github]
---
## 首先安装node js

## 安装npm可以省略，最新版的node已经集成
### 首先进入某个目录，此例子为D:\
## 安装hexo

```bash
D:\>npm install hexo-cli -g
```

###初始化博客
```bash
D:\>hexo init blog
```
###进入blog并安装
```bash
D:\>cd blog
D:\blog>npm install
```
<!-- more -->
###启动服务器
```bash
D:\blog>hexo server
```
###到这一步，如果，没有错误，则人博客安装成功，在浏览器输入
```bash
127.0.0.1:4000
```
可以看到hexo已经成功

## 设置使用自己喜欢的主题
首先关闭服务器
```bash
ctrl+c
```
询问是否输入
```bash
y
```
终止服务器

hexo有很多的开源主题，可以自己搜索一个，这里用一个小清新的主题[yilia](https://github.com/litten/hexo-theme-yilia "hexo主题")

### 下载主题
```bash
浏览器打开 https://github.com/litten/hexo-theme-yilia/archive/master.zip 进行下载
```
### 设置主题
将下载到的zip选择解压到当前目录，并改变文件夹名称为yilia
将yilia移动到D:\blog\themes文件夹下
打开D:\blog\_config.yml文件找到
```bash
theme: landscape
```
改为：
```bash
theme: yilia
```

在命令行中输入：
```bash
D:\blog>hexo server
```
重启服务器，访问 127.0.0.1:4000 查看主题已经改变

## 部署安装到github
### 前驱条件
1. 首先到[github](https://github.com/ "gitHub")注册账号，并新建一个仓库（具体步骤略），需要注意仓库的名称就是 yourAccountName.github.io,页面不要关闭，本例仓库：dr-yanglong.github.com
2. 安装[git](http://git-scm.com/downloads "git"),并生成SSH key，具体步骤参照[git 入门](http://git.oschina.net/wzw/git-quick-start)，测试能够完全访问到github

### 部署到github
在D:\blog下面右键，选择
```bash
Git Init Here
```
然后再右键，选择
```bash
Git Bash
```
在弹出CMD命令窗口稳定后，输入
```bash
npm install hexo-deployer-git --save
```
接着用编辑器（记事本/下载[sublime](http://www.sublimetext.com/2)）打开
> _config.yml

从70行修改：
```bash
deploy:
  type: git
  repository: git@github.com:DR-YangLong/dr-yanglong.github.io.git
  branch: master
```
注意每个 **“:”** 后面有空格，并且 **“repository”** 后面是你的git的仓库地址，在你创建完仓库的那个页面有
#### 执行命令部署到github
最后一步，执行
```bash
hexo d -g
```
将博客部署到github

打开github仓库页面，F5刷新，如果看到有很多代码，点击右边的
> Settings

在打开的页面找到：
> GitHub Pages

如果下面显示类似：
>  Your site is published at http://dr-yanglong.github.io. 

那么，你的博客就弄好了，接下来就是改一些名称，图片之类的。怎么改可以参照[Hexo官网](http://hexo.io/zh-cn/)全中文，相信没压力。

## 下面是一些命令
```bash
hexo new "postName" #新建文章
hexo new page "pageName" #新建页面
hexo generate #生成静态页面至public目录
hexo server #开启预览访问端口（默认端口4000，'ctrl + c'关闭server）
hexo deploy #将.deploy目录部署到GitHub
hexo help  # 查看帮助
hexo version  #查看Hexo的版本

hexo deploy -g  #生成加部署==hexo d -g
hexo server -g  #生成加预览==hexo s -g

#简写
hexo n == hexo new
hexo g == hexo generate
hexo s == hexo server
hexo d == hexo deploy
```