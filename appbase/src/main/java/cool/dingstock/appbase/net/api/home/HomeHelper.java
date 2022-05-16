package cool.dingstock.appbase.net.api.home;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cool.dingstock.appbase.constant.ServerConstant;
import cool.dingstock.appbase.entity.bean.TaobaoTimeStamp;
import cool.dingstock.appbase.entity.bean.home.HomeData;
import cool.dingstock.appbase.entity.bean.home.HomeGotemBean;
import cool.dingstock.appbase.entity.bean.home.HomeTabBean;
import cool.dingstock.appbase.helper.SettingHelper;
import cool.dingstock.appbase.net.parse.ParseCallback;
import cool.dingstock.lib_base.json.JSONHelper;
import cool.dingstock.lib_base.stroage.ConfigFileHelper;
import cool.dingstock.lib_base.util.CollectionUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class HomeHelper {

    private volatile static HomeHelper instance;

    private static final String MONITOR_TAB_ID = "monitor";
    private HomeData homeData;
    private static OkHttpClient client;

    public static HomeHelper getInstance() {
        if (null == instance) {
            synchronized (HomeHelper.class) {
                if (null == instance) {
                    instance = new HomeHelper();
                }
            }
        }
        return instance;
    }

    private HomeHelper() {
        client = new OkHttpClient();
    }


    public HomeData getCacheHomeData() {
        return homeData;
    }

    public void setCacheHomeData(HomeData homeData) {
        this.homeData = homeData;
    }


    /**
     * 获取首页的tab列表
     *
     * @return 回调
     */
    public List<HomeTabBean> getTabDataList() {
        String configJson = ConfigFileHelper.getConfigJson("home_tab.json");
        if (TextUtils.isEmpty(configJson)) {
            return null;
        }
        List<HomeTabBean> homeTabBeanList = JSONHelper.fromJsonList(configJson, HomeTabBean.class);
        if (CollectionUtils.isEmpty(homeTabBeanList)) {
            return null;
        }
        if (SettingHelper.getInstance().isMonitorTabFirst()) {
            List<HomeTabBean> finalList = new ArrayList<>();
            for (HomeTabBean homeTabBean : homeTabBeanList) {
                if (MONITOR_TAB_ID.equals(homeTabBean.getTabId())) {
                    finalList.add(0, homeTabBean);
                } else {
                    finalList.add(homeTabBean);
                }
            }
            return finalList;
        } else {
            return homeTabBeanList;
        }
    }





    /**
     * 获取陪跑 gotem 数据
     *
     * @param page     （0->）
     * @param callback 回调
     */
    public void getGotemData(int page, @NonNull ParseCallback<List<HomeGotemBean>> callback) {
        // TODO: 3/23/21  网关迁移
        callback.onFailed("","");
//        ParseRequest.newBuilder()
//                .function(ServerConstant.Function.GOTEM)
//                .param(ServerConstant.ParamKEY.PAGE, page)
//                .enqueue(type, callback);
    }


    /**
     * 获取淘宝时间戳
     *
     * @param taoBaoTimestampBack
     */
    public void fetchTBTamp(TaoBaoTimestampBack taoBaoTimestampBack) {
        Request request = new Request.Builder().url(ServerConstant.Function.TAOBAO_TIMESTAMP).build();
        try {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    taoBaoTimestampBack.error(e);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String jsonString = Objects.requireNonNull(response.body().string());
                    TaobaoTimeStamp taobaoTimeStamp = new Gson().fromJson(jsonString, TaobaoTimeStamp.class);
                    taoBaoTimestampBack.success(taobaoTimeStamp.getData().getT());
                }
            });
        }catch (Exception e){
        }
    }

    public interface TaoBaoTimestampBack {
        void success(String timestamp);

        void error(Exception e);
    }
}
