package com.release.cameralibrary.photo;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.release.cameralibrary.R;
import com.release.cameralibrary.Utils;


/**
 * @author Mr.release
 * @create 2019/7/18
 * @Describe
 */
public class GridAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private boolean shape;
    private Context mContext;

    public boolean isShape() {
        return shape;
    }

    public void setShape(boolean shape) {
        this.shape = shape;
    }

    public GridAdapter(Context context) {
        mContext = context;
        inflater = LayoutInflater.from(context);
    }

    public void update() {
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (Bimp.selectBitmap.size() == 3) {
            return 3;
        }
        return (Bimp.selectBitmap.size() + 1);
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_published_grida, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.item_grida_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == Bimp.selectBitmap.size()) {
            Glide.with(mContext)
                    .asDrawable()
                    .load(ContextCompat.getDrawable(mContext, R.mipmap.icon_add_photo))
//                    .centerCrop()
                    .into(holder.image);
            if (position == Bimp.max) {
                holder.image.setVisibility(View.GONE);
            }
        } else {
            if (Bimp.selectBitmap.get(position).getImagePath().contains("http"))
                Glide.with(mContext)
                        .load(Bimp.selectBitmap.get(position).getImagePath())
//                        .centerCrop()
                        .into(holder.image);
            else
                Glide.with(mContext)
                        .asBitmap()
                        .load(Utils.decodeFile(Bimp.selectBitmap.get(position).getImagePath()))
//                        .centerCrop()
                        .into(holder.image);
        }
        return convertView;
    }

    public class ViewHolder {
        public ImageView image;
    }
}
