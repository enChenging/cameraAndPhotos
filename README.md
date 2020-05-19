cameralibrary
-

[![](https://jitpack.io/v/enChenging/cameraAndPhotos.svg)](https://jitpack.io/#enChenging/cameraAndPhotos)

效果图如下：

<div align="left" >
	<img src="https://github.com/enChenging/cameraAndPhotos/blob/master/screenshot/video.gif" width="200">
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
implementation 'com.github.enChenging:cameraAndPhotos:1.2.5'
```
详细使用见工程里的[simple](https://github.com/enChenging/cameraAndPhotos/tree/master/simple)

使用核心代码：
```java

CpUtils.camera(MainActivity.this);//拍照
CpUtils.photo(MainActivity.this);//相册选取单张
CpUtils.photo2(MainActivity.this);相册选取单张(ucrop裁剪)
CpUtils.galleryPhoto(MainActivity.this);//相册选取多张

 /****************选取多张图片的配置及初始化开始*****************************/
CpUtils.init(5, R.color.colorPrimary);// 初始化最大选择数、设置图册主题风格
adapter = CpUtils.initGridAdapter(this, gridview);
gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
	if (position == Bimp.selectBitmap.size()) {
	 //增加
	} else {
	//展示
	    CpUtils.lookPhoto(MainActivity.this, position);
	}
    }
});
 /****************选取多张图片的配置及初始化结束*****************************/

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
super.onActivityResult(requestCode, resultCode, data);
File file = CpUtils.onActivityResult(requestCode, resultCode, data, this, Type, mIv_image);
}


```


## 混淆

```java
#cameralibrary
-dontwarn com.release.cameralibrary.**
-keep class com.release.cameralibrary.**{*;}

```

声明
-
本控件用作分享与学习。

关于作者
-
[CSDN博客：https://blog.csdn.net/AliEnCheng/article/details/103819711](https://blog.csdn.net/AliEnCheng/article/details/103819711)





