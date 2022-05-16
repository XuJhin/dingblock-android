package cool.dingstock.monitor.ui.remindSetting

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.MonitorConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.entity.bean.config.RemindTimeEntity
import cool.dingstock.appbase.entity.bean.home.bp.MonitorRemindMsgEntity
import cool.dingstock.appbase.helper.MonitorRemindHelper
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.monitor.R
import cool.dingstock.appbase.dialog.remind.MonitorRemindTimeItemBinder
import cool.dingstock.appbase.dialog.remind.MonitorRemindWayItemBinder
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.config.RemindConfigEntity
import cool.dingstock.appbase.entity.bean.home.AlarmFromWhere
import cool.dingstock.appbase.helper.RemindConfigRefreshHelper
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.NotificationsUtils
import cool.dingstock.monitor.databinding.ActivityRemindSettingBinding

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [MonitorConstant.Path.MONITOR_REMIND_SETTING]
)
class RemindSettingActivity :
    VMBindingActivity<RemindSettingViewModel, ActivityRemindSettingBinding>() {

    private val remindTimeAdapter by lazy {
        DcBaseBinderAdapter(arrayListOf())
    }

    private val remindWayAdapter by lazy {
        DcBaseBinderAdapter(arrayListOf())
    }

    private val remindTimeItemBinder by lazy {
        MonitorRemindTimeItemBinder()
    }

    private val remindWayItemBinder by lazy {
        MonitorRemindWayItemBinder()
    }

    private var mLastTimeRemindWayClickPos = 0
    private var mLastTimeRemindTimeClickPos = 0
    private var mRemindTimeCount = 0

    private var newRemindTimeConfig = RemindTimeEntity()

    private val helper: MonitorRemindHelper =
        MonitorRemindHelper(AlarmFromWhere.SETTING_PAGE, 0, "")

    override fun moduleTag(): String {
        return ModuleConstant.MONITOR
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        initRv()
        viewModel.fetchRemindSetting()
        showLoadingView()
    }

    override fun initListeners() {
        viewBinding.apply {
            ivBack.setOnShakeClickListener {
                finish()
            }
            tvSave.setOnShakeClickListener {
                if (!NotificationsUtils.isNotificationEnabled(context)
                    && (remindWayAdapter.data[1] as MonitorRemindMsgEntity).isSelected
                ) {
                    helper.openSystemSettingPage(context)
                } else {
                    newRemindTimeConfig = RemindTimeEntity(
                        (remindTimeAdapter.data[0] as MonitorRemindMsgEntity).isSelected,
                        (remindTimeAdapter.data[1] as MonitorRemindMsgEntity).isSelected,
                        (remindTimeAdapter.data[2] as MonitorRemindMsgEntity).isSelected,
                        (remindTimeAdapter.data[3] as MonitorRemindMsgEntity).isSelected,

                        (remindWayAdapter.data[0] as MonitorRemindMsgEntity).isSelected,
                        (remindWayAdapter.data[1] as MonitorRemindMsgEntity).isSelected,
                        true
                    )

                    val remindType =
                        if ((remindWayAdapter.data[0] as MonitorRemindMsgEntity).isSelected) {
                            helper.remindTypeCalendar
                        } else {
                            helper.remindTypeDcPush
                        }
                    viewModel.updateRemindSetting(
                        remindType,
                        helper.initRemindApiData(newRemindTimeConfig)
                    )
                }
            }
        }
    }

    override fun initBaseViewModelObserver() {
        viewModel.apply {
            isUpDataSuccess.observe(this@RemindSettingActivity) {
                RemindConfigRefreshHelper.isRefreshRemindConfig = false
                finish()
            }
            remindConfigLiveData.observe(this@RemindSettingActivity) {
                hideLoadingView()
                initRvData(it)
            }
            isFetchDataSuccess.observe(this@RemindSettingActivity) {
                hideLoadingView()
            }
        }
        super.initBaseViewModelObserver()
    }

    private fun initRv() {
        remindTimeItemBinder.setClickListener {
            if (mRemindTimeCount == 1) {
                val isSelect = (remindTimeAdapter.data[it] as MonitorRemindMsgEntity).isSelected
                if (!isSelect) {
                    (remindTimeAdapter.data[it] as MonitorRemindMsgEntity).isSelected = true
                    remindTimeAdapter.notifyDataItemChanged(it)
                    mRemindTimeCount += 1
                }
            } else {
                val isSelect = (remindTimeAdapter.data[it] as MonitorRemindMsgEntity).isSelected
                (remindTimeAdapter.data[it] as MonitorRemindMsgEntity).isSelected = !isSelect
                remindTimeAdapter.notifyDataItemChanged(it)
                if (isSelect) {
                    mRemindTimeCount -= 1
                } else {
                    mRemindTimeCount += 1
                }
            }
            mLastTimeRemindTimeClickPos = it

            when (it) {
                0 -> UTHelper.commonEvent(UTConstant.Monitor.RemindSetP_Time, "name", "开始前1分钟")
                1 -> UTHelper.commonEvent(UTConstant.Monitor.RemindSetP_Time, "name", "开始前5分钟")
                2 -> UTHelper.commonEvent(UTConstant.Monitor.RemindSetP_Time, "name", "开始前10分钟")
                3 -> UTHelper.commonEvent(UTConstant.Monitor.RemindSetP_Time, "name", "开始前15分钟")
            }

        }

        remindWayItemBinder.setClickListener {
            if (mLastTimeRemindWayClickPos != it) {
                (remindWayAdapter.data[mLastTimeRemindWayClickPos] as MonitorRemindMsgEntity).isSelected =
                    false
                (remindWayAdapter.data[it] as MonitorRemindMsgEntity).isSelected = true

                remindWayAdapter.notifyDataItemChanged(mLastTimeRemindWayClickPos)
                remindWayAdapter.notifyDataItemChanged(it)
            }
            mLastTimeRemindWayClickPos = it

            when (it) {
                0 -> UTHelper.commonEvent(UTConstant.Monitor.RemindSetP_Remind, "name", "日历")
                1 -> UTHelper.commonEvent(UTConstant.Monitor.RemindSetP_Remind, "name", "推送")
            }
        }

        remindTimeAdapter.addItemBinder(MonitorRemindMsgEntity::class.java, remindTimeItemBinder)
        remindWayAdapter.addItemBinder(MonitorRemindMsgEntity::class.java, remindWayItemBinder)

        viewBinding.apply {
            rvRemindTime.apply {
                adapter = remindTimeAdapter
                layoutManager = GridLayoutManager(context, 2)
            }
            rvRemindWay.apply {
                adapter = remindWayAdapter
                layoutManager = LinearLayoutManager(context)
            }
        }
    }

    private fun initRvData(entity: RemindConfigEntity) {
        var isCalendar = false
        var isAppPush = false

        var choose1 = false
        var choose2 = false
        var choose3 = false
        var choose4 = false

        if (entity.remindType == helper.remindTypeCalendar) {
            isCalendar = true
        } else {
            isAppPush = true
        }

        entity.remindAt?.forEach {
            if (it == helper.oneMinute) choose1 = true
            if (it == helper.fiveMinute) choose2 = true
            if (it == helper.tenMinute) choose3 = true
            if (it == helper.fifteenMinute) choose4 = true
        }

        if (!choose1 && !choose2 && !choose3 && !choose4) {
            choose1 = true
            isCalendar = true
            isAppPush = false
        }

        initLastStatus(isCalendar, isAppPush, choose1, choose2, choose3, choose4)

        remindTimeAdapter.setList(
            arrayListOf(
                MonitorRemindMsgEntity(
                    msg = "开始前1分钟",
                    icon = null,
                    isSelected = choose1,
                    pos = 0
                ),
                MonitorRemindMsgEntity(
                    msg = "开始前5分钟",
                    icon = null,
                    isSelected = choose2,
                    pos = 1
                ),
                MonitorRemindMsgEntity(
                    msg = "开始前10分钟",
                    icon = null,
                    isSelected = choose3,
                    pos = 2
                ),
                MonitorRemindMsgEntity(
                    msg = "开始前15分钟",
                    icon = null,
                    isSelected = choose4,
                    pos = 3
                ),
            )
        )

        remindWayAdapter.setList(
            arrayListOf(
                MonitorRemindMsgEntity(
                    msg = "添加到日历",
                    icon = R.drawable.icon_day_remind,
                    isSelected = isCalendar,
                    pos = 0
                ),
                MonitorRemindMsgEntity(
                    msg = "App推送通知",
                    icon = R.drawable.icon_remind_push,
                    isSelected = isAppPush,
                    pos = 1
                ),
            )
        )
    }

    private fun initLastStatus(
        isCalendar: Boolean,
        isAppPush: Boolean,
        choose1: Boolean,
        choose2: Boolean,
        choose3: Boolean,
        choose4: Boolean
    ) {
        mLastTimeRemindWayClickPos = 0
        mLastTimeRemindTimeClickPos = 0
        mRemindTimeCount = 0

        if (choose4) {
            mRemindTimeCount += 1
            mLastTimeRemindTimeClickPos = 3
        }
        if (choose3) {
            mRemindTimeCount += 1
            mLastTimeRemindTimeClickPos = 2
        }
        if (choose2) {
            mRemindTimeCount += 1
            mLastTimeRemindTimeClickPos = 1
        }
        if (choose1) {
            mRemindTimeCount += 1
            mLastTimeRemindTimeClickPos = 0
        }

        if (isCalendar) {
            mLastTimeRemindWayClickPos = 0
        }
        if (isAppPush) {
            mLastTimeRemindWayClickPos = 1
        }
    }
}