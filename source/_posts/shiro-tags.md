title: Fremark集成Shiro标签
date: 2015-06-13 17:09:56
categories: [java]
tags: [shiro,shiro freemark标签]
---

#Shiro整合Freemark标签
写个java类，如下
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
package com.dr.architecture.common.shiro;

import com.dr.architecture.common.shiro.tags.ShiroTags;
import freemarker.template.TemplateException;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.IOException;

/**
 * com.up360.extension.security.shiro
 * functional describe:shiro freemark标签 https://github.com/jagregory/shiro-freemarker-tags
 *
 * @author DR.YangLong
 * @version 1.0  datetime:2014/12/10 16:06
 */
public class ShiroFreemarkTagsConfigurer extends FreeMarkerConfigurer {
    @Override
    public void afterPropertiesSet() throws IOException, TemplateException {
        super.afterPropertiesSet();
        this.getConfiguration().setSharedVariable("shiro", new ShiroTags());
    }
}
```
在配置spring的视图解析器时，将**FreeMarkerConfigurer**替换为此类即可。具体可参考shiro-freemarker-tags项目中的说明。