package cool.dingstock.appbase.net.api.mine;

import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import cool.dingstock.appbase.dagger.AppBaseApiHelper;
import cool.dingstock.appbase.entity.bean.mine.MineIndexBean;
import cool.dingstock.appbase.entity.bean.mine.MineMallData;
import cool.dingstock.appbase.entity.bean.mine.VipActivityBean;
import cool.dingstock.appbase.entity.event.circle.EventFollowerChange;
import cool.dingstock.appbase.entity.event.im.RelationshipChangeEvent;
import cool.dingstock.appbase.helper.IMHelper;
import cool.dingstock.appbase.net.parse.ParseCallback;
import cool.dingstock.appbase.net.retrofit.exception.DcError;
import cool.dingstock.lib_base.json.JSONHelper;
import cool.dingstock.lib_base.stroage.ConfigFileHelper;
import cool.dingstock.lib_base.util.CollectionUtils;
import cool.dingstock.lib_base.util.ResUtil;

public class MineHelper {

    @Inject
    MineApi mineApi;

    private volatile static MineHelper instance;

    public static MineHelper getInstance() {
        if (null == instance) {
            synchronized (MineHelper.class) {
                if (null == instance) {
                    instance = new MineHelper();
                }
            }
        }
        return instance;
    }

    private MineHelper() {
        AppBaseApiHelper.INSTANCE.getAppBaseComponent().inject(this);
    }


    public List<MineIndexBean> getMineData() {
        String configJson = ConfigFileHelper.getConfigJson("mine_item.json");
        if (TextUtils.isEmpty(configJson)) {
            return null;
        }
        List<MineIndexBean> mineIndexBeanList = JSONHelper.fromJsonList(configJson, MineIndexBean.class);
        if (CollectionUtils.isEmpty(mineIndexBeanList)) {
            return null;
        }
        for (MineIndexBean homeTabBean : mineIndexBeanList) {
            homeTabBean.setIconIntRes(ResUtil.getDrawableByRes(homeTabBean.getIconRes()));
        }
        return mineIndexBeanList;
    }




    public void mall(ParseCallback<MineMallData> callback) {
        mineApi.mall().subscribe(res->{
            if(!res.getErr()){
                callback.onSucceed(res.getRes());
            }else {
                callback.onFailed(res.getCode()+"",res.getMsg());
            }
        },err->{
            callback.onFailed("",err.getMessage());
        });
    }


    public void checkIn(final ParseCallback<String> callback) {
        mineApi.checkIn()
                .subscribe(res->{
                    if(!res.getErr()){
                        callback.onSucceed(res.getRes());
                    }else {
                        callback.onFailed(res.getCode()+"",res.getMsg());
                    }
                },err->{
                    callback.onFailed("",err.getMessage());
                });
    }


    public void redeem(String id, final ParseCallback<String> callback) {
        mineApi.redeem(id)
                .subscribe(res->{
                    if(!res.getErr()){
                        callback.onSucceed(res.getRes());
                    }else {
                        callback.onFailed(res.getCode()+"",res.getMsg());
                    }

                },err->{
                    callback.onFailed("",err.getMessage());
                });
    }




    /**
     * 切换关注状态
     *
     * @param objectId
     * @param parseCallback
     */
    public void switchFollowState(@NotNull boolean follow, @NotNull String objectId,
                                  @NotNull ParseCallback<Integer> parseCallback) {
        mineApi.switchFollowState(objectId,follow)
                .subscribe(res->{
                    if(!res.getErr()){
                        if (follow) {
                            IMHelper.INSTANCE.getFollowIdList().add(objectId);
                        } else {
                            IMHelper.INSTANCE.getFollowIdList().remove(objectId);
                        }
                        IMHelper.INSTANCE.updateConversation(objectId, 1);
                        EventBus.getDefault().post(new RelationshipChangeEvent());
                        parseCallback.onSucceed(res.getRes());
                    }else {
                        parseCallback.onFailed(res.getCode()+"",res.getMsg());
                    }
                },err->{
                    parseCallback.onFailed(DcError.UNKNOW_ERROR_CODE+"",err.getMessage());
                });
    }





    /**
     * 获取限时优惠的商品数据
     *
     * @param activityType 活动类型
     */
    public void getActivityGoods(@NotNull String activityType,
                           @NotNull ParseCallback<VipActivityBean> parseCallback) {
        mineApi.vipActivityPrices(activityType)
                .subscribe(res->{
                    if(!res.getErr()){
                        parseCallback.onSucceed(res.getRes());
                    }else {
                        parseCallback.onFailed(res.getCode()+"",res.getMsg());
                    }
                },err->{
                    parseCallback.onFailed("",err.getMessage());
                });
    }

}
