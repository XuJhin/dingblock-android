package cool.dingstock.appbase.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import java.util.Arrays;

import cool.dingstock.appbase.R;
import cool.dingstock.appbase.util.SpanUtil;
import cool.dingstock.appbase.util.TypefaceHUtil;

public class IconTextView extends AppCompatTextView {

    public IconTextView(Context context) {
        super(context);
        initViews(null);
    }

    public IconTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(attrs);
    }

    public IconTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(attrs);
    }

    private void initViews(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.IconTextView);

        if (typedArray.hasValue(R.styleable.IconTextView_iconColor)) {
            int iconColor = typedArray.getColor(R.styleable.IconTextView_iconColor,
                    ContextCompat.getColor(getContext(), R.color.common_multiEdit_light_icon));
            setTextColor(iconColor);
        }

        if (!typedArray.hasValue(R.styleable.IconTextView_android_textStyle)) {
            setTypeface();
            return;
        }

        int textStyle = typedArray.getInt(R.styleable.IconTextView_android_textStyle, Typeface.NORMAL);
        setTypeface(textStyle);

        typedArray.recycle();
    }

    private void setTypeface() {
        if (!isInEditMode()) {
            setTypeface(TypefaceHUtil.getIconFontTypeface());
        }
    }

    public void setTypeface(int style) {
        if (!isInEditMode()) {
            super.setTypeface(TypefaceHUtil.getIconFontTypeface(), style);
        }
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (!TextUtils.isEmpty(text) && text.toString().contains("x") && !text.toString().contains("sandBox")) {
            String[] strings = text.toString().split("x");
            int index = 0;
            for (String childStr : Arrays.asList(strings)) {
                text = SpanUtil.genStringWithNormalFont(text, index + childStr.length(), 1);
                index = index + childStr.length();
            }
        }
        super.setText(text, type);
    }

    public void setIcon(CharSequence text) {
        setText(text);
    }

    public void setIcon(@StringRes int resId) {
        setText(resId);
    }

    public void setIconColor(@ColorInt int color) {
        setTextColor(color);
    }

    public void setIconSize(float size) {
        setTextSize(size);
    }

    public void setIconSize(int unit, float size) {
        setTextSize(unit, size);
    }

}
