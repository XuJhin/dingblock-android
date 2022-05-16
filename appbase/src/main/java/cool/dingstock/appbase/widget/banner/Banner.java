package cool.dingstock.appbase.widget.banner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cool.dingstock.appbase.R;
import cool.dingstock.appbase.imageload.GlideHelper;
import cool.dingstock.appbase.entity.bean.home.HomeBanner;
import cool.dingstock.lib_base.thread.ThreadPoolHelper;
import cool.dingstock.lib_base.util.CollectionUtils;
import cool.dingstock.lib_base.util.Logger;
import cool.dingstock.lib_base.util.SizeUtils;


/**
 * Created by wangshuwen on 2017/10/21.
 */

public class Banner extends RelativeLayout {

    public static enum ShowType {
        Type1,
        Type2
    }

    /**
     * 轮播的间隔秒数
     */
    private static final int SHUFFLING_TIME = 5000;

    private Double BANNER_PERCENT = 0.24;

    private View rootView;

    private ViewPager bannerPager;
    private View cardView;
    private LinearLayout pointLayer;

    private List<HomeBanner> mHomeBannerList;
    /**
     * 记录上一次点的位置，默认为0
     */
    private int previousPosition;
    /**
     * 最小轮播的数量
     */
    private static final int MINI_COUNT = 2;

    private ScheduledExecutorService mScheduledPoolExecutor;

    private BannerItemClickListener mListener;


    private int circleDrawableRes = R.drawable.common_banner_dot_selector;

    private int circleMargeBottom = 6;


    private ShowType showType = ShowType.Type1;

    public void setBANNER_PERCENT(Double BANNER_PERCENT) {
        this.BANNER_PERCENT = BANNER_PERCENT;
    }

    public interface BannerItemClickListener {
        void onItemClick(String linkedUrl, int index);
    }

    public Banner(@NonNull Context context) {
        this(context, null);
    }

