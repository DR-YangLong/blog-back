title: Git入门
date: 2016-05-28 21:30:00
categories: [git]
tags: [java,git]
---

## git入门
git是一个免费开源的分布式版本控制系统。
### 安装

[gitbash下载](https://git-scm.com/downloads "点击下载"),[图形化管理工具下载](https://tortoisegit.org/download/ "tortoisegit")


### 工作区与版本库，暂存区
> 工作区

电脑中看到的目录，就是工作区。


> 版本库

被git管理的目录叫做版本库，此目录下任何文件都被git追踪管理，包括修改，新增，删除，每次修改git都有相应的历史记录，以便可以进行变更追踪以及恢复。

* 本地仓库：在本机的仓库，记录本机的变更记录。
* 远程仓库：在远程主机上的仓库，记录所有相关的操作，当远程仓库和本机仓库关联起来以后，才组成一个完整git系统。在本地进行的操作一旦提交，远程仓库就会应用这些操作在远程仓库上。
<!-- more -->
> 暂存区

用户将一次操作向版本库提交时，要分为2步：
1. 新增
2. 提交

第一步，新增时操作就储存在暂存区。此时，操作并没有真正的对原来的版本起任何作用。

### 基本操作
基本操作包括仓库的创建，文件管理，远程仓库关联，分支管理。

#### 本地操作

> 创建本地仓库

~~~ bash
mkdir test
cd test
git init
~~~
使用**git init**创建一个本地仓库。可以在目录下发现有一个*.git*的目录。

> 管理修改

~~~ bash
#在工作区新增文件
echo test>>test.md
git status
#向本地仓库新增文件
git add test.md
#查看工作区状态
git status
#修改test.md新增内容
vim test.md
#提交
git commit -m "git change content"
#查看状态，可以看到git提示文本被修改，所以上面的提交仅仅是提交了第一次的操作，原因是我们仅仅新增了第一次操作到暂存区，commit仅会将暂存区的操作提交到仓库
git status
#此时可以有2个选择，一个是撤销修改，一个是新增修改到暂存区
~~~
> 撤销修改

当你新增一个修改到暂存区前发现原来的才是对的，你修改的是错的，没用的，这时候可以将修改撤销
~~~ bash
dream@dream:~/IdeaProjects/test$ cat test.md
test for git
dream@dream:~/IdeaProjects/test$ git checkout -- test.md
dream@dream:~/IdeaProjects/test$ cat test.md
test
~~~
可以看到，文件已经恢复到了第一次修改时。

> 查看修改

当你修改了一个文件，但是已经忘记修改了什么地方的时候，可以使用以下命令查看
~~~ bash
git diff fileName
~~~

> 删除文件

~~~ bash
dream@dream:~/IdeaProjects/test$ rm test.md
dream@dream:~/IdeaProjects/test$ git status
位于分支 master
尚未暂存以备提交的变更：
  （使用 "git add/rm <文件>..." 更新要提交的内容）
  （使用 "git checkout -- <文件>..." 丢弃工作区的改动）

	删除：     test.md

修改尚未加入提交（使用 "git add" 和/或 "git commit -a"）
~~~
如上，如果要真正的删除文件，必须在删除后新增修改，然后commit，这样文件才会从版本庫删除，如果删错了，也很简单，可以使用checkout命令找回来。

> 版本回退

如果你发现需要一个很早之前的版本怎么办，这时候就可以使用版本回退。
> 查看版本记录：git log [--pretty=oneline]
> 回退版本：git reset [--hard|soft|mixed|merge|keep] <commitId|HEAD>

~~~ bash
git每次commit会自动生成commitId,git log中的16进制串。git使用HEAD标示版本，HEAD表示当前版本，HEAD^表示上一个版本，HEAD^^表示上上的版本，以此类推，也可以使用HEAD～[n]表示往前第几个版本，n为自然正整数。
~~~
常用的3种模式：
* hard：工作区重置到指定commitId的版本，指定commitId以后的修改全部丢弃。HEAD指到commitId。
* soft：工作区内容不做任何改变，仅仅将HEAD
指到commitId，指定commitId以后的修改全部放入到暂存区，可以使用git status查看。如果此时使用commit，就会将commitId之后的所有更改都提交。
* mixed：默认模式，HEAD指向commitId，工作区的修改都会被保留，但commitId之后的修改不会放入暂存区，需要使用add手动去添加修改。

5种模式的区别详见   [模式区别](http://blog.csdn.net/hudashi/article/details/7664464 "前往")

~~~ bash
dream@dream:~/IdeaProjects/test$ cat test.md
test for reset
dream@dream:~/IdeaProjects/test$ git log --pretty=oneline
13850ac7f584717576f3090e7a722ce0937e9f16 reset
7175b5992fca5a89c1fa6dbfc6a6589d1a9a3002 git change content
dream@dream:~/IdeaProjects/test$ git reset --hard HEAD^
HEAD 现在位于 7175b59 git change content
dream@dream:~/IdeaProjects/test$ cat test.md
test
~~~

#### 远程仓库操作

> 添加远程仓库

~~~ bash
 git remote add origin [remote repo url]
~~~
> 推送到远程仓库

~~~ bash
#添加远程仓库后第一次推送
git push -u origin master
#非第一推送
git push origin master
~~~

> 从远程仓库克隆

~~~ bash
git clone [remote repo url]
~~~

> 从远程仓库获取更新

~~~ bash
git pull
~~~
更新可能有冲突，这时使用git status可以看到有哪些文件冲突，逐一修改后，使用 add/commit提交修改。

~~~ bash
a123
<<<<<<< HEAD
b456
=======
b789
>>>>>>> 6853e5ff961e684d3a6c02d4d06183b5ff330dcc
c
~~~
以上说明b这一行有冲突，<<<<<<<与=======之间（上面部分）是我们修改的，======与>>>>>>>之间是别人修改的。

#### 分支管理
分支可以看作并行的版本库，他们可以互不干扰，在必要的时候又可以合并修改。

> 查看分支

~~~ bash
#查看本地分支
git branch
#查看所有分支，包括远程
git branch -a
#查看远程分支情况
git remote -v
~~~

> 创建本地分支

~~~ bash
git branch [branchName]
~~~

> 推送本地分支到远程仓库

~~~ bash
git push origin [branchName]
~~~
> 分支切换

~~~ bash
git checkout [branchName]
#创建一个分支并切换到新创建的分支
git checkout -b [branchName]
~~~

> 删除分支

~~~ bash
#删除本地分支
git branch -d [branchName]
#删除远程分支，或者将一个已删除的本地分支推送到远程分支
git push origin --delete [branchName]
#或者使用
git push origin :[branchName]
~~~

> 分支合并

~~~ bash
dream@dream:~/IdeaProjects/test$ git branch
  master
* testBranch
dream@dream:~/IdeaProjects/test$ cat test.md
test for merger
dream@dream:~/IdeaProjects/test$ git checkout master
切换到分支 'master'
dream@dream:~/IdeaProjects/test$ cat test.md
test
dream@dream:~/IdeaProjects/test$ git merge testBranch
更新 7175b59..473eec8
Fast-forward
 test.md | 2 +-
 1 file changed, 1 insertion(+), 1 deletion(-)
dream@dream:~/IdeaProjects/test$ git status
位于分支 master
无文件要提交，干净的工作区
dream@dream:~/IdeaProjects/test$ cat test.md
test for merger
~~~
步骤：
> 1. 切换到待合并分支上，git pull更新。
> 2. 切换到要合并的主分支上，git pull更新。
> 3. git merge [待合并branchName]合并分支。
> 3. git status 查看合并状态。
> 4. 解决冲突文件。
> 5. git add/commit 提交已发解决的冲突文件。
> 6. 合并结束。

在合并后如果后悔，可以使用rest命令进行版本回退。

#### 忽略文件
在日常开发中，有的文件，比如一些开发环境相关的文件，如果你提交了，可能造成别人无法正常运行项目，毕竟每个人的环境配置不可能一模一样，特别是在协同开发下。所以对于一些文件，我们需要本地保留，但同时又不想把它提交到版本仓库中。

~~~ bash
echo *.iml>>.gitignore
~~~
新建一个.gitignore后缀的文件，将需要忽略的文件或文件夹添加到此文件中，每个一行。

#### stash
用于将修改暂存到git栈中，并将工作区内容恢复到最近一次的提交。
> 1. git stash: 备份当前的工作区的内容，从最近的一次提交中读取相关内容，让工作区保证和上次提交的内容一致。同时，将当前的工作区内容保存到git栈中。如果要使用消息，使用git stash save "message"。
> 2. git stash pop: 从git栈中读取最近一次保存的内容，恢复工作区的相关内容。由于可能存在多个Stash的内容，所以用栈来管理，pop会从最近的一个stash中读取内容并恢复。
> 3. git stash list: 显示git栈内的所有备份，可以利用这个列表来决定从哪个地方恢复。
> 4. git stash clear: 清空git栈。此时使用gitg等图形化工具会发现，原来stash的哪些节点都消失了。
> 5. git stash apply [version]:将当前工作空间置为指定的暂存状态，通过暂存版本号。
适用场景，想要保存当前修改，但又不想提交当前修改时使用。

~~~ bash
dream@dream:~/IdeaProjects/test$ vim test.md
dream@dream:~/IdeaProjects/test$ git stash save "1"
Saved working directory and index state On master: 1
HEAD 现在位于 473eec8 te
dream@dream:~/IdeaProjects/test$ vim test.md
dream@dream:~/IdeaProjects/test$ git stash save "2"
Saved working directory and index state On master: 2
HEAD 现在位于 473eec8 te
dream@dream:~/IdeaProjects/test$ git stash list
stash@{0}: On master: 2
stash@{1}: On master: 1
dream@dream:~/IdeaProjects/test$ git stash apply stash@{1}
位于分支 master
尚未暂存以备提交的变更：
  （使用 "git add <文件>..." 更新要提交的内容）
  （使用 "git checkout -- <文件>..." 丢弃工作区的改动）

	修改：     test.md

修改尚未加入提交（使用 "git add" 和/或 "git commit -a"）
dream@dream:~/IdeaProjects/test$ cat test.md
test for stash1
~~~

> 建议使用git stash save "message" 做标记，否则一旦多个版本，自己都不知道哪个对应什么修改= =


#### tags
标签常用于标记版本信息，标签就像一个不会变化的分支，被打上标签后，检出的工作空间状态就是打标签时刻的状态。
> 查看标签

~~~ bash
#列出所有标签
git tag
#列出符合条件的标签
git tag -l 'someKeyworldsAsREGEXP'
~~~
列出的tag列表顺序与打标签的顺序无关。

> 给分支打标签

git标签分为轻量级标签和附注标签。轻量标签是指向提交的引用，如果只是临时性的标注代码，使用轻量级标签；附注标签则是一个相对独立的对象，它有自身的校验和信息，包含着标签的名字，电子邮件地址和日期，以及标签说明，标签本身也允许使用GNU Privacy Guard (GPG)来签署或验证。一般我们都建议使用含附注型的标签，以便保留相关信息；
~~~ bash
#给当前分支此时的状态打上轻量级标签
git tag [tagName] -m [tag describe]
#给当前分支此时的状态打上附注标签
git tag -a [tagName] -m [tag describe]
#查看标签信息
git show [tagName]
#给指定的commit打上标签
git tag [tagName] -m [tag describe] [commitId]
git tag -a [tagName] -m [tag describe] [commitId]
~~~

> 切换到指定标签

~~~ bash
#切换到指定标签，此时工作区的状态变回打标签时的状态
#并且此时处于一个空的临时分支，可以依赖此时的工作空间状态创建新的分支
git checkout [tagName]
~~~


> 删除标签

如果要修改标签，只能先删除再重新打

~~~ bash
git tag -d [tagName]
~~~


> 发布标签

进行push操作时，标签是不会被提交到远程仓库的。需要显示的提交。
~~~ bash
#将某个指定标签提交到远程仓库
git push origin [tagName]
#将本地所有标签提交到远程服务器
git push origin --tags
~~~

> 标签签署

标签签署需要生成公鈅和私钥。
生成签名密钥：
~~~ bash
gpg --gen-key
~~~
按提示输入信息，有默认选项的没有必要的话可以直接回车。
给tag签名：
~~~ bash
#正常
git tag -s [tagName] -m [tag describe]
#如果报密钥不可用
git tag -u [上一步生成key时的姓名]  -s [tagName] -m [tag describe]
~~~
查看签署信息：
~~~ bash
git show [tagName]
~~~
其他的开发者如果没有验证通过，是不能够修改你签署过的标签的。验证标签必须拥有签署者的公鈅，放到key ring[*位置在生成密钥时提示的路径，密钥也在其中*] 中。然后使用以下命令验证：
~~~ bash
git tag -v [tagName]
~~~

### 恢复已删除分支
如果不小心删除了分支，可以使用
~~~ bash
git reflog show
~~~
或者
~~~ bash
git log -g
~~~
查看提交记录，然后使用
~~~bash
git branch [name] commitId
~~~
来从一个commitId恢复分支出来，新分支名称就是[name]。