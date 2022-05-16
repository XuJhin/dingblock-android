package cool.dingstock.appbase.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import cool.dingstock.appbase.util.TypefaceHUtil;

public class AveTextView extends AppCompatTextView {

    public AveTextView(Context context) {
        super(context);
        initViews();
    }

    public AveTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public AveTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews() {
        setTypeface();
    }

    private void setTypeface() {
        if (!isInEditMode()) {
            setTypeface(TypefaceHUtil.getAveTypeface());
        }
    }

}