    public Banner(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Banner(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initListener();
    }

    public void setCircleDrawableRes(int circleDrawableRes) {
        this.circleDrawableRes = circleDrawableRes;
    }

    public void setCircleMargeBottom(int circleMargeBottom) {
        this.circleMargeBottom = circleMargeBottom;
    }

    public void setShowType(ShowType showType) {
        this.showType = showType;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Logger.i("banner received ACTION_DOWN");
                stopShuffling();
                break;
            case MotionEvent.ACTION_UP:
                Logger.i("banner received ACTION_UP");
                startShuffling();
                break;
            case MotionEvent.ACTION_CANCEL:
                Logger.w("banner received ACTION_CANCEL");
                startShuffling();
                break;
            default:
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Logger.d(" Banner detached to the window");
        stopShuffling();
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Logger.d(" Banner attach to the window");
        startShuffling();
    }

    private Runnable mShufflingTask = new Runnable() {
        @Override
        public void run() {
            ThreadPoolHelper.getInstance().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (null == bannerPager) {
                        Logger.e("mShufflingTask bannerPager is null");
                        return;
                    }
                    bannerPager.setCurrentItem(bannerPager.getCurrentItem() + 1, true);
                }
            });
        }
    };


    private void initView(Context context) {
        rootView = LayoutInflater.from(context).inflate(R.layout.common_layout_banner, this, false);
        bannerPager = rootView.findViewById(R.id.common_banner);
        pointLayer = rootView.findViewById(R.id.common_banner_point_layer);
        cardView = rootView.findViewById(R.id.card_view);
        updateView();
        addView(rootView);
    }

    public void updateView() {
        ViewGroup.LayoutParams params = rootView.getLayoutParams();
        int bannerWidth = SizeUtils.getWidth() - SizeUtils.dp2px(24);
        if (showType == ShowType.Type1) {
            params.height = (int) (bannerWidth * BANNER_PERCENT);
        } else {
            params.height = (int) (bannerWidth * BANNER_PERCENT) + SizeUtils.dp2px(circleMargeBottom + 7);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) cardView.getLayoutParams();
            layoutParams.bottomMargin = SizeUtils.dp2px(21);
            cardView.setLayoutParams(layoutParams);
        }
        rootView.setLayoutParams(params);

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) pointLayer.getLayoutParams();
        layoutParams.bottomMargin = SizeUtils.dp2px(circleMargeBottom);
        pointLayer.setLayoutParams(layoutParams);
    }


    /**
     * 配置图片的风格
     */
    private void buildImageStyle(ImageView imageView, String imageUrl) {
        GlideHelper.INSTANCE.loadRadiusImage(imageUrl, imageView, getContext(), 8);
    }

    /**
     * 构架viewPager的子view
     */
    private void buildBannerViews() {
        if (null == mHomeBannerList) {
            Logger.e("Banner mHomeBannerList null");
            return;
        }
        List<View> viewList = new ArrayList<>();
        for (int i = 0; i < mHomeBannerList.size(); i++) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.common_item_banner, null);
            ImageView imageView = view.findViewById(R.id.common_item_banner_image);
            final int index = i;
            buildImageStyle(imageView, mHomeBannerList.get(index).getImageUrl());
            view.setOnClickListener(v -> callBackItemClick(mHomeBannerList.get(index).getTargetUrl(), index));
            viewList.add(view);
            //两张banner的时候出现空白页
            if (i == mHomeBannerList.size() - 1 && viewList.size() == 1) {
                i = -1;
            }
            if (i == mHomeBannerList.size() - 1 && viewList.size() == 2) {
                i = -1;
            }
        }
        bannerPager.setAdapter(new CommonBannerAdapter(viewList));
        bannerPager.setCurrentItem(previousPosition);
    }


    private void initListener() {
        bannerPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                // 获取新的位置
                int newPosition = position % mHomeBannerList.size();
                // 消除上一次的状态
                int realPointPosition = previousPosition % mHomeBannerList.size();
                pointLayer.getChildAt(realPointPosition).setEnabled(false);
                // 设置当前的状态点“点”
                pointLayer.getChildAt(newPosition).setEnabled(true);
                // 记录位置
                previousPosition = newPosition;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

        });
    }

    private void callBackItemClick(String linkedUrl, int index) {
        if (null != mListener) {
            mListener.onItemClick(linkedUrl, index);
        }
    }


    public void setBannerItemClickListener(BannerItemClickListener listener) {
        this.mListener = listener;
    }


    public void setData(List<HomeBanner> homeBannerList) {
        if (CollectionUtils.isEmpty(homeBannerList)) {
            Logger.e("Banner setData dataList is empty");
            return;
        }
        releaseAllView();
        this.mHomeBannerList = homeBannerList;
        //rv 会上下滑动会重复调用  为了保证上次一的position
        previousPosition = (Integer.MAX_VALUE / 2) - (Integer.MAX_VALUE / 2 % mHomeBannerList.size())
                + previousPosition;
        //init  PointLayer
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(SizeUtils.dp2px(6),
                SizeUtils.dp2px(6));
        params.leftMargin = SizeUtils.dp2px(3);
        params.rightMargin = SizeUtils.dp2px(3);
        for (int index = 0; index < mHomeBannerList.size(); index++) {
            View point = new View(getContext());
            point.setBackgroundResource(circleDrawableRes);
            point.setLayoutParams(params);
            int realPointPosition = previousPosition % mHomeBannerList.size();
            if (index == realPointPosition) {
                point.setEnabled(true);
            } else {
                point.setEnabled(false);
            }
            pointLayer.addView(point, params);
            //开始自动轮播
            startShuffling();
        }
        buildBannerViews();
    }


    /**
     * 开始轮播
     */
    public void startShuffling() {
        stopShuffling();
        Logger.d("banner startShuffling is called ");
        if (CollectionUtils.isEmpty(mHomeBannerList)
                || mHomeBannerList.size() < MINI_COUNT) {
            Logger.e("banner dataList is not Conform so can not shuffling");
            return;
        }
        mScheduledPoolExecutor = Executors.newSingleThreadScheduledExecutor();
        mScheduledPoolExecutor.scheduleAtFixedRate(mShufflingTask,
                SHUFFLING_TIME,
                SHUFFLING_TIME,
                TimeUnit.MILLISECONDS);
    }


    /**
     * 停止轮播
     */
    public void stopShuffling() {
        Logger.d("banner stopShuffling is called ");
        if (null == mScheduledPoolExecutor) {
            Logger.i("stopShuffling mScheduledPoolExecutor is null do nothing");
            return;
        }
        mScheduledPoolExecutor.shutdownNow();
        mScheduledPoolExecutor = null;
    }

    /**
     * 释放banner数据
     */
    public void releaseAllView() {
        if (null == bannerPager) {
            return;
        }
        bannerPager.setAdapter(new CommonBannerAdapter(null));
        pointLayer.removeAllViews();
        stopShuffling();
    }

}
