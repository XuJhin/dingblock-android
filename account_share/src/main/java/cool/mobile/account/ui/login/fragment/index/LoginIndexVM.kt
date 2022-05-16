package cool.mobile.account.ui.login.fragment.index

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.qipeng.yp.onelogin.QPOneLogin
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.mobile.account.share.AuthorizeStart
import cool.mobile.account.share.IAuthorizeCallback
import cool.dingstock.appbase.util.bindDialog
import cool.dingstock.appbase.net.api.account.AccountApi
import cool.dingstock.appbase.net.api.account.AccountHelper
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.account.DcLoginUser
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.push.DCPushManager
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.lib_base.util.StringUtils
import cool.mobile.account.dagger.AccountApiHelper
import javax.inject.Inject

/**
 * @author WhenYoung
 *  CreateAt Time 2020/12/9  17:49
 */
class LoginIndexVM : BaseViewModel() {

    val weChatLoginLiveData = MutableLiveData<WeChatLoginResult>()
    val onKeyLoginResult = MutableLiveData<Boolean>()

    @Inject
    lateinit var accountApi: AccountApi

    init {
        AccountApiHelper.apiAccountComponent.inject(this)
    }


    //微信登录
    fun loginWechat() {
        val deviceId = DCPushManager.getInstance().txDeviceToken
        if (StringUtils.isEmpty(deviceId)) {
            shortToast("登录错误请稍后再试~")
            return
        }
        postAlertLoading()
        AuthorizeStart.authorize(object : IAuthorizeCallback {
            override fun onAuthorizeSuccess(userId: String?, token: String?) {
                if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(token)) {
                    shortToast("获取授权失败")
                    UTHelper.commonEvent(UTConstant.Login.WX_LOGIN, "获取授权失败")
                    postAlertHide()
                    return
                }
                val subscribe = accountApi.loginWehChat(userId!!, token!!, deviceId)
                        .bindDialog(this@LoginIndexVM)
                        .subscribe({ res ->
                            res.res?.let { user -> LoginUtils.loginSuccess(user) }
                            weChatLoginLiveData.postValue(WeChatLoginResult(userId, token, res))
                        }, {
                            postAlertHide()
                            shortToast(it.message)
                        })
                addDisposable(subscribe)
            }

            override fun onAuthorizeFailed(errorCode: String?, errorMsg: String?) {
                shortToast("获取授权失败")
                postAlertHide()
                UTHelper.commonEvent(UTConstant.Login.WX_LOGIN, "获取授权失败")
            }

        })

    }

    //本机号码一键登录
    fun loginOneKey(cid: String, deviceId: String) {
        accountApi.loginCid(cid, deviceId)
                .subscribe({
                    if (it.err || it.res == null) {
                        shortToast(it.msg)
                        onKeyLoginResult.postValue(false)
                    } else {
                        it.res?.let { user -> LoginUtils.loginSuccess(user) }
                        AccountHelper.getInstance().saveUserPhone(it.res?.mobile)
                        onKeyLoginResult.postValue(true)
                    }
                }, {
                    shortToast(it.message)
                    onKeyLoginResult.postValue(false)
                })
    }


}

data class WeChatLoginResult(val userId: String, val token: String, val result: BaseResult<DcLoginUser>)

object YPHelper {
    enum class EnableType {
        UNKNOW, ENABLE, NOT
    }

    //CT电信、CU联通、CM移动
    fun getPlatAgreement(context: Context): String {
        when (QPOneLogin.getInstance().getSimOperator(context)) {
            "CT" -> {
                return "《天翼账号服务与隐私协议》"
            }
            "CU" -> {
                return "《联通统一认证服务条款》"
            }
            "CM" -> {
                return "《中国移动认证服务条款》"
            }
        }
        return ""
    }

    //CT电信、CU联通、CM移动
    fun getPlatServer(context: Context): String {
        when (QPOneLogin.getInstance().getSimOperator(context)) {
            "CT" -> {
                return "天 翼 账 号 提 供 认 证 服 务"
            }
            "CU" -> {
                return "中 国 联 通 提 供 认 证 服 务"
            }
            "CM" -> {
                return "中 国 移 动 提 供 认 证 服 务"
            }
        }
        return ""
    }

    fun getPlat(context: Context): String {
        return "operatorAgreement_"+QPOneLogin.getInstance().getSimOperator(context)
    }
}
