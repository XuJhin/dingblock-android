package cool.dingstock.calendar.sms

import android.Manifest
import android.os.Bundle
import android.text.TextUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.permissionx.guolindev.PermissionX
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.adapter.LoadMoreBinderAdapter
import cool.dingstock.appbase.constant.CalendarConstant
import cool.dingstock.appbase.constant.HomeConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.entity.bean.home.SmsRaffleBean
import cool.dingstock.appbase.entity.bean.home.SmsRegistrationBean
import cool.dingstock.appbase.entity.event.update.EventRefreshLotteryNote
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.net.api.calendar.CalendarHelper
import cool.dingstock.appbase.share.ShareParams
import cool.dingstock.appbase.share.ShareType
import cool.dingstock.appbase.util.CalendarReminderUtils
import cool.dingstock.appbase.util.LoginUtils.isLoginAndRequestLogin
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.appbase.widget.dialog.common.DcTitleDialog
import cool.dingstock.calendar.R
import cool.dingstock.calendar.databinding.ActivitySmsRegistrationBinding
import cool.dingstock.lib_base.util.TimeUtils
import cool.dingstock.lib_base.util.ToastUtil
import cool.dingstock.uicommon.calendar.dialog.ComparisonPriceDialog
import cool.dingstock.uicommon.product.dialog.SmsRegistrationDialog
import cool.dingstock.uicommon.product.item.HomeSmsRegistrationItemBinder
import cool.mobile.account.share.ShareDialog
import org.greenrobot.eventbus.EventBus


