cameralibrary
-
[![](https://jitpack.io/v/enChenging/cameraAndPhotos.svg)](https://jitpack.io/#enChenging/cameraAndPhotos)

效果图如下：

<div align="left" >
	<img src="" width="200">
</div>

## 用法

>Android Studio

将其添加到存储库build.gradle中
```xml
allprojects {
    repositories {
      	...
        maven{url 'https://jitpack.io'}
    }
}
```
 在build.gradle文件中的dependencies下添加引用：
	
```java
implementation 'com.github.enChenging:cameraAndPhotos:1.0.0'
```


使用：
```java


```


## 混淆

```java
#Alert
-dontwarn com.release.cameralibrary.**
-keep class com.release.cameralibrary.**{*;}

```

声明
-
本控件用作分享与学习。

关于作者
-
[CSDN博客：https://blog.csdn.net/AliEnCheng/article/details/103778244](https://blog.csdn.net/AliEnCheng/article/details/103778244)





