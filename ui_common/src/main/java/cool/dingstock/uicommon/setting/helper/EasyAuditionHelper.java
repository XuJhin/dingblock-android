package cool.dingstock.uicommon.setting.helper;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import cool.dingstock.lib_base.BaseLibrary;
import cool.dingstock.lib_base.util.Logger;


public class EasyAuditionHelper {

    private final float DEFAULT_VOLUME = 1.0F;

    private MediaPlayer mMediaPlayer;

    public EasyAuditionHelper() {
        mMediaPlayer = new MediaPlayer();
    }

    private MediaListener mMediaListener;

    public interface MediaListener {
        void onCompleted();

        void onError();
    }

    public void setMediaListener(MediaListener listener) {
        this.mMediaListener = listener;
    }

    public void play(Uri uri) {
        if (null == uri) {
            return;
        }

        stop();

        if (null == mMediaPlayer) {
            mMediaPlayer = new MediaPlayer();
        }

        try {
            mMediaPlayer.reset();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setVolume(DEFAULT_VOLUME, DEFAULT_VOLUME);
            mMediaPlayer.setOnPreparedListener(mOnPrepareListener);
            mMediaPlayer.setOnErrorListener(mOnErrorListener);
            mMediaPlayer.setOnCompletionListener(onCompletionListener);
            mMediaPlayer.setDataSource(BaseLibrary.getInstance().getContext(),uri);
            mMediaPlayer.setLooping(false);
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void stop() {
        Logger.d("stop alarm theme audition");
        if (null == mMediaPlayer) {
            Logger.e("media player is invalid");
            return;
        }

        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }

        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }
    
    private final MediaPlayer.OnPreparedListener mOnPrepareListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            Logger.d("onPrepared duration=" + mp.getDuration());
            mMediaPlayer.start();
        }
    };

    MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            Logger.d("onCompletion");
            if (null != mMediaListener) {
                mMediaListener.onCompleted();
            }
        }
    };

    MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Logger.e("onError");
            if (null != mMediaListener) {
                mMediaListener.onError();
            }
            return true;
        }
    };

}
