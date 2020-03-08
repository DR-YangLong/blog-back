title: Android学习第一课
date: 2016-04-03 18:01:56
categories: [android]
tags: [StudyJams,Android]
---

# 第一课笔记

## 基础知识：
### 一、XML（可扩展性标记语言）
#### element（元素）
> xml的元素指开始标签知道结束标签的部分

如
``` xml
<book>this is a book</book>
```
上面**<book>**到**</book>**就是一个元素，称为book。
> 元素之间的部分就是元素的值，比如上面的"this is a book"，有时候元素还拥有子元素，大部分的时候，xml由大量的元素，子元素组成一个树形结构

#### attribute（属性）
> xml的属性指每个元素可以携带的额外信息

如
``` xml
<book bokName="精通JAVA"  bookPrice="25.00">
    this is a book
</book>
```
上面book元素开始标签内的**bokName**和**bookPrice**就是属性，表示book的一些附加信息，当然这些信息也可以在**子元素**中描述。

<!-- more -->

#### value（值）
值由2部分组成，一部分为element的值，也就是element开始和结束直接的部分，另一部分为每个属性**=**后面的部分。

#### xml的树形结构
``` xml
<bookcase bookNum="2">
<book>
<bookName type="战争">三国演义</bookName>
<bookPrice >12.0</bookPrice>
</book>
<book>
<bookName type="奇幻">西游记</bookName>
<bookPrice>25.0</bookPrice>
</book>
</bookcase>
```
上面描述了一个书柜，书柜里有2本书，一本是**战争类型的**叫**三国演义**，价格是**12.0**，另外一本是**奇幻**类型的，叫**西游记**，书价是**25.0**

> xml的作用其实就是用来进行跨环境的信息传递

#### schema（命名空间）与dtd（文档类型定义【Document Type Definition】）
xml中的元素名称，属性都是开发者自己定义的，如果对于不同事物的信息2个开发者都使用了相同的元素名称，这时候就没法准确的确定xml到底描述的是哪一个事物，这时候就需要使用命名空间。
表格
``` xml
<table>
<tr>
<td>表格</td>
</tr>
</table>
```
桌子
``` xml
<table>
<name>桌子</name>
</table>
```
上面的2个xml，由于根元素都是**table**，在一起使用时就会造成混淆，接收者不知道这是桌子还是表格（由于接收人处理信息的前提是必须知道这个信息描述的是上面东西）。所以需要使用schema来告诉接收者他接收到的信息描述的是什么。
> 当然使用前缀也是可以区别的，如
<t:table>
<t:tr>
<t:td>表格</t:td>
</t:tr>
</t:table>
但是无论如何，总不可避免的会有重复出现的时候。

引入namespace：
``` xml
<t:table xmlns:t="http://www.simple.com/biaoge">
   <t:tr>
   <t:td>表格</t:td>
   </t:tr>
</t:table>

<f:table xmlns:f="http://www.simple.com/zhuozi">
   <f:name>桌子</f:name>
</f:table>
```
可以看到，namespace被放在元素开始标签中，并遵循以下格式
>  xmlns:namespace-prefix="namespaceURI"

这样，使得一个xml从根本上与另外一个xml区别开来。上面2个例子使用的是**显式**命名的方式，即将前缀指向一个*统一资源标示符（URI）*也就是一个网址，然后在每个标签中添加前缀。另外一种方式是使用默认命名方式（基本使用这种方式）：
```xml 
<table xmlns="http://www.simple.com/biaoge">
<tr>
<td>表格</td>
</tr>
</table>

<table xmlns="http://www.simple.com/zhuozi">
<name>桌子</name>
</table>

```

> 由于xml只是描述性的语言，对于有的值，是必须有约束性的，比如价格，只能是数字，所以就有了DTD文档

DTD文档会定义xml中有哪些元素，这些元素名字是什么【这就是为什么一定要你将元素名写正确的原因】，有哪些属性，属性名称是什么，属性值是什么类型，元素的值是否是必须的等等。总而言之，DTD就是给xml定义一个规范，一个约束，用来检查xml是否合法。
xml使用DTD有2中方式，内部定义和外部引入，一般使用外部引入。
> 内部定义：在xml中编写

