package cool.dingstock.mine.ui.score.record

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.constant.MineConstant
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.entity.bean.mine.ScoreRefreshEvent
import cool.dingstock.appbase.entity.bean.score.ScoreRecordEntity
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.mine.MineApi
import cool.dingstock.appbase.util.ClipboardHelper
import cool.dingstock.appbase.widget.CommonEmptyView
import cool.dingstock.lib_base.util.CollectionUtils
import cool.dingstock.lib_base.util.ToastUtil
import cool.dingstock.mine.R
import cool.dingstock.mine.dagger.MineApiHelper
import cool.dingstock.appbase.dialog.LogisticsInfoDialog
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.mine.databinding.ActivityScoreExchangeRecordBinding
import cool.dingstock.mine.itemView.ScoreExchangeRecordItemBinder
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

@RouterUri(scheme = RouterConstant.SCHEME, host = RouterConstant.HOST, path = [MineConstant.Path.EXCHANGE_RECORD])
class ScoreExchangeRecordActivity : VMBindingActivity<BaseViewModel, ActivityScoreExchangeRecordBinding>() {

    @Inject
    lateinit var minAny: MineApi
    private val mAdapter = DcBaseBinderAdapter(arrayListOf())
    private var nextKey: Long? = null
    private val itemBinder = ScoreExchangeRecordItemBinder()
    private var isRefresh = true

    //刷新 或者是第一次获取
    private fun fetchData() {
        isRefresh = true
        nextKey = null
        loadData()
    }


    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        MineApiHelper.apiMineComponent.inject(this)
        viewBinding.titleBar.title = "兑换记录"
        mAdapter.addItemBinder(ScoreRecordEntity::class.java, itemBinder)
        val emptyView = CommonEmptyView(this)
        emptyView.setBackgroundColor(ContextCompat.getColor(context, R.color.color_gray))
        emptyView.setPadding(0, 20, 0, 20)
        mAdapter.setEmptyView(emptyView)
        viewBinding.mineDrawerRv.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        viewBinding.exchangeListIndexRefresh.setOnRefreshListener {
            refreshPage(false)
        }
        refreshPage(true)
    }

    private fun refreshPage(isFirst: Boolean = true) {
        if (isFirst) {
            showLoadingView()
        }
        fetchData()
    }

    private fun finishRequest() {
        hideLoadingView()
        viewBinding.exchangeListIndexRefresh.finishRefresh()
    }

    override fun onStatusViewErrorClick() {
        super.onStatusViewErrorClick()
        refreshPage(true)
    }

    private fun loadData() {
        minAny.scorePage("redeem", nextKey)
                .subscribe({
                    hideLoadingView()
                    if (!it.err) {
                        finishRequest()
                        mAdapter.setList(it.res?.list)
                        nextKey = it.res?.nextKey
                    } else {
                        showErrorView(it.msg)
                    }
                }, {
                    showErrorView(it.message)
                })
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRemindRemove(scoreRefreshEvent: ScoreRefreshEvent) {
        nextKey = null
        loadData()
    }

    override fun initListeners() {
        viewBinding.titleBar.setLeftOnClickListener {
            finish()
        }
        mAdapter.loadMoreModule.setOnLoadMoreListener {
            minAny.scorePage("redeem", nextKey)
                    .subscribe({
                        if (!it.err) {
                            mAdapter.addData(it.res?.list ?: arrayListOf())
                            nextKey = it.res?.nextKey
                            if (CollectionUtils.isEmpty(it.res?.list)) {
                                mAdapter.loadMoreModule.isEnableLoadMore = false
                            }
                        } else {
                            mAdapter.loadMoreModule.isEnableLoadMore = false
                        }
                        mAdapter.loadMoreModule.loadMoreComplete()
                    }, {
                        mAdapter.loadMoreModule.loadMoreComplete()
                        mAdapter.loadMoreModule.isEnableLoadMore = false
                    })
        }

        itemBinder.mListener = object : ScoreExchangeRecordItemBinder.ActionListener {
            override fun onCopyClick(entity: ScoreRecordEntity) {
                (entity as? ScoreRecordEntity)?.code?.let {
                    ClipboardHelper.copyMsg(this@ScoreExchangeRecordActivity, it) {
                        ToastUtil.getInstance()._short(this@ScoreExchangeRecordActivity, "复制成功")
                    }
                }
            }

            override fun onInputMessage(id: String, eventId: String) {
                DcRouter(MineConstant.Uri.EXCHANGE_GOOD_DETAIL)
                        .putExtra(MineConstant.ExtraParam.FROM_WHERE, "FROM_EXCHANGE_PAGE")
                        .putExtra("id", id)
                        .putExtra("eventId", eventId)
                        .start()
//                DcRouter(MineConstant.Uri.RECEIVE_INFORMATION)
//                        .putExtra("id", id)
//                        .start()
            }

            override fun onCheckMail(entity: ScoreRecordEntity) {
                val dialog = LogisticsInfoDialog(context)
                dialog.setData(entity).show()
            }

            override fun onClickConfirmAddress(id: String, eventId: String) {
                DcRouter(MineConstant.Uri.EXCHANGE_GOOD_DETAIL)
                        .putExtra(MineConstant.ExtraParam.FROM_WHERE, "FROM_EXCHANGE_PAGE")
                        .putExtra("id", id)
                        .putExtra("eventId", eventId)
                        .start()
            }
        }
    }

    override fun moduleTag(): String {
        return ModuleConstant.MINE_MODULE
    }
}