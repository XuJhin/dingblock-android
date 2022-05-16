package cool.dingstock.lib_base.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cool.dingstock.lib_base.R;

/**
 * @author SherlockHolmes
 */
public class ToastUtil {

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    public ToastUtil() {

    }

    public static ToastUtil getInstance() {
        return InnerClass.instance;
    }

    public void _short(final Context context, final String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        makeTextAndShow(context, text, Toast.LENGTH_SHORT);
    }

    public void _long(final Context context, final String text) {
        HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if (context.getApplicationContext() != null) {
                    makeTextAndShow(context.getApplicationContext(), text, Toast.LENGTH_SHORT);
                }
            }
        });
    }

    public void makeTextAndShow(final Context context, final String text, final int time) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            showToast(context, text, time);
        } else {
            HANDLER.post(new Runnable() {
                @Override
                public void run() {
                    showToast(context, text, time);
                }
            });
        }
    }

    private void showToast(Context context, String text, int time) {
        if (context != null) {
            Toast toast = Toast.makeText(context, text, time);
            toast.setGravity(Gravity.TOP, 0, SizeUtils.dp2px(20));
            View inflate = LayoutInflater.from(context).inflate(R.layout.toast_view_layout, null, false);
            TextView tv = inflate.findViewById(R.id.tv);
            tv.setText(text);
            toast.setView(inflate);
            toast.show();
        }
    }


    private static class InnerClass {
        private static ToastUtil instance = new ToastUtil();
    }
}