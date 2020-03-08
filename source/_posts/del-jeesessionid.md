title: 去掉java项目URL地址栏后面的";JEESESSIONID="
date: 2015-07-07 19:09:56
categories: [java]
tags: [java,去掉JEESESSIONID,shiro 去掉JEESESSIONID]
---

# 关于JEESESSIONID
一般的J2EE项目，默认会使用JEESESSIONID作为会话id的标识，在服务器端和客户端交互中带上这个标识及其值，特别是shiro默认会在url后带这个后缀

## servlet3.0以后session id的保存方式
1. COOKIE 保存在客户端COOKIE中，通过请求读取cookie确认会话归属
2. URL 跟在url接连后面，默认方式，通过读取url后缀确认会话归属
3. SSL 使用SSL连接确认会话归属，这个最安全，但成本最高

## 如何去掉url后缀的";JEESESSIONID="
从上面看出，默认使用后缀，因此，只要改变session id的保存方式即可去掉，具体使用2中方式

### 设置web.xml
如果使用**Tomcat7**及以上版本，且开发基于**servlet3.0+**可以通过web.xml经行配置，如下
```xml
<session-config>
	<tracking-mode>COOKIE</tracking-mode>
</session-config>
```
<!-- more -->
### 使用filter
使用filter，去除后缀，DisableUrlSessionFilter：
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
package com.yanglong.common.web;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 去掉浏览后缀附带的J2EE session id字符串，默认为JSESSIONID，使用初始化属性sessionFix来指定
 */
public class DisableUrlSessionFilter implements Filter {
	private static final String DEFAULT_SESSION_SIGN="JSESSIONID";
    private FilterConfig filterConfig;

	public void destroy() {
		this.filterConfig=null;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (!(request instanceof HttpServletRequest)) {
			chain.doFilter(request, response);
			return;
		}
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		if (httpRequest.isRequestedSessionIdFromURL()) {  
            HttpSession session = httpRequest.getSession();
            if (session != null)  
                session.invalidate();  
        } 
		String url = httpRequest.getRequestURL().toString();
		String sessionFix=filterConfig.getInitParameter("sessionFix");
		sessionFix=(sessionFix==null||"".equals(sessionFix))?DEFAULT_SESSION_SIGN:sessionFix;
		sessionFix=";"+sessionFix+"=";
        int inx = url.indexOf(sessionFix);
        if(inx > 0)
        {
        	url = url.substring(0, inx);
        	httpResponse.sendRedirect(url);
        	return;
        }
        
		HttpServletResponseWrapper wrappedResponse = new HttpServletResponseWrapper(httpResponse) {
			public String encodeRedirectUrl(String url) {
				return url;
			}

			public String encodeRedirectURL(String url) {
				return url;
			}

			public String encodeUrl(String url) {
				return url;
			}

			public String encodeURL(String url) {
				return url;
			}
		};
		chain.doFilter(request, wrappedResponse);
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig=filterConfig;
	}

	public FilterConfig getFilterConfig() {
		return filterConfig;
	}

	public void setFilterConfig(FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
	}
}
```

将这个filter配置到web.xml，位置在编码filter之后，其他filter之前
```xml
<filter>
    <filter-name>disableUrlSessionFilter</filter-name>
    <filter-class>com.up360.core.web.DisableUrlSessionFilter</filter-class>
    <init-param>
        <param-name>sessionFix</param-name>
        <param-value>JEESESSIONID</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>disableUrlSessionFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

当然这个性能稍微差点，但在设置
> <tracking-mode>COOKIE</tracking-mode>

无效的情况下可以使用，最坏的情况2者都用。