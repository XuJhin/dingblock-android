package cool.dingstock.appbase.router;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import cool.dingstock.appbase.R;
import cool.dingstock.appbase.mvp.BaseActivity;
import cool.dingstock.appbase.mvp.DCActivityManager;
import cool.dingstock.appbase.share.ShareParams;
import cool.dingstock.appbase.share.ShareServiceHelper;
import cool.dingstock.appbase.share.ShareType;
import cool.dingstock.appbase.ut.UTHelper;

public class ExternalRouter {

    public static void route(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        BaseActivity topActivity = DCActivityManager.getInstance().getTopActivity();
        Uri mUri = Uri.parse(url);
        if (null == topActivity) {
            return;
        }
        try {
            if (url.equals("SNKRS://") || url.equals("snkrs://")) {
                url = url.replace("SNKRS://", "https://www.nike.com/launch/")
                        .replace("snkrs://", "https://www.nike.com/launch/");
            }
            if (url.startsWith(DcRouterUtils.DC_SHARE_IMAGE)) {
                shareImg(mUri, topActivity);
                return;
            }
            if (url.startsWith(DcRouterUtils.DC_SHARE_LINK)) {
                shareLink(mUri, topActivity);
                return;
            }
            if (url.startsWith(DcRouterUtils.DC_SHARE_MP)) {
                shareMp(mUri, topActivity);
                return;
            }
            if (url.contains(DcRouterUtils.DC_MINI_HOST)) {
                MiniRouter.openMiNi(Uri.parse(url));
                return;
            }
            if (url.startsWith(DcRouterUtils.DC_HOST)) {
                DcUriRequest builder = new DcUriRequest(topActivity, url);
                builder.start();
                return;
            }
            Intent startIntent = new Intent();
            startIntent.setData(Uri.parse(url));
            startIntent.setAction(Intent.ACTION_VIEW);
            //oppo  vivo 没有询问选择器的弹窗，真🤮🤮🤮🤮🤮
            if (Build.MANUFACTURER.equalsIgnoreCase("OPPO")
                    || Build.MANUFACTURER.equalsIgnoreCase("VIVO")) {
                Intent chooser = Intent.createChooser(startIntent, "浏览器");
                topActivity.startActivity(chooser);
            } else {
                topActivity.startActivity(startIntent);
            }

        } catch (Exception e) {
            e.printStackTrace();
            UTHelper.routerException(url, Intent.ACTION_VIEW);
        }
    }

    /**
     * 分享图片
     */
    private static void shareImg(Uri uri, Context context) {
        String shareUrl = uri.getQueryParameter("url");
        String title = uri.getQueryParameter("title");
        if (TextUtils.isEmpty(title)) {
            title = context.getString(R.string.app_name);
        }
        String content = uri.getQueryParameter("body");
        if (TextUtils.isEmpty(content)) {
            content = context.getString(R.string.common_dc_slogan);
        }
        String imageUrl = uri.getQueryParameter("imageUrl");
        String platformStr = uri.getQueryParameter("platforms");
        String pwdUrl = uri.getQueryParameter("pwdUrl");

        ShareType type = ShareType.Image;
        ShareParams params = new ShareParams();
        params.setTitle(title);
        params.setImageUrl(imageUrl);
        params.setLink(shareUrl);
        if (!TextUtils.isEmpty(platformStr)) {
            params.setPlatformStr(platformStr);
        }
        if (!TextUtils.isEmpty(pwdUrl)) {
            params.setPwdUrl(pwdUrl);
        }
        type.setParams(params);
        type.setParams(params);
        ShareServiceHelper.INSTANCE.share(context,type);
    }

    /**
     * 分享小程序
     */
    private static void shareMp(Uri uri, Context context) {
        String shareUrl = uri.getQueryParameter("url");
        String title = uri.getQueryParameter("title");
        if (TextUtils.isEmpty(title)) {
            title = context.getString(R.string.app_name);
        }
        String content = uri.getQueryParameter("body");
        if (TextUtils.isEmpty(content)) {
            content = context.getString(R.string.common_dc_slogan);
        }
        String path = uri.getQueryParameter("path");
        if (TextUtils.isEmpty(content)) {
            path = "";
        }
        String imageUrl = uri.getQueryParameter("imageUrl");
        String platformStr = uri.getQueryParameter("platforms");
        String pwdUrl = uri.getQueryParameter("pwdUrl");

        ShareType type = ShareType.Mp;
        ShareParams params = new ShareParams();
        params.setTitle(title);
        params.setMpPath(path);
        params.setImageUrl(imageUrl);
        params.setLink(shareUrl);
        if (!TextUtils.isEmpty(platformStr)) {
            params.setPlatformStr(platformStr);
        }
        if (!TextUtils.isEmpty(pwdUrl)) {
            params.setPwdUrl(pwdUrl);
        }
        type.setParams(params);
        ShareServiceHelper.INSTANCE.share(context,type);
    }

    /**
     * 分享链接
     */
    private static void shareLink(Uri uri, Context context) {
        String shareUrl = uri.getQueryParameter("url");
        String title = uri.getQueryParameter("title");
        if (TextUtils.isEmpty(title)) {
            title = context.getString(R.string.app_name);
        }
        String content = uri.getQueryParameter("body");
        if (TextUtils.isEmpty(content)) {
            content = context.getString(R.string.common_dc_slogan);
        }
        String imageUrl = uri.getQueryParameter("imageUrl");

        String platformStr = uri.getQueryParameter("platforms");
        String pwdUrl = uri.getQueryParameter("pwdUrl");

        ShareType type = ShareType.Link;
        ShareParams params = new ShareParams();
        params.setTitle(title);
        params.setContent(content);
        params.setImageUrl(imageUrl);
        params.setLink(shareUrl);
        if (!TextUtils.isEmpty(platformStr)) {
            params.setPlatformStr(platformStr);
        }
        if (!TextUtils.isEmpty(pwdUrl)) {
            params.setPwdUrl(pwdUrl);
        }
        type.setParams(params);
        type.setParams(params);
        ShareServiceHelper.INSTANCE.share(context,type);
    }

}
