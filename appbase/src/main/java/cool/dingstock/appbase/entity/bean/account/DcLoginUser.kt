package cool.dingstock.appbase.entity.bean.account

import android.text.TextUtils
import cool.dingstock.appbase.constant.AccountConstant
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.lib_base.BaseLibrary
import cool.dingstock.lib_base.login.ILoginUser
import cool.dingstock.lib_base.util.AppUtils
import cool.dingstock.lib_base.util.TimeUtils
import java.util.*

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/13  16:23
 */
open class DcLoginUser : ILoginUser {
    var id: String? = null
    var mobilePhoneNumber: String? = null
    var mobile: String? = null
    var nickName: String? = null
    var avatarUrl: String? = null
    var vipValidity: Long? = null
    var deviceId: String? = null
    var version: String? = null
    var imToken: String? = null
    var regions: List<String>? = null
    var channels: ArrayList<String>? = null
    var authData: Map<String, Map<String, String>>? = null
    var sessionToken: String? = null
    private var isVip: Boolean? = false

    //邀请码字段
    var inviteCode: String? = null
    var state: String? = null
    var avatarPendantId: String? = null
    var avatarPendantUrl: String? = null

    //2.9.8
    var nicknameUpdateAt: Long? = null//昵称上次修改时间
    var nicknameLiftBanAt: Long? = null//昵称下次可修改时间
    var zone: String? = null

    fun isVip(): Boolean {
        return isVip == true
    }

    fun getVipStr(): String? {
        if (null == vipValidity) {
            return "非会员"
        }
        return if (!isVip()) {
            "开通会员享更多惊喜特权"
        } else String.format(
            "将于%1$1s到期", TimeUtils.formatTimestamp(
                vipValidity
                    ?: 0, "yyyy-MM-dd"
            )
        )
    }

    fun isSmsAuthenticated(): Boolean {
        return isLinked(AccountConstant.AuthType.SMS)
    }

    private fun isLinked(authType: String): Boolean {
        return authData?.containsKey(authType) ?: false
    }

    fun getDescription(): String? {
        return String.format(
            "id: %s, 手机号: %s, 设备id: %s, 会员有效期: %s, 订阅频道: %s, 订阅地区: %s, app版本: %s",
            id,
            mobilePhoneNumber ?: "",
            deviceId ?: "",
            vipValidity ?: "",
            channels ?: "",
            regions ?: "",
            "android" + AppUtils.getVersionName(context = BaseLibrary.getInstance().context)
        )
    }

    fun save() {
        LoginUtils.updateUser(this)
    }
}