package cool.dingstock.appbase.webview.module

import android.content.Context
import cool.dingstock.appbase.constant.PushConstant
import cool.dingstock.appbase.dagger.AppBaseApiHelper
import cool.dingstock.appbase.entity.event.account.EventIsAuthorized
import cool.dingstock.appbase.net.api.account.AccountApi
import cool.dingstock.appbase.net.api.account.AccountHelper
import cool.dingstock.appbase.webview.bridge.BridgeEvent
import cool.dingstock.appbase.webview.bridge.IBridgeModule
import cool.dingstock.appbase.webview.bridge.IJsBridge
import cool.dingstock.appbase.webview.bridge.XBridgeMethod
import cool.dingstock.appbase.webview.entity.BridgeUserInfo
import cool.dingstock.lib_base.util.Logger
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.ref.WeakReference
import javax.inject.Inject

class AccountModule(
    override var context: WeakReference<Context>?,
    override var bridge: IJsBridge?
) : IBridgeModule {

    @Inject
    lateinit var accountApi: AccountApi

    override fun moduleName(): String = "account"

    init {
        EventBus.getDefault().register(this)
        AppBaseApiHelper.appBaseComponent.inject(this)
    }

    override fun release() {
        super.release()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onActiveSuccessEvent(event: EventIsAuthorized) {
        Logger.d("userLogin event  login= " + event.login)
        if (event.login) {
            bridge?.let {
                BridgeEvent.eventBuild().apply {
                    module = "user"
                    eventName = PushConstant.Event.USER_LOGGEDIN
                }.send(it)
            }
        }
    }

    @XBridgeMethod
    fun getUserInfo(event: BridgeEvent) {
        Logger.d("getUserInfo is called  --- ")
        val user = AccountHelper.getInstance().user
        //H5那边是这样判断的 userId 为空 真尴尬。。。
        if (null == user) {
            val userInfo = BridgeUserInfo()
            userInfo.apply {
                userId = ""
            }
            bridge?.let { event.toResponse().success(userInfo)?.send(it) }
            return
        }
        val disposable = accountApi.getUserByNet()
            .subscribe({
                val userInfo = BridgeUserInfo()
                val newUser = it.res
                userInfo.apply {
                    nickName = newUser?.nickName
                    userId = newUser?.id
                    avatarUrl = newUser?.avatarUrl
                    vipStatus = null != newUser?.vipValidity
                    vipValidity = newUser?.getVipStr()
                    sessionToken = newUser?.sessionToken
                    inviteCode = newUser?.inviteCode
                }
                bridge?.let { it1 -> event.toResponse().success(userInfo)?.send(it1) }
            }, {
                bridge?.let { it1 ->
                    event.toResponse()
                        .error("ERROR_FETCH_FAILED", "获取用户信息失败")
                        ?.send(it1)
                }
            })
    }


}