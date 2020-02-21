package com.release.cameralibrary;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mr.release
 * @create 2020-01-02
 * @Describe
 */
public class PermissionUtils {

    public static final int PERMISSON_REQUESTCODE = 0;

    /**
     * 需要进行检测的权限数组
     */
    public static String[] needPermissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * 是否有权限
     *
     * @param activity
     * @param permissions
     * @return
     */
    public static boolean hasPermissions(Activity activity, String[] permissions) {
        if (null != permissions) {
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(activity, permissions[i]) != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 请求权限
     *
     * @param activity
     * @param permissions
     */
    public static void reqPermissions(Activity activity, String[] permissions) {
        if (null != permissions) {
            List<String> perList = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(activity, permissions[i]) != 0) {
                    perList.add(permissions[i]);
                }
            }
            if (perList.size() > 0) {
                ActivityCompat.requestPermissions(activity, perList.toArray(new String[perList.size()]), PERMISSON_REQUESTCODE);
            }
        }
    }


    /**
     * 检查并且请求权限（如果没有就请求）
     *
     * @param activity
     * @param permissions
     * @return
     */
    public static boolean checkAndReqkPermission(Activity activity, String[] permissions) {
        if (hasPermissions(activity, permissions)) {
            return true;
        } else {
            reqPermissions(activity, permissions);
            return false;
        }
    }

    /**
     * 检测是否所有的权限都已经授权
     *
     * @param grantResults
     * @return
     */
    public static boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    /**
     * 显示提示信息
     */
    public static void showMissingPermissionDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("提示");
        builder.setMessage("当前应用缺少必要权限。请点击设置，开启所有权限");

        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        builder.setPositiveButton("设置",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings(context);
                    }
                });

        builder.setCancelable(false);

        builder.show();
    }

    /**
     * 启动应用的设置
     *
     * @param context
     */
    private static void startAppSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }
}
