package cool.dingstock.mobile.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.jetbrains.annotations.NotNull;

import cool.dingstock.appbase.constant.PayConstant;
import cool.dingstock.lib_base.R;
import cool.dingstock.lib_base.util.Logger;
import cool.dingstock.mobile.BoxPayCallBack;
import cool.dingstock.mobile.PayCallback;
//
//public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
//
//    private static final int SUCCESS_CODE = 0;
//    private static final int FAILED_CODE = -1;
//    private static final int CANCEL_CODE = -2;
//    public static String ID = PayConstant.WX_APP_ID;
//
//    private IWXAPI api;
//
//
//    private static PayCallback mPayCallback;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.common_activity_empty);
//        api = WXAPIFactory.createWXAPI(this, ID);
//        api.handleIntent(getIntent(), this);
//    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        setIntent(intent);
//        api.handleIntent(intent, this);
//    }
//
//    @Override
//    public void onReq(BaseReq req) {
//    }
//
//    @Override
//    public void onResp(BaseResp resp) {
//        Logger.d("TestPay", " errCode = " + resp.errCode);
//        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//            if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//                callback(resp);
//                finish();
//            }
//        }
//    }
//
//    private void callback(BaseResp resp) {
//
//        Logger.d("TestPay", " errCode = " + resp.errCode);
//        if (null == mPayCallback) {
//            return;
//        }
//        switch (resp.errCode) {
//            case SUCCESS_CODE:
//                mPayCallback.onSucceed();
//                break;
//            case FAILED_CODE:
//                mPayCallback.onFailed(String.valueOf(resp.errCode), resp.errStr);
//                break;
//            case CANCEL_CODE:
//                mPayCallback.onCancel();
//                break;
//            default:
//                mPayCallback.onFailed("UNKOWN_ERROR_CODE", "UNKOWN_ERROR_CODE");
//        }
//        mPayCallback = null;
//    }
//
//    public void setApi(IWXAPI api) {
//        this.api = api;
//    }
//
//    public static void setmPayCallback(PayCallback mPayCallback) {
//        WXPayEntryActivity.mPayCallback = mPayCallback;
//    }
//
//    public static void setupBoxCallback(@NotNull BoxPayCallBack boxPayCallBack) {
//
//    }
//}