package cool.dingstock.appbase.exception;

import android.text.TextUtils;

/**
 * Author: Shper
 * Description: 未绑定时异常
 * Version: V0.1 2016/12/28
 */
public class MVPNotBindException extends RuntimeException {

    public MVPNotBindException(String errMsg) {
        super(!TextUtils.isEmpty(errMsg) ? errMsg : "MVPNotBindException");
    }

}
