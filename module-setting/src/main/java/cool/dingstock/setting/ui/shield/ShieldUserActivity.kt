package cool.dingstock.setting.ui.shield

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.constant.MineConstant
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.entity.bean.ApiResult
import cool.dingstock.appbase.entity.bean.mine.AccountInfoBriefEntity
import cool.dingstock.appbase.list.activity.AbsListActivity
import cool.dingstock.setting.ui.shield.item.ShieldItemBinder
import cool.dingstock.setting.ui.shield.item.ShieldListener
import cool.dingstock.uicommon.setting.databinding.ActivityShieldUserBinding

/**
 * 屏蔽用户
 */
@RouterUri(scheme = RouterConstant.SCHEME, host = RouterConstant.HOST, path = [MineConstant.Path.Shield])
class ShieldUserActivity : AbsListActivity<ShieldViewModel,ActivityShieldUserBinding>() {

    var handlePosition = -1
    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        super.initViewAndEvent(savedInstanceState)
        viewBinding.titleBarShieldAccount.apply {
            setLineVisibility(false)
        }
        viewBinding.rvSettingShieldAccount.apply {
            layoutManager = LinearLayoutManager(this@ShieldUserActivity)
            adapter = pageAdapter
        }
        viewBinding.refreshLayoutShieldAccount.apply {
            setOnRefreshListener {
                viewModel.refresh()
            }
        }
        refresh()
        asyncUI()
    }

    /**
     * 处理请求结果
     */
    private fun asyncUI() {
        viewModel.refreshResult.observe(this, Observer {
            viewBinding.refreshLayoutShieldAccount.finishRefresh()
            when (it) {
                is ApiResult.Success<*> -> {
                    hideLoadingView()
                    val list = it.data as MutableList<AccountInfoBriefEntity>
                    if (list.isNullOrEmpty()) {
                        showRvEmpty()
                        return@Observer
                    }
                    updateDataList(list, false)
                }
                is ApiResult.Error -> {
                    showErrorView(it.throwable?.message)
                }
            }
        })
        viewModel.loadMoreResult.observe(this, Observer {
            when (it) {
                is ApiResult.Success<*> -> {
                    hideLoadingView()
                    val list = it.data as MutableList<AccountInfoBriefEntity>
                    if (list.isNullOrEmpty()) {
                        finishLoadMore()
                        return@Observer
                    }
                    updateDataList(list, true)
                }
                is ApiResult.Error -> {
                    loadMoreError()
                }
            }
        })
        viewModel.cancelResult.observe(this, Observer {
            hideLoadingDialog()
            when (it) {
                is ApiResult.Success<*> -> {
                    if (handlePosition == -1) {
                        return@Observer
                    }
                    showToastShort("已取消屏蔽")
                    pageAdapter.removeAt(handlePosition)
                    if (pageAdapter.data.isNullOrEmpty()) {
                        showRvEmpty()
                    }
                }
                is ApiResult.Error -> {
                    showToastShort("取消屏蔽失败${it.throwable?.message}")
                }
            }
        })
    }

    private fun refresh() {
        showLoadingView()
        viewModel.refresh()
    }

    override fun bindItemView() {
        val shieldItemBinder = ShieldItemBinder()
                .apply {
                    listener = object : ShieldListener {
                        override fun onShieldListener(data: AccountInfoBriefEntity, position: Int) {
                            handlePosition = position
                            showLoadingDialog()
                            viewModel.cancelShieldAccount(data)
                        }
                    }
                }
        pageAdapter.addItemBinder(AccountInfoBriefEntity::class.java, shieldItemBinder)
    }

    override fun fetchMoreData() {
        viewModel.fetchMoreData()
    }

    override fun generateViewModel(): ShieldViewModel {
        return ViewModelProvider(this)[ShieldViewModel::class.java]
    }

    override fun initListeners() {
    }

    override fun moduleTag(): String {
        return ModuleConstant.SETTING
    }
}