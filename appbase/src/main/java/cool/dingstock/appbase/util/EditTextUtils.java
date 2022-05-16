package cool.dingstock.appbase.util;

import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cool.dingstock.lib_base.util.SizeUtils;

/**
 * Created by wangshuwen on 2017/6/12.
 */

public class EditTextUtils {


    public static final int AMERICA_PHONE_LENGTH = 10;

    private static final String PHONE_NUM_REGEX = "^1[0-9]{10}$";

    private static final String CHINESE_REGEX = "^[\\u4e00-\\u9fa5]+$";


    /**
     * 验证手机号码
     *
     * @param phoneNumber 手机号码
     * @return boolean
     */
    public static boolean checkPhoneNumber(boolean isChina, String phoneNumber) {
        if (!isChina) {
            if (!TextUtils.isEmpty(phoneNumber)) {
                return true;
            }
            return false;
        }
        Pattern pattern = Pattern.compile(PHONE_NUM_REGEX);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }


    public static boolean checkChinese(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        Pattern pattern = Pattern.compile(CHINESE_REGEX);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    /**
     * 过滤字符串里面的空格
     *
     * @param phoneNum
     * @return
     */
    public static String filterSpace(String phoneNum) {
        if (TextUtils.isEmpty(phoneNum)) {
            return phoneNum;
        }
        String filterSpaceStr = phoneNum.replaceAll(" ", "");
        return filterSpaceStr;
    }

    /**
     * 将输入框按照文字自动空格
     *
     * @param s
     * @param start
     * @param before
     * @param editText
     */
    public static void sCodeTextChange(CharSequence s, int start, int before, EditText editText) {
        if (editText == null) {
            return;
        }
        if (TextUtils.isEmpty(s)) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (i != 1 && i != 3 && i != 5 && s.charAt(i) == ' ') {
                continue;
            } else {
                sb.append(s.charAt(i));
                if ((sb.length() == 2 || sb.length() == 4 || sb.length() == 6) && sb.charAt(sb.length() - 1) != ' ') {
                    sb.insert(sb.length() - 1, ' ');
                }
            }
        }
        if (!sb.toString().equals(s.toString())) {
            int index = start + 1;
            if (sb.charAt(start) == ' ') {
                if (before == 0) {
                    index++;
                } else {
                    index--;
                }
            } else {
                if (before == 1) {
                    index--;
                }
            }
            editText.setText(sb.toString());
            editText.setSelection(index);
        }
    }


    /**
     * 在输入框格式化手机号码
     *
     * @param s
     * @param start
     * @param before
     */
    public static void phoneNumTextChange(CharSequence s, int start, int before, EditText editText) {
        if (editText == null) {
            return;
        }
        if (TextUtils.isEmpty(s)) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (i != 3 && i != 8 && s.charAt(i) == ' ') {
                continue;
            } else {
                sb.append(s.charAt(i));
                if ((sb.length() == 4 || sb.length() == 9) && sb.charAt(sb.length() - 1) != ' ') {
                    sb.insert(sb.length() - 1, ' ');
                }
            }
        }
        if (!sb.toString().equals(s.toString())) {
            int index = start + 1;

            if (start >= sb.length()) {
                return;
            }
            if (sb.charAt(start) == ' ') {
                if (before == 0) {
                    index++;
                } else {
                    index--;
                }
            } else {
                if (before == 1) {
                    index--;
                }
            }

            editText.setText(sb.toString());
            if (index > editText.getText().length()) {
                index = editText.getText().length();
            }
            editText.setSelection(index);
        }
    }

    public static String formatPhoneNum(String phoneNum) {
        if (TextUtils.isEmpty(phoneNum)) {
            return "";
        }
        StringBuilder stringBuffer = new StringBuilder(phoneNum);
        for (int i = 0; i < phoneNum.length(); i++) {
            if (i == 3 || i == 8) {
                stringBuffer.insert(i, " ");
            }
        }
        return stringBuffer.toString();
    }




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
        int maxLines = textView.getMaxLines();
        if (maxLines > lines) {
            return lines;
        }
        return maxLines;
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

    /**
     * 注：StaticLayout是android中处理文字换行的一个类，TextView源码中也是通过这个类实现换行的，使用这个类可以
     * 在不进行TextView绘制的前提下得到TextView的宽高，这里我们只需要获取到高度即可，这个高度当然也可以通过post
     * 在run中获取，但是这样做会有一个问题，界面是先绘制显示然后再计算高度根据我们的逻辑来收缩TextView的高度，在列表中
     * 会出现闪烁的问题。使用这个类一定要注意构造方法中参数的传递，保证参数和布局中textView设置的一致，否则会有误差
     * 这个问题和获取三行高度的要求是一样的
     *
     * @param textView
     * @param content
     * @param width
     * @return
     */
    public static int getTotalLineHeight(TextView textView,float lineSpace, String content, int width) {
        if (TextUtils.isEmpty(content)) return 0;

        TextPaint textPaint = textView.getPaint();
        /**
         * width：表示textView的宽度，和布局保持一致，达到这个宽度，StaticLayout会对文字进行换行处理
         * 1 ：是行高
         * UIUtil.dip2Px(CONTENT_LINE_SPACING)  ：是行间距
         */
        StaticLayout staticLayout;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            staticLayout = getStaticLayout23(textView, width);
        } else {
            staticLayout = getStaticLayout(textView, width);
        }
        return staticLayout.getHeight();
    }

}
