package cool.dingstock.appbase.widget.videoview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

import java.io.FileDescriptor;

import cool.dingstock.lib_base.util.Logger;

public class DCSurface extends TextureView implements TextureView.SurfaceTextureListener {

    public VideoState mState;
    private MediaPlayer mMediaPlayer;
    private int mVideoWidth;//视频宽度
    private int mVideoHeight;//视频高度


    //回调监听
    public interface OnVideoPlayingListener {

        void onStart();

        void onPlaying(int duration, int percent);

        void onPause();

        void onRestart();

        void onPlayingFinish();

        void onSurfaceDestroyed();

        void onError();

    }

    //播放状态
    public enum VideoState {
        init, palying, pause
    }

    private OnVideoPlayingListener listener;

    public void setOnVideoPlayingListener(OnVideoPlayingListener listener) {
        this.listener = listener;
    }

    @SuppressLint("HandlerLeak")
    private Handler mProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if (listener != null && mState == VideoState.palying) {
                    listener.onPlaying(mMediaPlayer.getDuration(), mMediaPlayer.getCurrentPosition());
                    sendEmptyMessageDelayed(0, 100);
                }
            }
        }
    };

    public DCSurface(Context context) {
        super(context);
        init();
    }

    public DCSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DCSurface(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        initMediaPlayer();
        setSurfaceTextureListener(this);
    }


    String url;

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDataSource(FileDescriptor fileDescriptor) {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(fileDescriptor);
            mMediaPlayer.prepareAsync();
            mState = VideoState.init;
        } catch (Exception e) {
        }
    }


    public void play() {
        try {
            if (!TextUtils.isEmpty(url)) {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(url);
            }
            mMediaPlayer.prepareAsync();
            mState = VideoState.init;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            if (listener != null) listener.onPause();
        }
        mState = VideoState.pause;
    }

    public void resume() {
        int currentPosition = mMediaPlayer.getCurrentPosition();
        if (currentPosition > 0) {
            mMediaPlayer.seekTo(currentPosition);
            mMediaPlayer.start();
            mState = VideoState.palying;
            if (listener != null) listener.onRestart();
            getPlayingProgress();
        } else {
            play();
        }
    }

    public void stop() {
        mMediaPlayer.reset();
    }


    //播放进度获取
    private void getPlayingProgress() {
        mProgressHandler.sendEmptyMessage(0);
    }


    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Logger.i("onSurfaceTextureAvailable ");
        Surface mediaSurface = new Surface(surface);
        mMediaPlayer.setSurface(mediaSurface);
        mState = VideoState.palying;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        updateTextureViewSizeCenterCrop();
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (null != listener) {
            listener.onSurfaceDestroyed();
        }
        Logger.i("onSurfaceTextureDestroyed ");
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


    private void initMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(mp -> {
                mMediaPlayer.setVolume(1f, 1f);
                mp.start();
                mState = VideoState.palying;
                if (listener != null) listener.onStart();
                getPlayingProgress();
            });
            mMediaPlayer.setOnErrorListener((mp, what, extra) -> {
                if (listener != null) listener.onError();
                return true;
            });
            mMediaPlayer.setOnBufferingUpdateListener((mp, percent) -> {
            });
            mMediaPlayer.setOnCompletionListener(mp -> {
                mProgressHandler.removeMessages(0);
                mMediaPlayer.reset();
                if (listener != null) listener.onPlayingFinish();
            });
            mMediaPlayer.setOnVideoSizeChangedListener((mp, width1, height1) -> {
                mVideoHeight = mMediaPlayer.getVideoHeight();
                mVideoWidth = mMediaPlayer.getVideoWidth();
                updateTextureViewSizeCenterCrop();
            });
        }
    }


    private void updateTextureViewSizeCenterCrop() {
        float sx = (float) getWidth() / (float) mVideoWidth;
        float sy = (float) getHeight() / (float) mVideoHeight;
        Matrix matrix = new Matrix();
        //第1步:把视频区移动到View区,使两者中心点重合.
        matrix.preTranslate((getWidth() - mVideoWidth) / 2, (getHeight() - mVideoHeight) / 2);
        //第2步:因为默认视频是fitXY的形式显示的,所以首先要缩放还原回来.
        matrix.preScale(mVideoWidth / (float) getWidth(), mVideoHeight / (float) getHeight());
        //第3步,等比例放大或缩小,直到视频区的一边和View一边相等.如果另一边和view的一边不相等，则留下空隙
        if (sx >= sy) {
            matrix.postScale(sy, sy, getWidth() / 2, getHeight() / 2);
        } else {
            matrix.postScale(sx, sx, getWidth() / 2, getHeight() / 2);
        }
        setTransform(matrix);
        postInvalidate();
    }


}