package cool.dingstock.mine.ui.follow

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseBinderAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.adapter.itembinder.OnItemClickListener
import cool.dingstock.appbase.constant.*
import cool.dingstock.appbase.entity.bean.RequestState
import cool.dingstock.appbase.entity.bean.account.DcLoginUser
import cool.dingstock.appbase.entity.bean.mine.AccountInfoBriefEntity
import cool.dingstock.appbase.entity.event.account.EventIsAuthorized
import cool.dingstock.appbase.entity.event.relation.EventShieldChange
import cool.dingstock.appbase.list.activity.AbsListActivity
import cool.dingstock.appbase.net.api.account.AccountHelper
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.router.interceptor.DcLoginInterceptor
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.mine.databinding.ActivityFollowBinding
import cool.dingstock.uicommon.mine.item.ClickFollowStateListener
import cool.dingstock.uicommon.mine.item.FollowItemBinder
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 关注
 *
 * @author xujing
 * @since v1.5.3
 */
@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [MineConstant.Path.FOLLOW],
    interceptors = [DcLoginInterceptor::class]
)
class FollowActivity : AbsListActivity<FollowViewModel, ActivityFollowBinding>() {
    companion object {
        //用户关注的列表
        const val TYPE_STAR = "star"

        //关注用户的列表
        const val TYPE_FOLLOWED = "followed"

        //点赞的列表
        const val TYPE_FAVOR = "favor"

        //点赞评论的列表
        const val TYPE_FAVOR_COMMENT = "comment_favor"
    }

    override fun bindItemView() {
        val followItemBinder: FollowItemBinder by lazy { FollowItemBinder() }
        followItemBinder.apply {
            clickFollowStateListener = object : ClickFollowStateListener {
                override fun clickFollow(item: AccountInfoBriefEntity, position: Int) {
                    if (!LoginUtils.isLogin()) {
                        DcRouter(AccountConstant.Uri.INDEX)
                            .start()
                    }
                    viewModel.switchFollowState(item)
                }
            }
            onItemClickListener = object : OnItemClickListener {
                override fun onItemClick(
                    adapter: BaseBinderAdapter,
                    holder: BaseViewHolder,
                    position: Int
                ) {
                    val entity = adapter.getItem(position)
                    if (entity is AccountInfoBriefEntity) {
                        routeToUserIndex(entity)
                    }
                }
            }
        }
        pageAdapter.addItemBinder(AccountInfoBriefEntity::class.java, followItemBinder)
    }

    override fun fetchMoreData() {
        viewModel.loadMoreData()
    }

    override fun generateViewModel(): FollowViewModel {
        return ViewModelProvider(this)[FollowViewModel::class.java]
    }

    /**
     * 用户登录以后同步关注状态
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoginEvent(eventLogin: EventIsAuthorized) {
        if (eventLogin.login && LoginUtils.isLogin()) {
            refresh()
        }
    }

    /**
     * 用户屏蔽以后同步关注
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventRelationFollow(eventFollowChange: EventShieldChange?) {
        refresh()
    }

    /**
     * 当前type 关注列表还是粉丝列表
     */
    private var currentType: String? = TYPE_FOLLOWED

    /**
     * 用户Id
     */
    private var objectId: String? = ""

    /**
     * 获取参数
     */
    override fun initBundleData() {
        val uri = intent.data ?: return
        currentType = uri.getQueryParameter(MineConstant.ExtraParam.FOLLOW_TYPE)
        objectId = uri.getQueryParameter(MineConstant.PARAM_KEY.ID)
        viewModel.initType(currentType)
        viewModel.objectId = objectId
    }


    override fun initListeners() {
    }

    override fun moduleTag(): String {
        return ModuleConstant.MINE_MODULE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        super.initViewAndEvent(savedInstanceState)
        initBundleData()
        asyncUI()
        initView()
        refresh()
    }

    private fun refresh() {
        showLoadingView()
        viewModel.refresh()
    }

    private fun initView() {
        val userEntity: DcLoginUser? = AccountHelper.getInstance().user
        val pageTitle = setupPageTitle(userEntity?.id)
        viewBinding.titleBar.apply {
            title = pageTitle
        }
        viewBinding.mineFollowRv.apply {
            adapter = pageAdapter
            layoutManager = LinearLayoutManager(this@FollowActivity)
        }
        viewBinding.mineFollowRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
        pageAdapter.registerReloadDelegations(
            cool.dingstock.uicommon.helper.FollowUserReload(
                pageAdapter
            )
        )
        pageAdapter.registerReloadLifecycle(lifecycle)
    }

    private fun setupPageTitle(userId: String?): String {
        val isSelf = (userId == objectId)
        return when (currentType) {
            TYPE_FAVOR -> {
                "点赞的人"
            }
            TYPE_FAVOR_COMMENT -> {
                "点赞的人"
            }
            TYPE_FOLLOWED -> {
                if (isSelf) {
                    "粉丝"
                } else {
                    "关注Ta的人"
                }
            }
            TYPE_STAR -> {
                if (isSelf) {
                    "我关注的人"
                } else {
                    "Ta关注的人"
                }
            }
            else -> {
                ""
            }
        }
    }

    private fun routeToUserIndex(entity: AccountInfoBriefEntity) {
        DcUriRequest(this, MineConstant.Uri.DYNAMIC)
            .putUriParameter(MineConstant.PARAM_KEY.ID, entity.id)
            .start()
    }

    private fun asyncUI() {
        viewModel.apply {
            followError.observe(this@FollowActivity, Observer {
                showFailedDialog(it)
            })
            dataList.observe(this@FollowActivity, Observer {
                finishRefresh()
                updateDataList(it, false)
            })
            resultLiveData.observe(this@FollowActivity, Observer {
                hideLoadingView()
                when (it) {
                    is RequestState.Empty -> {
                        if (it.isRefresh) {
                            showRvEmpty()
                            finishRefresh()
                        } else {
                            finishLoadMore()
                        }
                    }
                    is RequestState.Error -> {
                        if (it.isRefresh) {
                            finishRefresh()
                            showErrorView(it.errorMsg ?: "请求错误") { refresh() }
                        } else {
                            return@Observer
                        }
                    }
                }
            })
            loadLiveData.observe(this@FollowActivity, Observer {
                updateDataList(it, true)
            })
            switchFollowResult.observe(this@FollowActivity, Observer { param ->
                val position: Int? =
                    pageAdapter.data.indexOfFirst { it is AccountInfoBriefEntity && it.id == param.id }
                position?.let {
                    pageAdapter.notifyItemChanged(position)
                }
                if (param.followed) {
                    showSuccessDialog("关注成功")
                } else {
                    showSuccessDialog("取关成功")
                }
            })
        }
    }

    private fun finishRefresh() {
        viewBinding.mineFollowRefresh.isRefreshing = false
        hideLoadingView()
    }
}
