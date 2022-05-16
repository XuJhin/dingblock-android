package cool.dingstock.appbase.util;

import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextDirectionHeuristics;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

/**
 * 类名：TextViewLinesUtil
 * 包名：cool.dingstock.appbase.util
 * 创建时间：2021/11/18 5:09 下午
 * 创建人： WhenYoung
 * 描述：
 **/
public class TextViewLinesUtil {
    /**
     * 获取文本行数
     * @param textView  控件
     * @param textViewWidth   控件的宽度  比如：全屏显示-就取手机的屏幕宽度即可。
     * @return
     */
    public static int getTextViewLines(TextView textView, int textViewWidth) {
        int width = textViewWidth - textView.getCompoundPaddingLeft() - textView.getCompoundPaddingRight();
        StaticLayout staticLayout;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            staticLayout = getStaticLayout23(textView, width);
        } else {
            staticLayout = getStaticLayout(textView, width);
        }
        int lines = staticLayout.getLineCount();
        return lines;
    }

    /**
     * sdk>=23
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private static StaticLayout getStaticLayout23(TextView textView, int width) {
        StaticLayout.Builder builder = StaticLayout.Builder.obtain(textView.getText(),
                0, textView.getText().length(), textView.getPaint(), width)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setTextDirection(TextDirectionHeuristics.FIRSTSTRONG_LTR)
                .setLineSpacing(textView.getLineSpacingExtra(), textView.getLineSpacingMultiplier())
                .setIncludePad(textView.getIncludeFontPadding())
                .setBreakStrategy(textView.getBreakStrategy())
                .setHyphenationFrequency(textView.getHyphenationFrequency())
                .setMaxLines(textView.getMaxLines() == -1 ? Integer.MAX_VALUE : textView.getMaxLines());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setJustificationMode(textView.getJustificationMode());
        }
        if (textView.getEllipsize() != null && textView.getKeyListener() == null) {
            builder.setEllipsize(textView.getEllipsize())
                    .setEllipsizedWidth(width);
        }
        return builder.build();
    }

    /**
     * sdk<23
     */
    private static StaticLayout getStaticLayout(TextView textView, int width) {
        return new StaticLayout(textView.getText(),
                0, textView.getText().length(),
                textView.getPaint(), width, Layout.Alignment.ALIGN_NORMAL,
                textView.getLineSpacingMultiplier(),
                textView.getLineSpacingExtra(), textView.getIncludeFontPadding(), textView.getEllipsize(),
                width);
    }
}
