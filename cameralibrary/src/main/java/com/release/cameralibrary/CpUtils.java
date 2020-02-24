package com.release.cameralibrary;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Mr.release
 * @create 2020-01-03
 * @Describe
 */
public class CpUtils {

    public static Uri mCropImageUri;
    public static File mTempFile;
    public static final int CAMERA_REQUEST_CODE = 101, PHOTO_REQUEST_CODE = 102, CROP_PHOTO_REUQEST_CODE = 103;
    private static String imageName;
    private static String mImagePath;

    /**
     * 拍照
     */
    public static void camera(Activity activity) {
        String imagePath = getImagePath(activity);
        imageName = System.currentTimeMillis() + ".png";
        mTempFile = getFile(imagePath, imageName);
        Log.i("cyc", "camera:" + mTempFile.toString());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, getUriForFile(activity, mTempFile));
            activity.startActivityForResult(intent, CAMERA_REQUEST_CODE);
        } else {
            Toast.makeText(activity, "请确认已插入SD卡", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 选取图片
     */
    public static void photo(Activity activity) {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {// 如果挂载成功。
            Intent intent = new Intent(Intent.ACTION_PICK, null);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            activity.startActivityForResult(intent, PHOTO_REQUEST_CODE);
        } else {
            Toast.makeText(activity, "请确认已插入SD卡", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 裁剪图片
     *
     * @param uri
     */
    public static void cropPhoto(Activity activity, Uri uri) {
        String imagePath = getImagePath(activity);
        mCropImageUri = Uri.parse("file://" + "/" + imagePath + imageName);
        Log.i("cyc", "cropPhoto:" + mCropImageUri.toString());
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCropImageUri);
        activity.startActivityForResult(intent, CROP_PHOTO_REUQEST_CODE);
    }

    /**
     * 获取手机存储图片路径
     *
     * @param activity
     */
    public static String getImagePath(Activity activity) {
        if (TextUtils.isEmpty(mImagePath))
            mImagePath = Environment.getExternalStorageDirectory() + "/" + activity.getPackageName() + "/cameraImage/";
        return mImagePath;
    }

    /**
     * 设置手机存储图片路径
     *
     * @param path
     * @return
     */
    public void setImagePath(String path) {
        this.mImagePath = path;
    }


    /**
     * 将图片保存到sd卡
     *
     * @param context
     * @param bitmap
     * @return
     */
    public static File getFileFromBitmap(Context context, Bitmap bitmap) {
        String path = Environment.getExternalStorageDirectory() + "/" + context.getPackageName() + "/cameraImage/";
        File file = getFile(path, imageName);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


    /**
     * 图片压缩
     *
     * @param path
     * @return
     */
    public static Bitmap zipFileFromPath(String path) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        opts.inDither = false; // Disable Dithering mode
        opts.inPurgeable = true; // Tell to gc that whether it needs free
        opts.inInputShareable = true; // Which kind of reference will be used to
        BitmapFactory.decodeFile(path, opts);

        final int REQUIRED_SIZE = 400;
        int scale = 1;
        if (opts.outHeight > REQUIRED_SIZE || opts.outWidth > REQUIRED_SIZE) {
            final int heightRatio = Math.round((float) opts.outHeight
                    / (float) REQUIRED_SIZE);
            final int widthRatio = Math.round((float) opts.outWidth
                    / (float) REQUIRED_SIZE);
            scale = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        Log.i("cyc", "缩放倍数：" + scale);
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = scale;
        Bitmap bm = BitmapFactory.decodeFile(path, opts).copy(Bitmap.Config.ARGB_8888, false);
        return bm;
    }

    /**
     * 图片压缩
     *
     * @param context
     * @param uri
     * @return
     */
    public static Bitmap zipFileFromUri(Context context, Uri uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; //只读取图片尺寸
        resolveUri(context, uri, options);

        final int REQUIRED_SIZE = 400;
        int scale = 1;
        if (options.outHeight > REQUIRED_SIZE || options.outWidth > REQUIRED_SIZE) {
            final int heightRatio = Math.round((float) options.outHeight
                    / (float) REQUIRED_SIZE);
            final int widthRatio = Math.round((float) options.outWidth
                    / (float) REQUIRED_SIZE);
            scale = heightRatio < widthRatio ? heightRatio : widthRatio;//
        }

        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;//读取图片内容
        options.inPreferredConfig = Bitmap.Config.ARGB_8888; //根据情况进行修改
        Bitmap bitmap = null;
        try {
            bitmap = resolveUriForBitmap(context, uri, options);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 获取btye[]
     *
     * @param bitmap
     * @return
     */
    public static byte[] getByteData(Bitmap bitmap) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] bytes = outputStream.toByteArray();
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * 获取Bitmap
     *
     * @param context
     * @param uri
     * @return
     */
    public static Bitmap getBitmapFromUri(Context context, Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 获取Uri
     *
     * @param context
     * @param imageFile
     * @return
     */
    public static Uri getUriFromFile(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * 创建文件
     *
     * @param path
     * @param imageName
     * @return
     */
    private static File getFile(String path, String imageName) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return new File(path, imageName);
    }

    /***
     * 获取Uri
     * 用于调取照相机
     * @param context
     * @param file
     * @return
     */
    private static Uri getUriForFile(Context context, File file) {
        if (context == null || file == null) {
            throw new NullPointerException();
        }
        Uri uri;
//        if (Build.VERSION.SDK_INT >= 24) {
//            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".FileProvider", file);
//        } else {
//            uri = Uri.fromFile(file);
//        }
        uri = Uri.fromFile(file);
        return uri;
    }

    private static void resolveUri(Context context, Uri uri, BitmapFactory.Options options) {
        if (uri == null) {
            return;
        }
        String scheme = uri.getScheme();
        if (ContentResolver.SCHEME_CONTENT.equals(scheme) ||
                ContentResolver.SCHEME_FILE.equals(scheme)) {
            InputStream stream = null;
            try {
                stream = context.getContentResolver().openInputStream(uri);
                BitmapFactory.decodeStream(stream, null, options);
            } catch (Exception e) {
                Log.w("resolveUri", "Unable to open content: " + uri, e);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        Log.w("resolveUri", "Unable to close content: " + uri, e);
                    }
                }
            }
        } else if (ContentResolver.SCHEME_ANDROID_RESOURCE.equals(scheme)) {
            Log.w("resolveUri", "Unable to close content: " + uri);
        } else {
            Log.w("resolveUri", "Unable to close content: " + uri);
        }
    }

    private static Bitmap resolveUriForBitmap(Context context, Uri uri, BitmapFactory.Options options) {
        if (uri == null) {
            return null;
        }

        Bitmap bitmap = null;
        String scheme = uri.getScheme();
        if (ContentResolver.SCHEME_CONTENT.equals(scheme) ||
                ContentResolver.SCHEME_FILE.equals(scheme)) {
            InputStream stream = null;
            try {
                stream = context.getContentResolver().openInputStream(uri);
                bitmap = BitmapFactory.decodeStream(stream, null, options);
            } catch (Exception e) {
                Log.w("resolveUriForBitmap", "Unable to open content: " + uri, e);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        Log.w("resolveUriForBitmap", "Unable to close content: " + uri, e);
                    }
                }
            }
        } else if (ContentResolver.SCHEME_ANDROID_RESOURCE.equals(scheme)) {
            Log.w("resolveUriForBitmap", "Unable to close content: " + uri);
        } else {
            Log.w("resolveUriForBitmap", "Unable to close content: " + uri);
        }

        return bitmap;
    }


}
