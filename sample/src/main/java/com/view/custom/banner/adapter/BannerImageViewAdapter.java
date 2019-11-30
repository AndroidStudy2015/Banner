package com.view.custom.banner.adapter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述当前版本功能
 *
 * @Project: Banner
 * @author: cjx
 * @date: 2019-11-26 17:09  星期二
 */
public class BannerImageViewAdapter extends PagerAdapter {
    private List<Bitmap> mBitmaps = new ArrayList<>();

    public void setBitmaps(List<Bitmap> bitmaps) {
        mBitmaps = bitmaps;
    }

    @Override
    public int getCount() {
        return mBitmaps.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        ImageView imageView = new ImageView(container.getContext());
        imageView.setImageBitmap(mBitmaps.get(position));
        imageView.setBackgroundColor(Color.RED);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        container.addView(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("onclick",position+"");
            }
        });

        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
