package com.release.cameralibrary.photo;

import android.graphics.Bitmap;

import com.release.cameralibrary.CpUtils;

import java.io.Serializable;

public class ImageItem implements Serializable {
    public String imageId;
    public String thumbnailPath;
    public String imagePath;
    private Bitmap bitmap;
    public boolean isSelected = false;

    public ImageItem() {
    }

    public ImageItem(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public Bitmap getBitmap() {
        if (bitmap == null) {
            bitmap = CpUtils.zipFileFromPath(imagePath);
        }
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public String toString() {
        return "ImageItem{" +
                "imageId='" + imageId + '\'' +
                ", thumbnailPath='" + thumbnailPath + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", bitmap=" + bitmap +
                ", isSelected=" + isSelected +
                '}';
    }
}
