package net.dingblock.mobile.push;

import android.content.Context;
import android.text.TextUtils;

import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

import org.greenrobot.eventbus.EventBus;

import cool.dingstock.appbase.constant.PushConstant;
import cool.dingstock.appbase.entity.bean.push.PushMessage;
import cool.dingstock.appbase.entity.event.update.EventCommunityChange;
import cool.dingstock.appbase.entity.event.update.EventScoreChange;
import cool.dingstock.appbase.push.DCPushManager;
import cool.dingstock.lib_base.json.JSONHelper;
import cool.dingstock.lib_base.util.Logger;

public class DCPushReceiver extends XGPushBaseReceiver {

    @Override
    public void onRegisterResult(Context context, int i, XGPushRegisterResult xgPushRegisterResult) {

    }

    @Override
    public void onUnregisterResult(Context context, int i) {

    }

    @Override
    public void onSetTagResult(Context context, int i, String s) {

    }

    @Override
    public void onDeleteTagResult(Context context, int i, String s) {

    }

    @Override
    public void onSetAccountResult(Context context, int i, String s) {

    }

    @Override
    public void onDeleteAccountResult(Context context, int i, String s) {

    }

    @Override
    public void onSetAttributeResult(Context context, int i, String s) {

    }

    @Override
    public void onQueryTagsResult(Context context, int i, String s, String s1) {

    }

    @Override
    public void onDeleteAttributeResult(Context context, int i, String s) {

    }

    @Override
    public void onTextMessage(Context context, XGPushTextMessage xgPushTextMessage) {
        Logger.d("onMessage ==="
                + ", title: " + xgPushTextMessage.getTitle()
                + ", content:" + xgPushTextMessage.getContent() + " thread Id =" + Thread.currentThread().getId());
        String title = xgPushTextMessage.getTitle();
        String content = xgPushTextMessage.getContent();
        PushMessage pushMessage = JSONHelper.fromJson(content, PushMessage.class);
        if (null == pushMessage) {
            return;
        }
        //服务端事件通知
        String event = pushMessage.getEvent();
        if (!TextUtils.isEmpty(event)) {
            assert event != null;
            switch (event) {
                case PushConstant.Event.UPDATE_USER_INFO:
                    DCPushManager.getInstance().onUpdateUserInfoEvent();
                    break;
                case PushConstant.Event.SCORE_STATUS_CHANGE:
                    //积分变化，发送事件
                    EventBus.getDefault().post(new EventScoreChange(true));
                    break;
                case PushConstant.Event.COMMUNITY_FOLLOW:
                case PushConstant.Event.COMMUNITY_REPLY:
                case PushConstant.Event.COMMUNITY_COMMENT:
                case PushConstant.Event.COMMUNITY_FAVOR:
                    EventBus.getDefault().post(new EventCommunityChange(event));
                    break;
            }
        }
    }

    @Override
    public void onNotificationClickedResult(Context context, XGPushClickedResult xgPushClickedResult) {
    }

    @Override
    public void onNotificationShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {

    }
}
