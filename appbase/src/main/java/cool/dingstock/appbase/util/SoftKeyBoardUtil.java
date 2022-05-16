package cool.dingstock.appbase.util;

import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by wangshuwen on 2017/11/30.
 */

public class SoftKeyBoardUtil {

    public static void hideSoftKeyboard(Context context, View view) {
        if (null == context) {
            return;
        }
        if (null == view) {
            return;
        }
        final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != imm) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public static void showSoftKeyboard(Context context, final View view) {
        if (null == context) {
            return;
        }
        if (null == view) {
            return;
        }
        final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != imm && !isKeyBoardShow(view.getRootView())) {
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    view.requestFocus();
                }
            }, 100);
        }
    }


    /**
     * 软键盘是否显示
     *
     * @param rootView
     * @return
     */
    private static boolean isKeyBoardShow(View rootView) {
        final int softKeyboardHeight = 100;
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        int heightDiff = rootView.getBottom() - r.bottom;
        return heightDiff > softKeyboardHeight * dm.density;
    }



}