@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [HomeConstant.Path.SMS_REGISTRATION]
)
class SmsRegistrationActivity :
    VMBindingActivity<SmsRegistrationViewModel, ActivitySmsRegistrationBinding>() {

    private val mAdapter: LoadMoreBinderAdapter by lazy {
        LoadMoreBinderAdapter()
    }

    private val itemBinder: HomeSmsRegistrationItemBinder by lazy {
        HomeSmsRegistrationItemBinder()
    }

    private var whiteList: List<String> = emptyList()

    private val smsRegistrationDialog by lazy {
        SmsRegistrationDialog()
    }

    private val comparisonPriceDialog by lazy {
        ComparisonPriceDialog()
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        initRv()
        viewBinding.refresh.setOnRefreshListener {
            refreshPage(false)
        }
        refreshPage(true)
    }

    private fun openChooseDialog(smsRaffleBean: SmsRaffleBean?) {
        smsRaffleBean?.sms?.let { homeRaffleSmsBean ->
            //可以改到viewModel中去
            showLoadingDialog()
            viewModel.calendarApi.smsInputTemplate(smsRaffleBean.id)
                .subscribe({
                    hideLoadingDialog()
                    if (!it.err && it.res != null) {
                        smsRegistrationDialog.setSmsData(
                            smsRaffleBean.id,
                            homeRaffleSmsBean,
                            it.res!!
                        )
                        smsRegistrationDialog.show(supportFragmentManager, "smsRegistrationDialog")
                    } else {
                        ToastUtil.getInstance()._short(context, it.msg)
                    }
                }, {
                    hideLoadingDialog()
                    ToastUtil.getInstance()._short(context, it.message)
                })
        }
    }

    private fun initRv() {
        itemBinder.mListener = object : HomeSmsRegistrationItemBinder.ActionListener {
            override fun onShareClick(
                name: String,
                imageUrl: String,
                smsRaffleBean: SmsRaffleBean?
            ) {
                showShareView(name, imageUrl, smsRaffleBean)
            }

            override fun onAlarmClick(
                name: String,
                imageUrl: String,
                smsRaffleBean: SmsRaffleBean?
            ) {
                val user = getAccount()
                if (user != null) {
                    loadDataToCalender(name, smsRaffleBean!!)
                }
            }

            override fun onSmsClick(name: String, imageUrl: String, smsRaffleBean: SmsRaffleBean?) {
                if (!isLoginAndRequestLogin(context)) {
                    return
                }
                CalendarHelper.raffleAction(smsRaffleBean?.id, true).subscribe { res ->
                    if (!res.err) {
                        EventBus.getDefault().post(EventRefreshLotteryNote())
                    }
                }
                if (smsRaffleBean?.showOldPage == true) {
                    val user = getAccount()
                    if (user != null) {
                        routerToSms(name, imageUrl, smsRaffleBean)
                    }
                } else {
                    openChooseDialog(smsRaffleBean)
                }
            }

            override fun onSearchPriceClick(
                id: String?,
            ) {
                id?.let {
                    viewModel.searchPrice(it)
                }
            }

        }
        mAdapter.addItemBinder(SmsRegistrationBean::class.java, itemBinder)
        viewBinding.rv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }
    }

    override fun initBaseViewModelObserver() {
        super.initBaseViewModelObserver()
        viewModel.apply {
            smsRegistrationListData.observe(this@SmsRegistrationActivity) {
                finishRequest()
                if (it.isNullOrEmpty()) {
                    showEmptyView()
                } else {
                    mAdapter.setList(it)
                }
            }
            smsWhiteListData.observe(this@SmsRegistrationActivity) {
                whiteList = it
                itemBinder.whiteList = whiteList
            }
            priceListLiveData.observe(this@SmsRegistrationActivity) {
                comparisonPriceDialog.setData(it)
                comparisonPriceDialog.show(supportFragmentManager, "comparisonPriceDialog")
            }
        }
    }

    override fun initListeners() {
        viewBinding.circleActivityTopicDetailBackIcon.setOnShakeClickListener {
            finish()
        }
    }

    private fun refreshPage(isFirst: Boolean = true) {
        if (isFirst) {
            showLoadingView()
        }
        viewModel.fetchData()
    }

    private fun finishRequest() {
        hideLoadingView()
        viewBinding.refresh.finishRefresh()
    }

    override fun showEmptyView() {
        mAdapter.setEmptyView(R.layout.common_empty_layout)
        mAdapter.setList(emptyList())
    }

    private fun showShareView(name: String, imageUrl: String, smsRaffleBean: SmsRaffleBean?) {
        if (null == smsRaffleBean) {
            return
        }
        val content = String.format(
            getString(R.string.raffle_share_content_template),
            smsRaffleBean.brand?.name,
            name
        )

        val type = ShareType.Link
        val params = ShareParams()
        params.title = getString(R.string.raffle_share_title)
        params.content = content
        params.imageUrl = imageUrl
        params.link =
            if (TextUtils.isEmpty(smsRaffleBean.shareLink)) smsRaffleBean.link else smsRaffleBean.shareLink
        type.params = params

        val shareDialog = ShareDialog(context)
        shareDialog.shareType = type
        shareDialog.show()
    }

    private fun routerToSms(name: String, imageUrl: String, smsRaffleBean: SmsRaffleBean?) {
        val smsStartAt = smsRaffleBean?.smsStartAt
        if (smsStartAt != null) {
            if (smsStartAt > System.currentTimeMillis()) {
                showTimingDialog(smsStartAt, smsRaffleBean, name, imageUrl)
            } else {
                DcRouter(HomeConstant.Uri.SMS_EDIT)
                    .putExtra(CalendarConstant.DataParam.KEY_SMS, smsRaffleBean.sms)
                    .start()
            }
        }
    }

    private fun showTimingDialog(
        smsStartAt: Long,
        smsRaffleBean: SmsRaffleBean?,
        name: String, imageUrl: String,
    ) {
        val niceDate = TimeUtils.formatTimestampCustom(smsStartAt, "MM月dd日 HH:mm")
        val messageContent = "抽签将在$niceDate 开始,请稍后再试"
        DcTitleDialog.Builder(this)
            .title("短信抽签还未开始")
            .content(messageContent)
            .cancelTxt("取消")
            .confirmTxt("提醒我")
            .onConfirmClick {
                loadDataToCalender(name, smsRaffleBean)
            }
            .builder()
            .show()
    }

    private fun remind(smsRaffleBean: SmsRaffleBean?, name: String) {
        val brand = smsRaffleBean?.brand
        val place = if (null == brand) "" else brand.name
        val releaseDateString = smsRaffleBean?.releaseDateString
        val method = smsRaffleBean?.method
        val des = """
            ${name}
            $place
            $releaseDateString
            $method
            """.trimIndent()
        val addSuccess = CalendarReminderUtils.addCalendarEvent(
            context,
            des,
            "",
            TimeUtils.format2Mill(smsRaffleBean?.notifyDate!!)
        )
        showToastShort(if (addSuccess) R.string.home_setup_success else R.string.home_setup_failed)
    }

    private fun loadDataToCalender(name: String, smsRaffleBean: SmsRaffleBean?) {
        PermissionX.init(this)
            .permissions(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR)
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(deniedList, "日历权限是此功能正常运行所依赖的权限，请授予", "明白", "取消")
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(deniedList, "是否前往设置页开启日历权限", "前往", "取消")
            }
            .request { allGranted, _, _ ->
                if (allGranted) {
                    remind(smsRaffleBean, name)
                } else {
                    showToastShort("获取日历权限失败")
                }
            }
    }

    override fun moduleTag(): String {
        return HomeConstant.HOME_TAB_ID
    }
}