> 外部引入： <!DOCTYPE 根元素 SYSTEM "文件名">

## XML参考
[XML知识]("http://www.w3school.com.cn/x.asp" "点击前往")
[DTD知识]("http://www.w3school.com.cn/dtd/" "点击前往")


## Android界面布局
主要用来定义视图界面，包括布局方式，控件大小，颜色等。
### 界面（UI）的组成：布局容器与视图组件。
#### 五种布局：
共有五种布局方式，分别是：FrameLayout（框架布局），LinearLayout （线性布局），AbsoluteLayout（绝对布局），RelativeLayout（相对布局），TableLayout（表格布局）。

> 像素相关概念：

> 1. Px（Pixel像素）：不同设备显示效果相同。这里的“相同”是指像素数不会变，比如指定UI长度是100px，那不管分辨率是多少UI长度都是100px。也正是因为如此才造成了UI在小分辨率设备上被放大而失真，在大分辨率上被缩小。 
> 2. Dip（Device-independent pixel，设备独立像素）：同dp，可作长度单位，不同设备有不同的显示效果,这个和设备硬件有关，一般我们为了支持WVGA、HVGA和QVGA 推荐使用这个，不依赖像素。dip和具体像素值的对应公式是dip值 =设备密度/160* pixel值，可以看出在dpi（像素密度）为160dpi的设备上1px=1dip。
> 3. Resolution（分辨率）：指手机屏幕垂直和水平方向上的像素个数。比如分辨率是480*320，则指设备垂直方向有480个像素点，水平方向有320个像素点。
> 4. Dpi（像素密度）：指每英寸中的像素数。如160dpi指手机水平或垂直方向上每英寸距离有160个像素点。假定设备分辨率为320*240，屏幕长2英寸宽1.5英寸，dpi=320/2=240/1.5=160。
> 5. Density（密度）：指每平方英寸中的像素数。Density=Resolution/Screen size|
> 6. Sp（放大像素）：主要用于字体显示（best for textsize）。根据 google 的建议，TextView 的字号最好使用 sp 做单位，而且TextView默认使用 sp 作为字号单位。SP是基于系统字体设置的。

由于不同设备的尺寸和分辨率都不相同，所以最好不要使用像素来布局，以免造成在像素密度比较高的设备上造成视图组件过小。使用DP能够获得较为统一的体验。

转换关系：
> dp与px转换的方法：
``` java
public static int dip2px(Context context, float dipValue){ 　　
final float scale = context.getResources().getDisplayMetrics().density; 
return (int)(dipValue * scale +0.5f); 
}
public static int px2dip(Context context, float pxValue){ 　　
final float scale = context.getResource().getDisplayMetrics().density; 　　
return (int)(pxValue / scale +0.5f); 
}
```

#### 第一课view组件：

TextView:文本框
``` xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Hello World!" />
```
> 属性： wrap_content ——自适应内容DP；textSize ——字号，不同设备相同值得字号显示体验一致；textAppearance ——Large，small同样定义字体大小sp无关。textColor ——设置字体颜色。background ——设置view的背景色。

ImageView:图片
``` xml
<ImageView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" />
```
> 属性：src ——资源文件引入，使用@drawable会查找位于资源目录下drawable目录中文件，不必使用后缀；scaleType ——图片缩放类型center：居中，centerCrop:剪切居中。

Button:按钮
``` xml
<Button
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Button"/>
```

> 建议：按钮最少48dp。

## 组件和组件属性可以查询API获得
[Android UserGuide]("http://developer.android.com/intl/zh-cn/guide/index.html" "翻墙查看")

# 第一课总结
和学习JAVA WEB开发时一样的学习方式：API优先。结合实例。任何代码跑一把总能看到效果。写下代码，运行看到效果，总是令人激动的。希望和大家一起进步吧。