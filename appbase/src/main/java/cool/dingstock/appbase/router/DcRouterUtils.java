package cool.dingstock.appbase.router;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cool.dingstock.appbase.R;
import cool.dingstock.appbase.annotations.ActivityAnim;
import cool.dingstock.appbase.constant.UTConstant;
import cool.dingstock.appbase.constant.WebviewConstant;
import cool.dingstock.appbase.custom.CustomerManager;
import cool.dingstock.appbase.mvp.BaseActivity;
import cool.dingstock.appbase.mvp.DCActivityManager;
import cool.dingstock.appbase.net.mobile.MobileHelper;
import cool.dingstock.appbase.net.parse.ParseCallback;
import cool.dingstock.appbase.share.ShareParams;
import cool.dingstock.appbase.share.ShareServiceHelper;
import cool.dingstock.appbase.share.ShareType;
import cool.dingstock.appbase.ut.UTHelper;
import cool.dingstock.appbase.util.LoginUtils;
import cool.dingstock.lib_base.BaseLibrary;
import cool.dingstock.lib_base.thread.ThreadPoolHelper;
import cool.dingstock.lib_base.util.AppUtils;
import cool.dingstock.lib_base.util.KtStringUtils;
import cool.dingstock.lib_base.util.Logger;
import cool.dingstock.lib_base.util.StringUtils;
import cool.dingstock.lib_base.util.ToastUtil;

public class DcRouterUtils {

    //nike
    public static final String SNKRS = "SNKRS://";
    public static final String snkrs = "snkrs://";
    //外部浏览器打开
    public static final String EXTERNAL_WEBVIEW_HOST = "https://app.dingstock.net/browser/external";
    //本地页面
    public static final String DC_HOST = "https://app.dingstock.net/";
    //小程序页面
    public static final String DC_MINI_HOST = "https://app.dingstock.net/miniProgram/";
    public static final String DC_BP_HOST = "https://app.dingstock.net/bp/resolve/";
    public static final String DC_URL_COMMON = "https://app.dingstock.net/common/";
    public static final String DC_URL_COMMON_COPY = "https://app.dingstock.net/common/copy";
    public static final String DC_SHARE = "https://app.dingstock.net/share";
    public static final String DC_SHARE_LINK = "https://app.dingstock.net/share/link";
    public static final String DC_SHARE_MP = "https://app.dingstock.net/share/mp";
    public static final String DC_SHARE_IMAGE = "https://app.dingstock.net/share/image";
    //支持淘宝客
    public static final String DC_TAOBAOKE_TAOBAO = "https://app.dingstock.net/taobao";
    public static final String DC_TAOBAOKE_TMALL = "https://app.dingstock.net/tmall";
    //京东跳转
    public static final String DC_JD = "https://app.dingstock.net/jd";
    public static final String JD_SCHEME = "openapp.jdmobile";
    //http或者https前缀
    public static final String PREFIX_HTTP = "http";
    public static final String PREFIX_HTTPS = "https";
    //客服
    public static final String DC_CUSTOM = "https://app.dingstock.net/customerService/open";
    //秒杀功能
    public static final String BP_DESC = "https://share.dingstock.com.cn/bp/instructions.html";
    //BP商品链接说明
    public static final String BP_GOOD_LINK_DESC = "https://share.dingstock.com.cn/bp/uselink.html";
    public static final String BP_SUBMIT_AUTO = "https://share.dingstock.com.cn/bp/flassale.html";
    //参数都是驼峰，只是 兼容了 纯小写
    public static final String MIN_VER = "minVer";
    public static final String REQUIRE_AUTH = "requireAuth";
    public static final String NEED_AUTH = "1";
    public static final String NEED_LOGIN = "needLogin";
    public static final String TASK_ID = "taskId";
    private static final String DC_ACTION = "dc.action.router";
    //内部浏览器打开
    private static final String INTERNAL_WEBVIEW_HOST = "https://app.dingstock.net/browser/internal";
    //Hybird
    private static final String HYBIRD_WEBVIEW_HOST = "https://app.dingstock.net/browser/hybird";
    //shandw 本地浏览器 游戏页面
    private static final String SHANDW_WEBVIEW_HOST = "https://app.dingstock.net/browser/shandw";
    //需要外部打开的域名host
    private static final List<String> actionViewHostList = new ArrayList<>();

