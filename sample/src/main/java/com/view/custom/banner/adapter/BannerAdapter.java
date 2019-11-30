package com.view.custom.banner.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.view.custom.banner.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述当前版本功能
 *
 * @Project: Banner
 * @author: cjx
 * @date: 2019-11-26 17:09  星期二
 */
public class BannerAdapter extends PagerAdapter {
    private Context mContext;

    public BannerAdapter(Context context) {
        mContext = context;
    }

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
        View view = View.inflate(container.getContext(), R.layout.item_banner, null);
        ImageView imageView = view.findViewById(R.id.iv);
        imageView.setImageBitmap(mBitmaps.get(position));
        imageView.setBackgroundColor(Color.RED);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        container.addView(view);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("qweccc",position+"");
            }
        });

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
