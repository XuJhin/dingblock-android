package cool.dingstock.mine.ui.score.index

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseBinderAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.sankuai.waimai.router.annotation.RouterUri
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.simple.SimpleMultiListener
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.adapter.itembinder.OnItemClickListener
import cool.dingstock.appbase.constant.MineConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.score.ScoreExchangeItemEntity
import cool.dingstock.appbase.entity.bean.score.ScoreIndexUserInfoEntity
import cool.dingstock.appbase.entity.bean.score.ScoreTaskItemEntity
import cool.dingstock.appbase.entity.bean.score.SignCalendarEntity
import cool.dingstock.appbase.entity.event.update.EventScoreChange
import cool.dingstock.appbase.ext.*
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.appbase.util.isDarkMode
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.mine.R
import cool.dingstock.mine.databinding.ActivityScoreIndexBinding
import cool.dingstock.mine.dialog.ScoreCommonDescDialog
import cool.dingstock.mine.itemView.ScoreExchangeGoodsItemBinder
import cool.dingstock.mine.itemView.ScoreExchangeGoodsItemDiffCallback
import cool.dingstock.mine.itemView.ScoreTaskItemBinder
import cool.dingstock.mine.itemView.ScoreTaskItemDiffCallback
import cool.dingstock.uicommon.widget.SignViewDialog
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.min

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [MineConstant.Path.SCORE_INDEX]
)
class ScoreIndexActivity : VMBindingActivity<ScoreIndexVM, ActivityScoreIndexBinding>() {
    private val dayilyTaskAdapter = DcBaseBinderAdapter(arrayListOf())
    private val newbieTaskAdapter = DcBaseBinderAdapter(arrayListOf())
    private val exchangeAdapter = DcBaseBinderAdapter(arrayListOf())
    private val dailyItemBinder = ScoreTaskItemBinder()
    private val newbieItemBinder = ScoreTaskItemBinder()
    private val exchangeItemBinder = ScoreExchangeGoodsItemBinder()
    var indexUserInfoEntity: ScoreIndexUserInfoEntity? = null
    var mOffset = 0f
    private var mScrollY = 0

    companion object {
        private val IMAGE_DISTANCE: Int = 50.azDp.toInt()
    }

    private val continuedCheckInAdapter: DcBaseBinderAdapter by lazy {
        DcBaseBinderAdapter(arrayListOf())
    }

    override fun setSystemStatusBar() {
        super.setSystemStatusBar()
        StatusBarUtil.transparentStatus(this)
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        if (isDarkMode()) {
            viewBinding.mineScoreIvBg.visibility = View.INVISIBLE
        } else {
            viewBinding.mineScoreIvBg.visibility = View.VISIBLE
        }
        viewBinding.signLottieV.repeatCount = Int.MAX_VALUE
        viewBinding.mineScoreRefresh.apply {
            setHeaderInsetStart(50f)
            setOnMultiListener(object : SimpleMultiListener() {
                override fun onHeaderMoving(
                    header: RefreshHeader?,
                    isDragging: Boolean,
                    percent: Float,
                    offset: Int,
                    headerHeight: Int,
                    maxDragHeight: Int
                ) {
                    mOffset = offset / 2f
                    viewBinding.mineScoreIvBg.translationY = (mOffset - mScrollY)
                    val alpha = 1 - percent.coerceAtMost(1f)
                    viewBinding.titleBar.alpha = alpha
                }

                override fun onRefresh(refreshLayout: RefreshLayout) {
                    viewModel.refreshInfo()
                }
            })
        }
        initRv()
        initView()
        initObserver()
        viewModel.fetchData()
    }

