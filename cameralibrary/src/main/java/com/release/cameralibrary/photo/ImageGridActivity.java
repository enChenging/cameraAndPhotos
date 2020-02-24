package com.release.cameralibrary.photo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.release.cameralibrary.R;
import com.release.cameralibrary.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Mr.release
 * @create 2020-01-03
 * @Describe 图片的目录详细页
 */

public class ImageGridActivity extends AppCompatActivity implements OnClickListener {
    private List<ImageBucket> contentList;
    private RelativeLayout title;

    private List<ImageItem> dataList;
    private GridView gridView;
    private ImageGridAdapter adapter;
    private AlbumHelper helper;
    private Button bt;

    private TextView tv_cancel;
    private TextView tv_content;
    private PopupWindow pop = null;
    private ListView popListView = null;

    private BitmapCache cache = new BitmapCache();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_grid);

        if (Bimp.themeColor != 0)
            StatusBarUtil.setColorNoTranslucent(this, getResources().getColor(Bimp.themeColor));

        helper = AlbumHelper.getHelper();
        helper.init(getApplicationContext());

        contentList = helper.getImagesBucketList(false);
        dataList = new ArrayList<ImageItem>();
        for (int i = 0; i < contentList.size(); i++) {
            dataList.addAll(contentList.get(i).imageList);
        }
        ImageBucket bucket = new ImageBucket();
        bucket.bucketName = "所有图片";
        bucket.imageList = dataList;
        bucket.count = bucket.imageList.size();
        contentList.add(0, bucket);

        initView();

    }

    private void initView() {
        title = (RelativeLayout) findViewById(R.id.title);

        gridView = (GridView) findViewById(R.id.gridview);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new ImageGridAdapter();
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                adapter.notifyDataSetChanged();
            }

        });

        bt = (Button) findViewById(R.id.bt);

        if (Bimp.themeColor != 0) {
            bt.setBackground(getResources().getDrawable(Bimp.themeColor));
            title.setBackground(getResources().getDrawable(Bimp.themeColor));
        }

        int count = Bimp.selectBitmap.size() + Bimp.tempSelectBitmap.size();
        if (Bimp.selectBitmap.size() != count) {
            bt.setText("完成" + "(" + count + ")");
        }
        bt.setOnClickListener(this);

        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(this);

        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_content.setOnClickListener(this);

        View popLayout = getLayoutInflater().inflate(R.layout.poplayout, null);
        pop = new PopupWindow(popLayout, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);
        pop.setBackgroundDrawable(new BitmapDrawable(
                getApplicationContext().getResources(),
                Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)));
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setAnimationStyle(R.style.PopupAnimation1);
        popListView = (ListView) popLayout.findViewById(R.id.lv_content);
        LinearLayout lLlayout = popLayout.findViewById(R.id.popLayout);
        if (Bimp.themeColor != 0) {
            lLlayout.setBackground(getResources().getDrawable(Bimp.themeColor));
        }
        popListView.setAdapter(new MyAdapter());
        popListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                dataList = contentList.get(arg2).imageList;
                adapter.notifyDataSetChanged();
                pop.dismiss();
            }
        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();//取消
        if (id == R.id.tv_cancel) {
            finish();
            overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
            //完成
        } else if (id == R.id.bt) {
            Bimp.selectBitmap.addAll(Bimp.tempSelectBitmap);
            Bimp.tempSelectBitmap.clear();
            setResult(Activity.RESULT_OK);
            finish();
            overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
        } else if (id == R.id.tv_content) {//选择目录
            pop.showAsDropDown(title);
        }
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return contentList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.poplayoutitem, null);
            }

            TextView tv_content = (TextView) view.findViewById(R.id.tv_content);

            tv_content.setText(contentList.get(position).bucketName + "(" + contentList.get(position).count + ")");

            return view;
        }

    }

    class ImageGridAdapter extends BaseAdapter {

        BitmapCache.ImageCallback callback = new BitmapCache.ImageCallback() {
            @Override
            public void imageLoad(ImageView imageView, Bitmap bitmap, Object... params) {
                if (imageView != null && bitmap != null) {
                    String url = (String) params[0];
                    if (url != null && url.equals((String) imageView.getTag())) {
                        ((ImageView) imageView).setImageBitmap(bitmap);
                    }
                }
            }
        };

        @Override
        public int getCount() {
            int count = 0;
            if (dataList != null) {
                count = dataList.size();
            }
            return count;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        class Holder {
            private ImageView iv;
            private ImageView selected;
            private TextView text;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Holder holder;

            if (convertView == null) {
                holder = new Holder();
                convertView = getLayoutInflater().inflate(R.layout.item_image_grid, null);
                holder.iv = (ImageView) convertView.findViewById(R.id.image);
                holder.selected = (ImageView) convertView.findViewById(R.id.isselected);
                holder.text = (TextView) convertView.findViewById(R.id.item_image_grid_text);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            dataList.get(position).isSelected = false;
            if (Bimp.tempSelectBitmap.size() == 0) {
                dataList.get(position).isSelected = false;
            } else {
                for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
                    if (dataList.get(position).imageId.equals(Bimp.tempSelectBitmap.get(i).imageId)) {
                        dataList.get(position).isSelected = true;
                        break;
                    } else {
                        dataList.get(position).isSelected = false;
                    }
                }
            }

            final ImageItem item = dataList.get(position);

            holder.iv.setTag(item.imagePath);
            cache.displayBmp(holder.iv, item.thumbnailPath, item.imagePath, callback);
            if (item.isSelected) {
                holder.selected.setVisibility(View.VISIBLE);
                holder.selected.setImageResource(R.mipmap.icon_data_select);
                holder.text.setBackgroundResource(R.drawable.bgd_relatly_line);
            } else {
                holder.selected.setVisibility(View.INVISIBLE);
                holder.text.setBackgroundColor(0x00000000);
            }
            holder.iv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
//                    Log.i("cyc", "onClick: === " + (Bimp.selectBitmap.size() + Bimp.tempSelectBitmap.size()));

                    if (Bimp.selectBitmap.size() + Bimp.tempSelectBitmap.size() < Bimp.max) {
                        item.isSelected = !item.isSelected;
                        if (item.isSelected) {
                            holder.selected.setVisibility(View.VISIBLE);
                            dataList.get(position).setSelected(true);
                            Bimp.tempSelectBitmap.add(dataList.get(position));
                            holder.selected.setImageResource(R.mipmap.icon_data_select);
                            holder.text.setBackgroundResource(R.drawable.bgd_relatly_line);
                            bt.setText("完成" + "(" + (Bimp.selectBitmap.size() + Bimp.tempSelectBitmap.size()) + ")");
                        } else if (!item.isSelected) {
                            holder.selected.setVisibility(View.INVISIBLE);
                            dataList.get(position).setSelected(false);
                            Bimp.tempSelectBitmap.remove(dataList.get(position));
                            holder.text.setBackgroundColor(0x00000000);
                            bt.setText("完成" + "(" + (Bimp.selectBitmap.size() + Bimp.tempSelectBitmap.size()) + ")");
                        }
                    } else if (Bimp.selectBitmap.size() + Bimp.tempSelectBitmap.size() >= Bimp.max) {
                        if (item.isSelected == true) {
                            item.isSelected = !item.isSelected;
                            holder.selected.setVisibility(View.INVISIBLE);
                            dataList.get(position).setSelected(false);
                            Bimp.selectBitmap.remove(dataList.get(position));
                            if (item.isSelected) {
                                holder.selected.setVisibility(View.VISIBLE);
                                dataList.get(position).setSelected(true);
                                Bimp.tempSelectBitmap.add(dataList.get(position));
                                holder.selected.setImageResource(R.mipmap.icon_data_select);
                                holder.text.setBackgroundResource(R.drawable.bgd_relatly_line);
                                bt.setText("完成" + "(" + (Bimp.selectBitmap.size() + Bimp.tempSelectBitmap.size()) + ")");
                            } else if (!item.isSelected) {
                                holder.selected.setVisibility(View.INVISIBLE);
                                dataList.get(position).setSelected(false);
                                Bimp.tempSelectBitmap.remove(dataList.get(position));
                                holder.text.setBackgroundColor(0x00000000);
                                bt.setText("完成" + "(" + (Bimp.selectBitmap.size() + Bimp.tempSelectBitmap.size()) + ")");
                            }
                        } else {
                            Toast.makeText(ImageGridActivity.this, "最多选择" + Bimp.max + "张图片", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            });

            return convertView;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (pop.isShowing()) {
            pop.dismiss();
        }
        return super.onTouchEvent(event);
    }

}
