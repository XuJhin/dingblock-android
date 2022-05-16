package cool.dingstock.appbase.ut;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cool.dingstock.appbase.constant.UTConstant;
import cool.dingstock.appbase.helper.config.ConfigHelper;
import cool.dingstock.lib_base.BaseLibrary;
import cool.dingstock.lib_base.json.JSONHelper;
import cool.dingstock.lib_base.util.CollectionUtils;
import cool.dingstock.lib_base.util.Logger;

public class UTHelper {

    public static void init() {
    }

    public static void homeTab(String tabName) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("name", tabName);
        commonEvent(UTConstant.Home.MAIN_TAB_SELECT, paramMap);
    }

    public static void homeLevel1Tab(String tabName) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(UTConstant.Key.TAB_LEVEL1_NAME, tabName);
        commonEvent(UTConstant.Home.HOME_LEVEL1_TAB, paramMap);
    }

    public static void homeBanner(int pos, String imageUrl, String linkUrl) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(UTConstant.Key.POSITION, String.valueOf(pos));
        paramMap.put(UTConstant.Key.IMAGE_URL, imageUrl);
        paramMap.put(UTConstant.Key.LINK_URL, linkUrl);
        commonEvent(UTConstant.Home.HOME_BANNER, paramMap);
    }

    public static void homeFeaturedProduct(String name, int subscribeCount, boolean subscribed) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(UTConstant.Key.NAME, name);
        paramMap.put(UTConstant.Key.SUBSCRIBE_COUNT, String.valueOf(subscribeCount));
        paramMap.put(UTConstant.Key.SUBSCRIBED, String.valueOf(subscribed));
        commonEvent(UTConstant.Home.HOME_FEATURED_PRODUCT, paramMap);
    }


    public static void homeArticle(String title, String linkUrl) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(UTConstant.Key.TITLE, title);
        paramMap.put(UTConstant.Key.LINK_URL, linkUrl);
        commonEvent(UTConstant.Home.HOME_ARTICLE, paramMap);
    }


    public static void homeLab() {
        commonEvent(UTConstant.Home.HOME_LAB);
    }


    public static void vipLaunch() {
        commonEvent(UTConstant.Home.VIP_LAUNCH);
    }

    public static void monitorChannelSubscribe(String id, String name, String area, boolean subscribed) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(UTConstant.Key.SUBSCRIBED, String.valueOf(subscribed));
        paramMap.put(UTConstant.Key.ID, id);
        paramMap.put(UTConstant.Key.NAME, name);
        paramMap.put(UTConstant.Key.AREA, area);
        commonEvent(UTConstant.Monitor.MONITOR_CHANNEL_SUBSCRIBE, paramMap);
    }


    public static void monitorContent(String id, String name, String fullName, String dateTime) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(UTConstant.Key.ID, id);
        paramMap.put(UTConstant.Key.NAME, name);
        paramMap.put(UTConstant.Key.FULL_NAME, fullName);
        paramMap.put(UTConstant.Key.DATA_TIME, dateTime);
        commonEvent(UTConstant.Monitor.MONITOR_CONTENT, paramMap);
    }

    public static void monitorClickStork() {
        onEvent(UTConstant.Monitor.MONITOR_STOCK_CLICK);
    }

    public static void listClickExpand() {
        onEvent(UTConstant.Monitor.MONITOR_LIST_EXPAND);
    }

    public static void priceBrand(String name) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(UTConstant.Key.NAME, name);
        commonEvent(UTConstant.Price.PRICE_BRAND, paramMap);
    }

    public static void priceCategory(String id, String name) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(UTConstant.Key.ID, id);
        paramMap.put(UTConstant.Key.NAME, name);
        commonEvent(UTConstant.Price.PRICE_CATEGORY, paramMap);
    }

    public static void PriceSearch(String searchContent, String searchCategoryId) {
        Map<String, String> paramMap = new HashMap<>();
        if (!TextUtils.isEmpty(searchContent)) {
            paramMap.put(UTConstant.Key.SEARCH_CATEGORY_ID, searchContent);
        }
        if (!TextUtils.isEmpty(searchCategoryId)) {
            paramMap.put(UTConstant.Key.SEARCH_CONTENT, searchCategoryId);
        }
        commonEvent(UTConstant.Price.PRICE_SEARCH, paramMap);
    }

    public static void PriceRoleSwitch(String name) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(UTConstant.Key.AFTER_ROLE, name);
        commonEvent(UTConstant.Price.PRICE_ROLE_SWITCH, paramMap);
    }

    public static void priceRank(String name) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(UTConstant.Key.NAME, name);
        commonEvent(UTConstant.Price.PRICE_RANK, paramMap);
    }

    public static void priceDetail(String id, String name, String percentage, String price) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(UTConstant.Key.ID, id);
        paramMap.put(UTConstant.Key.NAME, name);
        paramMap.put(UTConstant.Key.PERCENTAGE, percentage);
        paramMap.put(UTConstant.Key.PRICE, price);
        commonEvent(UTConstant.Price.PRICE_DETAIL, paramMap);
    }

    public static void priceAds(String from, String imageUrl, String linkUrl) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(UTConstant.Key.FROM, from);
        paramMap.put(UTConstant.Key.IMAGE_URL, imageUrl);
        paramMap.put(UTConstant.Key.LINK_URL, linkUrl);
        commonEvent(UTConstant.Price.PRICE_ADS, paramMap);
    }


    public static void priceDetailChart(String id, String intervals, String size, String sizeType) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(UTConstant.Key.ID, id);
        paramMap.put(UTConstant.Key.INTERVALS, intervals);
        paramMap.put(UTConstant.Key.SIZE, size);
        paramMap.put(UTConstant.Key.SIZE_TYPE, sizeType);
        commonEvent(UTConstant.Price.PRICE_DETAIL_CHART, paramMap);
    }


    public static void priceRemind(String productId, String type, String size, String sizeType, List<String> platformList) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(UTConstant.Key.PRODUCT_ID, productId);
        paramMap.put(UTConstant.Key.TYPE, type);
        paramMap.put(UTConstant.Key.SIZE, size);
        paramMap.put(UTConstant.Key.SIZE_TYPE, sizeType);
        if (CollectionUtils.isNotEmpty(platformList)) {
            paramMap.put(UTConstant.Key.PLATFORM_LIST, JSONHelper.toJson(platformList));
        }
        commonEvent(UTConstant.Price.PRICE_REMIND, paramMap);
    }


    public static void priceGoPlatform(String platform) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(UTConstant.Key.PLATFORM, platform);
        commonEvent(UTConstant.Price.PRICE_GO_PLATFORM, paramMap);
    }


    public static void priceGenScreenshot() {
        onEvent(UTConstant.Price.PRICE_GEN_SCREENSHOT);
    }

    public static void communityTopicDetail(String name) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(UTConstant.Key.NAME, name);
        commonEvent(UTConstant.Circle.COMMUNITY_TOPIC_DETAIL, paramMap);
    }

    public static void communityDynamicDetail(String id, String name, int favorCount, int commentCount, String rogin) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(UTConstant.Key.ID, id);
        paramMap.put(UTConstant.Key.NAME, name);
        paramMap.put(UTConstant.Key.FAVOR_COUNT, String.valueOf(favorCount));
        paramMap.put(UTConstant.Key.COMMENT_COUNT, String.valueOf(commentCount));
        paramMap.put("origin", rogin);
        commonEvent(UTConstant.Circle.COMMUNITY_DYNAMIC_DETAIL, paramMap);
    }


    public static void communityPublish() {
        onEvent(UTConstant.Circle.COMMUNITY_PUBLISH);
    }


    public static void communityMessage(String title, String content) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(UTConstant.Key.TITLE, title);
        paramMap.put(UTConstant.Key.CONTENT, content);
        commonEvent(UTConstant.Circle.COMMUNITY_MESSAGE, paramMap);
    }


    public static void personalCenterCell(String title) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(UTConstant.Key.TITLE, title);
        commonEvent(UTConstant.Mine.PERSONAL_CENTER_CELL, paramMap);
    }


    public static void settingCell(String title) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(UTConstant.Key.TITLE, title);
        commonEvent(UTConstant.Setting.SETTING_CELL, paramMap);
    }


    public static void payPage(String role) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(UTConstant.Key.ROLE, role);
        commonEvent(UTConstant.Pay.PAY_PAGE, paramMap);
    }

    public static void payRequest(String provider) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(UTConstant.Key.PROVIDER, provider);
        commonEvent(UTConstant.Pay.PAY_REQUEST, paramMap);
    }


    public static void skipSplash(String id) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(UTConstant.Key.SPLASH_ID, id);
        commonEvent(UTConstant.Splash.SKIP_SPLASH, paramMap);
    }

    public static void endSplash(String id) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(UTConstant.Key.SPLASH_ID, id);
        commonEvent(UTConstant.Splash.END_SPLASH, paramMap);
    }

    public static void enterSplash(String id) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(UTConstant.Key.SPLASH_ID, id);
        commonEvent(UTConstant.Splash.ENTER_SPLASH, paramMap);
    }

    public static void showSplash(String id) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(UTConstant.Key.SPLASH_ID, id);
        commonEvent(UTConstant.Splash.SHOW_SPLASH, paramMap);
    }

    public static void deviceFinger(String data) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(UTConstant.Key.FINGERPRINT, data);
        commonEvent(UTConstant.Key.FINGERPRINT, paramMap);
    }

    public static void onResume(Context context) {
        MobclickAgent.onResume(context);
    }

    public static void onPause(Context context) {
        MobclickAgent.onPause(context);
    }

    public static void onPageStart(@NonNull String string) {
        Logger.d("onPageStart   " + string);
        MobclickAgent.onPageStart(string);
    }

    public static void onPageEnd(@NonNull String string) {
        Logger.d("onPageEnd   " + string);
        MobclickAgent.onPageEnd(string);
    }

    public static void onEvent(String name, Map<String, String> paramMap) {
        Logger.d("onEvent  name=" + name);
        if(ConfigHelper.INSTANCE.isAgreePolicy()){
            MobclickAgent.onEvent(BaseLibrary.getInstance().getContext(), name, paramMap);
        }
    }

    public static void onEvent(String name) {
        Logger.d("onEvent  name=" + name);
        if(TextUtils.isEmpty(name)){
            return;
        }
        if(ConfigHelper.INSTANCE.isAgreePolicy()){
            MobclickAgent.onEvent(BaseLibrary.getInstance().getContext(), name);
        }
    }


    public static void routerException(String url, String action) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("url", url);
        paramMap.put("action", action);
        onEvent(UTConstant.ROUTE.ROUTE_EXCEPTION, paramMap);
    }

    public static void commonEvent(String name, Map<String, String> paramMap) {
        if (paramMap == null || paramMap.size() == 0) {
            Logger.d("DcUt:", "name:" + name);
            onEvent(name);
        } else {
            onEvent(name, paramMap);
            Logger.d("DcUt:", "name:" + name + ",params:" + new Gson().toJson(paramMap));
        }
    }

    public static void commonEvent(String name, String key, String value) {
        Map<String, String> map = new HashMap<>();
        map.put(key, value);
        commonEvent(name, map);
    }

    //只有一个 参数，默认key 为name
    public static void commonEvent(String name, String nameValue) {
        Map<String, String> map = new HashMap<>();
        map.put("name", nameValue);
        commonEvent(name, map);
    }

    public static void commonEvent(String name, String key1, String value1, String key2, String value2) {
        Map<String, String> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        commonEvent(name, map);
    }

    public static void commonEvent(String name, String key1, String value1, String key2, String value2,String key3, String value3) {
        Map<String, String> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        commonEvent(name, map);
    }

    public static void commonEvent(String name) {
        Logger.d("DcUt,", "name:" + name);
        onEvent(name);
    }
    
}
