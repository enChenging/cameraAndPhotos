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
implementation 'com.github.enChenging:cameraAndPhotos:1.2.4'
```
详细使用见工程里的[simple](https://github.com/enChenging/cameraAndPhotos/tree/master/simple)

使用核心代码：
```java

CpUtils.camera(MainActivity.this);//拍照
CpUtils.photo(MainActivity.this);//相册选取单张
CpUtils.galleryPhoto(MainActivity.this);//相册选取多张

 /****************选取多张图片的配置及初始化开始*****************************/
CpUtils.init(5, R.color.colorPrimary);// 初始化最大选择数、设置图册主题风格
adapter = CpUtils.initGridAdapter(this, gridview);
gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
	if (position == Bimp.selectBitmap.size()) {
	    Type = 4;
	    if (PermissionUtils.checkAndReqkPermission(MainActivity.this, PermissionUtils.needPermissions))
		mAlert.show();
	} else {
	    CpUtils.lookPhoto(MainActivity.this, position);
	}
    }
});
 /****************选取多张图片的配置及初始化结束*****************************/

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case CpUtils.CAMERA_REQUEST_CODE:
                switch (Type) {
                    case 1:
                        //拍照 压缩图片
                        Bitmap bitmap = CpUtils.zipFileFromPath(CpUtils.mTempFile.getPath());
                        mIv_image.setImageBitmap(bitmap);
                        uploadFile(bitmap);
                        break;
                    case 2:
                        //拍照 自带裁剪图片
                        CpUtils.cropPhoto(this, CpUtils.getUriFromFile(MainActivity.this, CpUtils.mTempFile));
                        break;
                    case 3:
                        //拍照 三方裁剪图片
                        Crop.of(CpUtils.getUriFromFile(MainActivity.this, CpUtils.mTempFile), Uri.fromFile(new File(getCacheDir(), "cropped" + System.currentTimeMillis()))).asSquare().start(this);
                        break;
                    case 4:
                        //拍照 (九宫格图，图片加载时统一做了压缩)
                        ImageItem takePhoto = new ImageItem();
                        takePhoto.setImagePath(CpUtils.mTempFile.getPath());//Bimp.selectBitmap.get(0).getBitmap()获取使用时时做了压缩
                        Bimp.selectBitmap.add(takePhoto);
                        break;
                }
                break;
            case CpUtils.PHOTO_REQUEST_CODE:
                Uri uri = data.getData();
                switch (Type) {
                    case 1:
                        //图册 压缩图片
                        Bitmap bitmap = CpUtils.zipFileFromUri(this, uri);
                        mIv_image.setImageBitmap(bitmap);
                        uploadFile(bitmap);
                        break;
                    case 2:
                        //图册 自带裁剪图片
                        CpUtils.cropPhoto(this, uri);
                        break;
                    case 3:
                        //图册 三方裁剪图片
                        File file = new File(getCacheDir(), "cropped" + System.currentTimeMillis());
                        Crop.of(uri, Uri.fromFile(file)).asSquare().start(this);
                        break;
                }
                break;
            case CpUtils.CROP_PHOTO_REUQEST_CODE:
                //自带裁剪图片完成
                try {
                    Bitmap bitmap = CpUtils.getBitmapFromUri(this, CpUtils.mCropImageUri);
                    mIv_image.setImageBitmap(bitmap);
                    uploadFile(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case Crop.REQUEST_CROP:
                //三方裁剪图片完成
                Uri output = Crop.getOutput(data);
                Bitmap bitmap = CpUtils.zipFileFromUri(this, output);
                mIv_image.setImageBitmap(bitmap);
                uploadFile(bitmap);
                break;
        }

    }

/**
* 将图片上传到服务器
*
* @param bitmap
*/
private void uploadFile(Bitmap bitmap) {
   File picFile = CpUtils.getFileFromBitmap(this, bitmap);
   //....
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





