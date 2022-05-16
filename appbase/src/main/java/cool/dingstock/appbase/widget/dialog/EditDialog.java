package cool.dingstock.appbase.widget.dialog;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import cool.dingstock.appbase.R;
import cool.dingstock.appbase.util.SoftKeyBoardUtil;

public class EditDialog extends BaseDialog {

    private TextView titleTxt;
    private EditText editText;
    private TextView positiveTxt;
    private TextView inPositiveTxt;


    public interface EditDialogListener {
        void onClick(String content, EditDialog dialog);
    }


    private EditDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.common_dialog_edit;
    }

    @Override
    public void initViews() {
        titleTxt = mRootView.findViewById(R.id.common_edit_dialog_title_txt);
        editText = mRootView.findViewById(R.id.common_edit_dialog_edit);
        positiveTxt = mRootView.findViewById(R.id.common_edit_dialog_positive);
        inPositiveTxt = mRootView.findViewById(R.id.common_edit_dialog_inpositive);
    }


    @Override
    public void setDialogConfig(Window window) {
        window.setGravity(Gravity.CENTER);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    public static Builder newBuilder(@NonNull Context context) {
        return new Builder(context);
    }

    public static class Builder {

        private EditDialog editDialog;

        public Builder(@NonNull Context context) {
            editDialog = new EditDialog(context);
        }

        public Builder title(String title) {
            editDialog.titleTxt.setText(title);
            return this;
        }

        public static double getTextLength(String text) {
            double length = 0;
            for (int i = 0; i < text.length(); i++) {
                // text.charAt(i)获取当前字符是的chart值跟具ASCII对应关系255以前的都是英文或者符号之等而中文并不在这里面所以此方法可行</span>
                if (text.charAt(i) > 255) {
                    length += 2;
                } else {
                    length++;
                }
            }
            return length;
        }

        public Builder setMaxLength(int maxLength) {
            editDialog.editText.setFilters(new InputFilter[]{new InputFilter() {
                // 这个方法，返回空字符串，就代表匹配不成功，返回null代表匹配成功
                @Override
                public CharSequence filter(CharSequence source, int start, int end,
                                           Spanned dest, int dstart, int dend) {
                    // 获取字符个数(一个中文算2个字符)
                    if (getTextLength(dest.toString()) + getTextLength(source.toString()) > maxLength) {
                        return "";
                    }
                    return null;
                }
            }});
            return this;
        }


        public Builder hint(String hint) {
            editDialog.editText.setHint(hint);
            return this;
        }

        public Builder positiveTxt(String positiveTxt, EditDialogListener listener) {
            editDialog.positiveTxt.setText(positiveTxt);
            editDialog.positiveTxt.setOnClickListener(v -> {
                SoftKeyBoardUtil.hideSoftKeyboard(editDialog.getContext(), editDialog.mRootView);
                if (null != listener) {
                    String content = editDialog.editText.getText().toString();
                    if (!TextUtils.isEmpty(content)) {
                        content = content.trim();
                    }
                    listener.onClick(content, editDialog);
                }
            });
            return this;
        }


        public Builder inPositiveTxt(String inPositiveTxt, EditDialogListener listener) {
            editDialog.inPositiveTxt.setText(inPositiveTxt);
            editDialog.inPositiveTxt.setOnClickListener(v -> {
                editDialog.dismiss();
                SoftKeyBoardUtil.hideSoftKeyboard(editDialog.getContext(), editDialog.mRootView);
                if (null != listener) {
                    String content = editDialog.editText.getText().toString();
                    if (!TextUtils.isEmpty(content)) {
                        content = content.trim();
                    }
                    listener.onClick(content, editDialog);
                }
            });
            return this;
        }

        public EditDialog build() {
            return this.editDialog;
        }

        public void show() {
            editDialog.show();
        }

    }


}
