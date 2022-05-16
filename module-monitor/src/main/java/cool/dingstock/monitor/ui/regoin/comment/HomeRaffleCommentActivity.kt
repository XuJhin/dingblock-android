package cool.dingstock.monitor.ui.regoin.comment

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.constant.CalendarConstant
import cool.dingstock.appbase.constant.HomeConstant
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.entity.bean.home.HomeProductSketch
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.appbase.widget.IconTextView
import cool.dingstock.appbase.widget.stateview.StatusView.Companion.newBuilder
import cool.dingstock.lib_base.fix.AndroidInputFix
import cool.dingstock.lib_base.json.JSONHelper
import cool.dingstock.monitor.R
import cool.dingstock.monitor.databinding.HomeActivityProductCommentBinding
import cool.dingstock.post.adapter.DynamicBinderAdapter
import cool.dingstock.post.comment.CircleCommentBaseActivity
import cool.dingstock.post.item.DynamicCommentItemBinder.Style.Companion.HOME_RAFFLE


/**
 * 类名：HomeRaffleCommentActivity1
 * 包名：cool.dingstock.monitor.ui.regoin.comment
 * 创建时间：2021/12/25 4:56 下午
 * 创建人： WhenYoung
 * 描述：
 **/
@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [HomeConstant.Path.REGION_COMMENT]
)
class HomeRaffleCommentActivity:
    CircleCommentBaseActivity<HomeRaffleCommentVM, HomeActivityProductCommentBinding>() {
    var mProductIv: ImageView? = null
    var mDelIcon: IconTextView? = null
    var mNameTxt: TextView? = null
    var mInfoTxt: TextView? = null
    var mPriceTxt: TextView? = null
    var mRv: RecyclerView? = null
    var mContentRootLayer: FrameLayout? = null

    var adapter: DynamicBinderAdapter? = null

    override fun moduleTag(): String {
        return ModuleConstant.HOME_MODULE
    }

    override fun needLoadMore(): Boolean {
        return false
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

    override fun haveHeadSection(): Boolean {
        return false
    }


    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        dialogFull = false
        mProductIv = viewBinding.productComment.homeItemProductCommentIv
        mDelIcon = viewBinding.homeItemProductCommentDel
        mNameTxt = viewBinding.productComment.homeItemProductCommentNameTxt
        mInfoTxt = viewBinding.productComment.homeItemProductCommentInfoTxt
        mPriceTxt = viewBinding.productComment.homeItemProductCommentPriceTxt
        mRv = viewBinding.circleActivityCommentCommonRv
        mContentRootLayer = viewBinding.contentLayer
        initStatusView()
        super.initViewAndEvent(savedInstanceState)
        AndroidInputFix.assistActivity(findViewById(R.id.content))
        val raffleStr = intent.getStringExtra(CalendarConstant.DataParam.KEY_PRODUCT)
        if (TextUtils.isEmpty(raffleStr)) {
            finish()
            return
        }
        viewModel.productBean = JSONHelper.fromJson(raffleStr!!, HomeProductSketch::class.java)
        viewModel.commentType = uri.getQueryParameter(CalendarConstant.UrlParam.COMMENT_TYPE)?:CalendarConstant.CommentType.CALENDAR
        if (null == viewModel.productBean) {
            finish()
            return
        }
        showLoadingView()
        setProductData()
        initAdapter()
        ansyUI()
        viewModel.productComments()
    }

    private fun ansyUI() {
        viewBinding.apply {
            productComment.saleLayer.hide(viewModel.commentType != CalendarConstant.CommentType.CALENDAR)
        }
        viewModel.apply {

        }
    }

    override fun setSystemStatusBar() {
        super.setSystemStatusBar()
        StatusBarUtil.transparentStatus(this)
        StatusBarUtil.setDarkMode(this)
    }

    private fun initAdapter() {
        adapter = getRv().adapter as DynamicBinderAdapter?
        adapter!!.loadMoreModule.isEnableLoadMoreIfNotFullPage = false
        adapter!!.loadMoreModule.setOnLoadMoreListener { viewModel.loadMoreComments() }
        commentItemBinder.mStyle = HOME_RAFFLE
        commentsHeadBinder.titleTextSize = 14f
    }


    override fun initListeners() {
        super.initListeners()
        mDelIcon!!.setOnClickListener { v: View? -> finish() }
    }

    override fun onStatusViewErrorClick() {
        super.onStatusViewErrorClick()
        showLoadingView()
        viewModel.productComments()
    }

    private fun setProductData() {
        mProductIv!!.load(viewModel.productBean?.imageUrl)
        mNameTxt!!.text = viewModel.productBean?.name
        val price = viewModel.productBean?.price
        if (TextUtils.isEmpty(price) || "null".equals(price, ignoreCase = true)) {
            mPriceTxt!!.text = ""
        } else {
            mPriceTxt!!.text = "发售价：$price"
        }
        mInfoTxt!!.text = viewModel.productBean?.raffleCount?.toString() ?: ""
    }


    override fun initStatusView() {
        mStatusView = newBuilder()
            .with(this)
            .rootView(mContentRootLayer)
            .backgroundColor(getCompatColor(R.color.white))
            .build()
    }

    override fun isBottomPop(): Boolean {
        return true
    }
}