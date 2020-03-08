title: Maven入门
date: 2016-05-03 20:01:56
categories: [java]
tags: [java,maven]
---

# maven入门
## 为什么要使用maven
### Maven是什么
> Apache Maven is a software project management and comprehension tool. Based on the concept of a project object model (POM), Maven can manage a project's build, reporting and documentation from a central piece of information.

maven是一个跨平台的项目管理和构建工具。
### 为什么要使用Maven
maven使用一个xml配置文件管理项目的相关依赖信息以及构建信息，项目的所有相关设置都在这个入口进行，降低了管理的复杂的。与传统项目构建方式相比，maven将对lib的强依赖转换为引用依赖，一定程度上的减少了不同项目对依赖jar的重复添加。
> EG:project1需要使用jstl.jar，project2也需要使用jstl.jar，以往的方式是在2个项目的lib中都放入jstl.jar文件，也就是说在计算机里会同时有2个一样的jar包（另外可以使用引入的方式避免重复，但一般都不会这样做，因为协作的时候不应该依赖本地环境），如果N的项目，那简直爆炸了。如果使用maven，那么我们只需在配置文件中配置一下就解决了问题，所有的项目都将使用同一个jar，至于删除，那没关系，maven会自动去**仓库**下载此jar。

<!-- more -->

maven提供了依赖传递的定义，如果你使用一个jar，那么它依赖的其他jar将会自动导入，并且导入的版本是它所需要的正确版本，避免了以往手动添加jar包方式可能造成的不兼容。同时maven的目录设计将主代码和测试代码分离，打包项目时默认测试不打包，便于项目的开发管理，代码维护。maven定义了软件开发的生命周期，开发-测试-打包，一个项目发版，都将默认执行测试以确保无误，之后才会打包项目，提高了软件的质量。

### 前置的概念
> 1. 远程仓库：maven在管理项目时，使用配置文件的方式管理依赖jar，用户在配置文件中定义相关的jar依赖后，maven将会自动去下载这些jar，下载的服务器即称之为远程仓库
> 2. 本地仓库：maven从服务器下载下来的jar将会存在本地计算机的一个目录下，这个目录称之为本地仓库，本机所有项目如果pom中有相同的jar依赖，都将会使用本地仓库中的同一个jar。


## 环境部署
> 主流的IDE都已经集成maven环境。

以Windows为例，手动部署maven：
> 1. 部署java环境，略。
> 2. 下载[maven]("http://maven.apache.org/download.cgi" "点击下载")
> 3. 解压，将apache-maven-3.3.9[版本号可能不一样]移动到D盘根目录，重名为maven，全路径如此："D:\maven"
> 4. 配置环境变量：新建环境变量M2_HOME=D:\maven，添加环境变量Path=%M2_HOME%\bin,CMD下执行mvn -version 如果有版本等信息输出，说明成功
> 5. 配置本地仓库：打开D:\maven\conf\settings.xml,找到localRepository节点，将此节点注释去掉，并将值设为我们想要的路径，例如：<localRepository>D:\mavenRepo</localRepository>

> settings.xml还有很多其他设置，仅提供参考地址[maven全局配置]("http://maven.apache.org/settings.html" "maven全局配置")

## pom.xml
pom.xml作为项目的管理入口，定义了项目依赖的jar，适用的插件，以及很多其他的常用配置。

