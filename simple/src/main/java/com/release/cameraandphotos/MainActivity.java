package com.release.cameraandphotos;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.release.alert.Alert;
import com.release.cameralibrary.CpUtils;
import com.release.cameralibrary.PermissionUtils;
import com.release.cameralibrary.photo.Bimp;
import com.release.cameralibrary.photo.GridAdapter;
import com.release.cameralibrary.photo.GridViewNoScroll;
import com.release.cameralibrary.photo.ImageGridActivity;
import com.release.cameralibrary.photo.ImageItem;
import com.release.cameralibrary.photo.PhotoActivity;
import com.soundcloud.android.crop.Crop;

import java.io.File;

/**
 * @author Mr.release
 * @create 2020-01-03
 * @Describe
 */

public class MainActivity extends AppCompatActivity {

    private Alert mAlert, mAlert2, mAlert3, mAlert4;
    private ImageView mIv_image;
    private GridViewNoScroll gridview;
    private GridAdapter adapter;
    private int Type = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIv_image = findViewById(R.id.iv_image);
        gridview = findViewById(R.id.gridview);

        initView();
    }

    private void initView() {
        Bimp.selectBitmap.clear();// 清空图册
        Bimp.max = 5;// 初始化最大选择数
        Bimp.themeColor = R.color.colorPrimary;//设置图册主题风格

        gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (arg2 == Bimp.selectBitmap.size()) {
                    Type = 3;
                    if (PermissionUtils.checkAndReqkPermission(MainActivity.this, PermissionUtils.needPermissions))
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
                            CpUtils.camera(MainActivity.this);
                        } else {
                            CpUtils.photo(MainActivity.this);
                        }
                    }
                });
        mAlert2 = new Alert(MainActivity.this)
                .builder(Alert.Type.BOTTOM)
                .addItem("拍照(自带裁剪)")
                .addItem("图片(自带裁剪)")
                .setOnItemClickListener(new Alert.OnAlertItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (position == 0) {
                            CpUtils.camera(MainActivity.this);
                        } else {
                            CpUtils.photo(MainActivity.this);
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
                            CpUtils.camera(MainActivity.this);
                        } else {
                            startActivity(new Intent(MainActivity.this, ImageGridActivity.class));
                            overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
                        }
                    }
                });
        mAlert4 = new Alert(MainActivity.this)
                .builder(Alert.Type.BOTTOM)
                .addItem("拍照(三方裁剪)")
                .addItem("图片(三方裁剪)")
                .setOnItemClickListener(new Alert.OnAlertItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (position == 0) {
                            CpUtils.camera(MainActivity.this);
                        } else {
                            CpUtils.photo(MainActivity.this);
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.update();
    }

    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.btn:
                Type = 1;
                if (PermissionUtils.checkAndReqkPermission(this, PermissionUtils.needPermissions))
                    mAlert.show();
                break;
            case R.id.btn2:
                Type = 2;
                if (PermissionUtils.checkAndReqkPermission(this, PermissionUtils.needPermissions))
                    mAlert2.show();
                break;
            case R.id.btn3:
                //清空图片
                Bimp.selectBitmap.clear();
                adapter.update();
                mIv_image.setImageBitmap(null);
                break;
            case R.id.btn4:
                Type = 4;
                if (PermissionUtils.checkAndReqkPermission(this, PermissionUtils.needPermissions))
                    mAlert4.show();
                break;
        }
    }

    public void hasPermission() {
        switch (Type) {
            case 1:
                mAlert.show();
                break;
            case 2:
                mAlert2.show();
                break;
            case 3:
                mAlert3.show();
                break;
            case 4:
                mAlert4.show();
                break;
        }
    }


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
                        //拍照 (九宫格图，图片加载时统一做了压缩)
                        ImageItem takePhoto = new ImageItem();
                        takePhoto.setImagePath(CpUtils.mTempFile.getPath());//Bimp.selectBitmap.get(0).getBitmap()获取时做了压缩
                        Bimp.selectBitmap.add(takePhoto);
                        break;
                    case 4:
                        //拍照 三方裁剪图片
                        Crop.of(CpUtils.getUriFromFile(MainActivity.this, CpUtils.mTempFile), Uri.fromFile(new File(getCacheDir(), "cropped" + System.currentTimeMillis()))).asSquare().start(this);
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
                    case 4:
                        //图册 三方裁剪图片
                        Crop.of(uri, Uri.fromFile(new File(getCacheDir(), "cropped" + System.currentTimeMillis()))).asSquare().start(this);
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

    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        if (requestCode == PermissionUtils.PERMISSON_REQUESTCODE) {
            if (PermissionUtils.verifyPermissions(paramArrayOfInt)) {
                hasPermission();
            } else {
                PermissionUtils.showMissingPermissionDialog(this);
            }
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


}
