title: git推送本地已有代码到远程分支
date: 2015-07-07 18:09:56
categories: [git]
tags: [git,github]
---

## git 将本地仓库推送到远程仓库 **

1. 首先要在远程的git仓库上新建仓库，并记录地址，例如，我们创建的地址是
```bash
git@git.yanglong.com:root/example.git
```

2. gitbash进入本地已有代码的项目根文件夹下，执行下面的代码
```bash
git init
git add ./
git commit -m "init"
git remote add origin git@git.yanglong.com:root/example.git
git push -u origin master
```
