package cool.dingstock.post.activity

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.PostConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.entity.bean.topic.TalkTopicEntity
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.post.BasePostActivity
import cool.dingstock.post.databinding.ActivityMoreVideoBinding
import cool.dingstock.post.item.PostItemShowWhere

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [PostConstant.Path.MORE_VIEW]
)
class MoreVideoActivity : BasePostActivity<MoreVideoViewModel, ActivityMoreVideoBinding>() {
    var topicId: String? = ""
    var talkInfo: TalkTopicEntity? = null

    override fun initListeners() {
    }

    override fun fetchMoreData() {
        viewModel.loadMorePost()
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        super.initViewAndEvent(savedInstanceState)
        initViewAndEvent()
        asyncUI()
        fetchData()
    }

    override fun generateViewModel(): MoreVideoViewModel {
        return ViewModelProvider(this)[MoreVideoViewModel::class.java]
    }

    override fun moduleTag(): String {
        return ModuleConstant.POST
    }

    override fun initBundleData() {
        super.initBundleData()
        topicId = uri.getQueryParameter(PostConstant.UriParams.ID)
        viewModel.updateTopicId(topicId)
    }

    private fun fetchData() {
        showLoadingView()
        viewModel.refresh()
    }

    override fun onStatusViewErrorClick() {
        showLoadingView()
        viewModel.refresh()
    }

    private fun asyncUI() {
        viewModel.apply {
            refreshData.observe(this@MoreVideoActivity, Observer {
                talkInfo = it.talk
                if (it.posts.isNullOrEmpty()) {
                    showEmptyView()
                    return@Observer
                }
                updateDataList(it.posts, false)
            })
            liveDataLoadMore.observe(this@MoreVideoActivity, Observer {
                if (it.isNullOrEmpty()) {
                    finishLoadMore()
                    return@Observer
                }
                updateDataList(it, true)
            })
        }
    }


    private fun initViewAndEvent() {
        viewBinding.ivBack.setOnClickListener { finish() }
        viewBinding.rvPost.apply {
            layoutManager = LinearLayoutManager(this@MoreVideoActivity)
            adapter = pageAdapter
        }
        itembinder.updateShowWhere(PostItemShowWhere.MoreVideo)

    }

    override fun setSystemStatusBar() {
        StatusBarUtil.setLightMode(this)
        StatusBarUtil.transparentStatus(this)
    }

}