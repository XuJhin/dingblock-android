package cool.dingstock.appbase.constant;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface PayConstant {

    /**
     * the command types
     */
    @StringDef({PayType.ALI_PAY, PayType.WE_CHAT_PAY})
    @Retention(RetentionPolicy.SOURCE)
    @interface PayType {
        String ALI_PAY = "alipay";
        String WE_CHAT_PAY = "wechatPay";
    }

    String WX_APP_ID = "wxa5ea8aba36059455";

}
