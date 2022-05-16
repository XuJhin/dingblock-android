package cool.dingstock.post.ui.post.comment

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.constant.CircleConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicBean
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicDetailCommentsBean
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.lib_base.fix.AndroidInputFix
import cool.dingstock.lib_base.json.JSONHelper
import cool.dingstock.post.R
import cool.dingstock.post.comment.CircleCommentBaseActivity
import cool.dingstock.post.databinding.CircleActivitySecondCommentBinding
import cool.dingstock.post.item.DynamicCommentItemBinder.Style.Companion.DETAIL_SUB


/**
 * 类名：CircleSubCommentDetailActivity1
 * 包名：cool.dingstock.post.ui.post.comment
 * 创建时间：2021/12/25 11:37 上午
 * 创建人： WhenYoung
 * 描述：
 **/
@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [CircleConstant.Path.CIRCLE_SUB_COMMENTS]
)
class CircleSubCommentDetailActivity :
    CircleCommentBaseActivity<CircleSubCommentDetailVM, CircleActivitySecondCommentBinding>() {
    private var mIvClose: View? = null
    private var mTvTitle: TextView? = null
    private var mRv: RecyclerView? = null

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        dialogFull = false
        mIvClose = viewBinding.ivClose
        mRootView = viewBinding.rootView
        mTvTitle = viewBinding.tvTitle
        mRv = viewBinding.circleActivityCommentCommonRv
        mRootView = viewBinding.root
        initStatusView()
        super.initViewAndEvent(savedInstanceState)
        AndroidInputFix.assistActivity(findViewById(R.id.content))
        val mainCommentStr = intent.getStringExtra(CircleConstant.Extra.KEY_COMMENT)
        val mainStr = intent.getStringExtra(CircleConstant.Extra.CIRCLE_DYNAMIC)
        if (mainStr != null) {
            setMainBean(JSONHelper.fromJson(mainStr, CircleDynamicBean::class.java))
        }
        viewModel.model = intent.getStringExtra(CircleConstant.Extra.Model) ?: ""
        if (TextUtils.isEmpty(mainCommentStr)) {
            finish()
            return
        }
        viewModel.mainCommentBean =
            JSONHelper.fromJson(mainCommentStr!!, CircleDynamicDetailCommentsBean::class.java)
        viewModel.targetId = uri.getQueryParameter(CircleConstant.UriParams.ONE_COMMENT_ID) ?: ""
        if (null == viewModel.mainCommentBean) {
            finish()
            return
        }
        mTvTitle!!.text = "评论详情"
        showLoadingView()
        commentItemBinder.mStyle = DETAIL_SUB
        asyncUI()
        viewModel.secondaryComments()
    }

    override fun setSystemStatusBar() {
        super.setSystemStatusBar()
        StatusBarUtil.transparentStatus(this)
        StatusBarUtil.setDarkMode(this)
    }

    private fun asyncUI() {

    }



    override fun initListeners() {
        super.initListeners()
        setOnErrorViewClick {
            showLoadingView()
            viewModel.secondaryComments()
        }
        mIvClose!!.setOnClickListener { finish() }
        rvAdapter.loadMoreModule.isEnableLoadMore = false
        rvAdapter.loadMoreModule.isEnableLoadMoreIfNotFullPage = false
        rvAdapter.loadMoreModule.setOnLoadMoreListener {
            viewModel.loadMoreComments()
        }
    }

    override fun getRv(): RecyclerView {
        return mRv!!
    }

    override fun getEditLayer(): View {
        return viewBinding.commentLayout.editLayer
    }

    override fun getImgSel(): View {
        return viewBinding.commentLayout.circleDynamicDetailCommentImgIv
    }

    override fun getEmojiSel(): View {
        return viewBinding.commentLayout.circleDynamicDetailCommentEmojiIv
    }

    override fun needLoadMore(): Boolean {
        return false
    }

    override fun isBottomPop(): Boolean {
        return true
    }
}