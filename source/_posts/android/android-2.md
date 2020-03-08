title: Android布局初识
date: 2016-04-07 23:09:00
categories: [android]
tags: [StudyJams,Android]
---

# 布局
## View的关系
一个界面只能有一个根view，ViewGroup是也是一种view，通常用来作为多个view的容器， 进行界面布局，它是矩形的 。view和view间的关系，父子，兄弟关系，就像XML的结构一样。同样的可以多级的嵌套。
> ViewGroup继承于View，实现了ViewParent和ViewManager接口，可作为容器view的view继承了ViewGroup，而普通的view则直接继承View并且没有实现那2个接口，因此所有view【类没有定义为final】都可以通过继承后实现ViewParent和ViewManager接口变为容器view，很多自定的view其实都是用过继承，实现多个接口来做出想要的功能的。     

## 布局方式【待补充】
主要的布局方式有：
LinearLayout,RelativeLayout,Grid View,List View,Recycler View。

### 子view的大小设置方式：
> 1. 固定宽高：设置一个固定值，超出部分会被截去。

> 2. 设置为wrap-content，会自适应view的内容大小。

> 3. 设置为match-parent，会和父view一样的尺寸。

    **宽高可以分别设置**

### 权重layout-weight
view有布局权重，权重越大，所能分配到的父view的剩余空间越大，权重相等，会平分剩余的空间。可以用来作为布局中视图的均匀分布【垂直或水平】。

<!--more-->

###  LinearLayout
子view在父view中线性排列：垂直或水平。使用属性orientation指定。horizontal——水平。vertical——垂直。

### RelativeLayout
让子view相对于父view的位置进行排列，或是子view相对于其他子view的位置进行排列。
#### 子view相对父view属性：
> 1. layout_alignParentTop 是否与父view上对齐
> 2. layout_alignParentBottom 是否与父view下对齐
> 3. layout_alignParentLeft 是否与父view左对齐
> 4. layout_alignParentRight 是否与父view右对齐

	**默认值为false**

可以混合使用以上各个属性，如果不指定属性，默认添加到父View左上角。

#### 子view相对于子view位置：
使用相对位置需要用到view的id，使用id="@+id/{idName}"指定view的id。
使用以下属性定义
> 1. android:layout_above="@id/textView"  ——在上面
> 2. android:layout_alignLeft="@id/textView"  ——在左边
> 3. android:layout_alignBottom="@id/textView" ——在下边
> 4. android:layout_alignRight="@id/textView" ——在右边
> 5. android:layout_alignEnd="@id/textView" ——右【结束的地方】边缘一致 start是左边缘【开始的地方】一致

### 内边距和外边距【同CSS】
#### padding内边距
内边距指view中内容到view边缘的距离。

#### margin外边距
指view边缘到view边缘之间的距离。注意不论是父view还是子view都适用。

### List View
List View显示一列可以滚动项目的视图组，即常见的列表。

### Grid View
网格视图，类似table，可以滚动显示。

### TabLayout
选项卡布局

