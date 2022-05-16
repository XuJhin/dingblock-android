package cool.dingstock.appbase.ext

import android.app.Activity
import cool.dingstock.appbase.entity.bean.account.DcLoginUser
import cool.dingstock.appbase.util.LoginUtils

fun Activity.getCurrentUser(): DcLoginUser? {
    return LoginUtils.getCurrentUser()

}

fun Activity.getUserOrToLogin() {}