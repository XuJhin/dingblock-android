package net.dingblock.mobile.manager

import cool.dingstock.appbase.entity.bean.account.DcLoginUser
import cool.dingstock.appbase.entity.bean.account.UserInfoEntity

class DcAccountManager {

    /**
     * 是否登录
     */
    val isLogin: Boolean
        get() {
            return currentUser != null
        }


    private var _user: DcLoginUser? = null

    /**
     * 当前用户
     */
    var currentUser: DcLoginUser? = null
        get() {
            if (_user == null) {
                _user = getUserFromStore()
            }
            if (_user != null) {
                return _user
            }
            return _user
        }
        set(value) {
            _user = value
            storeUserInfo(value)
            field = value
        }


    private fun clear() {
        currentUser = null
        _user = null
    }

    private fun storeUserInfo(value: DcLoginUser?) {
        if (value == null) {
        } else {
        }


    }

    private fun getUserFromStore(): DcLoginUser? {
        return null
    }
}