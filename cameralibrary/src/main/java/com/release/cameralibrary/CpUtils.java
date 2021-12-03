package com.release.cameralibrary;

import static android.app.Activity.RESULT_OK;
import static com.yalantis.ucrop.util.FileUtils.getDataColumn;
import static com.yalantis.ucrop.util.FileUtils.isDownloadsDocument;
import static com.yalantis.ucrop.util.FileUtils.isExternalStorageDocument;
import static com.yalantis.ucrop.util.FileUtils.isMediaDocument;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.release.cameralibrary.photo.Bimp;
import com.release.cameralibrary.photo.GridAdapter;
import com.release.cameralibrary.photo.GridViewNoScroll;
import com.release.cameralibrary.photo.ImageGridActivity;
import com.release.cameralibrary.photo.ImageItem;
import com.release.cameralibrary.photo.PhotoActivity;
import com.yalantis.ucrop.UCrop;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Mr.release
 * @create 2020-01-03
 * @Describe
 */
public class CpUtils {

    public static File mTempFile;
    public static Uri mCameraUri;
    public static Uri mPhotoUri;
    public static boolean mIsCamera;
    public static final int CAMERA_REQUEST_CODE = 101, PHOTO_REQUEST_CODE = 102, CROP_PHOTO_REUQEST_CODE = 103;
    private static String imageName;
    private static String mImagePath;
    // 是否是Android 10以上手机
    private static boolean isAndroidQ = Build.VERSION.SDK_INT >= 29;

    /**
     * 初始化配置
     */
    public static void init(int max, int themeColor, int btnColor) {
        Bimp.selectBitmap.clear();// 清空图册
        Bimp.max = max;// 初始化最大选择数
        Bimp.themeColor = themeColor;//设置主题风格
        Bimp.btnColor = btnColor;//设置按钮颜色
    }

    public static void init(int max, int themeColor) {
        Bimp.selectBitmap.clear();// 清空图册
        Bimp.max = max;// 初始化最大选择数
        Bimp.themeColor = themeColor;//设置主题风格
    }

    /**
     * 初始化Gridview配置
     *
     * @param gridview
     * @param context
     */
    public static GridAdapter initGridAdapter(Context context, GridViewNoScroll gridview) {
        gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        GridAdapter adapter = new GridAdapter(context);
        gridview.setAdapter(adapter);
        return adapter;
    }

