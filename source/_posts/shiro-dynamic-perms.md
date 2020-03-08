title: Shiro动态权限实现
date: 2015-07-07 21:09:56
categories: [java]
tags: [java,shiro,shiro 动态权限]
---

# 关于Shiro实现资源匹配权限的方式及实现动态权限思路
shiro使用filter的进行权限认证和过滤，其原理是将url和权限标识配对，生成filterchain，当请求进入时，对用户进行验权，因而要实现动态权限，只要更新filterchain即可。[源码](https://github.com/DR-YangLong/webarchitecture/tree/master/dr-common/src/main/java/com/dr/architecture/common/shiro "shiro动态权限")

## 首先查看shiro的filterchain的生成
在文档中可以看到，shiro的filter实例都是由
>  ShiroFilterFactoryBean

来生成，其主要属性包含
```java
	private SecurityManager securityManager;
    private Map<String, Filter> filters;
    private Map<String, String> filterChainDefinitionMap; //资源权限对应map
    private String loginUrl;
    private String successUrl;
    private String unauthorizedUrl;
    private AbstractShiroFilter instance;
```
其中**filterChainDefinitionMap**属性就是我们所要动态改变的，
<!-- more -->
他实现了spring的2个核心接口
> FactoryBean, BeanPostProcessor

2个最主要的方法：
> protected AbstractShiroFilter createInstance();

负责创建filter

> protected FilterChainManager createFilterChainManager();

负责创建FilterChainManager,这个FilterChainManager就负责管理所有的filter和filterchain，那么我们要实现动态权限，就需要通过他进行对filter的操作。我们要用到他的主要方法就一个：
```java
void createChain(String chainName, String chainDefinition);
```
使用url和权限字符串来添加一个filter，由于filter是在map中维护的，因而我们就可以实现filter的动态替换，新增。

## 具体实现
1. 我们必须有个动态获取url--permission的dao
2. 我们要用dao获得的url--permission生成filter添加/替换FilterChainManager中的filter，实现者是一个service
3. 我们要实现service，就要获取到FilterChainManager

首先根据ShiroFilterFactoryBean的属性，我们知道要获取的
> Map<String, String> filterChainDefinitionMap

这样的东西，而且通过分析，他是一个LinkedHashMap，并且我们按shiro的默认权限字符串
> roles[1,2,3],perms[1,2,3]

这样的方式来进行验证，当然，你也可以自定义权限filter的别名，在获取时候变一下就好。
那么我们的DAO接口是这样的，
```java
//略
public interface DynamicPermissionDao {
  Map<String,String> findDefinitionsMap();
}
//略
public interface JdbcPermissionDao extends DynamicPermissionDao {
    /**
     * 获取url到权限的对应字符串
     *
     * @return
     */
    LinkedHashMap<String, String> generateDefinitions();
}
```

为了保证多来源，进行了接口继承。
接下来我们要进行service接口的设计，
```java
/**
 * com.shiro
 * functional describe:动态权限配置Service
 *
 * @author DR.YangLong [410357434@163.com]
 * @version 1.0 2015/1/17 10:33
 */
public interface DynamicPermissionService {
    /**
     * 资源权限的配置字符串模板
     */
    public static final String PREMISSION_STRING="perms[{0}]";
    /**
     * 角色权限的配置字符串模板
     */
    public static final String ROLE_STRING="roles[{0}]";

    /**
     * 初始化时获取当前已定义的filterchains
     */
    void init();

    /**
     * 更新框架资源权限配置，需要线程同步,此方法会动态添加definitions
     * 如果有重复的url，新的map将覆盖以前的map
     * 也就是说，以前链接的权限配置会被新的权限配置覆盖
     */
     void updatePermission(Map<String, String> newDefinitions);

    /**
     * 需要线程同步,此方法会加载静态配置，DynamicPermissionDao查询出来的配置
     *
     */
    void reloadPermission();
}
```

我们的实现类要实现动态添加/替换的功能，必定要想办法获取FilterChainManager，通过分析filterFactoryBean我们知道，他最终的目的是用来生成
> AbstractShiroFilter

而AbstractShiroFilter中有
> FilterChainResolver

他的实际类型是
> PathMatchingFilterChainResolver

那么通过PathMatchingFilterChainResolver我们可以获取到
> FilterChainManager

那么，最核心的问题就解决了，所以最终的service接口的实现：
```java
/*
        Copyright  DR.YangLong

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
*/
package com.dr.architecture.common.shiro.dynamic;

import org.apache.shiro.config.Ini;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.web.config.IniFilterChainResolverFactory;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * functional describe:shiro动态权限管理，<br/>
 * 配置权限时，尽量不要使用/**来配置（重置时将被清除，如果要使用，在{@link JdbcPermissionDao}实现类中最后添加key="/**"，value="anon"），每个链接都应该配置独立的权限信息<br/>
 *
 * @author DR.YangLong [410357434@163.com]
 * @version 1.0 2015/1/17 11:10
 */
public class DynamicPermissionServiceImpl implements DynamicPermissionService {
    private static final Logger logger = LoggerFactory.getLogger(DynamicPermissionServiceImpl.class);
    private AbstractShiroFilter shiroFilter;
    private DynamicPermissionDao dynamicPermissionDao;
    private String definitions = "";

    @PostConstruct
    public synchronized void init() {
        logger.debug("初始化filter加权限信息，在servlet的init方法之前。");
        reloadPermission();
        logger.debug("初始化filter权限信息成功，开始执行servlet的init方法。");
    }

    public synchronized void reloadPermission() {
        logger.debug("reload资源权限配置开始！");
        try {
            Map<String, String> pers = generateSection();
            DefaultFilterChainManager manager = getFilterChainManager();
            manager.getFilterChains().clear();
            addToChain(manager, pers);
        } catch (Exception e) {
            logger.error("reload资源权限配置发生错误！", e);
        }
        logger.debug("reload资源权限配置结束！");
    }

    private DefaultFilterChainManager getFilterChainManager() throws Exception {
        // 获取过滤管理器
        PathMatchingFilterChainResolver filterChainResolver = (PathMatchingFilterChainResolver) shiroFilter
                .getFilterChainResolver();
        DefaultFilterChainManager manager = (DefaultFilterChainManager) filterChainResolver.getFilterChainManager();
        return manager;
    }

    private void addToChain(DefaultFilterChainManager manager, Map<String, String> definitions) throws Exception {
        if (definitions == null || CollectionUtils.isEmpty(definitions)) {
            return;
        }
        //移除/**的过滤器链，防止新加的权限不起作用。
        manager.getFilterChains().remove("/**");
        for (Map.Entry<String, String> entry : definitions.entrySet()) {
            String url = entry.getKey();
            String chainDefinition = entry.getValue().trim().replace(" ", "");
            manager.createChain(url, chainDefinition);
        }
    }

    public synchronized void updatePermission(Map<String, String> newDefinitions) {
        logger.debug("更新资源配置开始！");
        try {
            // 获取和清空初始权限配置
            DefaultFilterChainManager manager = getFilterChainManager();
            newDefinitions.put("/**","anon");
            addToChain(manager, newDefinitions);
            logger.debug("更新资源权限配置成功。");
        } catch (Exception e) {
            logger.error("更新资源权限配置发生错误!", e);
        }
    }

    /**
     * 将静态配置的资源与要添加的动态资源整合在一起，生成shiro使用的权限map
     * {@see org.apache.shiro.spring.web.ShiroFilterFactoryBean#setFilterChainDefinitions(String)}
     *
     * @return Section
     */
    private Ini.Section generateSection() {
        Ini ini = new Ini();
        ini.load(definitions); // 加载资源文件节点串定义的初始化权限信息
        Ini.Section section = ini.getSection(IniFilterChainResolverFactory.URLS); // 使用默认节点
        if (CollectionUtils.isEmpty(section)) {
            section = ini.getSection(Ini.DEFAULT_SECTION_NAME);//如不存在默认节点切割,则使用空字符转换
        }
        /**
         * 加载非初始化定义的权限信息
         */
        Map<String, String> permissionMap = loadDynamicPermission();
        if (permissionMap != null && !permissionMap.isEmpty()) {
            section.putAll(permissionMap);
        }
        return section;
    }

    /**
     * 加载动态权限资源配置,map<ant url,comma-delimited chain definition>
     * @return map
     */
    private Map<String, String> loadDynamicPermission() {
        Map<String,String> map=dynamicPermissionDao.findDefinitionsMap();
        map.put("/**","anon");
        return map;
    }

    public DynamicPermissionDao getDynamicPermissionDao() {
        return dynamicPermissionDao;
    }

    public void setDynamicPermissionDao(DynamicPermissionDao dynamicPermissionDao) {
        this.dynamicPermissionDao = dynamicPermissionDao;
    }

    public AbstractShiroFilter getShiroFilter() {
        return shiroFilter;
    }

    public void setShiroFilter(AbstractShiroFilter shiroFilter) {
        this.shiroFilter = shiroFilter;
    }

    public String getDefinitions() {
        return definitions;
    }

    public void setDefinitions(String definitions) {
        this.definitions = definitions;
    }
}
```
到此，shiro动态权限基本实现了。所要做的不过是实现dao的接口，将service作为bean配置到spring配置文件中。
就像这样
```xml
  <bean id="shiroFilter" class="org.apache.shiro.spring.web.ShiroFilterFactoryBean">
        <property name="securityManager" ref="securityManager"/>
        <property name="loginUrl" value="/sso/login"/>
        <property name="unauthorizedUrl" value="/sso/login"/>
        <property name="successUrl" value="/home/index"/>
        <property name="filters">
            <util:map>
                <entry key="authc">
                    <bean class="org.apache.shiro.web.filter.authc.PassThruAuthenticationFilter"/>
                </entry>
            </util:map>
        </property>
        <!-- <property name="filterChainDefinitions">
             <value>
                 /els/share/**=anon
                 /teachers/t/invite/** = anon
                 /camp/comp/s/**=authc,roles[student]
                 /favicon.ico=anon
                 /** = authc,roles[student]
             </value>
         </property>-->
    </bean>

    <bean id="filterChainDefinitionsFactory"
          class="com.yanglong.common.shiro.dynamic.DynamicPermissionServiceImpl">
        <property name="dynamicPermissionDao" ref="jdbcPermissionDao"/>
        <property name="shiroFilter" ref="shiroFilter"/>
        <property name="definitions">
            <value>
                /favicon.ico=anon
                /sso/logout = logout
            </value>
        </property>
    </bean>

    <bean id="jdbcPermissionDao" class="com.yanglong.core.shiro.JdbcPermissionDaoImpl"/>
```