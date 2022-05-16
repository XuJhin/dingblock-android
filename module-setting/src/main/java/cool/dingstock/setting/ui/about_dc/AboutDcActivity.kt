package cool.dingstock.setting.ui.about_dc

import android.os.Bundle
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.constant.*
import cool.dingstock.appbase.ext.getColorRes
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.net.mobile.MobileHelper
import cool.dingstock.appbase.net.parse.ParseCallback
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.lib_base.util.AppUtils
import cool.dingstock.uicommon.setting.R
import cool.dingstock.uicommon.setting.databinding.ActivityAboutDcBinding

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [SettingConstant.Path.ABOUT_DC]
)
class AboutDcActivity : VMBindingActivity<BaseViewModel, ActivityAboutDcBinding>() {
    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        viewBinding.versionTv.text = "当前版本 V" + AppUtils.getVersionName(this)
    }

    override fun initListeners() {
        viewBinding.userAgreementFra.setOnShakeClickListener {
            UTHelper.commonEvent(UTConstant.Setting.SettingP_click_AboutDingchao, "name", "用户协议")
            getCouldUrl(ServerConstant.Function.ARREEMENT)
        }

        viewBinding.privacyPolicyFra.setOnShakeClickListener {
            UTHelper.commonEvent(UTConstant.Setting.SettingP_click_AboutDingchao, "name", "隐私政策")
            getCouldUrl(ServerConstant.Function.PRIVACY)
        }

        viewBinding.userPermission.setOnShakeClickListener {
            UTHelper.commonEvent(UTConstant.Setting.SettingP_click_AboutDingchao, "name", "应用权限")
            getCouldUrl(ServerConstant.Function.appPermissionsUrl)
        }

        viewBinding.userMsgList.setOnShakeClickListener {
            UTHelper.commonEvent(
                UTConstant.Setting.SettingP_click_AboutDingchao,
                "name",
                "个人信息收集清单"
            )
            getCouldUrl(ServerConstant.Function.personalInformationUrl)
        }

        viewBinding.threePartyList.setOnShakeClickListener {
            UTHelper.commonEvent(
                UTConstant.Setting.SettingP_click_AboutDingchao,
                "name",
                "第三方信息共享清单"
            )
            getCouldUrl(ServerConstant.Function.externalInformationUrl)
        }

        viewBinding.joinUsFra.setOnShakeClickListener {
            UTHelper.commonEvent(UTConstant.Setting.SettingP_click_AboutDingchao, "name", "加入我们")
            getCouldUrl("joinUs")
        }
    }

    private fun getCouldUrl(type: String?) {
        MobileHelper.getInstance().getDingUrl(type, object : ParseCallback<String?> {
            override fun onSucceed(data: String?) {
                data?.let { DcRouter(it).start() }
            }

            override fun onFailed(errorCode: String, errorMsg: String) {
            }
        })
    }

    override fun moduleTag(): String = ModuleConstant.SETTING
}