package cool.dingstock.appbase.net.retrofit.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import cool.dingstock.appbase.constant.HomeConstant;
import cool.dingstock.appbase.constant.ServerConstant;
import cool.dingstock.appbase.entity.bean.account.DcLoginUser;
import cool.dingstock.appbase.entity.event.account.EventSessionInvalid;
import cool.dingstock.appbase.exception.DcException;
import cool.dingstock.appbase.mvp.DCActivityManager;
import cool.dingstock.appbase.net.mobile.MobileHelper;
import cool.dingstock.appbase.router.DcUriRequest;
import cool.dingstock.appbase.util.LoginUtils;
import cool.dingstock.lib_base.BaseLibrary;
import cool.dingstock.lib_base.BuildConfig;
import cool.dingstock.lib_base.util.AppUtils;
import cool.dingstock.lib_base.util.HttpsCertUtils;
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import kotlin.text.Charsets;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RxRetrofitServiceManager {
    private static final int DEFAULT_TIME_OUT = 5;//超时时间 5s
    private static final int DEFAULT_READ_TIME_OUT = 10;
    private static String serverUrl = "https://apiv2.dingstock.net";
    //	private static String serverUrl = "https://oversea-dev.dingstock.net";
    private static RxRetrofitServiceManager instance;
    private static RxRetrofitServiceManager instance1;
    private final Retrofit mRetrofit;
    private OkHttpClient okHttpClient;
    public Retrofit basicRetrofit;
    public OkHttpClient basicOkHttp;

    Interceptor createHeaderInterceptor() {

        String versionName = AppUtils.INSTANCE.getVersionName(BaseLibrary.getInstance().getContext());
        int versionCode = AppUtils.INSTANCE.getVersionCode(BaseLibrary.getInstance().getContext());
        //         添加公共参数拦截器
        Interceptor commonInterceptor = chain -> {
            DcLoginUser currentUser = LoginUtils.INSTANCE.getCurrentUser();
            String token = currentUser != null ? currentUser.getSessionToken() : "";

            String channel = MobileHelper.getInstance().getCurrentChannel();
            String brand = MobileHelper.getInstance().getBrand();

            String mode = "light";
            if (DCActivityManager.getInstance().getTopActivity() != null) {
                int nightMode = DCActivityManager.getInstance().getTopActivity().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                mode = nightMode == Configuration.UI_MODE_NIGHT_YES ? "dark" : "light";
            }

            Request request = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("version", ServerConstant.VERSION)
                    .addHeader("app", "Android" + versionName + "(" + versionCode + ")")
                    .addHeader("token", token)
                    .addHeader("dc_channel", channel)
                    .addHeader("brand", brand)
                    .addHeader("interface-style", mode)
                    .build();
            Response response = chain.proceed(request);

            /*这里可以获取响应体 */
            ResponseBody responseBody = response.peekBody(1024 * 1024);
            String result = responseBody.string();
            //统一处理 请求结果
            handleResult(result);
            return response;
        };
        return commonInterceptor;
    }

    private RxRetrofitServiceManager(Context context) {
        if (BuildConfig.DEBUG) {
            SharedPreferences share = context.getSharedPreferences("net_env", Context.MODE_PRIVATE);
            serverUrl = share.getString("env", "https://dc-oa-dev-0703.dingstock.net");
        }

        // 创建 OKHttpClient
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);//连接超时时间
        builder.writeTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);//写操作 超时时间
        builder.readTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);//读操作超时时间
        //添加证书
        HttpsCertUtils.SSLParams sslParams = HttpsCertUtils.getSSLParams(HttpsCertUtils.Protocol.SSL,
                null, null, null);
        builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        builder.addInterceptor(createHeaderInterceptor());
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(log -> print(Log.DEBUG, "dc-http-response:", log))
                .setLevel(HttpLoggingInterceptor.Level.BODY);
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(loggingInterceptor);
        }

        okHttpClient = builder.build();
        // 创建Retrofit
        mRetrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .baseUrl(serverUrl)
                .build();


        if (BuildConfig.DEBUG) {
            SharedPreferences share = context.getSharedPreferences("net_env", Context.MODE_PRIVATE);
            serverUrl = share.getString("env", "https://dc-oa-dev-0703.dingstock.net");
        }
        createBasicRequest();

    }

    private void createBasicRequest() {
        // 创建 OKHttpClient
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);//连接超时时间
        builder.writeTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);//写操作 超时时间
        builder.readTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);//读操作超时时间
        //添加证书
        HttpsCertUtils.SSLParams sslParams = HttpsCertUtils.getSSLParams(HttpsCertUtils.Protocol.SSL,
                null, null, null);
        builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(log -> print(Log.DEBUG, "dc-http-response:", log))
                .setLevel(HttpLoggingInterceptor.Level.BODY);
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(loggingInterceptor);
        }

        basicOkHttp = builder.build();
        // 创建Retrofit
        basicRetrofit = new Retrofit.Builder()
                .client(basicOkHttp)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .baseUrl(serverUrl)
                .build();
    }

    private void handleResult(String result) throws DcException {
        int code = 0;
        String msg = "";
        boolean isError = false;
//        if(StringUtils.isEmpty(result)){
//            throw new DcException(0,"服务器开小差");
//        }
        try {//统一处理在这里
//            Logger.d("http:", result);
            JSONObject jsonObject = new JSONObject(result);
            code = jsonObject.getInt("code");
            msg = jsonObject.getString("msg");
            isError = jsonObject.getBoolean("err");

        } catch (JSONException e) {
//			e.printStackTrace();
        }
        if ((code == ServerConstant.ErrorCode.INVALID_SESSION1 || code == ServerConstant.ErrorCode.INVALID_SESSION) && LoginUtils.INSTANCE.isLogin()) {//登录过期
            //这里保证只会有一次 EventSessionInvalid事件发送
            synchronized (LoginUtils.INSTANCE) {
                try {
                    if (LoginUtils.INSTANCE.isLogin()) {
                        LoginUtils.INSTANCE.loginOut();
                        EventBus.getDefault().post(new EventSessionInvalid(true));
                        new DcUriRequest(BaseLibrary.getInstance().getContext(), HomeConstant.Uri.TAB).start();
                    }
                } catch (Exception ignored) {
                }
            }
            throw new DcException(code, msg);
        }
        if (isError) {
            throw new DcException(code, msg);
        }
    }

    /**
     * 获取RetrofitServiceManager
     *
     * @return
     */
    public static RxRetrofitServiceManager getInstance() {
        return instance;
    }

    public static void init(Context context) {
        if (instance == null) {
            synchronized (RxRetrofitServiceManager.class) {
                if (instance == null) {
                    instance = new RxRetrofitServiceManager(context);
                }
            }
        }
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public Retrofit getmRetrofit() {
        return mRetrofit;
    }

    public void resetHeader() {
        okHttpClient = okHttpClient.newBuilder()
                .addInterceptor(createHeaderInterceptor())
                .build();
    }

    /**
     * 获取对应的Service
     *
     * @param service Service 的 class
     * @param <T>
     * @return
     */
    public <T> T create(Class<T> service) {
        return mRetrofit.create(service);
    }

    private String cutStr(byte[] bytes, int subLength) {
        if (bytes == null || subLength < 1) {
            return null;
        }
        if (subLength >= bytes.length) {
            return new String(bytes);
        }
        String subStr = new String(Arrays.copyOf(bytes, subLength));
        return subStr.substring(0, subStr.length() - 1);
    }

    private void print(int priority, String tag, String content) {
        if (content.length() < 1000) {
            Log.println(priority, tag, content);
            return;
        }
        int maxByteNum = 4000;
        byte[] bytes = content.getBytes(Charsets.UTF_8);
        if (maxByteNum >= bytes.length) {
            Log.println(priority, tag, content);
            return;
        }
        while (maxByteNum < bytes.length) {
            String subStr = cutStr(bytes, maxByteNum);
            String desc = subStr == null ? "" : subStr;
            Log.println(priority, tag, desc);
            if (subStr != null) {
                bytes = Arrays.copyOfRange(bytes, subStr.getBytes(Charsets.UTF_8).length, bytes.length);
            }
        }
        Log.println(priority, tag, new String(bytes));
    }
}