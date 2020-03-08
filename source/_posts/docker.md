title: docker基础命令
date: 2019-07-07 19:09:56
categories: [java]
tags: [java,docker]
---
## Docker基础命令

### 运行容器：
> docker run -i -t ubuntu /bin/bash

> docker run -i -t ubuntu:12.04 /bin/bash 运行特定tag标定的容器

-i:开启STDIN    
-t:分配一个伪终端

> docker run --rm ....

--rm：运行结束后删除容器并清理，等价于容器退出后执行docker rm -v

<!-- more -->
### 端口映射
运行时使用p参数指定：
> docker run -p [宿主机绑定IP:端口号]:[容器端口号] ... 

### 运行并命名容器
> docker run --name app_named -i -t ubuntu /bin/bash

运行一个容器，并以app_named为名称。

### 容器列表
> docker ps  列出正在运行的容器

> docker ps -a 列出所有容器

> docker ps -n [x] 显示最后的x个容器

> docker ps -l 查看端口映射情况

> docker port [id/name] 查看容器端口映射

### 重新启动已经停止的容器
> docker start [id/name]

> docker restart [id/name]

### 附着到容器上
> docker attach [id/name]

### 创建守护式容器
> docker run --name deamond_named -d ubuntu /bin/sh -c "while true;do echo hello world;sleep 1;done"

-d参数使容器在后台运行。

### 查看容器日志
> docker logs [id/name] 查看已输出日志

> docker logs -f [id/name] 跟踪

> docker logs --tail 10 [id/name] 查看最后10行

> docker logs --tail 0 -f [id/name] 跟踪最新

> docker logs -f -t [id/name] 跟踪带时间戳

### 自定义日志驱动
运行时通过--log-driver指定。默认json-file,可选syslog，none。syslog，输出到系统日志，需要开启宿主机Syslog守护进程。none禁用日志。
> docker run --log-driver="syslog" --name deamond_named -d ubuntu /bin/sh -c "while true;do echo hello world;sleep 1;done"

### 查看容器内进程
> docker top [id/name]

### 查看docker状态信息，可多个
> docker stats [id/name]+

### 容器内额外启动新进程
> docker exec -d [id/name] [指令] 使用在运行的容器执行后台指令

>  docker exec -ti -u root [id/name] [指令]
使用容器的root用户运行指令

> docker exec -t -i [id/name] /bin/bash 使用在运行的容器执行交互命令

### 停止容器
> docker stop [id/name]

### 自动重启容器
> docker run --restart=always --name named -d ubuntu  /bin/sh -c "while true;do echo hello world;sleep 1;done"

restart参数：
* always:无论退出标志是什么，总是重启。
* on-failure:当退出标志不为0时重启，可指定重启次数：--restart=on-failure:5,表示尝试重启5次。

### 获取容器详细信息
> docker inspect [id/name]+ 获取所有信息

> docker inspect --format='&#123&#123.State.Running&#125&#125' [id/name]+ 获取模板指定信息

### 删除容器
> docker rm [id/name] 删除未运行的容器

> docker rm -f [id/name] 删除运行中的容器

> docker rm $(docker ps -a -q) 删除所有容器

### 列出本机所有可用镜像
> docker images

### 拉取镜像
> docker pull [name]:[tag] 拉取tag标注的镜像

### 查看镜像信息
> docker images [镜像名]

### 查找镜像
> docker search [镜像名]

