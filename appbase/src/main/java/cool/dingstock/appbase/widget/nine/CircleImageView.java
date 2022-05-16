package cool.dingstock.appbase.widget.nine;

import android.content.Context;
import android.util.AttributeSet;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cool.dingstock.appbase.R;
import cool.dingstock.imagepicker.views.roundImageView.RoundImageView;
import cool.dingstock.imagepre.ImagePreview;
import cool.dingstock.imagepre.bean.ImageInfo;
import cool.dingstock.appbase.entity.bean.circle.CircleImageBean;


public class CircleImageView extends NineGridLayout {
    protected static final int MAX_W_H_RATIO = 3;

    public CircleImageView(Context context) {
        super(context);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected boolean displayOneImage(RoundImageView imageView, CircleImageBean imageEntity, int parentWidth) {
        return true;
    }

    @Override
    protected void displayImage(RoundImageView imageView, CircleImageBean url) {
        Glide.with(getContext())
                .load(url.getThumbnail())
                .into(imageView);
    }

    @Override
    protected void onClickImage(int position, CircleImageBean url, List<CircleImageBean> urlList) {
        ArrayList<ImageInfo> preImageList = new ArrayList<>();
        if (urlList.isEmpty()) {
            return;
        }
        for (CircleImageBean circleImageBean : urlList) {
            ImageInfo imageInfo = new ImageInfo();
            imageInfo.setThumbnailUrl(circleImageBean.getThumbnail());
            imageInfo.setOriginUrl(circleImageBean.getUrl());
            preImageList.add(imageInfo);
        }
        ImagePreview.getInstance()
                .setContext(getContext())
                .setIndex(position)
                .setEnableDragClose(true)
                .setFolderName("DingChao")
                .setShowCloseButton(true)
                .setLoadStrategy(ImagePreview.LoadStrategy.NetworkAuto)
                .setErrorPlaceHolder(R.drawable.img_load)
                .setImageInfoList(preImageList)
                .start();
    }


}
