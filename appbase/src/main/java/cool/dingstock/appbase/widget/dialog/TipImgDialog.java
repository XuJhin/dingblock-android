package cool.dingstock.appbase.widget.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import cool.dingstock.appbase.R;
import cool.dingstock.appbase.widget.IconTextView;

public class TipImgDialog extends BaseDialog {

    private TextView titleTxt;
    private TextView contentTxt;
    private TextView btn;
    private ImageView iv;
    private IconTextView delIcon;
    private TextView bottomTxt;

    public TipImgDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.common_dialog_update;
    }

    @Override
    public void initViews() {
        titleTxt = mRootView.findViewById(R.id.common_update_dialog_title_txt);
        iv = mRootView.findViewById(R.id.common_update_dialog_iv);
        contentTxt = mRootView.findViewById(R.id.common_update_dialog_content_txt);
        btn = mRootView.findViewById(R.id.common_update_dialog_btn);
        delIcon = mRootView.findViewById(R.id.common_update_dialog_del_txt);
        bottomTxt = mRootView.findViewById(R.id.common_update_dialog_bottom_txt);
        delIcon.setOnClickListener(v -> dismiss());
    }


    @Override
    public void setDialogConfig(Window window) {
        window.setGravity(Gravity.CENTER);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    public static TipImgDialog.Builder newBuilder(@NonNull Context context) {
        return new TipImgDialog.Builder(context);
    }

    public static class Builder {

        private final TipImgDialog updateDialog;

        public Builder(@NonNull Context context) {
            updateDialog = new TipImgDialog(context);
        }

        public TipImgDialog.Builder title(String title) {
            updateDialog.titleTxt.setText(title);
            return this;
        }

        public TipImgDialog.Builder imgres(int res) {
            updateDialog.iv.setImageResource(res);
            return this;
        }

        public TipImgDialog.Builder content(String content) {
            updateDialog.contentTxt.setText(content);
            return this;
        }


        public TipImgDialog.Builder cancelable(boolean cancel) {
            updateDialog.setCancelable(cancel);
            updateDialog.delIcon.setVisibility(cancel ? View.VISIBLE : View.GONE);
            return this;
        }


        public TipImgDialog.Builder bottomTxt(String text, View.OnClickListener listener) {
            if (TextUtils.isEmpty(text)){
                updateDialog.bottomTxt.setVisibility(View.GONE);
                return this;
            }
            updateDialog.bottomTxt.setText(text);
            updateDialog.bottomTxt.setVisibility(View.VISIBLE);
            updateDialog.bottomTxt.setOnClickListener(v -> {
                updateDialog.dismiss();
                listener.onClick(v);
            });
            return this;
        }


        public TipImgDialog.Builder actionBtn(String text, View.OnClickListener listener) {
            updateDialog.btn.setText(text);
            updateDialog.btn.setOnClickListener(v -> {
                updateDialog.dismiss();
                listener.onClick(v);
            });
            return this;
        }

        public TipImgDialog build() {
            return this.updateDialog;
        }

        public void show() {
            updateDialog.show();
        }

    }

}