### pom示例
```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                      http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <!-- 基本配置，配置此项目的坐标、版本号、属性、依赖项目定义、使用的插件，父项目以及子项目 -->
    <!-- 父项目定义 -->
    <!--<parent>
    <groupId>simple.maven</groupId>
        <artifactId>root</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>-->
    <!-- 项目groupId -->
    <groupId>simple.maven</groupId>
    <!-- 项目artifactId -->
    <artifactId>maven</artifactId>
    <!-- 项目版本号 -->
    <version>1.0-SNAPSHOT</version>
    <!-- 项目打包方式 -->
    <packaging>jar</packaging>
    <!-- 子项目 -->
    <!--<modules>
        <module>sub-one</module>
        <module>sub-two</module>
    </modules>-->
    <!-- 属性定义，可作为变量引用 -->
    <properties>
        <!-- 定义项目打包编码，此编码会被compiler插件定义覆盖-->
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <!-- 定义项目其他属性 -->
        <java.version>1.8</java.version>
        <junit.version>3.8.1</junit.version>
    </properties>
    <!-- 依赖项目定义 -->
    <dependencies>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
        </dependency>
    </dependencies>
    <!-- 依赖管理，配置在此节点的依赖不会立即下载，只作为版本和项目依赖范围的界定，多数情况下是为了提供给子项目使用，是可选节点 -->
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.apache.commons</groupId>
                <artifactId>commons-lang3</artifactId>
                <version>3.4</version>
            </dependency>
        </dependencies>
    </dependencyManagement>
    <!-- 基本配置结束 -->

    <!-- 构建设置 -->
    <build>
        <!-- 最终打包出来时的jar名称-->
        <finalName>demo</finalName>
        <!-- 定义项目使用的插件-->
        <plugins>
            <!-- compiler插件-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>${java.version}</source>
                    <target>${java.version}</target>
                    <showWarnings>true</showWarnings>
                    <skip>true</skip>
                    <encoding>${project.build.sourceEncoding}</encoding>
                </configuration>
                <version>3.1</version>
            </plugin>
        </plugins>
    </build>
    <reporting>

    </reporting>

    <!-- 可选项目信息设置 -->
    <name>demo</name>
    <!-- -->
    <description>maven入门示例</description>
    <!-- 项目url -->
    <url>http://dr-yanglong.github.io/</url>
    <!-- 项目开始时间 -->
    <inceptionYear>2016</inceptionYear>
    <!-- 项目licenses-->
    <!--<licenses>
    </licenses>-->
    <!-- 组织描述-->
    <organization></organization>
    <!-- 开发人员描述 -->
    <developers></developers>
    <!-- 其他项目参与者描述 -->
    <contributors></contributors>

    <!-- 环境设置，项目中环境设置会与全局设置做并集，如果有重合，项目设置会覆盖全局设置 -->
    <!-- 项目远程依赖和插件仓库设置 -->
    <!--<repositories>
      <repository>
        <id>nexus</id>
        <name>nexus</name>
        <url>http://IP或域名/nexus/content/groups/public</url>
        <releases>
          <enabled>true</enabled>
        </releases>
        <snapshots>
          <enabled>true</enabled>
          <updatePolicy>daily</updatePolicy>
        </snapshots>
      </repository>
      <repository>
        <id>Central</id>
        <name>Maven中央库</name>
        <url>http://central.maven.org/maven2/</url>
      </repository>
    </repositories>
    <pluginRepositories>
      <pluginRepository>
        <id>nexus</id>
        <name>nexus</name>
        <url>http://IP或域名/nexus/content/groups/public</url>
        <releases>
          <enabled>true</enabled>
        </releases>
        <snapshots>
          <enabled>true</enabled>
          <updatePolicy>daily</updatePolicy>
        </snapshots>
      </pluginRepository>
      <pluginRepository>
        <id>Central</id>
        <name>Maven中央库</name>
        <url>http://central.maven.org/maven2/</url>
      </pluginRepository>
    </pluginRepositories>-->

    <!-- 部署仓库设置，需要配合settings.xml中的servers设置才能使用 -->
    <!--<distributionManagement>
      <repository>
        <id>my-releases</id>
        <name>Local Nexus Repository</name>
        <url>http://IP或域名/nexus/content/repositories/releases/</url>
      </repository>
      <snapshotRepository>
        <id>my-snapshots</id>
        <name>Local Nexus Repository</name>
        <url>http://IP或域名/nexus/content/repositories/snapshots/</url>
      </snapshotRepository>
    </distributionManagement>-->

    <!-- profile设置 -->
    <!--<profiles></profiles>-->
</project>

```
[pom详细文档]("http://maven.apache.org/pom.html" "点击前往")

### 依赖
依赖在以前的项目中表现为lib中的jar包，在maven中，称为dependency(依赖)，并在pom.xml文件中配置
#### 依赖的确定
> 依赖坐标

maven适用3个维度对依赖进行确定：groupId（组织坐标，通常为网址倒写），artifactId（项目坐标，通常为项目名称），version（版本坐标，为项目版本）。
```xml
<dependency>
    <groupId>com.thoughtworks.xstream</groupId>
    <artifactId>xstream</artifactId>
    <version>1.4.8</version>
</dependency>
```
此配置的意思是，项目依赖于com.thoughtworks.xstream下的1.4.8的xstream项目，最终效果是maven会到本地仓库检查是否有xstrem-1.4.8.jar，如果没有，将会到远程仓库下载到本地仓库。
#### 依赖检查
maven在添加每一个依赖时，都会检查此依赖的正确性以及此依赖有没有依赖其他依赖。
> 依赖的传递

