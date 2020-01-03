package com.release.cameralibrary.photo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Bimp {
	public static int max = 0;
	public static int themeColor = 0;

	public static List<ImageItem> selectBitmap = new LinkedList<ImageItem>(); //已经选择的图片
	public static List<ImageItem> tempSelectBitmap = new LinkedList<ImageItem>(); //选择的图片的临时列表

	public static Bitmap revitionImageSize(String path) throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		int i = 0;
		Bitmap bitmap = null;
		while (true) {
			if ((options.outWidth >> i <= 1000) && (options.outHeight >> i <= 1000)) {
				in = new BufferedInputStream(new FileInputStream(new File(path)));
				options.inSampleSize = (int) Math.pow(2.0D, i);//2的i次方
				options.inJustDecodeBounds = false;//分配内存空间
				bitmap = BitmapFactory.decodeStream(in, null, options);
				break;
			}
			i += 1;

		}
		return bitmap;
	}

}
