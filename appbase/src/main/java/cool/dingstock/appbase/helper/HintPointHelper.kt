package cool.dingstock.appbase.helper

import cool.dingstock.appbase.entity.event.im.UnreadNotificationEvent
import cool.dingstock.appbase.util.LoginUtils
import org.greenrobot.eventbus.EventBus


object HintPointHelper {

    private var isHaveDcHelperPoint = false //盯链小助手红点
    private var unReadIMMessageCount = 0 //未读im消息
    private var unReadNotificationCount = 0 //未读通知数目

    var helperTag = -1


    /**
     * 积分未领取
     * */
    private var haveUnReceive: Boolean = false

    fun updateHaveUnReceive(isHave: Boolean){
        haveUnReceive = isHave
    }

    fun enableShowMineBottomRedHint(): Boolean {
        return haveUnReceive
    }

    fun updateUnReadImMessageCount(count: Int) {
        unReadIMMessageCount = count
    }

    fun updateUnReadNotificationCount(count: Int) {
        unReadNotificationCount = count
    }

    fun getUnReadNotificationCount(): Int {
        return unReadNotificationCount
    }

    fun getUnReadImMessageCount(): Int {
        return unReadIMMessageCount
    }

    fun updateIsHaveDcHelperPoint(isHave: Boolean) {
        isHaveDcHelperPoint = isHave
    }

    fun clearImTabAllPoint() {
        unReadNotificationCount = 0
        unReadIMMessageCount = 0
        isHaveDcHelperPoint = false
    }

    fun isHavePointAtNotificationPage(): Boolean {
        return unReadNotificationCount != 0 && LoginUtils.isLogin()
    }

    fun isHavePointAtDcHelper(): Boolean {
        return isHaveDcHelperPoint && LoginUtils.isLogin()
    }

    fun isHavePointAtHomeImTab(): Boolean {
        return (isHaveDcHelperPoint || unReadNotificationCount != 0 || unReadIMMessageCount != 0) && LoginUtils.isLogin()
    }

    fun enableShowIMBottomRedHint(): Boolean {
        if (getUnReadNotificationCount() != 0) {
            EventBus.getDefault().post(UnreadNotificationEvent())
        }
        return isHavePointAtHomeImTab()
    }
}