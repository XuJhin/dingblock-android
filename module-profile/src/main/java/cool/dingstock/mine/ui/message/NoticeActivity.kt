package cool.dingstock.mine.ui.message

import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseBinderAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.adapter.itembinder.NoticeReplyItem
import cool.dingstock.appbase.adapter.itembinder.OnItemClickListener
import cool.dingstock.appbase.adapter.itembinder.OnNoticeClickListener
import cool.dingstock.appbase.constant.*
import cool.dingstock.appbase.entity.bean.home.NoticeItemEntity
import cool.dingstock.appbase.helper.PushCheckHelper
import cool.dingstock.appbase.list.activity.AbsListActivity
import cool.dingstock.lib_base.util.SizeUtils
import cool.dingstock.mine.R
import cool.dingstock.mine.databinding.ActivityNoticeBinding
import io.cabriole.decorator.LinearDividerDecoration

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [HomeConstant.Path.HOME_MESSAGE]
)
class NoticeActivity : AbsListActivity<NoticeViewModel, ActivityNoticeBinding>() {
    private var refreshLayout: SwipeRefreshLayout? = null

    override fun moduleTag(): String {
        return ModuleConstant.HOME_MODULE
    }

    private fun asyncUI() {
        viewModel.apply {
            listLiveData.observe(this@NoticeActivity,  {
                updateDataList(it, false)
                finishRefresh()
            })
            loadLiveData.observe(this@NoticeActivity,  {
                updateDataList(it, true)
            })
        }
        viewModel.followError.observe(this,  {
            hideLoadingDialog()
            showToastShort(it)
        })
        viewModel.switchFollowResult.observe(this,  { adapterPosition ->
            hideLoadingDialog()
            val noticeItem: NoticeItemEntity =
                pageAdapter.getDataList()[adapterPosition] as NoticeItemEntity
            noticeItem.user.followed = !noticeItem.user.followed!!
            (viewBinding.rvNotice.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
            pageAdapter.notifyItemChanged(adapterPosition)
        })
    }

    override fun initListeners() {
    }

    private fun firstLoadData() {
        viewModel.refresh(true)
    }

    private fun navigationToList(objectId: String?, type: String) {
        val pageType =
            if (type == "comment_favor") {
                "comment_favor"
            } else {
                "favor"
            }
        DcRouter(MineConstant.Uri.FOLLOW)
            .appendParams(
                hashMapOf(
                    MineConstant.ExtraParam.FOLLOW_TYPE to pageType,
                    MineConstant.PARAM_KEY.ID to objectId!!
                )
            )
            .start()
    }

    private fun navigationToCircleDetail(circleId: String) {
        DcRouter(CircleConstant.Uri.CIRCLE_DETAIL)
            .appendParams(hashMapOf(CircleConstant.UriParams.ID to circleId))
            .start()
    }

    private fun navigationToProduct(productId: String) {
        DcRouter(HomeConstant.Uri.DETAIL)
            .putUriParameter(HomeConstant.UriParam.KEY_PRODUCT_ID, productId)
            .start()
    }


    private fun switchFollow(followState: Boolean, userId: String, index: Int) {
        showLoadingDialog()
        viewModel.switchFollowState(followState, userId, index)
    }

    private fun finishRefresh() {
        refreshLayout?.isRefreshing = false
        hideLoadingView()
    }

    private fun navigationToUserDetail(userId: String) {
        DcRouter(MineConstant.Uri.DYNAMIC)
            .appendParams(hashMapOf(MineConstant.PARAM_KEY.ID to userId))
            .start()
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        super.initViewAndEvent(savedInstanceState)
        refreshLayout = findViewById(R.id.swipe_refresh_layout)
        viewBinding.titleBar.apply {
            setTitle(R.string.title_mine_notice)
            this.setLeftOnClickListener {
                finishPage()
            }
        }
        viewBinding.rvNotice.apply {
            layoutManager = LinearLayoutManager(this@NoticeActivity)
            adapter = pageAdapter
            addItemDecoration(
                LinearDividerDecoration.create(
                    color = ContextCompat.getColor(context, R.color.colorDivider),
                    size = SizeUtils.dp2px(0.5f),
                    leftMargin = SizeUtils.dp2px(20f),
                    rightMargin = SizeUtils.dp2px(20f),
                    orientation = RecyclerView.VERTICAL
                )
            )
        }
        refreshLayout?.apply {
            setOnRefreshListener {
                viewModel.refresh()
            }
        }
        asyncUI()
        firstLoadData()
    }

    override fun bindItemView() {
        val noticeReplyItem = NoticeReplyItem()
        noticeReplyItem.onNoticeClickListener = object : OnNoticeClickListener {
            override fun onClickAvatar(userId: String) {
                navigationToUserDetail(userId)
            }

            override fun onClickFavorList(objectId: String?, type: String) {
                navigationToList(objectId, type)
            }

            override fun onClickFollow(followState: Boolean, userId: String, index: Int) {
                switchFollow(followState, userId, index)
            }
        }
        noticeReplyItem.onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(
                adapter: BaseBinderAdapter,
                holder: BaseViewHolder,
                position: Int
            ) {
                val entity = adapter.data[position]
                if (entity is NoticeItemEntity) {
                    when (entity.type) {
                        "report" -> {
                            if (!TextUtils.isEmpty(entity.postId)) {
                                navigationToCircleDetail(entity.postId ?: "")
                            } else if (!TextUtils.isEmpty(entity.productId)) {
                                navigationToProduct(entity.productId ?: "")
                            }
                        }
                        else -> {
                            entity.original?.objectId?.let { navigationToCircleDetail(it) }
                        }
                    }
                }
            }
        }
        pageAdapter.addItemBinder(NoticeItemEntity::class.java, noticeReplyItem)
    }

    override fun fetchMoreData() {
        viewModel.loadMore()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishPage()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * 结束页面
     */
    private fun finishPage() {
        val pushCheckHelper = PushCheckHelper()
        pushCheckHelper.with(this)
            .start()
    }
}