package cool.dingstock.appbase.widget.dialog.vipget

import android.os.Bundle
import android.text.TextUtils
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.base.BaseObservablePopWindow
import cool.dingstock.appbase.constant.*
import cool.dingstock.appbase.dagger.AppBaseApiHelper
import cool.dingstock.appbase.databinding.ActivityVipGetDialogBinding
import cool.dingstock.appbase.entity.event.update.EventUserVipChange
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.account.AccountApi
import cool.dingstock.appbase.net.api.circle.CircleApi
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.lib_base.BaseLibrary
import cool.dingstock.lib_base.util.ToastUtil
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

/**
 * @author WhenYoung
 *  CreateAt Time 2020/9/25  14:32
 */
@RouterUri(scheme = RouterConstant.SCHEME, host = RouterConstant.HOST, path = [CommonConstant.Path.VIP_LAYERED_REMIND])
class VipLayeredRemindDialogActivity : BaseObservablePopWindow<BaseViewModel, ActivityVipGetDialogBinding>() {

    override fun moduleTag(): String {
        return ModuleConstant.DIALOG
    }

    @Inject
    lateinit var accountApi: AccountApi

    @Inject
    lateinit var circleApi: CircleApi

    var type: String = ""
    var imgUrl: String = ""

    override fun setSystemStatusBar() {
        StatusBarUtil.setTranslucent(this)
    }


    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        AppBaseApiHelper.appBaseComponent.inject(this)
        type = uri.getQueryParameter(CommonConstant.UriParams.TYPE) ?: ""
        imgUrl = uri.getQueryParameter(CommonConstant.UriParams.IMG_URL) ?: ""

        viewBinding.bgIv.load(imgUrl)
        when (type) {
            MineConstant.VipLayerType.activity1 -> {
                UTHelper.commonEvent(UTConstant.UserLayer.LayActivi1_NewUserPopup_exposure)
            }
            MineConstant.VipLayerType.activity2 -> {
                UTHelper.commonEvent(UTConstant.UserLayer.LayActivi2_TimePopup_exposure)
            }
            MineConstant.VipLayerType.activity3 -> {
                UTHelper.commonEvent(UTConstant.UserLayer.LayActivi3_FreePopup_exposure)
            }
            MineConstant.VipLayerType.activity4 -> {
                UTHelper.commonEvent(UTConstant.UserLayer.LayActivi4_OldUsersPopup_exposure)
            }
            MineConstant.VipLayerType.activity5 -> {
                UTHelper.commonEvent(UTConstant.UserLayer.LayActivi5_OldUsersPopup_exposure)
            }
            MineConstant.VipLayerType.activity6 -> {
                UTHelper.commonEvent(UTConstant.UserLayer.LayActivi6_FreePopup_exposure)
            }
        }

    }

    override fun initListeners() {
        viewBinding.apply {
            closeIv.setOnClickListener {
                finish()
            }
            bgIv.setOnClickListener {
                onPartyClick()
            }
        }
    }


    private fun onPartyClick() {
        if (!LoginUtils.isLoginAndRequestLogin(this)) {
            return
        }
        circleApi.trackUserLayer(type,LoginUtils.getCurrentUser()?.id)
            .subscribe({},{})
        val sub = accountApi.layerConfirm(type)
                .subscribe({
                    if (!it.err) {
                        receive(true)
                        if (!TextUtils.isEmpty(it.res?.link)) {
                            finish()
                            it.res?.link?.let { link ->
                                DcRouter(link).start()
                            }
                        } else {
                            ToastUtil.getInstance()._short(this, it.res?.message ?: "")
                            window.decorView.postDelayed({
                                finish()
                            },100)
                            //更新用户信息，并且发送会员变更事件
                            updateUser()
                        }
                    } else {
                        receive(false)
                        ToastUtil.getInstance()._short(this, it.msg ?: "")
                    }
                }, {
                    receive(false)
                    ToastUtil.getInstance()._short(this, it.message ?: "")
                })
        addDisposable(sub)
        when (type) {
            MineConstant.VipLayerType.activity1 -> {
                UTHelper.commonEvent(UTConstant.UserLayer.LayActivi1_NewUserPopup_click_PanicBuy)
            }
            MineConstant.VipLayerType.activity2 -> {
                UTHelper.commonEvent(UTConstant.UserLayer.LayActivi2_TimePopup_click_PanicBuy)
            }
            MineConstant.VipLayerType.activity4 -> {
                UTHelper.commonEvent(UTConstant.UserLayer.LayActivi4_NewUserPopup_click_PanicBuy)
            }
            MineConstant.VipLayerType.activity5 -> {
                UTHelper.commonEvent(UTConstant.UserLayer.LayActivi5_NewUserPopup_click_PanicBuy)
            }
        }
    }

    private fun updateUser() {
        accountApi.getUserByNet()
                .subscribe({
                    if (!it.err) {
                        EventBus.getDefault().post(EventUserVipChange(true))
                    }
                }, {})
    }

    private fun onDialogDismiss() {
        when (type) {
            MineConstant.VipLayerType.activity1 -> {
                UTHelper.commonEvent(UTConstant.UserLayer.LayActivi1_click_ClosePopup)
            }
            MineConstant.VipLayerType.activity2 -> {
                UTHelper.commonEvent(UTConstant.UserLayer.LayActivi2_click_ClosePopup)
            }
            MineConstant.VipLayerType.activity3 -> {
                UTHelper.commonEvent(UTConstant.UserLayer.LayActivi3_click_ClosePopup)
            }
            MineConstant.VipLayerType.activity4 -> {
                UTHelper.commonEvent(UTConstant.UserLayer.LayActivi4_click_ClosePopup)
            }
            MineConstant.VipLayerType.activity5 -> {
                UTHelper.commonEvent(UTConstant.UserLayer.LayActivi5_click_ClosePopup)
            }
            MineConstant.VipLayerType.activity6 -> {
                UTHelper.commonEvent(UTConstant.UserLayer.LayActivi6_click_ClosePopup)
            }
        }
    }

    private fun receive(success: Boolean) {
        when (type) {
            MineConstant.VipLayerType.activity3 -> {
                UTHelper.commonEvent(UTConstant.UserLayer.LayActivi3_click_FreeCollection, "status", if (success) "领取成功" else "领取失败")
            }
            MineConstant.VipLayerType.activity6 -> {
                UTHelper.commonEvent(UTConstant.UserLayer.LayActivi6_click_FreeCollection, "status", if (success) "领取成功" else "领取失败")
            }
        }
    }

    override fun finish() {
        super.finish()
        onDialogDismiss()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        onDialogDismiss()
    }
}