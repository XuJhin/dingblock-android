package cool.dingstock.mine.ui.index

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.simple.SimpleMultiListener
import cool.dingstock.appbase.constant.AccountConstant
import cool.dingstock.appbase.constant.MineConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.account.DcLoginUser
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicDetailUserBean
import cool.dingstock.appbase.entity.bean.circle.UserDetailBean
import cool.dingstock.appbase.entity.bean.score.MineScoreInfoEntity
import cool.dingstock.appbase.entity.event.account.EventActivated
import cool.dingstock.appbase.entity.event.account.EventIntegralChange
import cool.dingstock.appbase.entity.event.circle.EventCircleChange
import cool.dingstock.appbase.entity.event.circle.EventFollowerChange
import cool.dingstock.appbase.entity.event.circle.EventRemoveDynamicOfAccount
import cool.dingstock.appbase.entity.event.relation.EventShieldChange
import cool.dingstock.appbase.entity.event.update.*
import cool.dingstock.appbase.helper.IMHelper.refreshUserInfoCache
import cool.dingstock.appbase.helper.IMHelper.routeToConversationActivity
import cool.dingstock.appbase.lazy.FragmentLazyStatePageAdapter
import cool.dingstock.appbase.mvp.lazy.VmBindingLazyFragment
import cool.dingstock.appbase.net.api.account.AccountHelper
import cool.dingstock.appbase.net.mobile.MobileHelper
import cool.dingstock.appbase.refresh.DcWhiteRefreshHeader
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.LoginUtils.getCurrentUser
import cool.dingstock.appbase.util.LoginUtils.isLogin
import cool.dingstock.appbase.util.LoginUtils.isLoginAndRequestLogin
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.appbase.util.isWhiteMode
import cool.dingstock.appbase.viewmodel.HomeIndexViewModel
import cool.dingstock.appbase.widget.DynamicEmptyView
import cool.dingstock.appbase.widget.commontitledialog.CommonTitleDialog
import cool.dingstock.appbase.widget.dialog.common.DcTitleDialog
import cool.dingstock.lib_base.util.CollectionUtils
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.lib_base.util.SizeUtils
import cool.dingstock.lib_base.util.StringUtils
import cool.dingstock.mine.R
import cool.dingstock.mine.dagger.MineApiHelper.apiMineComponent
import cool.dingstock.mine.databinding.MineFragmentLayoutBinding
import cool.dingstock.mine.dialog.ShieldListener
import cool.dingstock.mine.dialog.ShieldUserDialog
import cool.dingstock.mine.enums.MineTabType
import cool.dingstock.mine.itemView.MineHeaderClickListener
import cool.dingstock.mine.itemView.MinePostHeaderView
import cool.dingstock.mine.ui.follow.FollowActivity
import cool.dingstock.post.helper.onScrollReleaseAllVideos
import cool.dingstock.uicommon.product.dialog.ClickUpDialog
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.abs

