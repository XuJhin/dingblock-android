package cool.mobile.account.share;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;


public class AuthorizeStart {

    public static void authorize(IAuthorizeCallback callback) {
        Platform platform = ShareSDK.getPlatform(Wechat.NAME);
        platform.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                if (null != callback) {
                    callback.onAuthorizeSuccess(platform.getDb().getUserId(), platform.getDb().getToken());
                }
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                if (null != callback) {
                    callback.onAuthorizeFailed(throwable.getLocalizedMessage(), throwable.getMessage());
                }
            }

            @Override
            public void onCancel(Platform platform, int i) {
                if (null != callback) {
                    callback.onAuthorizeFailed("cancel", "cancel");
                }
            }
        });
        platform.showUser(null);
    }
}
