package cool.dingstock.appbase.widget.videoview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import cool.dingstock.appbase.R;
import cool.dingstock.appbase.constant.UTConstant;
import cool.dingstock.appbase.router.DcUriRequest;
import cool.dingstock.appbase.constant.CommonConstant;
import cool.dingstock.appbase.entity.bean.circle.CircleLinkBean;
import cool.dingstock.appbase.ut.UTHelper;

public class DCVideoView extends RelativeLayout {

    private View mRootView;
    private DCSurface surfaceView;
    private ImageView playIcon;
    private ImageView firstIv;
    private TextView errorTxt;
    private ProgressBar progressBar;
    private String url;
    private String postId;
    private View ivMask;
    CircleLinkBean webpageLink;
    private String dynamicId;

    private OnClickListener OnVideoClickListener;

    public DCVideoView(Context context) {
        this(context, null);
    }

    public DCVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DCVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        mRootView = View.inflate(getContext(), R.layout.common_view_video, null);
        surfaceView = mRootView.findViewById(R.id.common_view_video_surface);
        progressBar = mRootView.findViewById(R.id.pb);
        playIcon = mRootView.findViewById(R.id.common_view_video_play_icon);
        firstIv = mRootView.findViewById(R.id.common_view_video_first_iv);
        errorTxt = mRootView.findViewById(R.id.common_view_video_error_txt);
        ivMask = mRootView.findViewById(R.id.common_view_video_mask_iv);
        playIcon.setOnClickListener(v -> {
            if(OnVideoClickListener!=null){
                OnVideoClickListener.onClick(v);
            }
            UTHelper.commonEvent(UTConstant.Circle.Dynamic_click_VideoDynamic_Play,"postId",postId);
            play();
        });
        surfaceView.setOnClickListener(v -> {
            routeToVideoPlay();
            if(OnVideoClickListener!=null){
                OnVideoClickListener.onClick(v);
            }
        });

        surfaceView.setOnVideoPlayingListener(new DCSurface.OnVideoPlayingListener() {
            @Override
            public void onStart() {
                Log.e("surfaceView","onStart");
            }

            @Override
            public void onPlaying(int duration, int percent) {
                Log.e("surfaceView","onPlaying");
                firstIv.setVisibility(GONE);
                playIcon.setVisibility(GONE);
                errorTxt.setVisibility(GONE);
                progressBar.setVisibility(GONE);
                ivMask.setVisibility(GONE);
            }

            @Override
            public void onPause() {
                Log.e("surfaceView","onPause");
                playIcon.setVisibility(VISIBLE);
                errorTxt.setVisibility(GONE);
                ivMask.setVisibility(VISIBLE);
            }

            @Override
            public void onRestart() {
                Log.e("surfaceView","onRestart");
                firstIv.setVisibility(GONE);
                playIcon.setVisibility(GONE);
                errorTxt.setVisibility(GONE);
                ivMask.setVisibility(GONE);
            }

            @Override
            public void onPlayingFinish() {
                Log.e("surfaceView","onPlayingFinish");
                firstIv.setVisibility(GONE);
                errorTxt.setVisibility(GONE);
                playIcon.setVisibility(VISIBLE);
                ivMask.setVisibility(VISIBLE);
            }

            @Override
            public void onSurfaceDestroyed() {
                Log.e("surfaceView","onSurfaceDestroyed");
                pause();
//                playIcon.setVisibility(VISIBLE);
//                ivMask.setVisibility(VISIBLE);
                errorTxt.setVisibility(GONE);
            }


            @Override
            public void onError() {
                stop();
                firstIv.setVisibility(GONE);
                playIcon.setVisibility(GONE);
                progressBar.setVisibility(GONE);
                errorTxt.setVisibility(VISIBLE);
            }
        });
        addView(mRootView);
    }

    public void play(){
        if (surfaceView.isPlaying()) {
//            ivMask.setVisibility(VISIBLE);
//            pause();
//            surfaceView.play();
        } else {
            ivMask.setVisibility(VISIBLE);
            playIcon.setVisibility(GONE);
            progressBar.setVisibility(VISIBLE);
            resume();
        }
    }

    private void routeToVideoPlay() {
        String imageUrl=null;
        String title=null;
        if(webpageLink!=null) {
            imageUrl = webpageLink.getImageUrl();
            title = webpageLink.getTitle();
        }
        Router(CommonConstant.Uri.VIDEO_VIEW)
                .putUriParameter(CommonConstant.UriParams.URL, url)
                .putUriParameter(CommonConstant.UriParams.CIRCLE_VIDEO_COVER,imageUrl==null?"":imageUrl)
                .putUriParameter(CommonConstant.UriParams.CIRCLE_VIDEO_TITLE, title==null?"":title)
                .start();
    }


    public void setUrl(String url,String postId) {
        this.postId = postId;
        this.url = url;
//        playIcon.setVisibility(VISIBLE);
        surfaceView.pause();
        surfaceView.stop();
        surfaceView.setUrl(url);
//        setFirstBitmap();
    }

    public String getUrl(){
        return this.url;
    }


    public void setFirstBitmap() {
//        int radius = SizeUtils.dp2px(4);
//        ImageLoad.load(url)
//                .radius(radius, radius, 0, 0)
//                .resize()
//                .into(firstIv);
//        ImageLoad.load(R.color.color_mask)
//                .radius(radius, radius, 0, 0)
//                .resize()
//                .into(ivMask);
//        firstIv.setVisibility(VISIBLE);
//        playIcon.setVisibility(VISIBLE);
    }


    public void pause() {
        surfaceView.pause();
    }

    public void resume() {
        surfaceView.resume();
    }


    public void stop() {
        playIcon.setVisibility(GONE);
        firstIv.setVisibility(GONE);
        ivMask.setVisibility(VISIBLE);
        surfaceView.stop();
    }


    public DcUriRequest Router(@NonNull String uri) {
        return new DcUriRequest(getContext(), uri);
    }

    public void setOnVideoClickListener(OnClickListener onVideoClickListener) {
        OnVideoClickListener = onVideoClickListener;
    }

    public void updateDynamicId(String id) {
        this.dynamicId = id;
    }
}