class MineFragment : VmBindingLazyFragment<MineFragmentViewModel, MineFragmentLayoutBinding>() {
    private var emptyView: View? = null
    private var postHeaderView: MinePostHeaderView? = null
    private var mineViewModel: MineFragmentViewModel? = null
    private var homeIndexViewModel: HomeIndexViewModel? = null
    private val fragmentList: MutableList<Fragment> = mutableListOf()
    private val titleList: MutableList<MineTabType> = mutableListOf()
    private var userId = ""
    private var nickName = ""
    private var mOffset = 0f
    private var defaultSelected = 0
    private var imageDistance = 0f
    private var isMine = false
    private var isFromOtherHub = false
    private var isInitFinish = false
    private var currentPageType = ""
    override fun reload() {
        super.reload()
        if (!isCreated) {
            return
        }
        if (viewModel.isMine) {
            val currentUser = getCurrentUser()
            if (currentUser != null) {
                refresh(true)
                setFragmentList(currentUser)
            } else {
                userId = ""
                viewModel.updateUserId("")
                defaultSelected = 0
                //用户退出登录了清空所数据
                if (postHeaderView != null) {
                    postHeaderView!!.initBub(viewModel.isMine)
                }
                titleList.clear()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        try {
            mineViewModel = ViewModelProvider(this)[MineFragmentViewModel::class.java]
        } catch (e: Exception) {
            Log.e("error", e.message!!)
        }
    }

    override fun switchPages(uri: Uri) {
        var page = uri.getQueryParameter(MineConstant.SWITCH_PAGE_TYPE)
        if (StringUtils.isEmpty(page)) {
            page = MineConstant.DYNAMIC_PAGE
        }
        if (isInitFinish) {
            viewBinding.vp.currentItem = getPageIndex(page)
        } else {
            setVpPage(page)
        }
        super.switchPages(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        homeIndexViewModel = ViewModelProvider(requireActivity())[HomeIndexViewModel::class.java]
    }

    override fun initVariables(
        rootView: View?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        viewModel.userId = userId
        viewModel.isMine = isMine
        val currentUser = getCurrentUser()
        if (currentUser != null && userId == currentUser.id && !isMine) {
            isFromOtherHub = true
        }
        setFragmentList(currentUser)
        isInitFinish = true
        resetHeader()
        initView()
        showLoadingView()
        initAdapter()
        initRefresh()
        asyncUI()
        apiMineComponent.inject(this)
    }

    private fun setFragmentList(currentUser: DcLoginUser?) {
        titleList.add(MineTabType.DYNAMIC)
        titleList.add(MineTabType.DEAL)
        if ((currentUser != null && userId == currentUser.id) || isMine) {
            titleList.add(MineTabType.LOTTERY_NOTES)
            viewBinding.lotteryNotes.visibility = View.VISIBLE
        }
        titleList.add(MineTabType.OWN_SHOES)
        setTabData(titleList)
    }

    override fun onLazy() {
        showLoadingView()
        refresh(true)
    }

    private fun initView() {
        viewBinding.apply {
            mineTopLeftIcon.setOnClickListener { requireActivity().finish() }
            tvDynamic.setOnClickListener { switchVpPage(0) }
            tvDeal.setOnClickListener { switchVpPage(1) }
            tvOwnShoes.setOnClickListener { switchVpPage(3) }
            lotteryNotes.setOnClickListener {
                if (vp.currentItem == 2) {
                    showHintDialog()
                } else {
                    switchVpPage(2)
                }
            }
        }

        emptyView =
            DynamicEmptyView(requireContext(), noBg = true, fullParent = false, isShow = true)
    }

    @SuppressLint("HandlerLeak")
    override fun initListeners() {
        resetStatusBarHeight()
        viewBinding.mineTopLeftMenu.setOnClickListener {
            openMineDrawer()
        }
    }

    private fun initAdapter() {
        postHeaderView = MinePostHeaderView(requireContext(), viewModel.isMine).apply {
            initListener(object : MineHeaderClickListener {
                override fun onClickThinkUp(name: String, num: Int) {
                    UTHelper.commonEvent(UTConstant.Mine.MyP_click_Like)
                    context?.let { ctx ->
                        ClickUpDialog(ctx).apply {
                            setData(name, num)
                        }.show()
                    }
                }

                override fun onClickFollow(objectId: String) {
                    if (!isLoginAndRequestLogin(requireContext())) {
                        return
                    }
                    val params = HashMap<String?, String?>()
                    params[MineConstant.ExtraParam.FOLLOW_TYPE] = FollowActivity.TYPE_STAR
                    params[MineConstant.PARAM_KEY.ID] = objectId
                    DcRouter(MineConstant.Uri.FOLLOW)
                        .appendParams(params)
                        .start()
                }

                override fun onClickFans(objectId: String) {
                    if (!isLoginAndRequestLogin(requireContext())) {
                        return
                    }
                    val params = HashMap<String?, String?>()
                    params[MineConstant.ExtraParam.FOLLOW_TYPE] = FollowActivity.TYPE_FOLLOWED
                    params[MineConstant.PARAM_KEY.ID] = objectId
                    DcRouter(MineConstant.Uri.FOLLOW)
                        .appendParams(params)
                        .start()
                }

                override fun clickLogin() {
                    DcRouter(AccountConstant.Uri.INDEX).start()
                }

                override fun clickVip() {
                    val currentUser = getCurrentUser()
                    if (currentUser != null) {
                        MobileHelper.getInstance()
                            .getCloudUrlAndRouter(requireContext(), MineConstant.VIP_CENTER)
                    }
                    UTHelper.payPage(if (currentUser?.vipValidity == null) "normal" else "vip")
                }

                override fun clickReceivePoints() {
                    var utContent = "无待领取"
                    val scoreInfoEntity = homeIndexViewModel?.scoreInfoEntity
                    if (scoreInfoEntity != null) {
                        if (scoreInfoEntity.unReceive) {
                            utContent = "有待领取角标"
                        }
                    }
                    UTHelper.commonEvent(
                        UTConstant.Score.IntegralP_MyPPageEntry,
                        "subscrip",
                        utContent
                    )
                    DcRouter(MineConstant.Uri.SCORE_INDEX)
                        .start()
                }

                override fun onClickMedal(objectId: String) {
                    UTHelper.commonEvent(
                        if (viewModel.userId == getCurrentUser()?.id) UTConstant.Mine.MyP_click_Medal
                        else UTConstant.Mine.OthersP_click_Medal
                    )
                    DcUriRequest(context, MineConstant.Uri.METAL_LIST)
                        .putUriParameter(MineConstant.PARAM_KEY.ID, objectId)
                        .start()
                }
            })
        }
        viewBinding.view.addView(postHeaderView)
    }

    override fun onFragmentResume() {
        if (pageVisible && isAdded) {
            refreshUserInfo(false)
            homeIndexViewModel?.refreshUnRedUnReceive()
        }
    }

    override fun onResume() {
        if (pageVisible && isAdded) {
            refreshUserInfo(false)
            homeIndexViewModel?.refreshUnRedUnReceive()
        }
        activity?.let { if (it.isWhiteMode()) StatusBarUtil.setDarkMode(it) }
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        activity?.let {
            if (homeIndexViewModel?.homeCurrentShowTab != HomeIndexViewModel.IndexTopTab.Fashion) {
                if (it.isWhiteMode()) StatusBarUtil.setLightMode(it)
            }
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    private fun setTabData(orderTag: List<MineTabType>) {
        val titleList: MutableList<String> = ArrayList()
        val it = orderTag.iterator()
        if (CollectionUtils.isNotEmpty(orderTag)) {
            while (it.hasNext()) {
                val title = it.next()
                titleList.add(title.key)
//                val fragment: Fragment = when (title) {
//                    MineTabType.DYNAMIC -> MineDynamicListFragment.newInstance(
//                        MineConstant.DYNAMIC_PAGE,
//                        isMine,
//                        userId
//                    )
//                    MineTabType.DEAL -> MineDynamicListFragment.newInstance(
//                        MineConstant.TRADING_PAGE,
//                        isMine,
//                        userId
//                    )
//                    MineTabType.LOTTERY_NOTES -> MineLotteryNoteFragment.getInstance(MineConstant.LOTTERY_NOTE)
//                    MineTabType.OWN_SHOES -> MineDynamicListFragment.newInstance(
//                        MineConstant.SERIES_PAGE,
//                        isMine,
//                        userId
//                    )
//                }
//                fragmentList.add(fragment)
            }
        }
        viewBinding.vp.offscreenPageLimit = 2
        val childFragmentManager = childFragmentManager
        val fragmentAdapter =
            FragmentLazyStatePageAdapter(childFragmentManager, fragmentList, titleList)
        viewBinding.vp.adapter = fragmentAdapter
        viewBinding.vp.currentItem = defaultSelected
        switchVpPageHint(defaultSelected)
        viewBinding.vp.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        UTHelper.commonEvent(UTConstant.Mine.MyP_click_Dynamic)
                    }
                    1 -> {
                        UTHelper.commonEvent(UTConstant.Mine.MyP_click_transaction)
                    }
                    2 -> {
                        UTHelper.commonEvent(UTConstant.Mine.MyP_click_Raffle)
                    }
                    3 -> {
                        UTHelper.commonEvent(UTConstant.Mine.MyP_click_ShoeStore)
                    }
                }
                switchVpPageHint(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun showHintDialog() {
        if (context != null) {
            val dialog = CommonTitleDialog.Builder(requireContext())
                .title("抽签笔记")
                .content(resources.getString(R.string.lottery_note_tips))
                .confirmTxt(resources.getString(R.string.dynamic_edit_i_know))
                .builder()
            dialog.setCancelable(false)
            dialog.show()
        }
    }

    private fun asyncUI() {
        if (homeIndexViewModel == null || mineViewModel == null) {
            return
        }
        homeIndexViewModel!!.mineScoreInfoLiveData.observe(
            this
        ) { mineScoreInfoEntity: MineScoreInfoEntity? ->
            postHeaderView!!.onScoreChange(
                mineScoreInfoEntity!!
            )
        }
        with(mineViewModel!!) {
            cancelShield.observe(this@MineFragment) {
                showSuccessDialog("已取消屏蔽")
                EventBus.getDefault().post(EventShieldChange())
            }
            shieldResult.observe(this@MineFragment) {
                hideLoadingDialog()
                showSuccessDialog("已屏蔽")
            }
            followResult.observe(this@MineFragment) {
                hideLoadingDialog()
                showSuccessDialog("关注成功")
                refreshUserInfo(false)
            }
            cancelFollow.observe(this@MineFragment) { aBoolean: Boolean ->
                if (aBoolean) {
                    hideLoadingDialog()
                    showSuccessDialog("已取消关注")
                    refreshUserInfo(false)
                }
            }
            accountInfoEntityLiveData.observe(
                this@MineFragment
            ) { circleDynamicDetailUserBean: CircleDynamicDetailUserBean? ->
                updateAccountInfo(circleDynamicDetailUserBean)
            }
            refreshLayoutLiveEvent.observe(this@MineFragment) {
                if (it) hideLoadingView()
                finishRefresh()
            }
            isMineSelfPage.observe(this@MineFragment) { isMine -> updateIsMineUI(isMine) }
            vipPrivilegeLiveData.observe(this@MineFragment) { list ->
                postHeaderView?.onVipHintChange(list)
            }
            isHaveNewMedal.observe(this@MineFragment) {
                postHeaderView?.onMedalHintChange(it)
            }
        }
    }

    private fun updateIsMineUI(isMine: Boolean) {
        if (isMine) {
            if (isLogin()) {
                viewBinding.mineOverlay.visibility = View.GONE
//                mineViewModel?.syncVipHint()
            } else {
                viewBinding.mineOverlay.visibility = View.VISIBLE
            }
            imageDistance = SizeUtils.dp2px(200f).toFloat()
            viewBinding.avatarShadow.viewShadowBg.visibility = View.VISIBLE
        } else {
            imageDistance = SizeUtils.dp2px(550f).toFloat()
            viewBinding.avatarShadow.viewShadowBg.visibility = View.GONE
        }
    }

    private fun resetHeader() {
        val user = getCurrentUser()
        if (user == null) {
            viewBinding.mineOverlay.visibility = View.VISIBLE
            viewBinding.mineTopLeftMenu.visibility = View.INVISIBLE
            return
        }
        if (mineViewModel?.userId != user.id) {
            viewBinding.mineOverlay.visibility = View.VISIBLE
            viewBinding.mineTopLeftIcon.visibility = View.VISIBLE
            viewBinding.mineTopLeftMenu.visibility = View.INVISIBLE
        } else {
            if (isFromOtherHub) {
                viewBinding.mineOverlay.visibility = View.GONE
                viewBinding.mineTopLeftIcon.visibility = View.VISIBLE
                viewBinding.mineTopLeftMenu.visibility = View.INVISIBLE
            } else {
                viewBinding.mineOverlay.visibility = View.GONE
                viewBinding.mineTopLeftIcon.visibility = View.INVISIBLE
                viewBinding.mineTopLeftMenu.visibility = View.VISIBLE
            }
        }
    }

    private fun updateFollowAndImState(vis1: Boolean, vis2: Boolean, vis3: Boolean) {
        viewBinding.followView.visibility =
            if (vis1) View.VISIBLE else View.GONE
        viewBinding.imViewWithoutFollow.visibility =
            if (vis2) View.VISIBLE else View.GONE
        viewBinding.imView.visibility =
            if (vis3) View.VISIBLE else View.GONE
    }

    private fun updateMineUI(userInfo: CircleDynamicDetailUserBean?) {
        val currentUser = getCurrentUser()
        if (currentUser == null) { //未登录
            updateFollowAndImState(vis1 = true, vis2 = true, vis3 = false)
        } else {
            if (userInfo!!.isBlock || userInfo.isBeBlocked) { //屏蔽全不可见
                updateFollowAndImState(vis1 = false, vis2 = false, vis3 = false)
            } else { //已关注、、未关注、、自己
                if (userInfo.followed) {
                    updateFollowAndImState(vis1 = false, vis2 = false, vis3 = true)
                } else if (!userInfo.followed && userInfo.objectId != currentUser.id) {
                    updateFollowAndImState(true, vis2 = true, vis3 = false)
                } else if (userInfo.objectId == currentUser.id) {
                    updateFollowAndImState(vis1 = false, vis2 = false, vis3 = false)
                }
            }
        }
        viewBinding.imViewWithoutFollow.setOnClickListener {
            UTHelper.commonEvent("UserHomeP_click_Message")
            if (getCurrentUser() == null) {
                DcRouter(AccountConstant.Uri.INDEX)
                    .start()
                return@setOnClickListener
            }
            goToConversation()
        }
        viewBinding.imView.setOnClickListener {
            UTHelper.commonEvent("UserHomeP_click_Message")
            if (getCurrentUser() == null) {
                DcRouter(AccountConstant.Uri.INDEX)
                    .start()
                return@setOnClickListener
            }
            goToConversation()
        }
        viewBinding.followView.setOnClickListener {
            UTHelper.commonEvent("UserHomeP_click_Attention")
            if (getCurrentUser() == null) {
                DcRouter(AccountConstant.Uri.INDEX)
                    .start()
                return@setOnClickListener
            }
            showLoadingDialog()
            mineViewModel?.followUser(userInfo!!.objectId)
        }
        viewBinding.mineOverlay.setOnClickListener {
            val shieldUserDialog = ShieldUserDialog(requireContext())
            shieldUserDialog.userId = userInfo!!.objectId
            shieldUserDialog.showCancel = userInfo.followed
            shieldUserDialog.shield = userInfo.isBlock
            shieldUserDialog.shieldListener = object : ShieldListener {
                override fun onShieldListener(userId: String, shield: Boolean) {
                    if (isLoginAndRequestLogin(context!!)) {
                        if (shield) {
                            mineViewModel?.cancelShield(userId)
                        } else {
                            DcTitleDialog.Builder(requireContext())
                                .title("确定屏蔽用户吗？")
                                .content("屏蔽后对方无法私信、回复、点赞、关注你，你也无法与对方互动。")
                                .cancelTxt("取消")
                                .confirmTxt("确定")
                                .onConfirmClick {
                                    mineViewModel?.shieldUser(userId)
                                }
                                .builder()
                                .show()
                        }
                    }
                }

                override fun onCancelFollowListener(userId: String) {
                    showLoadingDialog()
                    mineViewModel?.cancelFollow(userId)
                }
            }
            shieldUserDialog.show()
        }
    }

    private fun switchVpPageHint(pos: Int) {
        viewBinding.apply {
            val selectedColor = getCompatColor(R.color.color_text_black1)
            val unSelectedColor = getCompatColor(R.color.color_text_black4)
            tvDynamic.setTextColor(
                if (pos == 0) {
                    selectedColor
                } else {
                    unSelectedColor
                }
            )
            tvDeal.setTextColor(
                if (pos == 1) {
                    selectedColor
                } else {
                    unSelectedColor
                }
            )

            val isShowShoesPage = (pos == 2 && lotteryNotes.visibility == View.GONE) || pos == 3

            tvOwnShoes.setTextColor(
                if (isShowShoesPage) {
                    selectedColor
                } else {
                    unSelectedColor
                }
            )

            val isShowNoteBookPage = lotteryNotes.visibility == View.VISIBLE && pos == 2

            lotteryNotes.setTextColor(
                if (isShowNoteBookPage) {
                    selectedColor
                } else {
                    unSelectedColor
                }
            )
            TextViewCompat.setCompoundDrawableTintList(
                lotteryNotes,
                ColorStateList.valueOf(
                    if (isShowNoteBookPage) {
                        selectedColor
                    } else {
                        unSelectedColor
                    }
                )
            )
        }
        currentPageType = when (pos) {
            0 -> MineConstant.DYNAMIC_PAGE
            1 -> MineConstant.TRADING_PAGE
            2 -> MineConstant.LOTTERY_NOTE
            3 -> MineConstant.SERIES_PAGE
            else -> ""
        }
    }

    private fun switchVpPage(pos: Int) {
        viewBinding.vp.currentItem = pos
        switchVpPageHint(pos)
    }

    private fun goToConversation() {
        val userBean = viewModel.accountInfoEntityLiveData.value
        if (userBean != null) {
            refreshUserInfoCache(userBean.objectId, userBean.nickName, userBean.avatarUrl)
            routeToConversationActivity(requireContext(), 1, userBean.objectId)
        }
    }

    /**
     * 更新顶部用户信息
     *
     * @param circleDynamicDetailUserBean 用户数据
     */
    private fun updateAccountInfo(circleDynamicDetailUserBean: CircleDynamicDetailUserBean?) {
        var url = ""
        if (circleDynamicDetailUserBean != null) {
            url = circleDynamicDetailUserBean.bigAvatarUrl
        }
        nickName = circleDynamicDetailUserBean!!.nickName
        Glide.with(this)
            .load(url)
            .error(R.drawable.default_avatar)
            .placeholder(R.drawable.default_avatar)
            .into(viewBinding.mineAvatarIv)
        postHeaderView!!.item = circleDynamicDetailUserBean
        updateMineUI(circleDynamicDetailUserBean)
    }

    private fun initRefresh() {
        context?.let { ctx -> viewBinding.mineSwipeRefresh.setRefreshHeader(DcWhiteRefreshHeader(ctx)) }
        viewBinding.mineSwipeRefresh.setHeaderInsetStart(50f)
        viewBinding.mineSwipeRefresh.setOnMultiListener(object : SimpleMultiListener() {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                refresh(false)
            }

            override fun onHeaderMoving(
                header: RefreshHeader,
                isDragging: Boolean,
                percent: Float,
                offset: Int,
                headerHeight: Int,
                maxDragHeight: Int
            ) {
                super.onHeaderMoving(
                    header,
                    isDragging,
                    percent,
                    offset,
                    headerHeight,
                    maxDragHeight
                )
                mOffset = offset / 2f
                val scale = 1 + abs(mOffset / 500)
                viewBinding.mineAvatarBg.scaleX = scale
                viewBinding.mineAvatarBg.scaleY = scale
            }
        })
        viewBinding.homeFragmentAppBarLayout.addOnOffsetChangedListener(OnOffsetChangedListener { _: AppBarLayout?, verticalOffset: Int ->
            val distance = abs(verticalOffset)
            if (distance >= 180) {
                if (isMine) {
                    viewBinding.tvTitle.text = "我的"
                } else {
                    viewBinding.tvTitle.text = nickName
                }
            } else {
                viewBinding.tvTitle.text = ""
            }
            if (distance < imageDistance) {
                viewBinding.mineAvatarBg.translationY = -distance.toFloat()
            } else {
                viewBinding.mineAvatarBg.translationY = -imageDistance
            }

            val currentFragment = fragmentList[viewBinding.vp.currentItem]
//            if (currentFragment is MineDynamicListFragment) {
//                currentFragment.view?.findViewById<RecyclerView>(R.id.rv)?.let {
//                    val tag = it.adapter?.hashCode()?.toString() ?: ""
//                    onScrollReleaseAllVideos(it, tag, 0.4f)
//                }
//            }
        })
    }

    private fun finishRefresh() {
        viewBinding.mineSwipeRefresh.finishRefresh()
    }

    private fun getPageIndex(page: String?): Int = when (page) {
        MineConstant.DYNAMIC_PAGE -> 0
        MineConstant.TRADING_PAGE -> 1
        MineConstant.RAFFLE_PAGE -> {
            if (getCurrentUser() != null && (userId == getCurrentUser()?.id || isMine)) {
                2
            } else {
                0
            }
        }
        MineConstant.SERIES_PAGE -> {
            if (getCurrentUser() != null && (userId == getCurrentUser()?.id || isMine)) {
                3
            } else {
                2
            }
        }
        else -> 0
    }

    fun setVpPage(page: String?) {
        defaultSelected = getPageIndex(page)
    }

    private fun openMineDrawer() {
        homeIndexViewModel?.openMineDrawer()
    }

    fun refresh(isFirstLoad: Boolean) {
        homeIndexViewModel?.refreshUnRedUnReceive()
        mineViewModel?.refreshData(isFirstLoad)
        mineViewModel?.syncVipHint()
        if (getCurrentUser() != null && mineViewModel?.userId == getCurrentUser()?.id) {
            mineViewModel?.isHaveNewMedal()
        }
        EventBus.getDefault().post(EventRefreshMineCollectionAndMinePage(currentPageType))
    }

    private fun resetToolbar() {
        viewBinding.mineTopBar.setBackgroundColor(Color.argb(0, 0, 0, 0))
    }

    fun setData(data: UserDetailBean) {
        resetToolbar()
        var url = ""
        if (data.userInfo != null) {
            url = data.userInfo!!.bigAvatarUrl
        }
        Glide.with(this)
            .load(url)
            .error(R.drawable.default_avatar)
            .placeholder(R.drawable.default_avatar)
            .into(viewBinding.mineAvatarIv)
        postHeaderView!!.item = data.userInfo
    }

    override fun onStatusViewErrorClick() {
        super.onStatusViewErrorClick()
        showLoadingView()
        refresh(true)
    }

    private fun refreshUserInfo(isFirstLoad: Boolean) {
        mineViewModel?.fetchSelfBrief(viewModel.userId, isFirstLoad)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun scoreStatusChange(event: EventScoreChange?) {
        homeIndexViewModel?.fetchScoreInfo()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDynamicChange(event: EventCircleChange?) {
        refresh(false)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventRelationFollow(eventFollowChange: EventShieldChange?) {
        refreshUserInfo(false)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDynamicFollower(eventFollowerChange: EventFollowerChange?) {
        refreshUserInfo(false)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun shieldResult(removeDynamicOfAccount: EventRemoveDynamicOfAccount) {
        if (TextUtils.isEmpty(removeDynamicOfAccount.userId)) {
            return
        }
        refreshUserInfo(false)
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onEventActivated(eventActivated: EventActivated?) {
        Logger.d("eventActivated =====")
        if (null != AccountHelper.getInstance().user) {
            refreshUserInfo(false)
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onIntegralChange(event: EventIntegralChange?) {
        Logger.d("onIntegralChange =====")
        //        if (null != AccountHelper.getInstance().getUser()) {
////            AccountHelper.getInstance().getUser().fetchInBackground((object, e) -> runOnUiThread(""));
//        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateAvatar(eventUpdateAvatar: EventUserDataChange) {
        if (eventUpdateAvatar.isChange) {
            refreshUserInfo(false)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateVip(eventUserVipChange: EventUserVipChange?) {
        postHeaderView!!.onVipChange()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventMedalChange(event: EventMedalStateChange) {
        refresh(false)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventUpdatePendant(event: EventUpdatePendant) {
        refresh(false)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventUpdateAvatar(event: EventUpdateAvatar) {
        refresh(false)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventWearMedal(event: EventMedalWear) {
        refresh(false)
    }

    fun setMine(mine: Boolean): MineFragment {
        isMine = mine
        return this
    }

    fun setUserId(userId: String): MineFragment {
        this.userId = userId
        return this
    }

    companion object {
        fun getInstance(): MineFragment = MineFragment()
    }
}