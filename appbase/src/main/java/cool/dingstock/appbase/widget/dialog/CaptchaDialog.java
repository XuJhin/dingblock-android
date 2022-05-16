package cool.dingstock.appbase.widget.dialog;

import android.content.Context;

import androidx.annotation.NonNull;

import com.tencent.captchasdk.TCaptchaDialog;

import cool.dingstock.appbase.net.api.account.callback.ICaptchaCallback;
import cool.dingstock.appbase.entity.bean.account.CaptchaBean;
import cool.dingstock.lib_base.json.JSONHelper;

public class CaptchaDialog extends TCaptchaDialog {
    private static String TECENT_CAPTCHA_APPID = "2062739169";


    public CaptchaDialog(@NonNull Context context, ICaptchaCallback captchaCallback,OnShowListener onShowListener) {
        super(context, TECENT_CAPTCHA_APPID, jsonObject -> {
            CaptchaBean captchaBean = JSONHelper.fromJson(JSONHelper.toJson(jsonObject), CaptchaBean.class);
            if (null != captchaBean && null != captchaBean.getNameValuePairs()) {
                captchaCallback.onVerifyCallback(captchaBean.getNameValuePairs());
            }
        }, null);
        setOnShowListener(onShowListener);
    }






}
