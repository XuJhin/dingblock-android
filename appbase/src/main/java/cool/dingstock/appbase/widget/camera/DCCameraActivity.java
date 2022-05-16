package cool.dingstock.appbase.widget.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import cool.dingstock.appbase.R;
import cool.dingstock.appbase.databinding.CommonActivityCameraBinding;
import cool.dingstock.appbase.mvvm.activity.BaseViewModel;
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity;
import cool.dingstock.appbase.widget.TitleBar;
import cool.dingstock.lib_base.util.FileUtils;

public class DCCameraActivity extends VMBindingActivity<BaseViewModel, CommonActivityCameraBinding> {

    TitleBar titleBar;
    DCCameraView dcCameraView;
    DCCaptureLayer captureLayer;
    ImageView previewIv;

    private boolean isBack = true;

    public static final String KEY_LOCAL_FILE_PATH = "filePath";
    public static final int RESULT_DONE = 2000;

    private Bitmap currentBitmap;

    @Override
    public void initViewAndEvent(@Nullable Bundle savedInstanceState) {
        titleBar = viewBinding.commonActivityCameraTitleBar;
        dcCameraView = viewBinding.commonActivityCameraView;
        captureLayer = viewBinding.commonActivityCameraCaptureLayer;
        previewIv = viewBinding.commonActivityCameraPreviewIv;

        titleBar.setTitleColor(R.color.white);
        titleBar.setLeftIconColorRes(R.color.white);
        titleBar.setRightText(R.string.icon_switch);
        titleBar.setRightTextColor(R.color.white);
    }

    @Override
    protected void initListeners() {
        captureLayer.setListener(new DCCaptureLayer.CaptureListener() {
            @Override
            public void onCaptureClick() {
                dcCameraView.takePicture((bitmap, isVertical) -> {
                    if (null == bitmap) {
                        return;
                    }
                    currentBitmap = bitmap;
                    previewIv.setImageBitmap(bitmap);
                    previewIv.setVisibility(View.VISIBLE);
                    captureLayer.done();
                });
            }

            @Override
            public void onBackClick() {
                if (null != currentBitmap) {
                    currentBitmap.recycle();
                    currentBitmap = null;
                }
                dcCameraView.startPreview(isBack);
                previewIv.setVisibility(View.GONE);
                captureLayer.normal();
            }

            @Override
            public void onDoneClick() {
                if (null == currentBitmap) {
                    done("");
                    return;
                }
                String path = FileUtils.saveBitmapToPath(currentBitmap);
                done(path);
            }
        });
        titleBar.setRightOnClickListener(v -> {
            isBack = !isBack;
            dcCameraView.startPreview(isBack);
        });
    }

    @Override
    public String moduleTag() {
        return null;
    }


    private void done(String path) {
        Intent data = new Intent();
        data.putExtra(KEY_LOCAL_FILE_PATH, path);
        setResult(RESULT_DONE, data);
        finish();
    }
}
