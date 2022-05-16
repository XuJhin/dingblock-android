package cool.dingstock.lib_base.login

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import cool.dingstock.lib_base.BaseLibrary
import cool.dingstock.lib_base.stroage.ConfigSPHelper

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/12  9:53
 */
object LoginHelper {
    private var delegation: LoginActionDelegation? = null
    private var iLoginUser: ILoginUser? = null

    fun initHelper(context: Context, delegation: LoginActionDelegation) {
        PrivateLoginResultHandler.initHandler(context)
        this.delegation = delegation
    }

    fun isLogin(): Boolean {
        if (checkDelegationIsEmpty()) {
            return false
        }
        return delegation!!.isLogin()
    }

    fun <T : ILoginUser> getLoginUser(): T? {
        if (checkDelegationIsEmpty()) {
            return null
        }
        if (iLoginUser != null) {
            return iLoginUser as? T
        }
        try {
            val loginClazz = delegation?.getLoginClazz()
            val loginUserSpString = PrivateLoginResultHandler.getLoginUserSpString()
            iLoginUser = Gson().fromJson<T>(loginUserSpString, loginClazz)
            return iLoginUser as? T
        } catch (e: Exception) {
        }
        return null
    }

    fun loginSuccess(user: ILoginUser) {
        iLoginUser = user
        PrivateLoginResultHandler.onLoginSuccess(user)
    }

    fun loginOutSuccess() {
        iLoginUser = null
        PrivateLoginResultHandler.onLoginOutSuccess()
    }

    fun updateUser(user: ILoginUser) {
        iLoginUser = user
        PrivateLoginResultHandler.updateUser(user)
    }

    private fun checkDelegationIsEmpty(): Boolean {
        if (delegation == null) {
            throw Exception("请初始化代理 ---- 原因：没有调用LoginHelper.initHelper")
        }
        return false
    }

    private object PrivateLoginResultHandler {
        private var sp: SharedPreferences? = null
        private var spKey = "defaultKey" + "LoginResultSp"
        private var spLoginKey = "LoginSpCacheKey"

        fun initHandler(context: Context) {
            spKey = context.packageName + "LoginResultSp"
            sp = context.getSharedPreferences(spKey, Context.MODE_PRIVATE)
        }

        /**
         * 登录成功
         * */
        fun onLoginSuccess(iLoginUser: ILoginUser) {
            save(iLoginUser)
        }

        /**
         * 更新用户数据
         * */
        fun updateUser(iLoginUser: ILoginUser) {
            save(iLoginUser)
        }

        /**
         * 登出成功
         * */
        fun onLoginOutSuccess() {
            checkSp()
            var editor = sp?.edit()
            editor?.remove(spLoginKey)
            ConfigSPHelper.getInstance()
                .save(spLoginKey, "")
            var success = editor?.commit()
        }

        private fun save(iLoginUser: ILoginUser) {
            checkSp()
            val userJsonString: String = Gson().toJson(iLoginUser)
            var editor = sp?.edit()
            editor?.putString(spLoginKey, userJsonString)
            var success = editor?.commit()
        }

        fun getLoginUserSpString(): String? {
            checkSp()
            var result: String? = null
            result = sp?.getString(spLoginKey, null)
            return result
        }

        private fun checkSp() {
            if (sp == null) {
                sp = BaseLibrary.getInstance()
                    .context
                    .getSharedPreferences(spKey, Context.MODE_PRIVATE)
            }
        }
    }
}
