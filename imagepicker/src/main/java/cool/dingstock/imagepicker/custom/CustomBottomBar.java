package cool.dingstock.imagepicker.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import cool.dingstock.imagepicker.R;

import java.util.ArrayList;

import cool.dingstock.imagepicker.ImagePicker;
import cool.dingstock.imagepicker.bean.ImageItem;
import cool.dingstock.imagepicker.bean.ImageSet;
import cool.dingstock.imagepicker.bean.selectconfig.BaseSelectConfig;
import cool.dingstock.imagepicker.bean.selectconfig.MultiSelectConfig;
import cool.dingstock.imagepicker.utils.PCornerUtils;
import cool.dingstock.imagepicker.views.base.PickerControllerView;

public class CustomBottomBar extends PickerControllerView {
    private Button mDirButton;
    private TextView mPreview;
    private CheckBox mCheckBox;
    private String previewText;

    public CustomBottomBar(Context context) {
        super(context);
    }

    @Override
    public int getViewHeight() {
        return dp(50);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.picker_default_bottombar;
    }

    @Override
    protected void initView(View view) {
        mDirButton = view.findViewById(R.id.mDirButton);
        mPreview = view.findViewById(R.id.mPreview);
        mCheckBox = view.findViewById(R.id.mCheckBox);
        setCheckBoxDrawable(R.mipmap.picker_wechat_unselect, R.mipmap.picker_wechat_select);
        setBottomBarColor(Color.parseColor("#303030"));
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ImagePicker.isOriginalImage = isChecked;
            }
        });
        previewText = getContext().getString(R.string.picker_str_bottom_preview);
        mPreview.setText(previewText);
        mCheckBox.setText(getContext().getString(R.string.picker_str_bottom_original));
    }


    @Override
    public View getCanClickToCompleteView() {
        return null;
    }

    @Override
    public View getCanClickToIntentPreviewView() {
        return mPreview;
    }

    @Override
    public View getCanClickToToggleFolderListView() {
        return mDirButton;
    }

    @Override
    public void setTitle(String title) {
        mDirButton.setText(title);
    }

    @Override
    public void onTransitImageSet(boolean isOpen) {
    }

    @Override
    public void onImageSetSelected(ImageSet imageSet) {
        mDirButton.setText(imageSet.name);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void refreshCompleteViewState(ArrayList<ImageItem> selectedList, BaseSelectConfig selectConfig) {
        mPreview.setVisibility(View.VISIBLE);
        //????????????????????????????????????
        if (selectConfig instanceof MultiSelectConfig) {
            MultiSelectConfig selectConfig1 = (MultiSelectConfig) selectConfig;
            if (selectConfig1.isShowOriginalCheckBox()) {
                mCheckBox.setVisibility(View.VISIBLE);
                mCheckBox.setChecked(ImagePicker.isOriginalImage);
            } else {
                mCheckBox.setVisibility(View.GONE);
            }

            if (!selectConfig1.isPreview()) {
                mPreview.setVisibility(View.GONE);
            }
        }

        if (selectedList.size() > 0) {
            mPreview.setText(String.format("%s(%d)", previewText, selectedList.size()));
            mPreview.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            mPreview.setText(String.format("%s", previewText));
            mPreview.setTextColor(Color.parseColor("#50FFFFFF"));
        }
    }


    public void setCheckBoxDrawable(int unCheckDrawableID, int checkedDrawableID) {
        PCornerUtils.setCheckBoxDrawable(mCheckBox, checkedDrawableID, unCheckDrawableID);
    }

    public void setCheckBoxDrawable(Drawable unCheckDrawable, Drawable checkedDrawable) {
        PCornerUtils.setCheckBoxDrawable(mCheckBox, checkedDrawable, unCheckDrawable);
    }

    public void setBottomBarColor(int bottomBarColor) {
        setBackgroundColor(bottomBarColor);
    }
}