    static {
        actionViewHostList.add("m.poizon.com");
        actionViewHostList.add("www.oneniceapp.com");
        actionViewHostList.add("stockx.com");
        actionViewHostList.add("www.nike.com");
    }

    private DcRouterUtils() {
    }

    private static String getUriSite(Uri uri) {
        return uri.getScheme() + "://" + uri.getHost() + uri.getPath();
    }

    public static boolean isScheme(Uri uri) {
        if (null == uri) {
            return false;
        }
        String scheme = uri.getScheme();
        if (TextUtils.isEmpty(scheme)) {
            return false;
        }
        return !scheme.equals(PREFIX_HTTP) && !scheme.equals(PREFIX_HTTPS);
    }

    public static boolean isUrlNeedActionView(String urlStr) {
        if (TextUtils.isEmpty(urlStr)) {
            return false;
        }
        Uri uri = Uri.parse(urlStr);
        String host = uri.getHost();
        if (TextUtils.isEmpty(host)) {
            return false;
        }
        for (String actionViewUrlPrefix : actionViewHostList) {
            if (host.equals(actionViewUrlPrefix)) {
                return true;
            }
        }
        return false;
    }

    public static class Builder {

        private final Context mContext;

        private @ActivityAnim
        String mAnim;

        private Uri mUri;
        private Intent mIntent;
        private String packagePath;

        public Builder(@NonNull Context context, @NonNull String uriStr) {
            this.mContext = context;
            this.mIntent = new Intent();
            processUriStr(uriStr);
        }

        private void processUriStr(@NonNull String uriStr) {
            //checkUrl
            if (uriStr == null) {
                uriStr = "";
            }
            this.mUri = Uri.parse(uriStr);
            if (!checkUriParameterIsOk()) {//拦截器
                return;
            }

            //外部已经制定ACTION
            if (!TextUtils.isEmpty(this.mIntent.getAction())) {
                this.mUri = Uri.parse(uriStr);
                return;
            }
            //外部的页面
            if (uriStr.contains(EXTERNAL_WEBVIEW_HOST)) {
                this.mUri = Uri.parse(uriStr);
                this.mUri = Uri.parse(mUri.getQueryParameter("url"));
                this.mIntent.setAction(Intent.ACTION_VIEW);
                return;
            }
            //内部的url 本地界面 本地webView
            if (uriStr.contains(DC_HOST)) {
                this.mUri = Uri.parse(uriStr);
                this.mIntent.setAction(DC_ACTION);
                return;
            }
            //nike官网单独处理
            if (isNikeUrl(uriStr)) {
                this.mUri = Uri.parse(uriStr.replace("SNKRS://", "https://www.nike.com/launch/")
                        .replace("snkrs://", "https://www.nike.com/launch/")
                );
                this.mIntent.setAction(Intent.ACTION_VIEW);
                return;
            }
            //需要丢到外面的https 或者 http
            if (isUrlNeedActionView(uriStr)) {
                this.mUri = Uri.parse(uriStr);
                this.mIntent.setAction(Intent.ACTION_VIEW);
                return;
            }
            //内部的url http://www.baidu.com
            if (uriStr.contains(PREFIX_HTTP) || uriStr.contains(PREFIX_HTTPS)) {
                this.mUri = Uri.parse(WebviewConstant.Uri.INDEX);
                this.mUri = this.mUri.buildUpon().appendQueryParameter("url", uriStr).build();
                this.mIntent.setAction(DC_ACTION);
                return;
            }
            this.mUri = Uri.parse(uriStr);
            this.mIntent.setAction(Intent.ACTION_VIEW);
        }

