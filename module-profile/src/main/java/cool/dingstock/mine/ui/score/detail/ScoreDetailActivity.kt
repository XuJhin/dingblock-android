package cool.dingstock.mine.ui.score.detail

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.constant.MineConstant
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.entity.bean.score.ScoreRecordEntity
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.net.api.mine.MineApi
import cool.dingstock.appbase.widget.CommonEmptyView
import cool.dingstock.lib_base.util.CollectionUtils
import cool.dingstock.mine.R
import cool.dingstock.mine.dagger.MineApiHelper
import cool.dingstock.mine.databinding.ActivityScoreDetailBinding
import cool.dingstock.mine.itemView.ScoreDetailsItemBinder
import javax.inject.Inject

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [MineConstant.Path.EXCHANGE_DETAILS]
)
class ScoreDetailActivity : VMBindingActivity<BaseViewModel, ActivityScoreDetailBinding>() {
    @Inject
    lateinit var minAny: MineApi
    private val mAdapter = DcBaseBinderAdapter(arrayListOf())
    var nextKey: Long? = null

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        MineApiHelper.apiMineComponent.inject(this)
        viewBinding.titleBar
        viewBinding.titleBar.title = "积分明细"
        viewBinding.titleBar.setLeftOnClickListener {
            finish()
        }
        mAdapter.addItemBinder(ScoreRecordEntity::class.java, ScoreDetailsItemBinder())
        mAdapter.setEmptyView(CommonEmptyView(this))
        viewBinding.mineDrawerRv.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        val tv = TextView(this)
        tv.apply {
            text = "仅展示最近三个月的数据"
            layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            gravity = Gravity.CENTER
            setTextColor(getCompatColor(R.color.color_text_black5))
            textSize = 13f
            setPadding(0, 15, 0, 30)
        }

        mAdapter.addFooterView(tv)
        loadData()
    }

    override fun onStatusViewErrorClick() {
        super.onStatusViewErrorClick()
        loadData()
    }

    private fun loadData() {
        showLoadingView()
        minAny.scorePage(null, nextKey)
            .subscribe({
                hideLoadingView()
                if (!it.err) {
                    mAdapter.setList(it.res?.list)
                    nextKey = it.res?.nextKey
                    viewBinding.emptyView.hide(!CollectionUtils.isEmpty(it.res?.list))
                } else {
                    showErrorView(it.msg)
                }
            }, {
                showErrorView(it.message)
            })
    }

    override fun initListeners() {
        viewBinding.titleBar.setLeftOnClickListener {
            finish()
        }
        mAdapter.loadMoreModule.setOnLoadMoreListener {
            minAny.scorePage(null, nextKey)
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
    }

    override fun moduleTag(): String {
        return ModuleConstant.MINE_MODULE
    }
}