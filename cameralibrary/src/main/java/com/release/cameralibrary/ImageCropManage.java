package com.release.cameralibrary;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.release.cameralibrary.photo.Bimp;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;

/**
 * @author Mr.release
 * @create 2020-05-03
 * @Describe
 */

public class ImageCropManage {

    public static void startCropActivity(Activity activity, String path) {
        long currentTimeMillis = System.currentTimeMillis();
        Uri uri = Uri.fromFile(new File(path));
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(activity.getCacheDir(), currentTimeMillis + ".png")));
        uCrop = config(activity, uCrop);
        uCrop.start(activity);
    }

    public static UCrop config(Activity activity, @NonNull UCrop uCrop) {
        uCrop.withAspectRatio(1, 1);//设置裁剪图片的宽高比，比如16：9

        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);//设置裁剪图片可操作的手势
        options.setHideBottomControls(true);//是否隐藏底部容器，默认显示
        options.setShowCropGrid(false);//是否显示裁剪框网格
        options.setCropGridColor(activity.getResources().getColor(R.color.white));//设置裁剪框网格颜色
        options.setFreeStyleCropEnabled(true);//是否能调整裁剪框
        options.setShowCropFrame(true); //设置是否显示裁剪边框(true为方形边框)

//        options.setMaxBitmapSize(800);//设置图片压缩最大值
//        options.setCropGridStrokeWidth(50);//设置裁剪框网格宽
//        options.setCropFrameStrokeWidth(50);//设置裁剪框边的宽度

        if (Bimp.themeColor != 0) {
            options.setToolbarColor(ContextCompat.getColor(activity, Bimp.themeColor));
            options.setStatusBarColor(ContextCompat.getColor(activity, Bimp.themeColor));
            options.setActiveWidgetColor(ContextCompat.getColor(activity, Bimp.themeColor));
        }
        if (Bimp.btnColor != 0)
            options.setToolbarWidgetColor(ContextCompat.getColor(activity, Bimp.btnColor));
        else
            options.setToolbarWidgetColor(ContextCompat.getColor(activity, R.color.white));
        return uCrop.withOptions(options);
    }


}