        private boolean checkUriParameterIsOk() {
            try {
                String requireAuth = queryUriParameter(NEED_LOGIN);
                if (!StringUtils.isEmpty(requireAuth)) {
                    if (!checkoutLogin(requireAuth)) return false;
                }
                requireAuth = queryUriParameter(REQUIRE_AUTH);
                if (!checkoutLogin(requireAuth)) return false;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        /**
         * 兼容大小写查询参数
         */
        private String queryUriParameter(String key) {
            String queryParameter = mUri.getQueryParameter(key);
            if (TextUtils.isEmpty(queryParameter)) {
                queryParameter = mUri.getQueryParameter(key.toLowerCase());
            }
            return queryParameter;
        }

        private boolean checkoutLogin(String requireAuth) {
            if (NEED_AUTH.equalsIgnoreCase(requireAuth) || "true".equalsIgnoreCase(requireAuth)) {//需要登录
                return LoginUtils.INSTANCE.isLoginAndRequestLogin(mContext);
            }
            return true;
        }

        private boolean isNikeUrl(String url) {
            if (TextUtils.isEmpty(url)) {
                return false;
            }
            if (url.startsWith("SNKRS://") || url.startsWith("snkrs://")) {
                return true;
            }
            return url.contains("www.nike.com");
        }

        public Builder(@NonNull Context context, @NonNull String uriStr, @NonNull String action) {
            this.mContext = context;
            this.mIntent = new Intent();
            this.mIntent.setAction(action);
            processUriStr(uriStr);
        }

        public Intent getIntent() {
            checkIntent();
            checkUri();
            return this.mIntent;

        }

        /**
         * Set the Intent to Router builder.
         *
         * @param intent The Intent to start.
         */

        public Builder setIntent(@NonNull Intent intent) {
            this.mIntent = intent;
            return this;
        }

        private void checkIntent() {
            if (null == this.mIntent) {
                this.mIntent = new Intent();
                Logger.i("Creates a new Intent.");
            }
        }

        @SuppressLint("WrongConstant")
        public boolean checkUri() {
            this.mIntent.setData(this.mUri);
            //外部的页面都统一返回 成功
            if (!DC_ACTION.equals(this.mIntent.getAction())) {
                return true;
            }
            //盯链内部页面H5返回
            if (getUriSite(this.mUri).contains(INTERNAL_WEBVIEW_HOST)) {
                return true;
            }
            //盯链内部页面H5返回
            if (getUriSite(this.mUri).contains(HYBIRD_WEBVIEW_HOST)) {
                return true;
            }
            //盯链内部页面H5返回
            if (getUriSite(this.mUri).contains(SHANDW_WEBVIEW_HOST)) {
                return true;
            }
            //盯链内部native页面检查版本支持
            if (getUriSite(this.mUri).contains(DC_HOST) && !checkUriVersion(this.mUri)) {
                Logger.w("Uri=" + getUriSite(this.mUri), " version > " +
                        AppUtils.INSTANCE.getVersionName(BaseLibrary.getInstance().getContext()) + " show update Dialog");
                ThreadPoolHelper.getInstance().runOnUiThread(()
                        -> ToastUtil.getInstance().makeTextAndShow(mContext, "当前版本不支持", Toast.LENGTH_SHORT));
                showUpdateTipDialog();
                return false;
            }
            List<ResolveInfo> resolveInfoList = this.mContext.getPackageManager()
                    .queryIntentActivities(this.mIntent, PackageManager.GET_INTENT_FILTERS);
            List<ResolveInfo> serviceList = this.mContext.getPackageManager()
                    .queryIntentServices(this.mIntent, PackageManager.GET_INTENT_FILTERS);
            if ((null == serviceList || serviceList.isEmpty()) && (null == resolveInfoList || resolveInfoList.isEmpty())) {//不是Activity 也不是 Service
                Logger.i(String.format("Not Found the %1$s activity. so can't start.", this.mUri.toString()));
                ThreadPoolHelper.getInstance().runOnUiThread(()
                        -> ToastUtil.getInstance().makeTextAndShow(mContext, "未找到对应页面", Toast.LENGTH_SHORT));
                return false;
            }
            return true;
        }

        private void showUpdateTipDialog() {
            BaseActivity topActivity = DCActivityManager.getInstance().getTopActivity();
            if (null == topActivity) {
                return;
            }
            if (!(topActivity instanceof BaseActivity)) {
                return;
            }
            BaseActivity dcActivity = topActivity;
            dcActivity.makeAlertDialog().setMessage("当前版本暂不支持该功能，请前往应用商店更新")
                    .setNegativeButton(R.string.common_cancel, (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("去下载", (dialog, which) -> {
                        MobileHelper.getInstance().getDownLoadUrl(new ParseCallback<String>() {
                            @Override
                            public void onSucceed(String data) {
                                if (TextUtils.isEmpty(data)) {
                                    return;
                                }
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                dcActivity.startActivity(intent);
                            }

                            @Override
                            public void onFailed(String errorCode, String errorMsg) {

                            }
                        });
                    }).show();
        }

        private boolean checkUriVersion(Uri uri) {
            if (null == uri) {
                return false;
            }
            //兼容 minVer 和 minver
            String urlVersion = queryUriParameter(MIN_VER);
            if (TextUtils.isEmpty(urlVersion)) {
                return true;
            }
            return KtStringUtils.INSTANCE.compareVersion(urlVersion, AppUtils.INSTANCE.getVersionName(BaseLibrary.getInstance().getContext())) <= 0;
        }

        public Builder anim(@ActivityAnim String anim) {
            this.mAnim = anim;
            return this;
        }

        public Builder setPackage(String packagePath) {
            this.packagePath = packagePath;
            return this;
        }

        /**
         * Set the general action to be performed.
         *
         * @param action An action name, such as ACTION_VIEW.  Application-specific actions should
         *               be prefixed with the vendor's package name.
         * @see Intent#setAction(String)
         */
        public Builder setAction(@NonNull String action) {
            checkIntent();
            this.mIntent.setAction(action);
            return this;
        }

        /**
         * Set special flags controlling how this Intent is handled.
         *
         * @param flags The desired flags.
         * @see Intent#setFlags(int)
         */
        public Builder setFlags(int flags) {
            checkIntent();
            this.mIntent.setFlags(flags);
            return this;
        }

        /**
         * dd additional flags to the intent (or with existing flags value).
         *
         * @param flags The desired flags.
         * @see Intent#addFlags(int)
         */
        public Builder addFlags(int flags) {
            checkIntent();
            this.mIntent.addFlags(flags);
            return this;
        }

        /**
         * Set an explicit MIME data type.
         *
         * @param type The MIM type of the data being handled by this Intent.
         * @see Intent#setType(String)
         */
        public Builder setType(@NonNull String type) {
            checkIntent();
            this.mIntent.setType(type);
            return this;
        }

        /**
         * Add extended data to the Intent.
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The boolean data value.
         * @see Intent#putExtra(String, boolean)
         */
        public Builder putExtra(@NonNull String name, boolean value) {
            checkIntent();
            this.mIntent.putExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the Intent.
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The boolean array data value.
         * @see Intent#putExtra(String, boolean[])
         */
        public Builder putExtra(@NonNull String name, boolean[] value) {
            checkIntent();
            this.mIntent.putExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the Intent.
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The byte data value.
         * @see Intent#putExtra(String, byte)
         */
        public Builder putExtra(@NonNull String name, byte value) {
            checkIntent();
            this.mIntent.putExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the Intent.
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The byte array data value.
         * @see Intent#putExtra(String, byte[])
         */
        public Builder putExtra(@NonNull String name, byte[] value) {
            checkIntent();
            this.mIntent.putExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the Intent.
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The char data value.
         * @see Intent#putExtra(String, char)
         */
        public Builder putExtra(@NonNull String name, char value) {
            checkIntent();
            this.mIntent.putExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the Intent.
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The char array data value.
         * @see Intent#putExtra(String, char[])
         */
        public Builder putExtra(@NonNull String name, char[] value) {
            checkIntent();
            this.mIntent.putExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the Intent.  T
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The short data value.
         * @see Intent#putExtra(String, short)
         */
        public Builder putExtra(@NonNull String name, short value) {
            checkIntent();
            this.mIntent.putExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the Intent.
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The short array data value.
         * @see Intent#putExtra(String, short[])
         */
        public Builder putExtra(@NonNull String name, short[] value) {
            checkIntent();
            this.mIntent.putExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the Intent.
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The integer data value.
         * @see Intent#putExtra(String, int)
         */
        public Builder putExtra(@NonNull String name, int value) {
            checkIntent();
            this.mIntent.putExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the Intent.
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The int array data value.
         * @see Intent#putExtra(String, int[])
         */
        public Builder putExtra(@NonNull String name, int[] value) {
            checkIntent();
            this.mIntent.putExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the Intent.
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The long data value.
         * @see Intent#putExtra(String, long)
         */
        public Builder putExtra(@NonNull String name, long value) {
            checkIntent();
            this.mIntent.putExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the Intent.
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The byte array data value.
         * @see Intent#putExtra(String, long[])
         */
        public Builder putExtra(@NonNull String name, long[] value) {
            checkIntent();
            this.mIntent.putExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the Intent.
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The float data value.
         * @see Intent#putExtra(String, float)
         */
        public Builder putExtra(@NonNull String name, float value) {
            checkIntent();
            this.mIntent.putExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the Intent.
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The float array data value.
         * @see Intent#putExtra(String, float[])
         */
        public Builder putExtra(@NonNull String name, float[] value) {
            checkIntent();
            this.mIntent.putExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the Intent.
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The double data value.
         * @see Intent#putExtra(String, double)
         */
        public Builder putExtra(@NonNull String name, double value) {
            checkIntent();
            this.mIntent.putExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the Intent.
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The double array data value.
         * @see Intent#putExtra(String, double[])
         */
        public Builder putExtra(@NonNull String name, double[] value) {
            checkIntent();
            this.mIntent.putExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the Intent.
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The String data value.
         * @see Intent#putExtra(String, String)
         */
        public Builder putExtra(@NonNull String name, String value) {
            checkIntent();
            this.mIntent.putExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the Intent.
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The String array data value.
         * @see Intent#putExtra(String, String[])
         */
        public Builder putExtra(@NonNull String name, String[] value) {
            checkIntent();
            this.mIntent.putExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the Intent.
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The CharSequence data value.
         * @see Intent#putExtra(String, CharSequence)
         */
        public Builder putExtra(@NonNull String name, CharSequence value) {
            checkIntent();
            this.mIntent.putExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the Intent.
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The CharSequence array data value.
         * @see Intent#putExtra(String, CharSequence)
         */
        public Builder putExtra(@NonNull String name, CharSequence[] value) {
            checkIntent();
            this.mIntent.putExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the Intent.
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The Parcelable data value.
         * @see Intent#putExtra(String, Parcelable)
         */
        public Builder putExtra(@NonNull String name, Parcelable value) {
            checkIntent();
            this.mIntent.putExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the Intent.
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The Parcelable[] data value.
         * @see Intent#putExtra(String, Parcelable[])
         */
        public Builder putExtra(@NonNull String name, Parcelable[] value) {
            checkIntent();
            this.mIntent.putExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the Intent.
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The Serializable data value.
         * @see Intent#putExtra(String, Serializable)
         */
        public Builder putExtra(@NonNull String name, Serializable value) {
            checkIntent();
            this.mIntent.putExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the Intent.
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The ArrayList<Parcelable> data value.
         * @see Intent#putParcelableArrayListExtra(String, ArrayList)
         */
        public Builder putParcelableArrayListExtra(@NonNull String name, ArrayList<? extends Parcelable> value) {
            checkIntent();
            this.mIntent.putParcelableArrayListExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the Intent.
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The ArrayList<Integer> data value.
         * @see Intent#putIntegerArrayListExtra(String, ArrayList)
         */
        public Builder putIntegerArrayListExtra(@NonNull String name, ArrayList<Integer> value) {
            checkIntent();
            this.mIntent.putIntegerArrayListExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the Intent.
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The ArrayList<String> data value.
         * @see Intent#putStringArrayListExtra(String, ArrayList)
         */
        public Builder putStringArrayListExtra(@NonNull String name, ArrayList<String> value) {
            checkIntent();
            this.mIntent.putStringArrayListExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the Intent.
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The ArrayList<CharSequence> data value.
         * @see Intent#putCharSequenceArrayListExtra(String, ArrayList)
         */
        public Builder putCharSequenceArrayListExtra(@NonNull String name, ArrayList<CharSequence> value) {
            checkIntent();
            this.mIntent.putCharSequenceArrayListExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the Intent.
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The Bundle data value.
         * @see Intent#putExtra(String, Bundle)
         */
        public Builder putExtra(@NonNull String name, Bundle value) {
            checkIntent();
            this.mIntent.putExtra(name, value);
            return this;
        }

        /**
         * Add a set of extended data to the Intent.
         *
         * @param extras The Bundle of extras to add to this Intent.
         * @see Intent#putExtras(Bundle)
         */
        public Builder putExtras(@NonNull Bundle extras) {
            checkIntent();
            this.mIntent.putExtras(extras);
            return this;
        }

        /**
         * Encodes the key and value and then appends the parameter to the query string.
         *
         * @param key   which will be encoded
         * @param value which will be encoded
         */
        public Builder putUriParameter(@NonNull String key, @NonNull String value) {
            if (null == this.mUri) {
                throw new RouterException("The uri can't be Null.");
            }

            this.mUri = this.mUri.buildUpon().appendQueryParameter(key, value).build();
            return this;
        }

        public void start() {
            start(false);
        }

        /**
         * Launch a new activity. You will not receive any information about when the activity
         * exits.
         *
         * @throws RouterException
         * @throws android.content.ActivityNotFoundException
         * @see Activity#startActivity(Intent, Bundle)
         */
        public void start(boolean enableAnimation) {
            if (mUri == null) {
                return;
            }

            if (!StringUtils.isEmpty(packagePath)) {
                mIntent.setPackage(packagePath);
//            }else {
//                mIntent.setPackage(packagePath);
            }
            if (mUri.getQueryParameter("url") != null) {
                Logger.d("router:", mUri.getQueryParameter("url") + "----------------------------------------------------");
            }
            UTHelper.commonEvent(UTConstant.ROUTE.ROUTE, "url", mUri.getQueryParameter("url"));
            if (getUriSite(this.mUri).startsWith(DC_SHARE_IMAGE)) {
                ThreadPoolHelper.getInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        shareImg();
                    }
                });
                return;
            }
            if (getUriSite(this.mUri).startsWith(DC_SHARE_LINK)) {
                ThreadPoolHelper.getInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        shareLink();
                    }
                });

                return;
            }
            if (getUriSite(this.mUri).startsWith(DC_SHARE_MP)) {
                ThreadPoolHelper.getInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        shareMp();
                    }
                });

                return;
            }
            if (getUriSite(this.mUri).contains(DC_MINI_HOST)) {
                MiniRouter.openMiNi(this.mUri);
                return;
            }
            //跳转到天猫
            if (getUriSite(this.mUri).contains(DC_TAOBAOKE_TAOBAO)) {
                String url = mUri.getQueryParameter("url");
                if (null == url || url.isEmpty()) {
                    return;
                }
                return;
            }
            if (getUriSite(this.mUri).contains(DC_TAOBAOKE_TMALL)) {
                String url = mUri.getQueryParameter("url");
                if (null == url || url.isEmpty()) {
                    return;
                }
                return;
            }
            //跳转到京东App
            if (getUriSite(this.mUri).contains(DC_JD)) {
                String url = mUri.getQueryParameter("url");
                if (null == url || url.isEmpty()) {
                    return;
                }
                return;
            }
            if (this.mUri.toString().contains(JD_SCHEME)) {
                String url = mUri.getQueryParameter("url");
                if (null == url || url.isEmpty()) {
                    return;
                }
                return;
            }
            //打开客服中心
            if (getUriSite(this.mUri).startsWith(DC_CUSTOM)) {
                CustomerManager.getInstance().showCustomServiceActivity(mContext);
                return;
            }
            if (null == this.mContext || !checkUri()) {
                return;
            }

            if (!(this.mContext instanceof Activity) || isScheme(mUri)) {
                this.mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            Logger.i(String.format("Start the %1$s  action %2$s", this.mUri.toString(), this.mIntent.getAction()));
            try {
                if (((Build.MANUFACTURER.equalsIgnoreCase("OPPO"))
                        || (Build.MANUFACTURER.equalsIgnoreCase("VIVO")))
                        && Intent.ACTION_VIEW.equals(this.mIntent.getAction())
                        && !isScheme(this.mUri)) {
                    Intent chooser = Intent.createChooser(this.mIntent, "浏览器");
                    this.mContext.startActivity(chooser);
                } else {
                    if (checkIsService()) {
                        this.mContext.startService(this.mIntent);
                    } else {
                        this.mContext.startActivity(this.mIntent);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.getInstance().makeTextAndShow(this.mContext, "不支持链接:" + this.mUri.getScheme(), Toast.LENGTH_LONG);
                UTHelper.routerException(this.mUri.toString(), this.mIntent.getAction());
            }
            if ((this.mContext instanceof Activity) && enableAnimation) {
//                if (ActivityAnim.PRESENT.equals(this.mAnim)) {
//                    ((Activity) this.mContext).overridePendingTransition(R.anim.common_activity_in,
//                            R.anim.common_activity_out);
//                }
                ((Activity) this.mContext).overridePendingTransition(cool.dingstock.appbase.R.anim.on_activity_open_enter,
                        cool.dingstock.appbase.R.anim.on_activity_open_exit);
            }
        }

        /**
         * 分享图片
         */
        private void shareImg() {
            String shareUrl = mUri.getQueryParameter("url");
            String title = mUri.getQueryParameter("title");
            if (TextUtils.isEmpty(title)) {
                title = mContext.getString(R.string.app_name);
            }
            String content = mUri.getQueryParameter("body");
            if (TextUtils.isEmpty(content)) {
                content = mContext.getString(R.string.common_dc_slogan);
            }
            String imageUrl = mUri.getQueryParameter("imageUrl");
            String platformStr = mUri.getQueryParameter("platforms");
            String pwdUrl = mUri.getQueryParameter("pwdUrl");

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
            ShareServiceHelper.INSTANCE.share(mContext, type);
        }

        /**
         * 分享小程序
         */
        private void shareMp() {
            String shareUrl = mUri.getQueryParameter("url");
            String title = mUri.getQueryParameter("title");
            if (TextUtils.isEmpty(title)) {
                title = mContext.getString(R.string.app_name);
            }
            String content = mUri.getQueryParameter("body");
            if (TextUtils.isEmpty(content)) {
                content = mContext.getString(R.string.common_dc_slogan);
            }
            String path = mUri.getQueryParameter("path");
            if (TextUtils.isEmpty(content)) {
                path = "";
            }
            String imageUrl = mUri.getQueryParameter("imageUrl");
            String platformStr = mUri.getQueryParameter("platforms");
            String pwdUrl = mUri.getQueryParameter("pwdUrl");

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
            type.setParams(params);
            ShareServiceHelper.INSTANCE.share(mContext, type);
        }

        /**
         * 分享链接
         */
        private void shareLink() {
            String title = mUri.getQueryParameter("title");
            if (TextUtils.isEmpty(title)) {
                title = mContext.getString(R.string.app_name);
            }
            String content = mUri.getQueryParameter("body");
            if (TextUtils.isEmpty(content)) {
                content = mContext.getString(R.string.common_dc_slogan);
            }
            String url = mUri.getQueryParameter("url");
            if (TextUtils.isEmpty(url)) {
                url = "";
            }

            String imgUrl = mUri.getQueryParameter("imageUrl");
            if (TextUtils.isEmpty(imgUrl)) {
                imgUrl = "";
            }

            String platformStr = mUri.getQueryParameter("platforms");
            String pwdUrl = mUri.getQueryParameter("pwdUrl");

            ShareType type = ShareType.Link;
            ShareParams params = new ShareParams();
            params.setTitle(title);
            params.setContent(content);
            params.setImageUrl(imgUrl);
            params.setLink(url);
            if (!TextUtils.isEmpty(platformStr)) {
                params.setPlatformStr(platformStr);
            }
            if (!TextUtils.isEmpty(pwdUrl)) {
                params.setPwdUrl(pwdUrl);
            }
            type.setParams(params);
        }

        @SuppressLint("WrongConstant")
        private boolean checkIsService() {
            List<ResolveInfo> serviceList = this.mContext.getPackageManager()
                    .queryIntentServices(this.mIntent, PackageManager.GET_INTENT_FILTERS);
            if (null == serviceList || serviceList.isEmpty()) {
                Logger.i(String.format("Not Found the %1$s activity. so can't start.", this.mUri.toString()));
                return false;
            }
            return true;
        }

        /**
         * Launch an activity for which you would like a result when it finished. When this activity
         * exits, your onActivityResult() method will be called with the given requestCode.
         *
         * @param requestCode If >= 0, this code will be returned in onActivityResult() when the
         *                    activity exits.
         * @throws RouterException
         * @throws android.content.ActivityNotFoundException
         * @see Activity#startActivityForResult(Intent, int)
         */
        public void startForResult(int requestCode) {
            if (null == this.mContext || !checkUri() || !(this.mContext instanceof Activity)) {
                ToastUtil.getInstance().makeTextAndShow(this.mContext, "未找到对应页面", Toast.LENGTH_SHORT);
                return;
            }
            Logger.i(String.format("Start the %1$s Activity by requestCode: %2$s.", this.mUri.toString(), requestCode));
            ((Activity) this.mContext).startActivityForResult(this.mIntent, requestCode);
        }

        public void startForResult(int requestCode, boolean enableAni) {
            if (null == this.mContext || !checkUri() || !(this.mContext instanceof Activity)) {
                ToastUtil.getInstance().makeTextAndShow(this.mContext, "未找到对应页面", Toast.LENGTH_SHORT);
                return;
            }
            Logger.i(String.format("Start the %1$s Activity by requestCode: %2$s.", this.mUri.toString(), requestCode));
            ((Activity) this.mContext).startActivityForResult(this.mIntent, requestCode);
            if (enableAni) {
                ((Activity) this.mContext).overridePendingTransition(cool.dingstock.appbase.R.anim.on_activity_open_enter,
                        cool.dingstock.appbase.R.anim.on_activity_open_exit);
            }
        }
    }

}
