package cool.dingstock.setting.ui.setting.index

import android.os.Bundle
import android.text.TextUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.constant.*
import cool.dingstock.appbase.entity.bean.setting.PushSoundEnum
import cool.dingstock.appbase.helper.BpProOrderHelper
import cool.dingstock.appbase.helper.SettingHelper
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.net.api.account.AccountHelper
import cool.dingstock.appbase.push.DCPushManager
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.GlideCacheUtil
import cool.dingstock.appbase.widget.commondialog.CommonDialog
import cool.dingstock.appbase.widget.recyclerview.adapter.BaseRVAdapter
import cool.dingstock.lib_base.util.AppUtils
import cool.dingstock.lib_base.util.CollectionUtils
import cool.dingstock.uicommon.setting.R
import cool.dingstock.uicommon.setting.adapter.SettingIndexFoot
import cool.dingstock.uicommon.setting.adapter.SettingIndexItem
import cool.dingstock.uicommon.setting.adapter.SettingLogoutFoot
import cool.dingstock.uicommon.setting.databinding.SettingActivityIndexBinding
import cool.dingstock.uicommon.setting.helper.EasyAuditionHelper
import java.util.*

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [SettingConstant.Path.INDEX]
)
class SettingIndexActivity :
    VMBindingActivity<SettingIndexViewModel, SettingActivityIndexBinding>() {
    private var rvAdapter: BaseRVAdapter<SettingIndexItem?>? = null
    private var pushSoundItem: SettingIndexItem? = null
    private var disturbItem: SettingIndexItem? = null
    private var easyAuditionHelper: EasyAuditionHelper? = null
    private var logoutFoot: SettingLogoutFoot? = null
    private var logoutSection = -1


    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        rvAdapter = BaseRVAdapter()
        easyAuditionHelper = EasyAuditionHelper()
        viewBinding.settingIndexRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = rvAdapter
        }
        setRvData()
        liveDataObserver()


    }

    private fun liveDataObserver() {
        viewModel.routeLiveData.observe(this) {
            navigationTo(it)
        }
        viewModel.signOutLiveData.observe(this) {
            onLogoutSuccess()
        }
    }

    override fun initListeners() {
        rvAdapter!!.setOnItemViewClickListener { item: SettingIndexItem?, sectionKey: Int, _: Int ->
            when (item!!.data.type) {
                "accountSetting" -> {
                    UTHelper.commonEvent(UTConstant.Setting.SettingP_click_Set, "name", "账户设置")
                    DcRouter(SettingConstant.Uri.ACCOUNT_SETTING).start()
                }
                "about" -> {
                    UTHelper.commonEvent(UTConstant.Setting.SettingP_click_Set, "name", "关于盯链")
                    DcRouter(SettingConstant.Uri.ABOUT_DC).start()
                }
                "clearCache" -> {
                    UTHelper.commonEvent(UTConstant.Setting.SettingP_click_Set, "name", "清理缓存")
                    try {
                        GlideCacheUtil.getInstance().clearImageAllCache(this)
                        BpProOrderHelper.clearOrderData()
                        showToastLong("清除成功")
                    } catch (e: Exception) {

                    }
                }
                "registerOut" -> {
                    try {
                        //注销登录
                    } catch (e: Exception) {
                    }
                }
                "videoSetting" -> {
                    UTHelper.commonEvent(UTConstant.Setting.SettingP_click_Vedio)
                    DcRouter(SettingConstant.Uri.SET_VIDEO).start()
                }
                else -> {
                }
            }
            UTHelper.settingCell(item.data.name)
        }
    }


    override fun moduleTag(): String {
        return ModuleConstant.SETTING
    }

    private fun setRvData() {
        val settingSectionList = SettingHelper.getInstance().geSettingItemList()
        if (CollectionUtils.isEmpty(settingSectionList)) {
            rvAdapter!!.showEmptyView()
            return
        }
        for (settingSection in settingSectionList) {
            val sectionKey = settingSectionList.indexOf(settingSection)
            if (settingSection!!.head == null) {
                settingSection.head = ""
            }
            val headView =
                cool.dingstock.uicommon.setting.adapter.SettingIndexHead(settingSection.head)
            rvAdapter!!.addHeadView(sectionKey, headView)
            val itemList: MutableList<SettingIndexItem?> = ArrayList()
            for (index in settingSection.items!!.indices) {
                val settingItemBean = settingSection.items!!.get(index)
                when (settingItemBean.type) {
                    "monitorFirst" -> settingItemBean.switchOpen =
                        SettingHelper.getInstance().isMonitorTabFirst
                    "pushSound" -> settingItemBean.value =
                        DCPushManager.getInstance().getPushSound(context).getName()
                    "disturb" -> settingItemBean.value =
                        SettingHelper.getInstance().disturbData.getDisplayValue()
                    else -> {
                    }
                }
                val settingIndexItem = SettingIndexItem(settingItemBean)
                //通知声音
                if (settingItemBean.type == "pushSound") {
                    pushSoundItem = settingIndexItem
                }
                if (settingItemBean.type == "disturb") {
                    disturbItem = settingIndexItem
                }
                if (settingItemBean.type == "about") {
                    settingIndexItem.data.value = "V" + AppUtils.getVersionName(context)
                }
                if (settingItemBean.type == "monitorFirst") {
                    settingItemBean.switchOpen = SettingHelper.getInstance().isMonitorTabFirst
                }
                //开关
                settingIndexItem.setSwitchListener { item: SettingIndexItem, isChecked: Boolean ->
                    if ("monitorFirst" == item.data.type) {
                        UTHelper.commonEvent(
                            UTConstant.Setting.SettingP_click_Set,
                            "name",
                            "将监控设为首页(${if (isChecked) "开" else "关"})"
                        )
                        SettingHelper.getInstance().saveMonitorTabFirst(isChecked)
                        showToastShort("重新启动APP生效")
                    }
                }
                //这里确定这个item的位置
                if (itemList.size == 0) {
                    settingIndexItem.isStart = true
                }
                if (index == settingSection.items!!.size - 1) {
                    settingIndexItem.isEnd = true
                }
                itemList.add(settingIndexItem)
            }
            if (!TextUtils.isEmpty(settingSection.foot)) {
                rvAdapter!!.addFootView(sectionKey, SettingIndexFoot(settingSection.foot))
            }
            rvAdapter!!.setItemViewList(sectionKey, itemList)
            if (sectionKey == settingSectionList.size - 1
                && null != AccountHelper.getInstance().user
            ) {
                logoutFoot = SettingLogoutFoot("")
                logoutSection = sectionKey
                logoutFoot!!.setLogoutListener(object : SettingLogoutFoot.LogoutListener {
                    override fun onLogoutClick() {
                        if (null == AccountHelper.getInstance().user) {
                            return
                        }
                        UTHelper.commonEvent(UTConstant.Setting.SettingP_click_sign_out)
                        UTHelper.commonEvent(UTConstant.Setting.SettingP_click_Set, "name", "退出登录")
                        showSignOutDialog()
                    }
                })
                rvAdapter!!.addFootView(sectionKey, logoutFoot!!)
            }
        }
    }

    private fun showSignOutDialog() {
        CommonDialog.Builder(this)
            .content(getString(R.string.setting_logout_confirm_title))
            .cancelTxt(getString(R.string.common_cancel))
            .confirmTxt(getString(R.string.common_confirm))
            .onConfirmClick {
                viewModel.signOut()
            }
            .builder()
            .show()
    }


    private fun play(targetType: PushSoundEnum?) {
        easyAuditionHelper?.stop()
        if (null == targetType) {
            return
        }
        val soundUri = PushSoundEnum.getSoundUri(targetType)
        easyAuditionHelper?.play(soundUri)
    }

    override fun onPause() {
        super.onPause()
        easyAuditionHelper?.stop()
    }

    private fun navigationTo(url: String?) {
        hideLoadingDialog()
        DcRouter(url!!).start()
    }

    private fun onLogoutSuccess() {
        finish()
        if (null == rvAdapter) {
            return
        }
        if (null != logoutFoot && logoutSection >= 0) {
            rvAdapter!!.removeFootView(logoutSection, logoutFoot!!)
        }
    }
}