    /**
     * 拍照
     */
    public static void camera(Activity activity) {
        imageName = System.currentTimeMillis() + ".png";
        if (isAndroidQ) {
            mCameraUri = createImageUri(activity);
        } else {
            mTempFile = getFile(getImagePath(activity), imageName);
            mCameraUri = getUriForFile(mTempFile);
        }
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraUri);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            activity.startActivityForResult(intent, CAMERA_REQUEST_CODE);
        } else {
            Toast.makeText(activity, "请确认已插入SD卡", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 选取单张图片
     */
    public static void photo(Activity activity) {
        imageName = System.currentTimeMillis() + ".png";
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {// 如果挂载成功。
            Intent intent = new Intent(Intent.ACTION_PICK, null);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            activity.startActivityForResult(intent, PHOTO_REQUEST_CODE);
        } else {
            Toast.makeText(activity, "请确认已插入SD卡", Toast.LENGTH_SHORT).show();
        }
    }

    public static void photo2(Activity activity) {
        imageName = System.currentTimeMillis() + ".png";
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {// 如果挂载成功。
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activity.startActivityForResult(intent, PHOTO_REQUEST_CODE);
        } else {
            Toast.makeText(activity, "请确认已插入SD卡", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 选取多张图片
     */
    public static void galleryPhoto(Activity activity) {
        activity.startActivity(new Intent(activity, ImageGridActivity.class));
        activity.overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
    }

    /**
     * 查看单张图片
     */
    public static void lookPhoto(Context context, int position) {
        Intent intent = new Intent(context, PhotoActivity.class);
        intent.putExtra("ID", position);
        context.startActivity(intent);
    }

    /**
     * 裁剪图片
     *
     * @param uri
     */
    public static void cropPhoto(Activity activity, File file, Uri uri) {
//        String imagePath = getImagePath(activity);
//        mCropImageUri = Uri.parse("file://" + "/" + imagePath + imageName);
//        Log.i("cyc", "cropPhoto:" + mCropImageUri.toString());
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        if (Build.MANUFACTURER.equals("HUAWEI")) {
            intent.putExtra("aspectX", 9998);
            intent.putExtra("aspectY", 9999);
        } else {
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
        }
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
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
     * /storage/emulated/0/com.release.testcameraandphotos/cameraImage/1638517212921.png
     *
     * @param bitmap
     * @return
     */
    public static File getFileFromBitmap(Bitmap bitmap, String imagePath) {
        String path = imagePath.substring(0, imagePath.lastIndexOf(File.separator) + 1);
        String imageName = imagePath.substring(imagePath.lastIndexOf(File.separator) + 1);
        Log.i("cyc", "path: " + path + "   imageName:" + imageName);
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
     * 压缩图片（质量压缩）
     *
     * @param bitmap
     */
    public static File compressImage1(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于500kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            long length = baos.toByteArray().length;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        String filename = format.format(date);
        File file = new File(Environment.getExternalStorageDirectory(), filename + ".png");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            try {
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        recycleBitmap(bitmap);
        return file;
    }

    private static void recycleBitmap(Bitmap... bitmaps) {
        if (bitmaps == null) {
            return;
        }
        for (Bitmap bm : bitmaps) {
            if (null != bm && !bm.isRecycled()) {
                bm.recycle();
            }
        }
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
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
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
     * createImageUri
     *
     * @param context
     * @return
     */
    private static Uri createImageUri(Context context) {
        //imagePath:/external/images/media/146139
        String status = Environment.getExternalStorageState();
        //判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        } else {
            return context.getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, new ContentValues());
        }
    }

    /**
     * 获取根据Uri获取文件存储地址
     *
     * @param context
     * @param contentUri
     * @return
     */
    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /***
     * 获取Uri
     * 用于调取照相机
     *
     * @param file
     * @return
     */
    private static Uri getUriForFile(File file) {
        if (file == null) {
            throw new NullPointerException();
        }
        return Uri.fromFile(file);
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


    public static int getScreenWidth(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }

    @SuppressLint({"NewApi"})
    public static String getPath(Context context, Uri uri) {
        boolean isKitKat = Build.VERSION.SDK_INT >= 19;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            String docId;
            String[] split;
            String type;
            if (isExternalStorageDocument(uri)) {
                docId = DocumentsContract.getDocumentId(uri);
                split = docId.split(":");
                type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else {
                if (isDownloadsDocument(uri)) {
                    docId = DocumentsContract.getDocumentId(uri);
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                    return getDataColumn(context, contentUri, (String) null, (String[]) null);
                }

                if (isMediaDocument(uri)) {
                    docId = DocumentsContract.getDocumentId(uri);
                    split = docId.split(":");
                    type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    String selection = "_id=?";
                    String[] selectionArgs = new String[]{split[1]};
                    return getDataColumn(context, contentUri, "_id=?", selectionArgs);
                }
            }
        } else {
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, (String) null, (String[]) null);
            }

            if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }

        return null;
    }


    public static File onActivityResult(int requestCode, int resultCode, Intent data, Activity activity, int type, ImageView imageView) {
        File mFile = null;
        if (resultCode != RESULT_OK) return null;
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                mIsCamera = true;
                Bitmap realBitmap;
                String realPath;
                if (isAndroidQ) {
                    realBitmap = zipFileFromUri(activity, mCameraUri);
                    realPath = getRealPathFromUri(activity, mCameraUri);
                } else {
                    realBitmap = zipFileFromPath(mTempFile.getPath());
                    realPath = mTempFile.getPath();
                }
                Log.i("cyc", "cameraUri: " + mCameraUri);
                Log.i("cyc", "realPath: " + realPath);
                File realFile = getFileFromBitmap(realBitmap, realPath);
                switch (type) {
                    case 1:
                        //拍照 压缩图片
                        imageView.setImageBitmap(realBitmap);
                        mFile = realFile;
                        break;
                    case 2:
                        //拍照 自带裁剪图片
                        if (isAndroidQ) {
                            cropPhoto(activity, realFile, mCameraUri);
                        } else {
                            cropPhoto(activity, realFile, getUriFromFile(activity, mTempFile));
                        }
                        break;
                    case 4:
                        //拍照 (九宫格图，图片加载时统一做了压缩)
                        ImageItem takePhoto = new ImageItem();
                        takePhoto.setImagePath(realPath);//Bimp.selectBitmap.get(0).getBitmap()获取使用时时做了压缩
                        Bimp.selectBitmap.add(takePhoto);
                        break;
                    case 5:
                        //拍照 三方ucrop裁剪图片2
                        ImageCropManage.startCropActivity(activity, new File(realPath).getPath());
                        break;
                }
                break;
            case PHOTO_REQUEST_CODE:
                mIsCamera = false;
                mPhotoUri = data.getData();
                Log.i("cyc", "photoUri: " + mPhotoUri);
                switch (type) {
                    case 1:
                        //图册 压缩图片
                        Bitmap bitmap = zipFileFromUri(activity, mPhotoUri);
                        imageView.setImageBitmap(bitmap);
                        mFile = getFileFromBitmap(bitmap, getRealPathFromUri(activity, mPhotoUri));
                        break;
                    case 2:
                        //图册 自带裁剪图片
                        String path = getPath(activity, mPhotoUri);
                        cropPhoto(activity, new File(path), mPhotoUri);
                        break;
                    case 5:
                        //图册 三方裁剪图片
                        path = getPath(activity, mPhotoUri);
                        ImageCropManage.startCropActivity(activity, new File(path).getPath());
                        break;
                }
                break;
            case CROP_PHOTO_REUQEST_CODE:
                //自带裁剪图片完成
                Uri uri;
                if (mIsCamera)
                    uri = mCameraUri;
                else
                    uri = mPhotoUri;
                try {
                    Bitmap bitmap = getBitmapFromUri(activity, uri);
                    imageView.setImageBitmap(bitmap);
                    mFile = getFileFromBitmap(bitmap, getRealPathFromUri(activity, uri));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case UCrop.REQUEST_CROP:
                //三方裁剪图片完成
                final Uri resultUri = UCrop.getOutput(data);
                Bitmap bitmap = zipFileFromUri(activity, resultUri);
                imageView.setImageBitmap(bitmap);
                Log.i("cyc", "resultUri: " + resultUri.getPath());
                mFile = getFileFromBitmap(bitmap, resultUri.getPath());
                break;
        }

        return mFile;
    }

}
