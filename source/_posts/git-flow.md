title: Git Flow使用
date: 2016-09-08 17:40:56
categories: [java]
tags: [github]
---

# git flow入门
软件在开发中的周期为：新功能定义->新功能实现->新功能测试->新功能BUG修复->新功能发布上线->紧急线上bug修复；
使用git的分支以及分支合并可以很好的完成各周期对应的实际操作，但是必须熟知git命令并且规范性比较差，git flow是一个基于git的工作流规范工具。
它集成了git的分支，tag等命令，整合为软件开发周期中的各个阶段，可以很容易就实现软件的规范化开发。
基本流程图（来源于博客ngulc，不再重复画了）：
![FLOW](https://images2015.cnblogs.com/blog/805129/201608/805129-20160814142051093-1392948534.png)

<!--more -->

## 初始化git-flow
* 在已有的git仓库执行
> git flow init

回复几个问题，可以全部使用默认，也可以自定义，建议默认。
初始化即完成。同时会生成一个**本地 develop**分支并切换到此分支。
将develop分支推送到远程仓库，作为常驻分支。
> git push -u origin develop


## 开始开发新模块
使用命令
> git flow feature start [模块名称]

开始一个新的模块开发，此命令基于develop分创建一个名为[模块名称]的新**本地分支**并切换到此分支。

## 协作开发新模块
当需要协作开发时，可以将新模块分支推送到远程仓库：
> git flow feature publish [模块名称]

其他开发人员即可通过如下命令获取新模块：
> git flow feature pull origin [模块名称]

也可以使用（2.0后将只能使用此命令）
> git flow feature track [模块名称]

pull远程模块分支的变更。

## 新模块开发完成
使用命令
> git flow feature finish [模块名称]

完成一个新模块开发。此命令将[模块名称]分支合并到develop分支，删除[模块名称]分支并切换回develop分支。
需要注意的是，必须使工作空间保持干净才能够执行此命令。

## 发布新模块
发布命令：
> git flow release start [RELEASE] [BASE]

RELEASE：版本名称
BASE：develop分支下的commit的sha-1值。
命令仅基于develop分支创建本地release分支，将此分支推送到远程仓库：
> git flow release publish [RELEASE]

使用
> git flow release track [RELEASE]

pull远程release分支的变更。

## 完成新模块
> git flow release finish [RELEASE]

合并release分支到master分支，用release分支名打tag；合并release分支到develop分支；删除release分支。

## 紧急修复
发现某个提交有bug，使用
> git flow hotfix start [VERSION] [BASENAME]

VERSION：修正版本名称   
BASENAME： release分支名    
从master新开一个分支，分支名称为VERSION参数名。

## 完成修复
> git flow hotfix finish [VERSION]

完成修复。修复分支合并到master，develop，master分支打上修正版本tag。


## 完成feature或release时出现分叉
如果提示**undiverge branches**，说明develop分支或master分支在你提交前已经有新的提交，需要先checkout到develop或master分支进行pull，然后rebase当前的feature分支或者release分支。然后再执行finish命令。
如：
```bash
git checkout develop
git pull
git flow feature rebase [featureTree]
[此处可能需要处理冲突文件，处理完后运行git rebase --continue]
git flow feature finish [featureTree]
[有可能提示featureTree有分叉，这时，由于远程和本地不一样，按git流程，先pull，然后处理冲突文件，然后commit，push，完成后再次运行finish命令]

```


# 参考资料
* https://github.com/nvie/gitflow
* http://jeffkreeftmeijer.com/2010/why-arent-you-using-git-flow/
* http://www.cnblogs.com/lcngu/p/5770288.html
* http://danielkummer.github.io/git-flow-cheatsheet/index.zh_CN.html
