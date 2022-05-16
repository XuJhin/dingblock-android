package cool.dingstock.appbase.widget.camera;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;

import cool.dingstock.appbase.R;
import cool.dingstock.lib_base.util.Logger;

public class DCCameraView extends FrameLayout {

    private DCFocusView mFocusView;
    private TextureView mTextureView;
    private CameraEngine mCameraEngine;
    private float mWidth;
    private float mHeight;

    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    private float preDiff;
    private SurfaceTexture mSurface;

    public DCCameraView(Context context) {
        this(context, null);
    }

    public DCCameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DCCameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        mCameraEngine = new CameraEngine(getContext());
        View rootView = View.inflate(getContext(), R.layout.common_view_camera, null);
        mFocusView = rootView.findViewById(R.id.common_view_camera_focus);
        mTextureView = rootView.findViewById(R.id.common_view_camera_surface);
        addView(rootView);
        post(() -> {
            mWidth = getWidth();
            mHeight = getHeight();
            startPreview(true);
        });
        initListener();
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mCameraEngine.release(getContext());
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            return scaleGestureDetector.onTouchEvent(event);
        }
        return gestureDetector.onTouchEvent(event);
    }


    private void initListener() {
        scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float currentSpan;
                if (preDiff != 0) {
                    currentSpan = detector.getCurrentSpan() - preDiff;
                } else {
                    preDiff = detector.getCurrentSpan();
                    return false;
                }
                preDiff = detector.getCurrentSpan();
                mCameraEngine.zoom(currentSpan);
                return false;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                preDiff = 0;
            }
        });

        gestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent event) {
                focus(event);
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent event) {
                focus(event);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });

        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                Logger.d("mWidth=" + width + " mHeight=" + height);
                mSurface = surface;
                startPreview(true);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                Logger.d("mWidth=" + width + " mHeight=" + height);
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                Logger.d("onSurfaceTextureDestroyed   ---");
                mCameraEngine.doStopPreview();
                mSurface = null;
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        });

    }


    private void focus(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        startFocusAnim(x, y);
        mCameraEngine.handleFocus(x, y, new DCCameraFocusCallback() {
            @Override
            public void onFocusSuccess() {
                mFocusView.setVisibility(GONE);
            }

            @Override
            public void onFocusFailed() {

            }
        });
    }

    private void startFocusAnim(float x, float y) {
        float focusWidth = mFocusView.getWidth();
        float focusHeight = mFocusView.getHeight();

        mFocusView.setVisibility(VISIBLE);
        //最小判断
        if (x < focusWidth / 2) {
            x = focusWidth / 2;
        }
        if (y < focusHeight / 2) {
            y = focusHeight / 2;
        }
        //最大判断
        if (x > mWidth - focusWidth / 2) {
            x = mWidth - focusWidth / 2;
        }
        if (y > mHeight - focusHeight / 2) {
            y = mHeight - focusHeight / 2;
        }
        mFocusView.setX(x - focusWidth / 2);
        mFocusView.setY(y - focusHeight / 2);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mFocusView, "scaleX", 1, 0.5f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mFocusView, "scaleY", 1, 0.5f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mFocusView, "alpha", 1f, 0.4f, 1f, 0.4f, 1f, 0.4f, 1f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(scaleX).with(scaleY).before(alpha);
        animSet.setDuration(400);
        animSet.start();
    }


    public void takePicture(DCTakePictureCallback callback) {
        mCameraEngine.takePicture(callback);
    }


    public void startPreview(boolean faceBack) {
        if (null == mSurface || mWidth == 0 || mHeight == 0) {
            Logger.w("wait to init ...");
            return;
        }
        mCameraEngine.startPreview(getContext(), faceBack, mHeight / mWidth, mSurface);
        mCameraEngine.autoFocus();
    }
}
