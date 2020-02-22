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
implementation 'com.github.enChenging:cameraAndPhotos:1.0.0'
implementation 'com.github.enChenging:Alert:1.0.1'
```
详细使用见工程里的[simple](https://github.com/enChenging/cameraAndPhotos/tree/master/simple)

使用核心代码：
```java
Bimp.selectBitmap.clear();// 清空图册
Bimp.max = 3;// 初始化最大选择数
Bimp.themeColor = R.color.colorPrimary;//设置图册主题风格

    gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
adapter = new GridAdapter(this);
    adapter.update();
    gridview.setAdapter(adapter);
    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if (arg2 == Bimp.selectBitmap.size()) {
            Type = 3;
            mAlert3.show();
        } else {
            Intent intent = new Intent(MainActivity.this, PhotoActivity.class);
            intent.putExtra("ID", arg2);
            startActivity(intent);
        }
    }
});

mAlert = new Alert(MainActivity.this)
            .builder(Alert.Type.BOTTOM)
            .addItem("拍照")
            .addItem("图片")
            .setOnItemClickListener(new Alert.OnAlertItemClickListener() {
    @Override
    public void onItemClick(View view, int position) {
        if (position == 0) {
            Utils.camera(MainActivity.this);
        } else {
            Utils.photo(MainActivity.this);
        }
    }
});
mAlert2 = new Alert(MainActivity.this)
            .builder(Alert.Type.BOTTOM)
            .addItem("拍照(裁剪)")
            .addItem("图片(裁剪)")
            .setOnItemClickListener(new Alert.OnAlertItemClickListener() {
    @Override
    public void onItemClick(View view, int position) {
        if (position == 0) {
            Utils.camera(MainActivity.this);
        } else {
            Utils.photo(MainActivity.this);
        }
    }
});
mAlert3 = new Alert(MainActivity.this)
            .builder(Alert.Type.BOTTOM)
            .addItem("拍照")
            .addItem("图片(选取多张图片)")
            .setOnItemClickListener(new Alert.OnAlertItemClickListener() {
    @Override
    public void onItemClick(View view, int position) {
        if (position == 0) {
            Utils.camera(MainActivity.this);
        } else {
            startActivity(new Intent(MainActivity.this, ImageGridActivity.class));
            overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
        }
    }
});

@SuppressLint("StringFormatInvalid")
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    switch (requestCode) {
        case Utils.CAMERA_REQUEST_CODE:
            if (resultCode == RESULT_OK) {
                switch (Type) {
                    case 1:
                        //压缩图片
                        Bitmap bitmap = Utils.decodeFile(Utils.mTempFile.getAbsolutePath());
                        mIv_image.setImageBitmap(bitmap);
                        File picFile = Utils.setPicToSdCard(PATH_SD_ICON, bitmap);
                        uploadPicTask(picFile);
                        break;
                    case 2:
                        //裁剪图片
                        Uri imageContentUri = Utils.getImageContentUri(MainActivity.this, Utils.mTempFile);
                        Utils.cropPhoto(this, imageContentUri);
                        break;
                    case 3:
                        ImageItem takePhoto = new ImageItem();
                        takePhoto.setImagePath(Utils.mTempFile.getPath());
                        Bimp.selectBitmap.add(takePhoto);
                        break;

                }
            }
            break;
        case Utils.PHOTO_REQUEST_CODE:
            if (resultCode == RESULT_OK) {
                switch (Type) {
                    case 1: {
                        //压缩图片
                        Bitmap bitmap = Utils.decodeUri(this, data.getData());
                        mIv_image.setImageBitmap(bitmap);
                        break;
                    }
                    case 2: {
                        //裁剪图片
                        Utils.cropPhoto(this, data.getData());
                        break;
                    }
                }
            }
            break;
        case Utils.CROP_PHOTO_REUQEST_CODE:
            if (resultCode == RESULT_OK) {
                try {
                    mBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(Utils.mUritempFile));
                    mIv_image.setImageBitmap(mBitmap);
                    File picFile = Utils.setPicToSdCard(PATH_SD_ICON, mBitmap);
                    uploadPicTask(picFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
    }
    super.onActivityResult(requestCode, resultCode, data);
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





