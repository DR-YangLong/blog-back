title: Android像素计算
date: 2016-04-07 23:09:00
categories: [android]
tags: [Android]
---
dp与px换算，sp同：        
px=dp*(dpi/160)
dp=px/(dpi/160)=px*160/dpi
dpi=(对角线长度像素值)/手机显示屏尺寸
对角线长度像素值=(分辨率宽的平方+分辨率高的平方)开平方