    private fun initView() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
    }

    override fun setOnErrorViewClick(onClickListener: View.OnClickListener) {
        super.setOnErrorViewClick(onClickListener)
        viewModel.refreshInfo()
    }

    private fun initObserver() {
        viewModel.infoLiveData.observe(this) {
            it.userInfo?.let { userInfo ->
                initUserInfo(userInfo, true)
            }
            viewBinding.dailyTaskCard.hide(it.taskInfo?.dailies?.isNullOrEmpty() ?: true)
            dayilyTaskAdapter.setList(it.taskInfo?.dailies)
            viewBinding.noviceTaskCard.hide(it.taskInfo?.newbies?.isNullOrEmpty() ?: true)
            newbieTaskAdapter.setList(it.taskInfo?.newbies)
            viewBinding.exchangeCard.hide(it.productInfo?.isNullOrEmpty() ?: true)
            exchangeAdapter.setList(it.productInfo)
            continuedCheckInAdapter.setList(it.userInfo?.signCalendar)
        }
        viewModel.refreshInfoLiveData.observe(this) {
            viewBinding.mineScoreRefresh.finishRefresh()
            it.userInfo?.let { userInfo ->
                initUserInfo(userInfo, true)
            }
            viewBinding.dailyTaskCard.hide(it.taskInfo?.dailies?.isNullOrEmpty() ?: true)
            viewBinding.noviceTaskCard.hide(it.taskInfo?.newbies?.isNullOrEmpty() ?: true)
            viewBinding.exchangeCard.hide(it.productInfo?.isNullOrEmpty() ?: true)
            val listDaliy = arrayListOf<Any>()
            it.taskInfo?.dailies?.forEach { element ->
                listDaliy.add(element)
            }
            dayilyTaskAdapter.getDiffer().submitList(listDaliy, null)
            val listNewbie = arrayListOf<Any>()
            it.taskInfo?.newbies?.forEach { element ->
                listNewbie.add(element)
            }
            newbieTaskAdapter.getDiffer().submitList(listNewbie, null)
            val listExchange = arrayListOf<Any>()
            it.productInfo?.forEach { element ->
                listExchange.add(element)
            }
            exchangeAdapter.getDiffer().submitList(listExchange, null)
            //刷新签到日历
            val listCalendar = arrayListOf<Any>()
            it.userInfo?.signCalendar?.forEach { element ->
                listCalendar.add(element)
            }
            continuedCheckInAdapter.getDiffer().submitList(listCalendar, null)
        }
        viewModel.signLiveData.observe(this) {
            val signView = SignViewDialog(this, false)
            signView.signListener = object : SignViewDialog.SignListener {
                override fun onSignComplete(entity: ScoreIndexUserInfoEntity) {
                    //刷新页面
                    EventBus.getDefault().post(EventScoreChange(true))
                }
            }
            signView.startSign(it)
        }
        viewModel.switchAlertLiveData.observe(this) {
            viewBinding.signInHintSwb.isChecked = it
        }
        viewModel.refreshFailedLivedata.observe(this) {
            showToastShort(it)
            viewBinding.mineScoreRefresh.finishRefresh()
        }
    }

    override fun initListeners() {
        initScrollListener()
        viewBinding.signInHintSwb.setOnShakeClickListener {
            UTHelper.commonEvent(
                UTConstant.Score.IntegralP_SignReminder,
                "switch",
                if (viewBinding.signInHintSwb.isChecked) "打开" else "关闭"
            )
            viewModel.switchAlert(viewBinding.signInHintSwb.isChecked)
        }
        viewBinding.layoutAlertClockIn.setOnShakeClickListener {
            viewBinding.signInHintSwb.isChecked = !viewBinding.signInHintSwb.isChecked
            UTHelper.commonEvent(
                UTConstant.Score.IntegralP_SignReminder,
                "switch",
                if (viewBinding.signInHintSwb.isChecked) "打开" else "关闭"
            )
            viewModel.switchAlert(viewBinding.signInHintSwb.isChecked)
        }
        viewBinding.signLottieV.setOnShakeClickListener {
            viewModel.startSign()
        }
        viewBinding.tvLeft.setOnShakeClickListener {
            viewModel.gotoIntegralDetail()
        }
        //定位到积分兑换位置
        viewBinding.exchangeTv.setOnShakeClickListener {
            UTHelper.commonEvent(UTConstant.Score.IntegralP_click_RedeemNow)
            viewBinding.scrollParent.scrollY = viewBinding.exchangeCard.y.toInt() - 80.dp.toInt()
        }
        viewBinding.signResultImg.setOnClickListener {
            UTHelper.commonEvent(UTConstant.Score.IntegralP_click_ViewSignature)
            indexUserInfoEntity?.let {
                SignViewDialog(this, false).setData(it).show()
            }
        }
        viewBinding.exchangeRecodeLayer.setOnClickListener {
            UTHelper.commonEvent(UTConstant.Score.IntegralP_click_ExchangeRecord)
            DcRouter(MineConstant.Uri.EXCHANGE_RECORD).start()
        }
        viewBinding.scoreTv.setOnShakeClickListener {
            UTHelper.commonEvent(UTConstant.Score.IntegralP_click_PointsDetails)
            DcRouter(MineConstant.Uri.EXCHANGE_DETAILS).start()
        }
        viewBinding.scoreIv.setOnShakeClickListener {
            UTHelper.commonEvent(UTConstant.Score.IntegralP_click_PointsDetails)
            DcRouter(MineConstant.Uri.EXCHANGE_DETAILS).start()
        }
        viewBinding.ivBack.setOnClickListener {
            finish()
        }
        viewBinding.helper.setOnShakeClickListener {
            ScoreCommonDescDialog(context).setData(
                resources.getString(R.string.score_index_helper_title),
                resources.getString(R.string.score_index_helper_desc)
            ).show()
        }

        exchangeItemBinder.onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(
                adapter: BaseBinderAdapter,
                holder: BaseViewHolder,
                position: Int
            ) {
                val data = adapter.data[position]
                if (data is ScoreExchangeItemEntity) {
                    routeExchangeGoodDetail(data)
                }
            }
        }
        exchangeItemBinder.goodsExchangeListener =
            object : ScoreExchangeGoodsItemDiffCallback.GoodsExchangeClickListener {
                override fun onGoodsExchangeClick(data: ScoreExchangeItemEntity) {
                    routeExchangeGoodDetail(data)
                }
            }
        dailyItemBinder.taskReceiveClickListener =
            object : ScoreTaskItemBinder.TaskReceiveClickListener {
                override fun onTaskReceiveClick(data: ScoreTaskItemEntity) {
                    receiveScore(data)
                }
            }
        newbieItemBinder.taskReceiveClickListener =
            object : ScoreTaskItemBinder.TaskReceiveClickListener {
                override fun onTaskReceiveClick(data: ScoreTaskItemEntity) {
                    receiveScore(data)
                }
            }
    }

    private fun routeExchangeGoodDetail(data: ScoreExchangeItemEntity) {
        DcRouter(MineConstant.Uri.EXCHANGE_GOOD_DETAIL)
            .putExtra(MineConstant.ExtraParam.FROM_WHERE, "FROM_SCORE_INDEX")
            .putExtra(MineConstant.ExtraParam.EXCHANGE_GOOD, data)
            .putExtra(MineConstant.ExtraParam.EXCHANGE_USER_INFO, indexUserInfoEntity)
            .start()
    }

    private fun initUserInfo(userInfo: ScoreIndexUserInfoEntity, needSetData: Boolean) {
        indexUserInfoEntity = userInfo
        viewBinding.signInHintSwb.isChecked = userInfo.signAlert
        val score = if (userInfo.userCredit < 0) {
            0
        } else {
            userInfo.userCredit
        }
        viewBinding.scoreTv.text = "$score"
        viewBinding.avatarIv.loadAvatar(userInfo.avatarUrl)
        viewBinding.signLottieV.hide(userInfo.isSign)
        viewBinding.signResultImg.hide(!userInfo.isSign)
        viewBinding.signResultImg.load(userInfo.fortuneImageUrl, false)
        viewBinding.mineScoreIndexSignInfo.text = userInfo.creditsStr
        viewBinding.mineScoreIndexSignDay.text = userInfo.signCountStr
    }

    private fun initRv() {
        viewBinding.apply {
            dailyTaskRv.apply {
                dayilyTaskAdapter.addItemBinder(
                    ScoreTaskItemEntity::class.java,
                    dailyItemBinder,
                    ScoreTaskItemDiffCallback()
                )
                adapter = dayilyTaskAdapter
                layoutManager =
                    LinearLayoutManager(
                        this@ScoreIndexActivity,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
            }
            noviceTaskRv.apply {
                newbieTaskAdapter.addItemBinder(
                    ScoreTaskItemEntity::class.java,
                    newbieItemBinder,
                    ScoreTaskItemDiffCallback()
                )
                adapter = newbieTaskAdapter
                layoutManager =
                    LinearLayoutManager(
                        this@ScoreIndexActivity,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
            }
            exchangeRv.apply {
                exchangeAdapter.addItemBinder(
                    ScoreExchangeItemEntity::class.java,
                    exchangeItemBinder,
                    ScoreExchangeGoodsItemDiffCallback()
                )
                adapter = exchangeAdapter
                layoutManager =
                    GridLayoutManager(this@ScoreIndexActivity, 3, GridLayoutManager.VERTICAL, false)
            }
            rvContinueClockIn.apply {
                continuedCheckInAdapter.addItemBinder(
                    SignCalendarEntity::class.java,
                    ContinuedCheckInItem(),
                    CheckedItemDiffCallback()
                )
                layoutManager =
                    GridLayoutManager(this@ScoreIndexActivity, 7, GridLayoutManager.VERTICAL, false)
                adapter = continuedCheckInAdapter
            }
        }

    }

    private fun initScrollListener() {
        viewBinding.scrollParent.setOnScrollChangeListener(object :
            NestedScrollView.OnScrollChangeListener {
            val bgColor = resources.getColor(R.color.white)
            override fun onScrollChange(
                v: NestedScrollView?,
                scrollX: Int,
                scrollY: Int,
                oldScrollX: Int,
                oldScrollY: Int
            ) {
                val verticalOffset = scrollY.absoluteValue
                val scale = verticalOffset.toFloat() / IMAGE_DISTANCE
                val alpha = (scale * 255).toInt()

                val alphaColor =
                    Color.argb(min(alpha, 255), bgColor.red, bgColor.green, bgColor.blue)
                viewBinding.titleBar.setBackgroundColor(alphaColor)
            }
        })
        viewBinding.titleBar.setBackgroundResource(R.color.transparent)
    }

    fun receiveScore(data: ScoreTaskItemEntity) {
        viewModel.receiveScore(data)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun scoreStatusChange(event: EventScoreChange?) {
        viewModel.refreshInfo()
    }

    override fun moduleTag(): String = "mine"

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}