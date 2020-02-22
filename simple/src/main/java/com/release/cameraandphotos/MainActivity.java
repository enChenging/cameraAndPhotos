package com.release.cameraandphotos;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;


import com.release.alert.Alert;
import com.release.cameralibrary.PermissionUtils;
import com.release.cameralibrary.Utils;
import com.release.cameralibrary.photo.Bimp;
import com.release.cameralibrary.photo.GridAdapter;
import com.release.cameralibrary.photo.GridViewNoScroll;
import com.release.cameralibrary.photo.ImageGridActivity;
import com.release.cameralibrary.photo.ImageItem;
import com.release.cameralibrary.photo.PhotoActivity;

import java.io.File;

/**
 * @author Mr.release
 * @create 2020-01-03
 * @Describe
 */

public class MainActivity extends AppCompatActivity {

    private Alert mAlert, mAlert2, mAlert3;
    private Bitmap mBitmap;
    private String PATH_SD_ICON;
    private ImageView mIv_image;
    private GridViewNoScroll gridview;
    private GridAdapter adapter;
    private int Type = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PATH_SD_ICON = Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/图片";
        initView();
    }

    private void initView() {
        mIv_image = findViewById(R.id.iv_image);
        gridview = findViewById(R.id.gridview);

        Bimp.selectBitmap.clear();// 清空图册
        Bimp.max = 3;// 初始化最大选择数
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
                adapter.notifyDataSetChanged();
                mIv_image.setImageBitmap(null);
            break;
        }
    }


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
                            //
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


    /**
     * 将图片上传到服务器
     *
     * @param picFile
     */
    private void uploadPicTask(File picFile) {

        //....
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
        }
    }
}
