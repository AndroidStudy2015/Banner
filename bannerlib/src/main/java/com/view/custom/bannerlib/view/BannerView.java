package com.view.custom.bannerlib.view;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.view.custom.bannerlib.other.BannerScroller;

import java.lang.reflect.Field;

/**
 * 描述当前版本功能
 * 这个bannerview就是一个普通的ViewPager
 * 只不过你传递进来的页面必须是满足CABCA模式，这样会实现循环播放
 * 另外反射传递一个新的自定义的scroller，实现控制换页面的时长
 * 另外一个task+postDelay实现自动轮播
 * 注意外界回调pos从1开始
 *
 * @Project: BannerView
 * @author: cjx
 * @date: 2019-11-26 16:57  星期二
 */
public class BannerView extends ViewPager {
    private static final String TAG = "BannerView";
    private int count = 0;
    private BannerScroller mScroller;
    int scrollDuration = 500;
    int interval = 2000;
    boolean autoPlay = false;
    Handler handler = new Handler();


    public BannerView(@NonNull Context context) {
        this(context, null);
    }


    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

    }


    private void init() {
        setBannerScrollDuration(scrollDuration);
        addOnPageChangeListener();
    }


    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            int currentPosition = (getCurrentItem() + 1);
            setCurrentItem(currentPosition);
            handler.postDelayed(task, interval);

        }
    };


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (autoPlay) {
            int action = ev.getAction();
            switch (action) {
                // 手指抬起，继续轮播
                case MotionEvent.ACTION_UP:
                    startAutoPlay();
                    break;
                // 手指触碰到轮播图，停止轮播
                default:
                    handler.removeCallbacks(task);
                    break;
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    int lastSelectedPos = 0;

    private void addOnPageChangeListener() {
        setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                // 随着手指的拖拽一直在执行
                // 关键是position代表什么：
                // 1. 从0滑动到1：position一直是0，（positionOffset，positionOffsetPixels递增为viewpager的宽度），
                //              最后变为1的，（positionOffset，positionOffsetPixels此时归为0）


                // 2. 从1滑动到2：position一直是1，（positionOffset，positionOffsetPixels递增为viewpager的宽度），
                //               最后变为2的，（positionOffset，positionOffsetPixels此时归为0）


                // 3. 从1滑动到0：position一直是0（positionOffset，positionOffsetPixels递减为0）
                // 4. 从2滑动到1：position一直是1（positionOffset，positionOffsetPixels递减为0）

                Log.e(TAG,
                        "onPageScrolled:===>position:" + position +
                                "   positionOffset:" + positionOffset +
                                "   positionOffsetPixels:" + positionOffsetPixels);
                if (mOnBannerChangeListener != null) {
                    mOnBannerChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                // 当所选页面变化了才会回调，并且紧跟着onPageScrollStateChanged的state=0（松手一刹那）回调之后，并且此时此刻页面没有完全滑动到所选页面
                // 假如你现在是0，当你滑动了一少半后，又回到0，该回调不发生
                Log.e(TAG, "onPageSelected===>position:" + position);

                if (mOnBannerChangeListener != null) {
                    // 替身的位置需要处理
                    if ((position == count - 1)) {
                        position = 1;
                    } else if (position == 0) {
                        position = count - 2;
                    }

                    if (lastSelectedPos != position) {
                        mOnBannerChangeListener.onPageSelected(position);
                        lastSelectedPos = position;
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // state==1代表SCROLL_STATE_DRAGGING，开始滑动了，仅仅回调一次，代表开始拖拽
                if (state == 1) {
                    Log.e(TAG, "StateChanged===>state:SCROLL_STATE_DRAGGING" + state);
                    BorderAdjustment();
                }

                // state==2代表SCROLL_STATE_SETTLING，手指松开了，仅仅回调一次
                if (state == 2) {
                    Log.e(TAG, "StateChanged===>state:SCROLL_STATE_SETTLING" + state);
                }

                // state==0代表SCROLL_STATE_IDLE，滑动结束，仅仅回调一次
                if (state == 0) {
                    BorderAdjustment();
                    Log.e(TAG, "StateChanged===>state:SCROLL_STATE_IDLE" + state);

                }

                if (mOnBannerChangeListener != null) {
                    mOnBannerChangeListener.onPageScrollStateChanged(state);
                }
            }
        });
    }

    /**
     * 边界调整：
     * CABCA 模型（ABC为真正的数据，前面的C和最后的A：是为了凑出无限循环的效果，添加的辅助数据）
     * 思路：
     * 1. 第一次显示A（pos=1）
     * 2. 当往后滑到第二个A（pos=4）时候，突变到第一个A(pos=1)
     * 3. 当往前滑动到第一个C(pos=0)时候，突变到第二个C(pos=3)
     * 4. 注意：该方法要在onPageScrollStateChanged里
     * 开始滑动使用一次（例如：如果现在pos=4，开始滑动，立即pos=1，你会滑出pos=2，如果不写，迅速滑动时候，会卡顿,）
     * 结束滑动使用一次（1.让角标正常，不写的话会点击出的pos有问题，你会点在替身上，这样写的话你没有机会触摸前后的替身view
     * 2.让超出边界的CurrentItem归为正确的状态，不然会停止自动轮播，因为在task里的getCurrentItem() + 1越界了）
     */
    private void BorderAdjustment() {
        if (getCurrentItem() == count - 1) {
            setCurrentItem(1, false);
            Log.e(TAG, getCurrentItem() + "cur---end");

        } else if (getCurrentItem() == 0) {
            setCurrentItem(count - 2, false);
            Log.e(TAG, getCurrentItem() + "cur----start");

        } else {
            Log.e(TAG, getCurrentItem() + "cur---middle");

        }
    }

    @Override
    public void setAdapter(@Nullable PagerAdapter adapter) {
        super.setAdapter(adapter);
        if (adapter != null) {
            count = adapter.getCount();
            if (count >= 1) {
                setCurrentItem(1);//默认选中（pos=1），因为前一个（pos=0）是最后一个的替身
            }

        }
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void startAutoPlay() {
        autoPlay = true;
        handler.removeCallbacks(task);
        handler.postDelayed(task, interval);
    }

    public void stopAutoPlay() {
        autoPlay = false;
        handler.removeCallbacks(task);

    }


    /**
     * 设置viewpager滑动一屏所用的时间
     *
     * @param scrollDuration
     */
    public void setBannerScrollDuration(int scrollDuration) {
        try {
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            mScroller = new BannerScroller(getContext());
            mScroller.setDuration(scrollDuration);
            mField.set(this, mScroller);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public interface OnBannerChangeListener {
        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        void onPageSelected(int position);

        void onPageScrollStateChanged(int state);

    }

    private OnBannerChangeListener mOnBannerChangeListener;

    public void setOnBannerChangeListener(OnBannerChangeListener onBannerChangeListener) {
        mOnBannerChangeListener = onBannerChangeListener;
    }
}
