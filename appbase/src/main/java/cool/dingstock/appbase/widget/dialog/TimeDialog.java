package cool.dingstock.appbase.widget.dialog;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import cool.dingstock.appbase.R;

public class TimeDialog extends BaseDialog {

    private TextView titleTxt;
    private TextView contentTxt;
    private TextView btn;

    private String btnTxt;
    private int seconds;

    private Handler mHandler = new Handler(Looper.getMainLooper());


    public TimeDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.common_dialog_time;
    }

    @Override
    public void initViews() {
        titleTxt = mRootView.findViewById(R.id.common_dialog_time_title_txt);
        contentTxt = mRootView.findViewById(R.id.common_dialog_time_content_txt);
        contentTxt.setMovementMethod(ScrollingMovementMethod.getInstance());
        btn = mRootView.findViewById(R.id.common_dialog_time_btn);
        setCancelable(false);
        btn.setEnabled(false);
        setOnDismissListener(dialog -> mHandler.removeCallbacks(timeTask));
    }

    @Override
    public void setDialogConfig(Window window) {
        window.setGravity(Gravity.CENTER);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    private Runnable timeTask = new Runnable() {
        @Override
        public void run() {
            if (seconds == 0) {
                btn.setText(btnTxt);
                btn.setEnabled(true);
                return;
            }
            btn.setText(btnTxt + "(" + seconds + ")");
            mHandler.postDelayed(timeTask, 1000);
            --seconds;
        }
    };


    private void startCountdown() {
        mHandler.post(timeTask);
    }

    public static TimeDialog.Builder newBuilder(@NonNull Context context) {
        return new TimeDialog.Builder(context);
    }

    public static class Builder {

        private TimeDialog updateDialog;

        public Builder(@NonNull Context context) {
            updateDialog = new TimeDialog(context);
        }

        public TimeDialog.Builder title(String title) {
            updateDialog.titleTxt.setText(title);
            return this;
        }

        public TimeDialog.Builder content(String content) {
            updateDialog.contentTxt.setText(content);
            return this;
        }

        public TimeDialog.Builder actionTxtAndSeconds(String text,
                                                      int seconds,
                                                      View.OnClickListener listener) {
            updateDialog.btnTxt = text;
            updateDialog.seconds = seconds;
            updateDialog.btn.setOnClickListener(v -> {
                updateDialog.dismiss();
                listener.onClick(v);
            });
            return this;
        }

        public TimeDialog build() {
            return this.updateDialog;
        }

        public void show() {
            updateDialog.show();
            updateDialog.startCountdown();
        }

    }
}
