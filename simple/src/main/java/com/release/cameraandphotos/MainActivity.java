package com.release.cameraandphotos;

import android.annotation.TargetApi;
import android.content.Intent;
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

import java.io.File;

/**
 * @author Mr.release
 * @create 2020-01-03
 * @Describe
 */

public class MainActivity extends AppCompatActivity {

    private Alert mAlert;
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

        mAlert = new Alert(MainActivity.this)
                .builder(Alert.Type.BOTTOM)
                .addItem("拍照")
                .addItem("图片")
                .setOnItemClickListener(new Alert.OnAlertItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (position == 0) {//拍照
                            CpUtils.camera(MainActivity.this);
                        } else if (Type == 4) {//选取多张图片
                            CpUtils.galleryPhoto(MainActivity.this);
                        } else if (Type == 5) {//拍照+选取单张图片(ucrop裁剪)
                            CpUtils.photo2(MainActivity.this);
                        } else {
                            CpUtils.photo(MainActivity.this);
                        }
                    }
                });

        /****************选取多张图片的配置及初始化开始*****************************/
        CpUtils.init(5, R.color.colorPrimary, R.color.white);
        adapter = CpUtils.initGridAdapter(this, gridview);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (position == Bimp.selectBitmap.size()) {
                    //增加
                    Type = 4;
                    if (PermissionUtils.checkAndReqkPermission(MainActivity.this, PermissionUtils.needPermissions))
                        mAlert.show();
                } else {
                    //展示
                    CpUtils.lookPhoto(MainActivity.this, position);
                }
            }
        });
        /****************选取多张图片的配置及初始化结束*****************************/

    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.update();
    }

    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.btn:
                //拍照+选取单张图片
                Type = 1;
                if (PermissionUtils.checkAndReqkPermission(this, PermissionUtils.needPermissions))
                    mAlert.show();
                break;
            case R.id.btn2:
                //拍照+选取单张图片(系统自带裁剪)
                Type = 2;
                if (PermissionUtils.checkAndReqkPermission(this, PermissionUtils.needPermissions))
                    mAlert.show();
                break;
            case R.id.btn4:
                //拍照+选取单张图片(ucrop裁剪)
                Type = 5;
                if (PermissionUtils.checkAndReqkPermission(this, PermissionUtils.needPermissions))
                    mAlert.show();
                break;
            case R.id.btn_clear:
                //清空图片
                Bimp.selectBitmap.clear();
                adapter.update();
                mIv_image.setImageBitmap(null);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        File file = CpUtils.onActivityResult(requestCode, resultCode, data, this, Type, mIv_image);
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
        mAlert.show();
    }

}
