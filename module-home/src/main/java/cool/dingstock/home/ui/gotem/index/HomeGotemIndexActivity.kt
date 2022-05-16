package cool.dingstock.home.ui.gotem.index

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.constant.HomeConstant
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.entity.bean.home.HomeGotemBean
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.mvvm.status.PageLoadState
import cool.dingstock.appbase.widget.TitleBar
import cool.dingstock.appbase.widget.recyclerview.adapter.BaseRVAdapter
import cool.dingstock.home.adapter.item.HomeGotemItem
import cool.dingstock.home.databinding.HomeActivityGotemLayoutBinding
import cool.dingstock.lib_base.util.CollectionUtils

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [HomeConstant.Path.GOTEM_INDEX]
)
class HomeGotemIndexActivity : VMBindingActivity<HomeGotemIndexViewModel, HomeActivityGotemLayoutBinding>() {
    private lateinit var titleBar: TitleBar
    private lateinit var rv: RecyclerView
    private lateinit var refreshLayout: SwipeRefreshLayout
    private var rvAdapter: BaseRVAdapter<HomeGotemItem> = BaseRVAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        val vb = HomeActivityGotemLayoutBinding.inflate(layoutInflater, findViewById(android.R.id.content), false)
        titleBar = vb.homeGotemTitleBar
        rv = vb.homeGotemRv
        refreshLayout = vb.homeGotemRefresh
        super.onCreate(savedInstanceState)
        setContentView(vb.root)
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        showLoadingView()
        initRv()
        refreshLayout.isEnabled = false

        with(viewModel) {
            pageLoadState.observe(this@HomeGotemIndexActivity) {
                hideLoadingView()
                when (it) {
                    is PageLoadState.Error -> {
                        if (it.isRefresh) {
                            closePullRefresh()
                            showErrorView(it.msg)
                        } else {
                            hideLoadMore()
                        }
                    }
                    is PageLoadState.Success -> {
                        if (it.isRefresh) {
                            closePullRefresh()
                        } else {
                            hideLoadMore()
                        }
                    }
                    is PageLoadState.Empty -> {
                        if (it.isRefresh) {
                            closePullRefresh()
                            showEmptyView()
                        } else {
                            endLoadMore()
                        }
                    }
                    else -> Unit
                }
            }

            itemList.observe(this@HomeGotemIndexActivity) {
                setItemList(it.first, it.second)
            }
        }
    }

    private fun initRv() {
        rv.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        rv.adapter = rvAdapter
        rvAdapter.openLoadMore()
    }

    override fun initListeners() {
        setOnErrorViewClick {
            showLoadingView()
            viewModel.getGotemData(true)
        }
        refreshLayout.setOnRefreshListener { refresh(true) }
        rvAdapter.setOnLoadMoreListener { refresh(false) }
        rvAdapter.setOnItemViewClickListener { item, _, _ ->
            val map = HashMap<String?, String?>()
            map[HomeConstant.UriParam.KEY_GROUP_NAME] = item.data.title
            map[HomeConstant.UriParam.KEY_SUBTITLE] = item.data.subtitle
            map[HomeConstant.UriParam.KEY_IMAGE_URL] = item.data.imageUrl
            DcRouter(HomeConstant.Uri.GOTEM_CONTENT)
                .appendParams(map)
                .start()
        }
    }

    override fun moduleTag(): String {
        return ModuleConstant.HOME_MODULE
    }

    fun refresh(isRefresh: Boolean) {
        if (isRefresh) openLoadMore()
        viewModel.getGotemData(isRefresh)
    }

    fun closePullRefresh() {
        refreshLayout.isRefreshing = false
    }

    fun openLoadMore() {
        rvAdapter.closeLoadMore()
        rvAdapter.openLoadMore()
    }

    fun hideLoadMore() {
        rvAdapter.hideLoadMoreView()
    }

    fun endLoadMore() {
        rvAdapter.endLoadMore()
    }

    override fun showEmptyView() {
        rvAdapter.showEmptyView()
    }

    fun setItemList(gotemBeanList: List<HomeGotemBean>, isRefresh: Boolean) {
        if (isRefresh) {
            rvAdapter.clearAllSectionViews()
        }
        if (CollectionUtils.isEmpty(gotemBeanList)) {
            return
        }
        refreshLayout.isEnabled = true
        val itemList: MutableList<HomeGotemItem> = ArrayList()
        for (homeGotemBean in gotemBeanList) {
            itemList.add(HomeGotemItem(homeGotemBean))
        }
        rvAdapter.addItemViewList(itemList)
    }
}