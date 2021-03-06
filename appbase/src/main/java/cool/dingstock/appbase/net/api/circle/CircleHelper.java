package cool.dingstock.appbase.net.api.circle;

import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import cool.dingstock.appbase.dagger.AppBaseApiHelper;
import cool.dingstock.appbase.entity.bean.account.DcLoginUser;
import cool.dingstock.appbase.entity.bean.base.BaseResult;
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicVideoDetailBean;
import cool.dingstock.appbase.entity.bean.circle.CircleLinkBean;
import cool.dingstock.appbase.entity.bean.circle.CircleLocalBean;
import cool.dingstock.appbase.exception.DcException;
import cool.dingstock.appbase.net.api.account.AccountHelper;
import cool.dingstock.appbase.net.parse.ParseCallback;
import cool.dingstock.appbase.net.retrofit.exception.DcError;
import cool.dingstock.lib_base.json.JSONHelper;
import cool.dingstock.lib_base.stroage.ConfigFileHelper;
import cool.dingstock.lib_base.stroage.ConfigSPHelper;
import cool.dingstock.lib_base.util.CollectionUtils;
import cool.dingstock.lib_base.util.ResUtil;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;

public class CircleHelper {

    @Inject
    CircleApi circleApi;

    private static volatile CircleHelper instance = null;

    public static CircleHelper getInstance() {
        if (null == instance) {
            synchronized (CircleHelper.class) {
                if (null == instance) {
                    instance = new CircleHelper();
                }
            }
        }
        return instance;
    }

    public CircleHelper() {
        AppBaseApiHelper.INSTANCE.getAppBaseComponent().inject(this);
    }

    public boolean isShowIdentifyTip() {
        String key;
        DcLoginUser user = AccountHelper.getInstance().getUser();
        if (null != user) {
            key = user.getId() + "_identify_tip";
        } else {
            key = "no_user" + "_identify_tip";
        }
        return ConfigSPHelper.getInstance().getBoolean(key);
    }


    public void saveReadIdentifyTip() {
        String key;
        DcLoginUser user = AccountHelper.getInstance().getUser();
        if (null != user) {
            key = user.getId() + "_identify_tip";
        } else {
            key = "no_user" + "_identify_tip";
        }
        ConfigSPHelper.getInstance().save(key, true);
    }

    public boolean isShowDealTip() {
        String key;
        DcLoginUser user = AccountHelper.getInstance().getUser();
        if (null != user) {
            key = user.getId() + "_deal_tip";
        } else {
            key = "no_user" + "_deal_tip";
        }
        return ConfigSPHelper.getInstance().getBoolean(key);
    }


    public void saveReadDealTip() {
        String key;
        DcLoginUser user = AccountHelper.getInstance().getUser();
        if (null != user) {
            key = user.getId() + "_deal_tip";
        } else {
            key = "no_user" + "_deal_tip";
        }
        ConfigSPHelper.getInstance().save(key, true);
    }


    /**
     * ???????????????????????????
     *
     * @return ??????????????????
     */
    public List<CircleLocalBean> getIdentifyList() {
        String configJson = ConfigFileHelper.getConfigJson("identify_item.json");
        if (TextUtils.isEmpty(configJson)) {
            return null;
        }
        List<CircleLocalBean> circleIdentifyBeanList = JSONHelper.fromJsonList(configJson, CircleLocalBean.class);
        if (CollectionUtils.isEmpty(circleIdentifyBeanList)) {
            return null;
        }
        for (CircleLocalBean circleIdentifyBean : circleIdentifyBeanList) {
            circleIdentifyBean.setIconIntRes(ResUtil.getDrawableByRes(circleIdentifyBean.getIconRes()));
            circleIdentifyBean.setGuideIconIntRes(ResUtil.getDrawableByRes(circleIdentifyBean.getGuideIconRes()));
        }
        return circleIdentifyBeanList;
    }


    /**
     * ???????????????????????????
     *
     * @return ??????????????????
     */
    public List<CircleLocalBean> getDealList() {
        String configJson = ConfigFileHelper.getConfigJson("deal_item.json");
        if (TextUtils.isEmpty(configJson)) {
            return null;
        }
        List<CircleLocalBean> circleIdentifyBeanList = JSONHelper.fromJsonList(configJson, CircleLocalBean.class);
        if (CollectionUtils.isEmpty(circleIdentifyBeanList)) {
            return null;
        }
        for (CircleLocalBean circleIdentifyBean : circleIdentifyBeanList) {
            circleIdentifyBean.setIconIntRes(ResUtil.getDrawableByRes(circleIdentifyBean.getIconRes()));
            circleIdentifyBean.setGuideIconIntRes(ResUtil.getDrawableByRes(circleIdentifyBean.getGuideIconRes()));
        }
        return circleIdentifyBeanList;
    }


//    /**
//     * ??????????????????
//     *
//     * @param callback ??????
//     */
//    public void getCommunityTopic(ParseCallback<CircleDynamicListType> callback) {
//        Type type = new TypeToken<CircleDynamicListType>() {
//        }.getType();
//        ParseRequest.newBuilder()
//                .function(ParseServerConstant.Function.COMMUNITY_TOPIC_LIST)
//                .enqueue(type, callback);
//    }

    /**
     * ??????uri
     *
     * @param link     linkUrl
     * @param callback ??????
     */
    public void resolveCommunityLink(String link, ParseCallback<CircleLinkBean> callback) {
        circleApi.resolveLink(link)
                .subscribe(res->{
                    if(!res.getErr()){
                        callback.onSucceed(res.getRes());
                    }else {
                        callback.onFailed(res.getCode()+"",res.getMsg());
                    }
                },er->{
                    callback.onFailed("",er.getMessage());

                });
    }












    /**
     * ??????
     *
     * @param dynamicId ??????ID
     */
    public Flowable<BaseResult<String>> communityPostFavored(final String dynamicId,
                                     final boolean favored) {
        if (TextUtils.isEmpty(dynamicId)) {
            return Flowable.error(new DcException(DcError.UNKNOW_ERROR_CODE, "??????Id??????"));
        }
        return circleApi.communityPostFavored(favored,dynamicId);
    }





    /**
     * ????????????
     *
     * @param commentId ??????ID
     * @param favored   ???
     */
    public Flowable<BaseResult<String>> communityPostCommentFavored(String commentId, boolean favored) {
        return circleApi.communityPostCommentFavored(commentId,favored);
    }

    /**
     * ??????
     *
     * @param commentId ??????ID
     */
    public Flowable<BaseResult<String>> communityPostCommentReport(String commentId) {
        if (TextUtils.isEmpty(commentId)) {
            return Flowable.error(new DcException(DcError.UNKNOW_ERROR_CODE,"????????????"));
        }
        return circleApi.communityPostCommentReport(commentId);
    }







    public Disposable videoInfo(@NotNull String id, ParseCallback<CircleDynamicVideoDetailBean> callback) {

        return circleApi.videoInfo(id)
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
     * ????????????
     *
     * @param id
     */
    public Flowable<BaseResult<String>> postBlock(@NotNull String id) {
        return circleApi.postBlock(id);
    }



    public Flowable<BaseResult<String>> postReport(@NotNull String postId) {
        return circleApi.postReport(postId);
    }

}
