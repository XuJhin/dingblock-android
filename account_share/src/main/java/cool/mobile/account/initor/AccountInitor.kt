package cool.mobile.account.initor

import android.content.Context
import cool.dingstock.appbase.entity.bean.account.DcLoginUser
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.lib_base.login.LoginActionDelegation
import cool.dingstock.lib_base.login.LoginHelper

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/12  11:13
 */
object AccountInitor {

    fun init(context: Context) {
        LoginHelper.initHelper(context, object : LoginActionDelegation() {

            override fun getLoginClazz(): Class<DcLoginUser> {
                return DcLoginUser::class.java
            }

            override fun isLogin(): Boolean {
                return LoginUtils.getCurrentUser() != null
            }
            
        })
    }

}