package cool.dingstock.mobile.alipay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.alipay.sdk.app.PayTask;

import java.util.Map;

import cool.dingstock.mobile.PayCallback;


public class AliPayActivity extends Activity {

    private static final String PAY_SUCCESS_CODE = "9000";
    private static final String PAY_CANCEL_CODE = "6001";

    public static final int SDK_PAY_FLAG = 1000;
    private static final String KEY_ORDER = "order";

    public static PayCallback mCallback;

    public static void setMCallback(PayCallback mCallback) {
        AliPayActivity.mCallback = mCallback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String orderInfo = getIntent().getStringExtra(KEY_ORDER);
        if (TextUtils.isEmpty(orderInfo)) {
            finish();
            callbackFailed("ERROR_ORDER_INFO_EMPTY", "订单信息为空");
            return;
        }
        aliPay(orderInfo);
    }

    public static void startPay(Context context, String order, @NonNull PayCallback callback) {
        Intent intent = new Intent(context, AliPayActivity.class);
        intent.putExtra(KEY_ORDER, order);
        context.startActivity(intent);
        mCallback = callback;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == SDK_PAY_FLAG) {
                @SuppressWarnings("unchecked")
                PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                String resultStatus = payResult.getResultStatus();
                if (TextUtils.equals(resultStatus, PAY_SUCCESS_CODE)) {
                    callbackSuccess();
                } else if (TextUtils.equals(resultStatus, PAY_CANCEL_CODE)) {
                    callbackCancel();
                } else {
                    callbackFailed(resultStatus, payResult.getMemo());
                }
                finish();
            }
        }
    };

    private void aliPay(final String orderInfo) {
        final Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask aliPay = new PayTask(AliPayActivity.this);
                Map<String, String> result = aliPay.payV2(orderInfo, true);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }


    private void callbackFailed(String errorCode, String errorMsg) {
        if (null != mCallback) {
            mCallback.onFailed(errorCode, errorMsg);
        }
        mCallback = null;
    }


    private void callbackSuccess() {
        if (null != mCallback) {
            mCallback.onSucceed();
        }
        mCallback = null;
    }

    private void callbackCancel() {
        if (null != mCallback) {
            mCallback.onCancel();
        }
        mCallback = null;
    }

}
