package cool.dingstock.appbase.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import cool.dingstock.appbase.R;
import cool.dingstock.appbase.util.KtExtendUtilsKt;
import cool.dingstock.lib_base.util.SizeUtils;

public class TitleBar extends RelativeLayout {

    private static final int NO_SET = -99999999;
    private int titleBarBg = NO_SET;
    private int leftIconColor = NO_SET;
    private int titleColor = NO_SET;
    private int leftIconSvgResId = 0;
    private int rightTextEnableColor;
    private int rightTextDisableColor;
    private RelativeLayout rootView;
    private AppCompatImageView leftView;
    private View titleView;
    private IconTextView titleIconView;
    private ViewGroup rightLayer;
    private View rightIconView;
    private View bottomLine;
    private TextView leftTxt;

    public View getTitleView() {
        return titleView;
    }

    public TitleBar(Context context) {
        super(context);
        initView(null);
    }

    private void initView(@Nullable AttributeSet attrs) {
        rootView = (RelativeLayout) View.inflate(getContext(), R.layout.common_layout_titlebar, this);
        leftView = rootView.findViewById(R.id.common_titlebar_left_icon);
        leftTxt = rootView.findViewById(R.id.common_titlebar_left_txt);
        titleView = rootView.findViewById(R.id.common_titlebar_title_tv);
        titleIconView = rootView.findViewById(R.id.common_titlebar_title_icon);
        rightLayer = rootView.findViewById(R.id.common_titlebar_right_layer);
        rightIconView = rootView.findViewById(R.id.common_titlebar_right_icon);
        bottomLine = rootView.findViewById(R.id.common_titlebar_bottom_line);
        // Init View by Xml Style
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TitleBar);
        initTitleBar(typedArray);
        initLeftView(typedArray);
        initTitle(typedArray);
        initRightView(typedArray);
        initBottomLine(typedArray);
        typedArray.recycle();
    }

    // 设置 整体风格
    private void initTitleBar(TypedArray typedArray) {
        if (typedArray.hasValue(R.styleable.TitleBar_android_background)) {
            titleBarBg = typedArray.getColor(R.styleable.TitleBar_android_background,
                    ContextCompat.getColor(getContext(), R.color.white));
        }
        setTitleBarBg();
    }

    private void setTitleBarBg() {
        setBackgroundColor(titleBarBg != NO_SET ? titleBarBg : ContextCompat.getColor(getContext(),
                R.color.white));
    }

    private void initLeftView(TypedArray typedArray) {
        if (typedArray.hasValue(R.styleable.TitleBar_leftIconColor)) {
            leftIconColor = typedArray.getColor(R.styleable.TitleBar_leftIconColor,
                    ContextCompat.getColor(getContext(), R.color.color_text_black1));
        } else {
            leftIconColor = ContextCompat.getColor(getContext(), R.color.color_text_black1);
        }
        if (leftIconSvgResId == 0) {
            leftIconSvgResId = R.drawable.ic_icon_nav_back;
        }
        setLeftIconColorRes();
        leftView.setImageTintList(ColorStateList.valueOf(leftIconColor));
        setLeftOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == getContext() || !(getContext() instanceof Activity)) {
                    return;
                }
                ((Activity) getContext()).finish();
            }
        });
    }

    private void setLeftIconColorRes() {
        setLeftIconColorInt(leftIconColor != NO_SET ? leftIconColor : ContextCompat.getColor(getContext(),
                R.color.color_text_black1));
    }

    public void setLeftIconColorInt(@ColorInt int colorInt) {
        if (null == leftView) {
            return;
        }
        if (leftView instanceof ImageView) {
            KtExtendUtilsKt.setSvgColor(leftView, leftIconSvgResId, leftIconColor);
        }
    }

    public void setLeftOnClickListener(@Nullable OnClickListener listener) {
        if (null == leftView) {
            return;
        }
        if (leftView.getVisibility() == VISIBLE) {
            leftView.setOnClickListener(listener);
        }
        if (leftTxt.getVisibility() == VISIBLE) {
            leftTxt.setOnClickListener(listener);
        }
    }

    private void initTitle(TypedArray typedArray) {
        int titleStyle = typedArray.getInt(R.styleable.TitleBar_titleStyle, Typeface.BOLD);
        setTitleTypeface(titleStyle);
        if (typedArray.hasValue(R.styleable.TitleBar_titleTextColor)) {
            titleColor = typedArray.getColor(R.styleable.TitleBar_titleTextColor,
                    ContextCompat.getColor(getContext(), R.color.color_text_black1));
        }
        leftIconColor = ContextCompat.getColor(getContext(), R.color.color_text_black1);
        setTitleColor();
        int titleSize = typedArray.getDimensionPixelSize(R.styleable.TitleBar_titleTextSize, SizeUtils.sp2px(16));
        setTitleSize(SizeUtils.px2sp(titleSize));
        String titleText = typedArray.getString(R.styleable.TitleBar_titleText);
        setTitle(null != titleText ? titleText : "");
    }

    private void setTitleColor() {
        setTitleColorInt(titleColor != NO_SET ? titleColor : ContextCompat.getColor(getContext(),
                R.color.color_text_black1));
    }

    public void setTitleColorInt(@ColorInt int colorInt) {
        if (null == titleView) {
            return;
        }

        if (titleView instanceof IconTextView) {
            ((IconTextView) titleView).setTextColor(colorInt);
        }
    }

    public void setTitleSize(float size) {
        if (null == titleView) {
            return;
        }

        if (titleView instanceof IconTextView) {
            ((IconTextView) titleView).setTextSize(size);
        }
    }

    public void setTitleTypeface(@IntRange(from = 0) int style) {
        if (null == titleView) {
            return;
        }

        if (titleView instanceof IconTextView) {
            ((IconTextView) titleView).setTypeface(style);
        }
    }

    private void initRightView(TypedArray typedArray) {
        String rightText = typedArray.getString(R.styleable.TitleBar_rightText);
        if (!TextUtils.isEmpty(rightText)) {
            setRightText(rightText);
        }

        rightTextEnableColor = typedArray.getColor(R.styleable.TitleBar_rightTextColor,
                ContextCompat.getColor(getContext(), R.color.common_dc_theme_color));
        setRightTextColorInt(rightTextEnableColor);

        rightTextDisableColor = typedArray.getColor(R.styleable.TitleBar_rightTextDisableColor,
                ContextCompat.getColor(getContext(), R.color.common_titleBar_light_right_disable));

        int rightSize = typedArray.getDimensionPixelSize(R.styleable.TitleBar_rightTextSize, SizeUtils.sp2px(15));
        setRightTextSize(SizeUtils.px2sp(rightSize));

        int rightTextStyle = typedArray.getInt(R.styleable.TitleBar_rightTextStyle, Typeface.NORMAL);
        setRightTextTypeface(rightTextStyle);
    }

    public void setRightText(CharSequence text) {
        if (null == rightIconView) {
            return;
        }

        if (rightIconView instanceof IconTextView) {
            ((IconTextView) rightIconView).setText(text);
        }
    }

    public void setRightTextColorInt(@ColorInt int colorInt) {

        if (null == rightIconView) {
            return;
        }

        if (rightIconView instanceof IconTextView) {
            ((IconTextView) rightIconView).setTextColor(colorInt);
        }
    }

    public void setRightTextSize(@IntRange(from = 1) int size) {
        if (null == rightIconView) {
            return;
        }

        if (rightIconView instanceof IconTextView) {
            ((IconTextView) rightIconView).setTextSize(size);
        }
    }

    public void setRightTextTypeface(@IntRange(from = 0) int style) {
        if (null == rightIconView) {
            return;
        }

        if (rightIconView instanceof IconTextView) {
            ((IconTextView) rightIconView).setTypeface(style);
        }
    }

    private void initBottomLine(TypedArray typedArray) {
        boolean isHideLine = typedArray.getBoolean(R.styleable.TitleBar_hideLine, false);
        bottomLine.setVisibility(isHideLine ? GONE : INVISIBLE);
        bottomLine.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.common_titleBar_light_line));
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    private void hideDivider() {

    }

    public void setLeftIconPadding(int leftIconPadding) {
        leftView.setPadding(leftIconPadding, leftIconPadding, leftIconPadding, leftIconPadding);
    }

    public void setLeftIcon(@DrawableRes int resId) {
        if (null == leftView) {
            return;
        }
        leftView.setVisibility(VISIBLE);
        leftTxt.setVisibility(GONE);
        leftIconSvgResId = resId;
        KtExtendUtilsKt.setSvgColor(leftView, resId, leftIconColor);
    }

    public void setLeftTxt(String text) {
        leftTxt.setVisibility(VISIBLE);
        leftView.setVisibility(GONE);
        leftTxt.setText(text);
    }

    public void setLeftIconVisibility(boolean visible) {
        if (null == leftView) {
            return;
        }
        leftView.setVisibility(visible ? VISIBLE : GONE);
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    public void setLeftIconColorRes(@ColorRes int resId) {
        if (null == leftView) {
            return;
        }
        leftIconColor = getResources().getColor(resId);
        leftView.setImageResource(leftIconSvgResId);
        leftView.setImageTintList(getResources().getColorStateList(resId));
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    public void setLeftIconColor(int color) {
        if (null == leftView) {
            return;
        }
        final Drawable drawable = leftView.getDrawable();
        DrawableCompat.setTint(drawable, color);

    }

    public void setLeftIconResId(@IdRes int svgResId) {
        if (null == leftView) {
            return;
        }
        leftIconSvgResId = svgResId;
        KtExtendUtilsKt.setSvgColor(leftView, leftIconSvgResId, leftIconColor);
    }

    public void setLeftIconColorRes(@NonNull String colorStr) {
        if (!colorStr.startsWith("#") || colorStr.length() != 7 || colorStr.length() != 9) {
            return;
        }

        setLeftIconColorInt(Color.parseColor(colorStr));
    }

    public void setLeftView(@NonNull AppCompatImageView view, LayoutParams layoutParams) {
        if (null != leftView) {
            ViewGroup parent = (ViewGroup) leftView.getParent();
            if (null != parent) {
                parent.removeView(leftView);
            }
        }
        if (null == layoutParams) {
            layoutParams = (LayoutParams) leftView.getLayoutParams();
        }

        leftView = view;
        rootView.addView(leftView, getLayoutParams(leftView, layoutParams, RelativeLayout.ALIGN_PARENT_LEFT));
    }

    private LayoutParams getLayoutParams(@NonNull View view, LayoutParams layoutParams, int verb) {
        if (null == layoutParams) {
            layoutParams = (LayoutParams) view.getLayoutParams();
        }
        if (null == layoutParams) {
            layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        layoutParams.addRule(verb);

        return layoutParams;
    }

    public void setTitleIcon(CharSequence text) {
        if (null == titleView) {
            return;
        }
        titleIconView.setVisibility(VISIBLE);
        titleIconView.setText(text);
    }

    public void setTitleAlpha(float alpha) {
        titleView.setAlpha(alpha);
    }

    public CharSequence getTitle() {
        if (null == titleView) {
            return "";
        }
        if (titleView instanceof IconTextView) {
            return ((IconTextView) titleView).getText();
        }

        return "";
    }

    public void setTitle(CharSequence text) {
        if (null == titleView) {
            return;
        }

        if (titleView instanceof IconTextView) {
            ((IconTextView) titleView).setText(text);
        }
    }

    public void setTitle(@StringRes int resId) {
        setTitle(getContext().getString(resId));
    }

    public void setTitleLineCount(int count) {
        if (null == titleView) {
            return;
        }
        ((IconTextView) titleView).setSingleLine(false);
        ((IconTextView) titleView).setLines(count);
        if (count > 1) {
            ((IconTextView) titleView).setLineSpacing(SizeUtils.dp2px(3), 1);
        }
    }

    public void setTitleColor(@ColorRes int resId) {
        if (null == titleView) {
            return;
        }

        if (titleView instanceof IconTextView) {
            ((IconTextView) titleView).setTextColor(ContextCompat.getColor(getContext(), resId));
        }
    }

    public void setTitleView(@NonNull View view) {
        setTitleView(view, null);
    }

    public void setTitleView(@NonNull View view, LayoutParams layoutParams) {
        if (null != titleView) {
            rootView.removeView(titleView);
        }

        titleView = view;
        rootView.addView(titleView, getLayoutParams(titleView, layoutParams, RelativeLayout.CENTER_IN_PARENT));
    }

    public void setTitleOnClickListener(@Nullable OnClickListener listener) {
        if (null == titleView) {
            return;
        }

        titleView.setOnClickListener(listener);
    }

    public void setRightText(@StringRes int resId) {
        if (null == rightIconView) {
            return;
        }

        if (rightIconView instanceof IconTextView) {
            ((IconTextView) rightIconView).setText(resId);
        }
    }

    public void setRightTextColor(@ColorRes int resId) {
        if (null == rightIconView) {
            return;
        }

        if (rightIconView instanceof IconTextView) {
            ((IconTextView) rightIconView).setTextColor(ContextCompat.getColor(getContext(), resId));
        }
    }

    public boolean isRightEnable() {
        if (null == rightIconView) {
            return false;
        }

        return rightIconView.isEnabled();
    }

    public void setRightEnable(boolean enable) {
        if (null == rightIconView) {
            return;
        }

        rightLayer.setEnabled(enable);
        rightIconView.setEnabled(enable);
        setRightTextColorInt(enable ? rightTextEnableColor : rightTextDisableColor);
    }

    public void removeRightView() {
        if (null != rightLayer) {
            rightLayer.removeAllViews();
        }
    }

    public void setRightView(@NonNull View view) {
        setRightView(view, null);
    }

    public void setRightView(@NonNull View view, LayoutParams layoutParams) {
        rightLayer.removeAllViews();
        if (null != layoutParams) {
            rightLayer.addView(view, layoutParams);
        } else {
            rightLayer.addView(view);
        }

    }

    public void setRightView(@NonNull View view, int width, int height) {
        rightLayer.removeAllViews();
        rightLayer.addView(view, width, height);
    }

    public void setRightOnClickListener(@Nullable OnClickListener listener) {
        if (null == rightLayer) {
            return;
        }

        rightLayer.setOnClickListener(listener);
    }

    public void setLineVisibility(boolean isVisibility) {
        bottomLine.setVisibility(isVisibility ? VISIBLE : GONE);
    }

    public void setRightViewVisibility(int visibility) {
        rightIconView.setVisibility(visibility);
    }

    public boolean isLineVisibile() {
        return bottomLine.getVisibility() == VISIBLE;
    }

    public void setRightVisibility(boolean isVisibility) {
        rightLayer.setVisibility(isVisibility ? VISIBLE : GONE);
    }

}
