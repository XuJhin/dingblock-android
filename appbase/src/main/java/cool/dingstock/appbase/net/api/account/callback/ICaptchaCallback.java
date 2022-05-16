package cool.dingstock.appbase.net.api.account.callback;

import cool.dingstock.appbase.entity.bean.account.NameValueBean;

public interface ICaptchaCallback {
    void onVerifyCallback(NameValueBean captchaBean);
}
