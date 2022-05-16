package cool.dingstock.post.ui.post.detail

import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.sankuai.waimai.router.Router
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.constant.CircleConstant
import cool.dingstock.appbase.constant.HomeConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.circle.*
import cool.dingstock.appbase.entity.event.account.EventIsAuthorized
import cool.dingstock.appbase.entity.event.account.EventUserLoginOut
import cool.dingstock.appbase.entity.event.circle.*
import cool.dingstock.appbase.entity.event.relation.EventFollowFailed
import cool.dingstock.appbase.entity.event.update.EventMedalStateChange
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.helper.IMHelper.routeToConversationActivity
import cool.dingstock.appbase.helper.SettingHelper
import cool.dingstock.appbase.helper.TimeTagHelper
import cool.dingstock.appbase.net.mobile.MobileHelper
import cool.dingstock.appbase.serviceloader.INewSaleItemServer
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.LoginUtils.getCurrentUser
import cool.dingstock.appbase.util.LoginUtils.isLoginAndRequestLogin
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.appbase.util.cellularConnected
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.appbase.util.wifiConnected
import cool.dingstock.appbase.widget.TitleBar
import cool.dingstock.appbase.widget.leonids.LeonidsUtil
import cool.dingstock.appbase.widget.stateview.StatusView.Companion.newBuilder
import cool.dingstock.imagepicker.views.AvatarView
import cool.dingstock.lib_base.fix.AndroidInputFix
import cool.dingstock.lib_base.util.CollectionUtils
import cool.dingstock.lib_base.util.ScreenUtils
import cool.dingstock.lib_base.util.StringUtils
import cool.dingstock.post.R
import cool.dingstock.post.adapter.PostListener
import cool.dingstock.post.comment.CircleCommentBaseActivity
import cool.dingstock.post.databinding.CircleActivityDetailsBinding
import cool.dingstock.post.dialog.OverlayActionDialog
import cool.dingstock.post.dialog.OverlayActionDialog.Companion.instance
import cool.dingstock.post.dialog.PostActionListener
import cool.dingstock.post.item.DynamicItemBinder
import cool.dingstock.post.item.LINK_TYPE_VIDEO
import cool.dingstock.post.item.PostItemShowWhere
import cool.dingstock.post.view.DcVideoPlayer
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


/**
 * 类名：CircleDynamicDetailActivit1
 * 包名：cool.dingstock.post.ui.post.detail
 * 创建时间：2021/12/24 5:16 下午
 * 创建人： WhenYoung
 * 描述：
 **/