### 构建镜像
Dockerfile模板：
```txt
#注释 <>表示变量
FROM <镜像名>:<tag/digest>
MAINTAINER <用户名> "<邮箱>" 已被LABEl替代
RUN ["<指令>","<参数1>","<参数2>",...]构建时执行shell命令，多条用“;”分割，可指定多次
CMD ["<指令>","<参数1>","<参数2>",...]，运行时执行指令，只能指定一次，会被docker run 指定的指令覆盖
ENTRYPOINT ["<指定默认程序>","<参数1>","<参数2>",...]容器运行时默认运行此程序    
LABEL <key>=<value>，指定元数据，key=value的形式，多个空格分开
EXPOSE <端口>，指定容器运行时监听的端口，多个空格分开，要使端口可访问，需要在执行docker run时使用-p指定
ENV <key>=<value>，设定环境变量，多个空格分开
ADD <src>... <dest>   拷贝并解压。如果src是目录，只会拷贝目录中的文件。
ADD ["<src>",... "<dest>"]
COPY <src>... <dest> 拷贝不解压，src是目录只会拷贝文件。
COPY ["<src>",... "<dest>"]
VOLUME <container目录>，挂载卷，创建一个匿名volume，目录不存在会创建。
USER <daemon>，设定容器运行时的用户
WORKDIR <目录名>，设置工作目录，Dockerfile中其后的命令RUN、CMD、ENTRYPOINT、ADD、COPY等命令都会在该目录下执行。多次设置会拼接出层级目录。
ARG <name>=<default value>，设定构建时可传递值的变量，运行build时使用--build-arg key=value传入
ONBUILD <指令>，当该镜像被作为基础镜像使用时将要触发的指令
STOPSIGNAL <系统合法信号值>，当容器停止运行时所要发送的系统调用信号
SHELL ["<executable>", "<parameter>","<parameter>",...]指定要运行指令的shell程序，多个shell时使用，功能同RUN
```
ADD和COPY不能指定上下文文件夹以外的目录，ADD可使用URL，copy不行，2个命令都会覆盖容器中已存在的文件。


VOLUME：一个卷可以存在于一个或多个容器的指定目录，该目录可以绕过联合文件系统，并具有以下功能：
```txt
卷可以容器间共享和重用
容器并不一定要和其它容器共享卷
修改卷后会立即生效
对卷的修改不会对镜像产生影响
卷会一直存在，直到没有任何容器在使用它
```


使用USER指定用户时，可以使用用户名、UID或GID，或是两者的组合。以下都是合法的指定值：
```txt
USER user
USER user:group
USER uid
USER uid:gid
USER user:gid
USER uid:group
```
使用USER指定用户后，Dockerfile中其后的命令RUN、CMD、ENTRYPOINT都将使用该用户。镜像构建完成后，通过docker run运行容器时，可以通过-u参数来覆盖所指定的用户。

#### 构建命令：
> docker build -t="仓库名/镜像名:tag" -f="文件所在位置"  "上下文文件夹位置"

### 推送镜像到docker hub
> docker push [namespace]/[name]:[tag]

### 删除镜像
> docker rmi [id/name]+

### 从容器运行Registry
[参看](https://docs.docker.com/registry/deploying/)

### 运行容器并挂载卷
> docker run -d -p 80 -v $PWD/website:/var/www/html/website --name named  unbuntu nginx

-v [本地目录]:[容器目录]:[卷对容器的权限，读写：wr，只读：ro]  将本地目录设为容器卷

### 创建Docker Networking
> docker network create [name]

### 查看Docker Networking详细信息
> docker network inspect [name]

### 列出当前系统中所有网络
> docker network ls

### 删除网络
> docker network rm [name]

### 将容器添加到网络
运行时指定：
> docker run --net=[网络名] ...

已运行容器加入：
> docker network connect [网络名] [容器id/名称]

### 将容器从网络中断开
> docker network disconnect [网络名] [容器id/名称]

### 容器链接
> docker run --link [目标容器名]:[本容器使用的别名] ...

容器链接以后，当前容器可以访问目标容器所有expose的端口。一个容器在run时通过多次--link标志可以链接多个容器，同时一个容器也可以供多个容器链接。

### 卷
> docker volume 

* ls 查看所有卷
* rm [name] 删除一个卷
* create [name] 创建一个卷
* inspect [name] 查看卷详情
* prune  删除所有没在使用的卷


# Docker compose 
docker compose定义了一组服务，每个服务为一个容器。docker compose通过docker-compose.yml对这些服务进行编排。