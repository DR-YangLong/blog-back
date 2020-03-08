title: HEXO的分类
date: 2015-06-13 17:09:56
categories: [hexo]
tags: [博客,hexo,hexo github]
---

** 环境说明**
> 博客根目录I:\IDEAWorkspace\blog
> 主题根目录：I:\IDEAWorkspace\blog\themes\yilia

## 分类配置文件说明
首先在博客根目录下有一个  **_config.yml**  配置文件，此配置文件是全局配置文件，具体配置信息参考网上，这里只说分类，打开此文件找到

```bash
archive_dir: archives
category_dir: categories
```

如上，保持默认即可，此配置的意思是分类的目录为 *categories* 我们的md文件在服务器运行时的访问路径是
> 127.0.0.1:4000/categories/{我们定义的分类}

那么分类如何定义呢？

<!-- more -->
## 定义分类
首先，打开主题根目录（I:\IDEAWorkspace\blog\themes\yilia\）下的  **layout**  文件夹，看有没有
> category.ejs

文件，如果没有，新建一个，并写入以下内容：

```bash
<%- partial('_partial/archive', {pagination: config.category, index: true}) %>
```
打开同级目录下的  **_partial/post**  文件夹，查看有没有
> category.ejs

文件，如果没有，新建一个，并写入以下内容：

```bash
<% if (post.categories && post.categories.length){ %>
	<div class="article-category tagcloud">
	<%- list_categories(post.categories, {
	  show_count: false,
	  class: 'article-category',
	  style: 'none',
	  separator: ''
	}) %>
	</div>
<% } %>

```

然后，打开根目录（I:\IDEAWorkspace\blog）下  **scaffolds**  目录，打开里面的3个md文件，并在每个文件中添加一行
> categories:

例如  **post.md**  添加完后如下：

```bash
title: {{ title }}
date: {{ date }}
tags:
categories:
---

```

这里只是让我们运行
> hexo new page "pageName"

的时候生成如下的文件内容：

```bash
title: pageName
date: 2015-06-13 17:09:56
categories:
tags:
---

```

###接下来我们给文章添加分类，并让分类能够列出所有一样的分类文章

####给文章添加分类
新建一个md文件，填入内容：

```bash
title: hexo的分类
date: 2015-06-13 17:09:56
categories: [hexo]
tags: [博客,hexo,hexo github]
---
分类测试
```
保存。
####使分类能够访问
转到主题根目录下（I:\IDEAWorkspace\blog\themes\yilia），打开  **_config.yml**  找到
> menu

我的类似：

```bash
menu:
  主页: /
  所有文章: /archives
```
添加一个分类：
> hexo: /categories/hexo/

变成

```bash
menu:
  主页: /
  所有文章: /archives
  # 随笔: /tags/随笔
  hexo: /categories/hexo/
```
运行以下命令在本地查看效果：

1. hexo clean
2. hexo s -g

## 可以看到，文章分类的访问路径组成，首先在项目根目录下配置分类的访问路径，本例中是默认的
> categories

这步生成链接一部分：
> 127.0.0.1:4000/categories/

然后通过md文件中的
> categories: [hexo]

来生成具体分类目录：
> 127.0.0.1:4000/categories/hexo

接着，设置主题的菜单分类：
> hexo: /categories/hexo/

让文章分类和主题菜单分类导航关联起来，可以看到主题中的分类导航的链接必须是他之前2步走完生成的链接。


##中文分类映射
在项目的根目录的配置文件有这样的一个配置项：

```bash
# Category & Tag
default_category: uncategorized
category_map:
tag_map:
```
我们设置分类可能有中文，那么访问路径就带有中文，所以要把他们转换掉，这时候就可以设置分类映射：

```bash
# Category & Tag
default_category: uncategorized
category_map:
		中文分类名1: eng1
		中文分类名2: eng2
tag_map:
```
注意英文  **:**  和后面的内容间有空格，那么md文件中使用中文分类，然后在主题的配置文件中的分类导航像这样配置就好了：
> 中文分类名1: /categories/eng1