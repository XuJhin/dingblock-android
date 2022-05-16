package cool.dingstock.appbase.widget.clip;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.sankuai.waimai.router.annotation.RouterUri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cool.dingstock.appbase.constant.CommonConstant;
import cool.dingstock.appbase.constant.ModuleConstant;
import cool.dingstock.appbase.constant.RouterConstant;
import cool.dingstock.appbase.databinding.CommonActivityClipBinding;
import cool.dingstock.appbase.mvvm.activity.BaseViewModel;
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity;
import cool.dingstock.appbase.widget.subsampling.ImageSource;
import cool.dingstock.appbase.widget.subsampling.SubsamplingScaleImageView;

@RouterUri(scheme = RouterConstant.SCHEME, host = RouterConstant.HOST, path = {CommonConstant.Path.CLIP})
public class DCClipActivity extends VMBindingActivity<BaseViewModel, CommonActivityClipBinding> {

    public static final String KEY_IMAGE = "image";
    public static final String KEY_CLIP_URL = "clipUrl";

    SubsamplingScaleImageView siv;
    TextView doneTxt;
    ClipView clipView;

    private String imagePath;
    private final Handler mHandler = new Handler();
    private boolean inDrag;
    private boolean inCheck;
    private final RectF limitRect = new RectF();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initViewAndEvent(@Nullable Bundle savedInstanceState) {
        siv = viewBinding.commonActivityClipSiv;
        doneTxt = viewBinding.commonActivityClipDoneTxt;
        clipView = viewBinding.commonActivityClipView;

        imagePath = getIntent().getStringExtra(KEY_IMAGE);
        if (TextUtils.isEmpty(imagePath)) {
            finish();
            return;
        }
        File file = new File(imagePath);
        siv.setImage(ImageSource.uri(file.getAbsolutePath()));
        siv.setDoubleTapZoomDuration(200);
        siv.setMaxScale(10);
        siv.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM);
        siv.setPanLimit(SubsamplingScaleImageView.PAN_LIMIT_CENTER);
        siv.setOnStateChangedListener(new SubsamplingScaleImageView.OnStateChangedListener() {
            @Override
            public void onScaleChanged(float newScale, int origin) {
                resolvePositionCheck();
            }

            @Override
            public void onCenterChanged(PointF newCenter, int origin) {

            }
        });
        siv.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    inDrag = false;
                    inCheck = false;
                    mHandler.removeCallbacks(checkRunnable);
                    mHandler.postDelayed(checkRunnable, 100);
                    break;
                case MotionEvent.ACTION_DOWN:
                    inDrag = true;
                    break;

            }
            return false;
        });
    }

    private final Runnable checkRunnable = new Runnable() {
        @Override
        public void run() {
            if (!inDrag && !inCheck) {
                inCheck = true;
                resolvePositionCheck();
            }
        }
    };


    private void resolvePositionCheck() {
        PointF center = siv.getCenter();
        if (null == center) {
            return;
        }
        int clipWidth = clipView.getClipRect().right - clipView.getClipRect().left;
        int clipHeight = clipView.getClipRect().bottom - clipView.getClipRect().top;
        float x;
        float y;
        float left = clipWidth / 2f / siv.getScale();
        float top = clipHeight / 2f / siv.getScale();
        float right = left + (siv.getSWidth() * siv.getScale() - clipWidth) / siv.getScale();
        float bottom = top + (siv.getSHeight() * siv.getScale() - clipHeight) / siv.getScale();
        limitRect.set(left, top, right, bottom);

        if (center.x < limitRect.left) {
            if (siv.getSWidth() * siv.getScale() < clipWidth) {
                x = Math.max(center.x, limitRect.right);
            } else {
                x = limitRect.left;
            }
        } else if (center.x > limitRect.right) {
            if (siv.getSWidth() * siv.getScale() < clipWidth) {
                x = limitRect.left;
            } else {
                x = limitRect.right;
            }
        } else {
            x = center.x;
        }
        if (center.y < limitRect.top) {
            if (siv.getSHeight() * siv.getScale() < clipHeight) {
                if (center.y < limitRect.bottom) {
                    y = limitRect.bottom;
                } else {
                    y = center.y;
                }
            } else {
                y = limitRect.top;
            }
        } else if (center.y > limitRect.bottom) {
            if (siv.getSHeight() * siv.getScale() < clipHeight) {
                y = limitRect.top;
            } else {
                y = limitRect.bottom;
            }
        } else {
            y = center.y;
        }
        if (x != center.x || y != center.y) {
            siv.animateCenter(new PointF(x, y))
                    .withDuration(100)
                    .withEasing(SubsamplingScaleImageView.EASE_OUT_QUAD)
                    .withInterruptible(false)
                    .start();
        }
    }


    @Override
    protected void initListeners() {
        doneTxt.setOnClickListener((View v) -> {
            Bitmap bitmap = clip();
            if (null == bitmap) {
                return;
            }
            try {
                File tempFile = File.createTempFile("" + (System.currentTimeMillis() / 1000),
                        ".jpg", getCacheDir());
                FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                Intent intent = new Intent();
                intent.putExtra(KEY_CLIP_URL, tempFile.getAbsolutePath());
                setResult(RESULT_OK, intent);
                finish();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }


    private Bitmap clip() {
        PointF center = siv.getCenter();
        if (null == center) {
            return null;
        }
        int clipWidth = clipView.getClipRect().right - clipView.getClipRect().left;
        int clipHeight = clipView.getClipRect().bottom - clipView.getClipRect().top;
        RectF cut = new RectF(
                (center.x - clipWidth / 2f / siv.getScale()),
                (center.y - clipHeight / 2f / siv.getScale()),
                (center.x + clipWidth / 2f / siv.getScale()),
                (center.y + clipHeight / 2f / siv.getScale()));
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        int width = (int) Math.min(cut.right - cut.left, bitmap.getWidth());
        int height = (int) Math.min(cut.bottom - cut.top, bitmap.getHeight());
        int left = (int) Math.max(cut.left, 0);
        int top = (int) Math.max(cut.top, 0);
        if (left + width > bitmap.getWidth() || top + height > bitmap.getHeight()) {
            showToastShort("请移动到裁剪区域");
            return null;
        }
        return Bitmap.createBitmap(bitmap,
                left, top, width, height);
    }


    @Override
    public String moduleTag() {
        return ModuleConstant.COMMON;
    }
}
