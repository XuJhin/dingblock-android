package cool.dingstock.appbase.router;

import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import cool.dingstock.appbase.toast.TopToast;
import cool.dingstock.lib_base.BaseLibrary;
import cool.dingstock.appbase.constant.PayConstant;
import cool.dingstock.lib_base.util.Logger;

public class MiniRouter {

    private static final String KEY_PATH = "path";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_TYPE = "type";

    public static void openMiNi(Uri uri) {
        if (null == uri) {
            return;
        }
        String path = uri.getQueryParameter(KEY_PATH);
        String username = uri.getQueryParameter(KEY_USERNAME);
        String type = uri.getQueryParameter(KEY_TYPE);
        Logger.d("openMiNi  path=" + path + " ;username=" + username + " ;type=" + type);
        if (TextUtils.isEmpty(username)) {
            showToast("小程序参数错误");
            return;
        }
        IWXAPI iwxapi = WXAPIFactory.createWXAPI(BaseLibrary.getInstance().getContext(), PayConstant.WX_APP_ID, false);
        if (!iwxapi.isWXAppInstalled()) {
            showToast("未安装微信");
            return;
        }
        if (TextUtils.isEmpty(type)) {
            type = "release";
        }
        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        req.userName = username;
        if (!TextUtils.isEmpty(path)) {
            req.path = path;
        }
        switch (type) {
            case "test":
                req.miniprogramType = WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_TEST;
                break;
            case "preview":
                req.miniprogramType = WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_PREVIEW;
                break;
            default:
                req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;
        }
        iwxapi.sendReq(req);
    }


    private static void showToast(String text) {
        TopToast.INSTANCE.showToast(BaseLibrary.getInstance().getContext(), text, Toast.LENGTH_SHORT);
    }


}
