package cool.dingstock.appbase.widget.menupop;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatCheckedTextView;

/**
 * Created by felix on 16/11/21.
 */
public class OptionItemView extends AppCompatCheckedTextView {

    public OptionItemView(Context context) {
        this(context, null, 0);
    }

    public OptionItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OptionItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    public OptionItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {

    }
}
