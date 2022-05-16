package cool.dingstock.appbase.widget.dialog;

import android.content.Context;

import com.tencent.captchasdk.TCaptchaDialog;

import cool.dingstock.appbase.net.api.account.callback.ICaptchaCallback;
import cool.dingstock.appbase.entity.bean.account.CaptchaBean;
import cool.dingstock.lib_base.json.JSONHelper;
import cool.dingstock.lib_base.util.StringUtils;

/**
 * @author WhenYoung
 * CreateAt Time 2020/11/20  15:17
 */
public class CaptchaDialogFactory {
    private static final String TECENT_CAPTCHA_APPID = "2062739169";
    private boolean isValidation = false;

    public CaptchaDialog getCaptchaDialog(Context context,
                                          ICaptchaCallback captchaCallback,
                                          OnVerifyCancelListener onCancelListener) {
        return new CaptchaDialog(context, captchaCallback, onCancelListener);
    }

    public interface OnVerifyCancelListener {
        void onCancel();
    }

    public class CaptchaDialog extends TCaptchaDialog {

        public CaptchaDialog(Context context,
                             ICaptchaCallback captchaCallback,
                             OnVerifyCancelListener onCancelListener) {
            super(context, TECENT_CAPTCHA_APPID, jsonObject -> {
                CaptchaBean captchaBean = JSONHelper.fromJson(JSONHelper.toJson(jsonObject), CaptchaBean.class);
                if (null != captchaBean
                        && null != captchaBean.getNameValuePairs()
                        && !StringUtils.isEmpty(captchaBean.getNameValuePairs().getTicket())) {
                    isValidation = true;
                    captchaCallback.onVerifyCallback(captchaBean.getNameValuePairs());
                }
            }, null);
            setOnDismissListener(dialog -> {
                if (!isValidation && onCancelListener != null) {
                    onCancelListener.onCancel();
                }
            });
        }
    }

}
