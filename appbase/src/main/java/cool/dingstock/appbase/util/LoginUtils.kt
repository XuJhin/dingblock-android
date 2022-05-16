package cool.dingstock.appbase.util

import android.content.Context
import com.sankuai.waimai.router.Router
import cool.dingstock.appbase.constant.AccountConstant
import cool.dingstock.appbase.entity.bean.account.DcLoginUser
import cool.dingstock.appbase.entity.event.account.EventUserLogin
import cool.dingstock.appbase.entity.event.account.EventUserLoginOut
import cool.dingstock.appbase.entity.event.im.EventSystemNotice
import cool.dingstock.appbase.helper.HintPointHelper
import cool.dingstock.appbase.helper.IMHelper
import cool.dingstock.lib_base.login.LoginHelper
import org.greenrobot.eventbus.EventBus

/**
 * @author WhenYoung
 *  CreateAt Time 2020/12/9  14:42
 */
object LoginUtils {

    private var loginSuccessListeners = arrayListOf<LoginSuccessListener>()


    fun isLogin(): Boolean {
        return LoginHelper.isLogin()
    }

    fun isLoginAndRequestLogin(context: Context): Boolean {
        if (isLogin()) {
            return true
        }
        Router.startUri(context, AccountConstant.Uri.INDEX)
        return false
    }

    fun getCurrentUser(): DcLoginUser? {
        return LoginHelper.getLoginUser<DcLoginUser>()
    }

    fun loginOut() {
        EventBus.getDefault().post(EventSystemNotice(-1))
        IMHelper.logout()
        LoginHelper.loginOutSuccess()
        EventBus.getDefault().post(EventUserLoginOut())
    }

    fun loginSuccess(user: DcLoginUser) {
        EventBus.getDefault().post(EventSystemNotice(HintPointHelper.helperTag))
        LoginHelper.loginSuccess(user)
        IMHelper.getUserRelationList(onDatabaseOpened = {}, onSuccess = {}, onErr = {})
        EventBus.getDefault().post(EventUserLogin())
    }

    fun updateUser(user: DcLoginUser) {
        LoginHelper.updateUser(user)
        IMHelper.refreshUserInfoCache(user.id!!, user.nickName ?: user.id!!, user.avatarUrl ?: "")
    }

    fun registerLoginSuccessListener(listener: LoginSuccessListener) {
        loginSuccessListeners.add(listener)
    }

    fun handLoginSuccess() {
        loginSuccessListeners.let {
            for (l in it) {
                l.handOnLoginSuccess()
            }
        }
        loginSuccessListeners.clear()
    }


    /**
     * 移除所有监听，避免内存泄漏
     * */
    fun removeAllLoginListener() {
        loginSuccessListeners.let {
            for (l in it) {
                l.removeLoginListener()
            }
        }
        loginSuccessListeners.clear()
    }

    abstract class LoginSuccessListener {
        fun handOnLoginSuccess() {
            onLoginSuccess()
        }

        abstract fun removeLoginListener()

        abstract fun onLoginSuccess()
    }


}