一个依赖如果还依赖其他依赖，称之为传递依赖：A->B，B->C，B->D，则A->(B,C,D)。
上面的xstream依赖其他的项目，那么就产生了传递依赖，如下
```xml
    <dependency>
      <groupId>xmlpull</groupId>
      <artifactId>xmlpull</artifactId>
    </dependency>
    <dependency>
      <groupId>xpp3</groupId>
      <artifactId>xpp3_min</artifactId>
    </dependency>
```
那么，我们不必再添加以上依赖到项目pom中，maven会自动将它们下载到本地仓库并作为本项目依赖使用。

> 依赖的排除

当有的传递依赖我们想要自己指定，那么为了不导入不同版本的2个依赖（更多的时候是为了解决冲突和兼容），我们需要使用依赖排除将传递产生的依赖排除掉
```xml
<dependency>
    <groupId>commons-beanutils</groupId>
    <artifactId>commons-beanutils</artifactId>
    <version>1.9.2</version>
    <exclusions>
        <exclusion>
            <groupId>commons-logging</groupId>
            <artifactId>commons-logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```
#### 依赖版本的控制
有自己项目pom中配置的依赖，有传递依赖，并且仅版本不同，那么如何确定依赖哪一个？
> maven由最短路径原则确定具体依赖哪一个。
如果本地显式依赖了某个jar包，则用本地的。
如果本地没有显式依赖，而是通过依赖传递依赖的某个jar包：
首先根据最短依赖原则确定；
如果路径长度都相同，则根据依赖书写顺序确定。

> 如果有版本不兼容和冲突，还是需要使用依赖排除

#### 依赖的范围scope
  * compile，缺省值，适用于所有阶段，会随着项目一起发布。
  * provided，类似compile，期望JDK、容器或使用者会提供这个依赖。如servlet.jar。
  * runtime，只在运行时使用，如JDBC驱动，适用运行和测试阶段。
  * test，只在测试时使用，用于编译和运行测试代码。不会随项目发布。
  * system，类似provided，需要显式提供包含依赖的jar，Maven不会在Repository中查找它

system使用示例：
```xml
<dependency>
      <groupId>javax.sql</groupId>
      <artifactId>jdbc-stdext</artifactId>
      <version>2.0</version>
      <scope>system</scope>
      <systemPath>${java.home}/lib/rt.jar</systemPath>
</dependency>
```
区别仅是多了systemPath节点，此节点的值是jar包的绝对路径。
### 插件
maven提供默认的插件，这些插件分别负责项目不同阶段所需要做的事情。另外可自行配置项目中需要使用的其它插件，比如servlet容器tomcat插件，jetty插件。
#### Maven的生命周期
maven的生命周期定义了maven运行时的每一个步骤，让我们无需自行定义，由于由maven定制，所以不同的项目中生命周期都是一致的，使得我们不必去具体了解各个项目是如何构建的。
* Clean Lifecycle 在进行真正的构建之前进行一些清理工作。
* Default Lifecycle 构建的核心部分，编译，测试，打包，部署等等。
* Site Lifecycle 生成项目报告，站点，发布站点。

三个生命周期完全独立。[完整描述]("http://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html" "完整描述")
#### 插件配置
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <source>1.8</source>
        <target>1.8</target>
        <showWarnings>true</showWarnings>
        <skip>true</skip>
        <encoding>UTF-8</encoding>
    </configuration>
    <version>3.1</version>
</plugin>
<plugin>
    <groupId>org.apache.tomcat.maven</groupId>
    <artifactId>tomcat7-maven-plugin</artifactId>
    <configuration>
        <uriEncoding>UTF-8</uriEncoding>
        <url>http://127.0.0.1/manager/text</url>
        <server>mytom</server>
        <path>/${build.finalName}</path>
        <update>true</update>-->
        <path>/</path>
        <port>8888</port>
        <update>true</update>
    </configuration>
    <version>2.2</version>
</plugin>
```
使用configuration配置插件的自定义设置。

#### 默认的插件
clean：用于清理项目
compiler：用于编译
install：用于打包
deploy：用于部署

#### 运行插件
首先了解插件的运行命令，例如tomcat7插件的运行命令为run，则
> mvn tomcat7:run

运行tomcat7插件。插件还有phase和goals配置，涉及到maven的不同生命周期中的不同阶段（phase），goals是插件定义的不同的功能。具体参考生命周期完整描述。
