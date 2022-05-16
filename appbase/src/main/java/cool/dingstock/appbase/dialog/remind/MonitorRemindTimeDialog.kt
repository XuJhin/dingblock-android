package cool.dingstock.appbase.dialog.remind

import android.annotation.SuppressLint
import android.content.Context
import androidx.fragment.app.BaseBottomFullViewBindingFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import cool.dingstock.appbase.R
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.databinding.DialogMonitorRemindTimeLayoutBinding
import cool.dingstock.appbase.entity.bean.config.RemindTimeEntity
import cool.dingstock.appbase.entity.bean.home.bp.MonitorRemindMsgEntity
import cool.dingstock.appbase.helper.MonitorRemindHelper
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.home.AlarmFromWhere
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.lib_base.stroage.ConfigSPHelper


class MonitorRemindTimeDialog :
    BaseBottomFullViewBindingFragment<DialogMonitorRemindTimeLayoutBinding>() {

    var mContext: Context? = null
    var mRemindTimeConfig: RemindTimeEntity = RemindTimeEntity()
    var raffleId: String = ""
    var desc: String = ""
    var pos: Int = 0
    var productId: String = ""
    var notifyData: Long = 0L
    var fromWhere = AlarmFromWhere.SETTING_PAGE

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

    private val remindDialogIsNotDefault = "remindDialogIsDefault"

    @SuppressLint("SetTextI18n")
    override fun initDataEvent() {
        initView()
        initListener()
        initRv()
        initRvData()
    }

    private fun initView() {
        val isNotDefault = ConfigSPHelper.getInstance().getBoolean(remindDialogIsNotDefault)
        viewBinding.apply {
            ivIsDefault.isSelected = !isNotDefault//mRemindTimeConfig.isSetDefault
        }
    }

    private fun initListener() {
        viewBinding.apply {
            ivIsDefault.setOnShakeClickListener {
                val isClose = it.isSelected
                it.isSelected = !it.isSelected
                UTHelper.commonEvent(
                    UTConstant.Monitor.RemindSet_Default,
                    "operating",
                    if (isClose) "关闭" else "开启"
                )
            }

            tvConfirm.setOnShakeClickListener {
                val remindConfig = RemindTimeEntity(
                    (remindTimeAdapter.data[0] as MonitorRemindMsgEntity).isSelected,
                    (remindTimeAdapter.data[1] as MonitorRemindMsgEntity).isSelected,
                    (remindTimeAdapter.data[2] as MonitorRemindMsgEntity).isSelected,
                    (remindTimeAdapter.data[3] as MonitorRemindMsgEntity).isSelected,

                    (remindWayAdapter.data[0] as MonitorRemindMsgEntity).isSelected,
                    (remindWayAdapter.data[1] as MonitorRemindMsgEntity).isSelected,
                    ivIsDefault.isSelected
                )
                val monitorRemindHelper = MonitorRemindHelper(fromWhere, pos, productId)
                val isSetSuccess = mContext?.let { it1 ->
                    monitorRemindHelper.setRemindInDialog(
                        remindConfig,
                        it1,
                        raffleId,
                        desc,
                        ivIsDefault.isSelected,
                        notifyData
                    )
                }

                val remindType =
                    if ((remindWayAdapter.data[0] as MonitorRemindMsgEntity).isSelected) {
                        monitorRemindHelper.remindTypeCalendar
                    } else {
                        monitorRemindHelper.remindTypeDcPush
                    }

                if (ivIsDefault.isSelected) {
                    context?.let { it1 ->
                        monitorRemindHelper.updateRemindSetting(
                            it1,
                            remindType,
                            monitorRemindHelper.initRemindApiData(remindConfig)
                        )
                    }
                } else {
                    ConfigSPHelper.getInstance().save(remindDialogIsNotDefault, true)
                }
                if (isSetSuccess == true) {
                    dismiss()
                }
            }
        }
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
                0 -> UTHelper.commonEvent(UTConstant.Monitor.RemindSet_Time, "name", "开始前1分钟")
                1 -> UTHelper.commonEvent(UTConstant.Monitor.RemindSet_Time, "name", "开始前5分钟")
                2 -> UTHelper.commonEvent(UTConstant.Monitor.RemindSet_Time, "name", "开始前10分钟")
                3 -> UTHelper.commonEvent(UTConstant.Monitor.RemindSet_Time, "name", "开始前15分钟")
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
                0 -> UTHelper.commonEvent(UTConstant.Monitor.RemindSet_Remind, "name", "日历")
                1 -> UTHelper.commonEvent(UTConstant.Monitor.RemindSet_Remind, "name", "推送")
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

    private fun initRvData() {
        initLastStatus()

        remindTimeAdapter.setList(
            arrayListOf(
                MonitorRemindMsgEntity(
                    msg = "开始前1分钟",
                    icon = null,
                    isSelected = mRemindTimeConfig.isChoose1,
                    pos = 0
                ),
                MonitorRemindMsgEntity(
                    msg = "开始前5分钟",
                    icon = null,
                    isSelected = mRemindTimeConfig.isChoose2,
                    pos = 1
                ),
                MonitorRemindMsgEntity(
                    msg = "开始前10分钟",
                    icon = null,
                    isSelected = mRemindTimeConfig.isChoose3,
                    pos = 2
                ),
                MonitorRemindMsgEntity(
                    msg = "开始前15分钟",
                    icon = null,
                    isSelected = mRemindTimeConfig.isChoose4,
                    pos = 3
                ),
            )
        )

        remindWayAdapter.setList(
            arrayListOf(
                MonitorRemindMsgEntity(
                    msg = "添加到日历",
                    icon = R.drawable.icon_day_remind,
                    isSelected = mRemindTimeConfig.isCalendar,
                    pos = 0,
                ),
                MonitorRemindMsgEntity(
                    msg = "App推送通知",
                    icon = R.drawable.icon_remind_push,
                    isSelected = mRemindTimeConfig.isAppPush,
                    pos = 1
                )
            )
        )
    }

    private fun initLastStatus() {
        mLastTimeRemindWayClickPos = 0
        mLastTimeRemindTimeClickPos = 0
        mRemindTimeCount = 0

        if (mRemindTimeConfig.isChoose4) {
            mRemindTimeCount += 1
            mLastTimeRemindTimeClickPos = 3
        }
        if (mRemindTimeConfig.isChoose3) {
            mRemindTimeCount += 1
            mLastTimeRemindTimeClickPos = 2
        }
        if (mRemindTimeConfig.isChoose2) {
            mRemindTimeCount += 1
            mLastTimeRemindTimeClickPos = 1
        }
        if (mRemindTimeConfig.isChoose1) {
            mRemindTimeCount += 1
            mLastTimeRemindTimeClickPos = 0
        }

        if (mRemindTimeConfig.isCalendar) {
            mLastTimeRemindWayClickPos = 0
        }
        if (mRemindTimeConfig.isAppPush) {
            mLastTimeRemindWayClickPos = 1
        }
    }
}