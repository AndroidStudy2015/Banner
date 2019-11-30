package com.view.custom.banner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.view.custom.banner.adapter.BannerAdapter;
import com.view.custom.bannerlib.view.BannerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<Bitmap> bannerBitmaps = new ArrayList<>();
    private BannerView mBannerView;
    private LinearLayout mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBannerView = findViewById(R.id.banner_view);
        mIndicator = findViewById(R.id.ll_indicator);
        mBannerView.setOffscreenPageLimit(3);
        bannerBitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.img3));
        bannerBitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.img1));
        bannerBitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.img2));
        bannerBitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.img3));
        bannerBitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.img1));
        BannerAdapter imgAdapter = new BannerAdapter(this);
        imgAdapter.setBitmaps(bannerBitmaps);
        mBannerView.setAdapter(imgAdapter);


        for (int i = 0; i < 3; i++) {
            View view = new View(this);
            view.setBackgroundColor(Color.WHITE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, 10);
            params.leftMargin = 10;
            params.rightMargin = 10;
            view.setLayoutParams(params);
            mIndicator.addView(view);

        }


        mBannerView.setOnBannerChangeListener(new BannerView.OnBannerChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < mIndicator.getChildCount(); i++) {
                    View childAt = mIndicator.getChildAt(i);
                    if (childAt != null) {
                        Log.e("ccc", "selected" + position+"i:"+i);

                        if (i == position - 1) {
                            childAt.setBackgroundColor(Color.BLUE);
                        } else {
                            childAt.setBackgroundColor(Color.WHITE);

                        }

                    }

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        mBannerView.startAutoPlay();

    }


    @Override
    protected void onPause() {
        super.onPause();
        mBannerView.stopAutoPlay();

    }
}
