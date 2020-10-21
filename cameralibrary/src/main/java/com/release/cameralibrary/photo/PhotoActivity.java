package com.release.cameralibrary.photo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.release.cameralibrary.R;
import com.release.cameralibrary.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * 查看已选照片的页
 */

public class PhotoActivity extends AppCompatActivity {

    private ArrayList<View> listViews = null;
    private ViewPager pager;
    private MyPageAdapter adapter;
    private int count;
    RelativeLayout photo_relativeLayout;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        photo_relativeLayout = (RelativeLayout) findViewById(R.id.photo_relativeLayout);
        if (Bimp.themeColor != 0 && Bimp.themeColor != R.color.white) {
            StatusBarUtil.setColorNoTranslucent(this, getResources().getColor(Bimp.themeColor));
            photo_relativeLayout.setBackgroundColor(getResources().getColor(Bimp.themeColor));
        }
        Button photo_bt_exit = (Button) findViewById(R.id.photo_bt_exit);
        photo_bt_exit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                finish();
            }
        });
        Button photo_bt_del = (Button) findViewById(R.id.photo_bt_del);
        photo_bt_del.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (listViews.size() == 1) {
                    Bimp.selectBitmap.clear();
                    finish();
                } else {
                    Bimp.selectBitmap.remove(count);
                    pager.removeAllViews();
                    listViews.remove(count);
                    adapter.setListViews(listViews);
                    adapter.notifyDataSetChanged();
                }

            }
        });

        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setOffscreenPageLimit(1);
        pager.setOnPageChangeListener(pageChangeListener);
        for (int i = 0; i < Bimp.selectBitmap.size(); i++) {
            Log.i("cyc", "Bimp.selectBitmap.size():" + Bimp.selectBitmap.size());
            initListViews(Bimp.selectBitmap.get(i));
        }

        adapter = new MyPageAdapter(listViews);
        pager.setAdapter(adapter);
        Intent intent = getIntent();
        int id = intent.getIntExtra("ID", 0);
        pager.setCurrentItem(id);
    }

    private void initListViews(ImageItem imageItem) {
        if (listViews == null)
            listViews = new ArrayList<View>();
        ImageView img = new ImageView(this);
        img.setBackgroundColor(0xff000000);
        if (imageItem.getImagePath().contains("http"))
            Glide.with(this)
                    .load(imageItem.getImagePath())
                    .into(img);
        else {
            Bitmap bitmap = imageItem.getBitmap();
            img.setImageBitmap(bitmap);
        }
        img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        listViews.add(img);
    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

        public void onPageSelected(int arg0) {
            count = arg0;
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        public void onPageScrollStateChanged(int arg0) {

        }
    };

    class MyPageAdapter extends PagerAdapter {

        private ArrayList<View> listViews;

        private int size;

        public MyPageAdapter(ArrayList<View> listViews) {
            this.listViews = listViews;
            size = listViews == null ? 0 : listViews.size();
        }

        public void setListViews(ArrayList<View> listViews) {
            this.listViews = listViews;
            size = listViews == null ? 0 : listViews.size();
        }

        public int getCount() {
            return size;
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(listViews.get(arg1 % size));
        }

        public void finishUpdate(View arg0) {
        }

        public Object instantiateItem(View arg0, int arg1) {
            try {
                ((ViewPager) arg0).addView(listViews.get(arg1 % size), 0);

            } catch (Exception e) {
            }
            return listViews.get(arg1 % size);
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

    }
}