@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [CircleConstant.Path.CIRCLE_DETAIL]
)
class CircleDynamicDetailActivity :
    CircleCommentBaseActivity<CircleDynamicDetailViewModel, CircleActivityDetailsBinding>() {

    var mTitleBar: TitleBar? = null
    var mRv: RecyclerView? = null
    var mHeadIv: AvatarView? = null
    var mHeadFra: View? = null
    var mHeadVerified: View? = null
    var antiFraudTv: View? = null

    val dynamicItemBinder = DynamicItemBinder(this)
    private var overlayActionDialog: OverlayActionDialog? = null

    var timer: Timer? = null
    var timerTask: TimerTask? = null
    val handler by lazy {
        object : Handler(mainLooper) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                if (msg.what == 0) {
                    dynamicItemBinder.onTimerUpdate {
                        timer?.cancel()
                        timerTask?.cancel()
                        timer = null
                        timerTask = null
                    }
                }
            }
        }
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        mTitleBar = viewBinding.circleActivityCommentCommonTitleBar
        mRv = viewBinding.circleActivityCommentCommonRv
        mRootView = viewBinding.rootView
        mHeadIv = viewBinding.headIv
        mHeadFra = viewBinding.headFra
        mHeadVerified = viewBinding.groupItemDynamicUserVerified
        antiFraudTv = viewBinding.antiFraudTv
        mStatusView = newBuilder()
            .with(this)
            .rootView(viewBinding.screenContent)
            .build()
        super.initViewAndEvent(savedInstanceState)
        AndroidInputFix.assistActivity(viewBinding.rootView)
        viewModel.isAutoComment =
            uri.getQueryParameter(CircleConstant.UriParams.IS_AUTO_COMMENT)?.toBooleanStrictOrNull()
                ?: false
        viewModel.isFromShoes =
            uri.getBooleanQueryParameter(CircleConstant.UriParams.DETAIL_FROM_SHOES, false)
        viewModel.mainId = uri.getQueryParameter(CircleConstant.UriParams.ID) ?: ""
        viewModel.targetId = viewModel.mainId //动态详情里面 的就是targetId 就是postId
        mHeadIv!!.visibility = View.VISIBLE
        showLoadingView()
        initTitleBar()
        asyncUI()
        viewModel.communityPostDetail()
    }

    private fun asyncUI() {

        viewModel.followFailed.observe(this) { s: String? ->
            hideLoadingDialog()
            showToastShort(s)
        }
        viewModel.apply {
            showAntiFraudLiveData.observe(this@CircleDynamicDetailActivity) {
                antiFraudTv?.hide(!it)
            }
            postDataLiveData.observe(this@CircleDynamicDetailActivity) {
                setPostData(it)
            }
            headIvLiveData.observe(this@CircleDynamicDetailActivity) {
                setHeadIv(it)
            }
            headPendant.observe(this@CircleDynamicDetailActivity) {
                setHeadIvPendant(it)
            }
            headVerifiedLiveData.observe(this@CircleDynamicDetailActivity) {
                setHeadVerified(it)
            }
            showCommentEditLiveData.observe(this@CircleDynamicDetailActivity) {
                showCommentEdit()
            }
            scroll2CommentLiveData.observe(this@CircleDynamicDetailActivity) {
                scroll2Comment()
            }
        }
    }

    private fun initTitleBar() {
        mTitleBar?.apply {
            title = "动态详情"
            val isFashion =
                intent.getBooleanExtra(CircleConstant.UriParams.DETAIL_FROM_FASHION, false)
            isRightEnable = !isFashion
            setRightVisibility(!isFashion)
            val rightView: AppCompatImageView = LayoutInflater.from(context)
                .inflate(R.layout.title_bar_right_overlay, mTitleBar, false) as AppCompatImageView
            setRightView(rightView)
            rightView.apply {
                imageTintList = getColorStateList(R.color.color_text_black4)
                setOnClickListener {
                    if (overlayActionDialog != null) {
                        UTHelper.commonEvent(
                            UTConstant.Circle.Dynamic_click_TradingDynamicP,
                            "ActionName",
                            "更多"
                        )
                        overlayActionDialog?.showDialog(supportFragmentManager, "overlay")
                    }
                }
            }
        }

        antiFraudTv?.setOnClickListener {
            viewModel.mainBean?.user?.id?.let {
                if (viewModel.mainBean?.isDeal == true) {
                    UTHelper.commonEvent(
                        UTConstant.Circle.Dynamic_click_TradingDynamicP,
                        "ActionName",
                        "防骗指南"
                    )
                }
            }
            MobileHelper.getInstance().getCloudUrlAndRouter(this, "TradeTip")
        }
    }

    override fun onStatusViewErrorClick() {
        super.onStatusViewErrorClick()
        showLoadingView()
        viewModel.communityPostDetail()
    }

    override fun initListeners() {
        super.initListeners()
        //先关闭loadMore，在数据请求成功
        // 的时候打开 LoadMore
        rvAdapter.loadMoreModule.isEnableLoadMore = false
        rvAdapter.loadMoreModule.isEnableLoadMoreIfNotFullPage = false
        rvAdapter.loadMoreModule.setOnLoadMoreListener {
            viewModel.loadMoreComments()
        }
        viewBinding.commentLayout.contactBtn.setOnClickListener {
            if (!isLoginAndRequestLogin(this)) {
                return@setOnClickListener
            }
            viewModel.mainBean?.user?.id?.let {
                if (viewModel.mainBean?.isDeal == true) {
                    UTHelper.commonEvent(
                        UTConstant.Circle.Dynamic_click_TradingDynamicP,
                        "ActionName",
                        "联系ta"
                    )
                }
                viewModel.wantTrading(viewModel.mainBean?.id)
                viewModel.mainBean?.user?.id?.let {
                    routeToConversationActivity(this, 1, it)
                }
            }
        }
        viewBinding.commentLayout.courierBtn.setOnShakeClickListener {
            if (isLoginAndRequestLogin(this)) {
                UTHelper.commonEvent(UTConstant.Common.ExpressP_click_Ent, "source", "动态详情页")
                MobileHelper.getInstance().getCloudUrlAndRouter(this, "dcExpress")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        timer = null
        timerTask?.cancel()
        timerTask = null
    }

    override fun getHeadView(): AvatarView? {
        return mHeadIv
    }

    override fun getHeadGroup(): View? {
        return mHeadFra
    }

    override fun needLoadMore(): Boolean {
        return true
    }

    override fun getRv(): RecyclerView {
        return mRv!!
    }

    override fun getTitleBar(): TitleBar? {
        return mTitleBar
    }

    override fun getHeadVerified(): View? {
        return mHeadVerified
    }

    override fun getRootView(): View {
        return mRootView
    }

    override fun getImgSel(): View {
        return viewBinding.commentLayout.circleDynamicDetailCommentImgIv
    }

    override fun getEmojiSel(): View {
        return viewBinding.commentLayout.circleDynamicDetailCommentEmojiIv
    }

    override fun getEditLayer(): View {
        return viewBinding.commentLayout.editLayer
    }

    override fun setSystemStatusBar() {
        setSystemStatusBarMode()
        StatusBarUtil.transparentStatus(this)
    }

    override fun fakeStatusView(): View {
        return viewBinding.fakeStatusBar.root
    }

    /**
     * 动态内容
     *
     * @param circleDynamicBean 动态实体
     */
    private fun setPostData(circleDynamicBean: CircleDynamicBean?) {
        if (null == circleDynamicBean) {
            return
        }
        if (circleDynamicBean.isDeal) {
            if (viewModel.mainBean?.user?.id?.isNotEmpty() == true) {
                if (getCurrentUser() != null && viewModel.mainBean?.user?.id == getCurrentUser()!!.id) {
                    viewBinding.commentLayout.contactBtn.visibility = View.GONE
                } else {
                    viewBinding.commentLayout.contactBtn.visibility = View.VISIBLE
                }
            } else {
                viewBinding.commentLayout.contactBtn.visibility = View.VISIBLE
            }

            var enableShowTips = false
            if (MobileHelper.getInstance().configData.expressEntryList?.dealDetailEntry?.enabled == true && circleDynamicBean.isDeal) {
                val timeShow = TimeTagHelper.checkTimeTag(
                    TimeTagHelper.DynamicDetailsExpressTips,
                    TimeTagHelper.TimeTag.ONCE_DAY
                )
                if (timeShow) {
                    enableShowTips = true
                    TimeTagHelper.updateTimeTag(
                        TimeTagHelper.DynamicDetailsExpressTips,
                        System.currentTimeMillis()
                    )
                }
            }
            viewBinding.commentLayout.courierPreferentialLayer.hide(!enableShowTips)
            viewBinding.commentLayout.courierPreferentialTv.text =
                if (getCurrentUser()?.isVip() == false) MobileHelper.getInstance().configData.expressEntryList?.dealDetailEntry?.tip
                else MobileHelper.getInstance().configData.expressEntryList?.dealDetailEntry?.vipTip

            viewBinding.commentLayout.courierBtn.hide(MobileHelper.getInstance().configData.expressEntryList?.dealDetailEntry?.enabled != true)

            if (viewBinding.commentLayout.courierBtn.isVisible && viewBinding.commentLayout.contactBtn.isVisible) {
                viewBinding.commentLayout.editHintTv.visibility = View.INVISIBLE
            }
        }
        val webpageLink = circleDynamicBean.webpageLink
        val videoBean: CircleVideoBean? = circleDynamicBean.video
        if (webpageLink != null) {
            if (webpageLink.type == LINK_TYPE_VIDEO) {
                if (!webpageLink.imageUrl.isNullOrEmpty()) {
                    viewBinding.videoPlayer.isVisible = true
                    viewBinding.titleRl.isVisible = false
                    setVideoPlayer(
                        null,
                        webpageLink.imageUrl,
                        webpageLink.title,
                        webpageLink.duration,
                        circleDynamicBean
                    )
                    viewBinding.videoPlayer.dynamicId = circleDynamicBean.id
                }
            }
        } else if (videoBean != null) {
            if (!videoBean.url.isNullOrEmpty()) {
                viewBinding.videoPlayer.isVisible = true
                viewBinding.titleRl.isVisible = false
                setVideoPlayer(
                    videoBean.url!!,
                    videoBean.imageUrl,
                    videoBean.title,
                    videoBean.duration,
                    circleDynamicBean
                )
                viewBinding.videoPlayer.dynamicId =
                    "${circleDynamicBean.id}${CircleConstant.Extra.AUTO_PLAY}"
            }
        } else {
            viewBinding.videoPlayer.isVisible = false
            viewBinding.titleRl.isVisible = true
        }

        val isFashionLost =
            intent.getBooleanExtra(CircleConstant.UriParams.DETAIL_FROM_FASHION_LIST, false)
        if (isFashionLost) {
            if (circleDynamicBean.id != null) {
                EventBus.getDefault()
                    .post(EventViewCount(circleDynamicBean.id!!, circleDynamicBean))
            }
            val server = Router.getService(
                INewSaleItemServer::class.java,
                HomeConstant.ServerLoader.NEW_SALE_ITEM
            )
            rvAdapter.registerReloadDelegations(server.getReload(rvAdapter))
            rvAdapter.registerReloadLifecycle(lifecycle)
            rvAdapter.addItemBinder(
                CircleDynamicBean::class.java,
                server.getNewSale<CircleDynamicBean, BaseViewHolder>(false) {
                    showCommentEdit()
                })
        } else {
            dynamicItemBinder.updateShowWhere(PostItemShowWhere.Detail)
            dynamicItemBinder.maxLineLimit = false
            dynamicItemBinder.postListener = object : PostListener {
                override fun collectPost(entity: CircleDynamicBean, position: Int) {
                    dynamicItemBinder.collectPost(entity, position)
                }

                override fun raisePost(entity: CircleDynamicBean, position: Int) {
                    dynamicItemBinder.raisePost(entity, position)
                }

                override fun commentPost(entity: CircleDynamicBean, position: Int) {
                    showCommentEdit()
                }

                override fun sharePost(entity: CircleDynamicBean, position: Int) {
                    dynamicItemBinder.sharePost(entity, position)
                }
            }
            dynamicItemBinder.topicClickAction = {
                UTHelper.commonEvent(
                    UTConstant.Circle.EnterSource_TopicDetailsP,
                    "position",
                    "动态详情页"
                )
            }
            rvAdapter.addItemBinder(CircleDynamicBean::class.java, dynamicItemBinder)

            if (circleDynamicBean.isSeries) {
                val certificationFail = circleDynamicBean.blockedReason?.isNotEmpty() == true
                viewBinding.commentLayout.root.hide(certificationFail)
            }

            setLotteryTimer(circleDynamicBean.lotteryMap?.getStatus() == LotteryEntity.Status.drawing)

        }
        rvAdapter.getDataList().add(0, circleDynamicBean)
        initOverlayDialog(circleDynamicBean)
    }

    private fun setVideoPlayer(
        url: String?,
        imageUrl: String?,
        title: String?,
        duration: Int?,
        item: CircleDynamicBean
    ) {
        fakeStatusView().setBackgroundColor(Color.BLACK)
        StatusBarUtil.setDarkMode(this)
        val thumb = ImageView(context).apply {
            setImageResource(R.drawable.empty_find)
            Glide.with(context).load(imageUrl).into(this)
        }
        viewBinding.videoPlayer.apply {
            thumbImageViewLayout.isVisible = true
            thumbImageView = thumb
            isCollected = item.isCollect ?: false
            isFavored = item.favored
            favorCount = item.favorCount
            commentCount = item.commentCount
            setUpLazy(url, true, null, null, title)
            setThumbPlay(false)
            shrinkImageRes = R.drawable.ic_icon_shrink
            enlargeImageRes = R.drawable.ic_icon_enlarge
            titleTextView.isVisible = true
            titleTextView.text = title
            backButton.isVisible = true
            backButton.setImageResource(R.drawable.icon_back_white)
            backButton.setOnClickListener {
                finish()
            }
            fullscreenButton.setOnClickListener {
                UTHelper.commonEvent(UTConstant.Circle.Dynamic_click_FullScreen)
                startWindowFullscreen(context, actionBar = false, statusBar = true)
            }
            isAutoFullWithSize = false
            setIsTouchWiget(false)
            dismissControlTime = 5000
            videoDuration = duration ?: 0
            setActionListener(object : DcVideoPlayer.ActionListener {
                override fun clickShare(view: View) {
                    dynamicItemBinder.sharePost(item, 0)
                }

                override fun clickCollect(view: View) {
                    dynamicItemBinder.collectPost(item, 0)
                }

                override fun clickComment(view: View) {
                    showCommentEdit()
                }

                override fun clickRaise(view: View) {
                    dynamicItemBinder.raisePost(item, 0)
                }

                override fun longClickRaise(view: View?, favored: Boolean) {
                    view?.let { raise ->
                        raise.setOnLongClickListener {
                            if (!isLoginAndRequestLogin(it.context)) {
                                dynamicItemBinder.raisePost(item, 0)
                                return@setOnLongClickListener true
                            }
                            var runnable: Runnable? = null
                            runnable = Runnable {
                                if (it.isPressed) {
                                    LeonidsUtil.animAndVibrator(it)
                                    it.postDelayed(runnable, 100)
                                } else {
                                    if (!item.favored) {
                                        dynamicItemBinder.raisePost(item, 0)
                                    }
                                }
                            }
                            it.postDelayed(runnable, 100)
                            true
                        }
                    }
                }

            })
        }
        lifecycle.addObserver(viewBinding.videoPlayer)
        if (viewBinding.videoPlayer.dynamicId?.endsWith(CircleConstant.Extra.AUTO_PLAY) == true) {
            if (this.wifiConnected()) {
                if (SettingHelper.getInstance().isWifiAutoPlay) {
                    viewBinding.videoPlayer.startButton?.performClick()
                }
            } else if (this.cellularConnected()) {
                if (SettingHelper.getInstance().isCellularAutoPlay) {
                    viewBinding.videoPlayer.startButton?.performClick()
                }
            }
        }
    }

    private fun initOverlayDialog(circleDynamicBean: CircleDynamicBean) {
        overlayActionDialog = instance(circleDynamicBean, 1)
        overlayActionDialog!!.updateShowWhere(PostItemShowWhere.Detail)
        overlayActionDialog!!.setActionListener(object : PostActionListener {
            override fun postReportResult(isSuccess: Boolean, msg: String?, index: Int?) {
                if (isSuccess) {
                    showSuccessDialog(msg)
                    overlayActionDialog!!.dismissAllowingStateLoss()
                } else {
                    showFailedDialog(msg)
                }
            }

            override fun userBlockResult(isSuccess: Boolean, msg: String?, index: Int?) {
                if (isSuccess) {
                    showSuccessDialog(msg)
                    overlayActionDialog!!.dismissAllowingStateLoss()
                    if (msg != "已取消屏蔽") {
                        finishActivity()
                    }
                } else {
                    showFailedDialog(msg)
                }
            }

            override fun postBlockResult(isSuccess: Boolean, msg: String?, index: Int?) {
                if (isSuccess) {
                    showSuccessDialog(msg)
                    finishActivity()
                    overlayActionDialog!!.dismissAllowingStateLoss()
                } else {
                    showFailedDialog(msg)
                }
            }

            override fun deletePostResult(isSuccess: Boolean, msg: String?, index: Int?) {
                if (isSuccess) {
                    showSuccessDialog("删除成功")
                    finishActivity()
                    overlayActionDialog!!.dismissAllowingStateLoss()
                } else {
                    showFailedDialog(msg)
                }
            }
        })
    }

    private fun finishActivity() {
        val countDownTimer: CountDownTimer = object : CountDownTimer(1500, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                finish()
            }
        }
        countDownTimer.start()
    }

    fun showCommentEdit() {
        commentDialog.show()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refresh(eventShieldChange: EventRemoveDynamicOfAccount?) {
        viewModel.communityPostDetail()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refresh(event: EventMedalStateChange?) {
        viewModel.communityPostDetail()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLogin(event: EventIsAuthorized?) {
        if (!StringUtils.isEmpty(viewModel.mainBean?.user?.id)) {
            if (getCurrentUser() != null && viewModel.mainBean?.user?.id == getCurrentUser()!!.id) {
                viewBinding.commentLayout.contactBtn.visibility = View.GONE
            } else {
                viewBinding.commentLayout.contactBtn.visibility = View.VISIBLE
            }
        } else {
            viewBinding.commentLayout.contactBtn.visibility = View.VISIBLE
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoginOut(event: EventUserLoginOut?) {
        if (!StringUtils.isEmpty(viewModel.mainBean?.user?.id)) {
            if (getCurrentUser() != null && viewModel.mainBean?.user?.id == getCurrentUser()!!.id) {
                viewBinding.commentLayout.contactBtn.visibility = View.GONE
            } else {
                viewBinding.commentLayout.contactBtn.visibility = View.VISIBLE
            }
        } else {
            viewBinding.commentLayout.contactBtn.visibility = View.VISIBLE
        }
    }


    /**
     * 加载更多 添加列表
     *
     * @param comments 评论列表
     */
    fun addComments(comments: ArrayList<CircleDynamicDetailCommentsBean>) {
        if (CollectionUtils.isEmpty(comments)) {
            return
        }
        if (!lastSectionIsAdd) {
            lastSectionIsAdd = true
            rvAdapter.getDataList().add(DynamicCommentHeaderBean(lastHeadStr!!, lastSectionKey))
        }
        rvAdapter.getDataList().addAll(comments)
    }

    override fun showEmptyViewWithComment(): Boolean {
        return true
    }

    private fun scroll2Comment() {
        val view = View(this)
        view.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            ScreenUtils.getScreenHeight(this)
        )
        rvAdapter.addFooterView(view)
        val layoutManager = mRv!!.layoutManager
        if (layoutManager is LinearLayoutManager) {
            layoutManager.scrollToPositionWithOffset(1, 0)
        }
        //等rv第一屏刷新完成之后 判断Footer是否已经显示（当评论Item无法显示完一屏的时候就需要显示） ，如果已经显示 那就显示。如果没有显示。再也不用显示了
        mRv!!.post {

            //判断View是否已经显示在屏幕上
            val rect = Rect()
            view.getLocalVisibleRect(rect)
            if (rect.bottom - rect.top > 0) {
                view.visibility = View.VISIBLE
                val layoutParams = view.layoutParams
                val visibleHeight = rect.bottom - rect.top
                layoutParams.height = visibleHeight
                view.layoutParams = layoutParams
                rvAdapter.loadMoreModule.isEnableLoadMore = false
            } else {
                view.visibility = View.GONE
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun followFailed(eventFollowFailed: EventFollowFailed) {
        showFailedDialog(eventFollowFailed.msg)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFavoredReceived(eventFavored: EventFavored) {
        eventFavored.dynamicBean?.let {
            viewBinding.videoPlayer.apply {
                isFavored = it.favored
                favorCount = it.favorCount
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCollectReceived(eventFollowerChange: EventCollectChange) {
        eventFollowerChange.entity?.let {
            viewBinding.videoPlayer.isCollected = it.isCollect ?: false
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCommentCountChange(eventCommentCount: EventCommentCount) {
        eventCommentCount.entity?.let {
            viewBinding.videoPlayer.commentCount = it.commentCount
        }
    }

    override fun showEmptyView() {}

    override fun showErrorView(text: String?) {
        super.showErrorView(text)
        mTitleBar!!.removeRightView()
    }

    private fun setLotteryTimer(needTimer: Boolean) {
        if (needTimer) {
            timer = Timer()
            timerTask = object : TimerTask() {
                override fun run() {
                    handler.sendEmptyMessage(0)
                }
            }
            timer?.schedule(timerTask, 0, 1000)
        } else {
            timer?.cancel()
            timerTask?.cancel()
            timer = null
            timerTask = null
        }
    }

}