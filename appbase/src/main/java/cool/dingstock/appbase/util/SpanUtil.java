package cool.dingstock.appbase.util;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.TypefaceSpan;

import androidx.annotation.StringRes;

import cool.dingstock.lib_base.BaseLibrary;

/**
 * Created by wangshuwen on 2017/7/4.
 */

public class SpanUtil {

    private static String getString(@StringRes int id) {
        return BaseLibrary.getInstance().getContext().getResources().getString(id);
    }

    public static SpannableStringBuilder genStringWithNormalFont(CharSequence content, int start, int length) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(content);
        spannable.setSpan(
                new TypefaceSpan("sans-serif"),
                start,
                start + length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }
}
