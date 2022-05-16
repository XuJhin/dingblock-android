package cool.dingstock.mine.ui.vip

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.constant.*
import cool.dingstock.appbase.entity.bean.mine.VipPrivilegeEntity
import cool.dingstock.appbase.entity.bean.vip.VipPriceEntity
import cool.dingstock.appbase.ext.*
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.net.mobile.MobileHelper
import cool.dingstock.appbase.router.interceptor.DcLoginInterceptor
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.lib_base.util.SizeUtils
import cool.dingstock.lib_base.util.ToastUtil
import cool.dingstock.mine.R
import cool.dingstock.mine.databinding.ActivityVipCenterBinding
import cool.dingstock.mine.itemView.VipPrivilegeItemBinder
import cool.dingstock.mobile.PayCallback
import cool.dingstock.mobile.PayHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import java.text.SimpleDateFormat


@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [MineConstant.Path.VIP_CENTER],
    interceptors = [DcLoginInterceptor::class]
)
class VipCenterActivity : VMBindingActivity<VipCenterVm, ActivityVipCenterBinding>() {
    enum class PayType(val str: String) {
        AliPay(PayConstant.PayType.ALI_PAY), WeChatPay(PayConstant.PayType.WE_CHAT_PAY)
    }

    private val vipChoicerList by lazy {
        arrayListOf(
            viewBinding.vipCenterChoicerLayer.vipLin1,
            viewBinding.vipCenterChoicerLayer.vipLin2,
            viewBinding.vipCenterChoicerLayer.vipLin3
        )
    }
    private val vipTotalPriceTvList by lazy {
        arrayListOf(
            viewBinding.vipCenterChoicerLayer.vip1TotalPriceTv,
            viewBinding.vipCenterChoicerLayer.vip2TotalPriceTv,
            viewBinding.vipCenterChoicerLayer.vip3TotalPriceTv
        )
    }
    private val vipOldPriceTvList by lazy {
        arrayListOf(
            viewBinding.vipCenterChoicerLayer.vip1OldPriceTv,
            viewBinding.vipCenterChoicerLayer.vip2OldPriceTv,
            viewBinding.vipCenterChoicerLayer.vip3OldPriceTv
        )
    }
    private val vipMonthPriceTvList by lazy {
        arrayListOf(
            viewBinding.vipCenterChoicerLayer.vip1MonthTv,
            viewBinding.vipCenterChoicerLayer.vip2MonthTv,
            viewBinding.vipCenterChoicerLayer.vip3MonthTv
        )
    }
    private val vipDiscountPriceTvList by lazy {
        arrayListOf(
            viewBinding.vipCenterChoicerLayer.vipTag1Tv,
            viewBinding.vipCenterChoicerLayer.vipTag2Tv,
            viewBinding.vipCenterChoicerLayer.vipTag3Tv
        )
    }
    private val vipNamePriceTvList by lazy {
        arrayListOf(
            viewBinding.vipCenterChoicerLayer.vip1MonthTv,
            viewBinding.vipCenterChoicerLayer.vip2MonthTv,
            viewBinding.vipCenterChoicerLayer.vip3MonthTv
        )
    }

    private val vipPriceArrowList by lazy {
        arrayListOf(
            viewBinding.vipCenterChoicerLayer.viewArrowDay,
            viewBinding.vipCenterChoicerLayer.viewArrowMonth,
            viewBinding.vipCenterChoicerLayer.viewArrowYear
        )
    }

    private val vipPriceIvList by lazy {
        arrayListOf(
            viewBinding.vipCenterChoicerLayer.viewDayVip,
            viewBinding.vipCenterChoicerLayer.viewMonthVip,
            viewBinding.vipCenterChoicerLayer.viewYearVip
        )
    }


    private val prices = ArrayList<VipPriceEntity>()
    private var currentIndex: Int = 0
    private var payType = PayType.WeChatPay

    //折扣码是否确定
    private var isCodeCheck = false
    private var mAdapter = DcBaseBinderAdapter(arrayListOf())

    private var utEventId = ""
    private var isVip = false

    override fun fakeStatusView(): View = viewBinding.fakeStatusBar.fakeStatusBar

    override fun setSystemStatusBar() {
        StatusBarUtil.setDarkMode(this)
        StatusBarUtil.transparentStatus(this)
    }

    @SuppressLint("WrongViewCast")
    override fun bottomBarView(): View? {
        return findViewById(R.id.bottom_bar)
    }

    private fun setLimitRvHeight() {
        //限定rv高度
        val lp: ViewGroup.LayoutParams = viewBinding.vipPrivilegeLayer.mineDrawerRv.layoutParams
        lp.height = UIUtil.dip2px(context, 220.0)
        viewBinding.vipPrivilegeLayer.mineDrawerRv.layoutParams = lp
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        utEventId = uri.getQueryParameter(CommonConstant.UriParams.UT_EVENT_ID) ?: ""
        viewModel.load(false)
        mAdapter.addItemBinder(VipPrivilegeEntity::class.java, VipPrivilegeItemBinder())
        viewBinding.vipPrivilegeLayer.mineDrawerRv.apply {
            adapter = mAdapter
            layoutManager = GridLayoutManager(context, 4, LinearLayoutManager.VERTICAL, false)
        }
        initObserver()
        initView()
        onChoiceClick(0)
        setupViewScroll()
    }

    private fun setupViewScroll() {
        viewBinding.rootView.viewTreeObserver.addOnGlobalLayoutListener {
            try {
                val rect = Rect()
                this.window?.decorView?.getWindowVisibleDisplayFrame(rect)
                val currentHeight = rect.bottom
                val height = SizeUtils.getHeight()
                val loginBottom =
                    height - viewBinding.vipCenterPromotionLayer.kolEditLayer.y - viewBinding.vipCenterPromotionLayer.kolEditLayer.height
                val keyBoardHeight = height - currentHeight
                val offset = keyBoardHeight - loginBottom
                try {
                    if (offset > 0) {
                        ani2Top(-offset - 400)
                    } else {
                        ani2Bottom()
                    }
                } catch (e: Exception) {
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun ani2Top(offSet: Float) {
        val newOffSet = offSet + 150.dp
        val translationY =
            ObjectAnimator.ofFloat(
                viewBinding.layoutContent,
                "translationY",
                viewBinding.layoutContent.y,
                newOffSet
            )
        translationY.duration = 300L
        translationY.start()
    }

    private fun ani2Bottom() {
        val translationY =
            ObjectAnimator.ofFloat(
                viewBinding.layoutContent,
                "translationY",
                viewBinding.layoutContent.y,
                0f
            )
        translationY.duration = 300L
        translationY.start()
    }

    override fun initListeners() {
        viewBinding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.load(true)
            //清空所有状态
            clearAllState()
        }
        for (i in vipChoicerList.indices) {
            vipChoicerList[i].setOnClickListener {
                onChoiceClick(i)
            }
        }
        viewBinding.vipCenterPayTypeLayer.aliPayLayer.setOnClickListener {
            onPaySel(PayType.AliPay)
        }
        viewBinding.vipCenterPayTypeLayer.wechatPayLayer.setOnClickListener {
            onPaySel(PayType.WeChatPay)
        }
        viewBinding.vipCenterPromotionLayer.codeCheckTv.setOnClickListener {
            UTHelper.commonEvent(UTConstant.Mine.VipP_click_LnvitationCode)
            if (isCodeCheck) {
                viewModel.cancelCode()
            } else {
                val code = viewBinding.vipCenterPromotionLayer.kolEdit.text.toString()
                if (TextUtils.isEmpty(code)) {
                    ToastUtil.getInstance()
                        ._short(this, resources.getString(R.string.not_input_promotion_hint))
                    return@setOnClickListener
                }
                viewModel.checkCode(code)
            }
        }
        viewBinding.vipCenterBuyBtn.setOnClickListener {
            try {
                val vipPriceEntity = prices[currentIndex]
                UTHelper.commonEvent(
                    UTConstant.Mine.VipP_click_Open,
                    "button",
                    viewBinding.vipCenterBuyBtn.text.toString(),
                    "name",
                    vipPriceEntity.month.toString().plus("个月")
                )
                PayHelper.instance?.generateOrder(
                    this,
                    payType.str,
                    vipPriceEntity.androidId,
                    object : PayCallback {
                        override fun onSucceed() {
                            viewModel.updateUser()
                            viewModel.load(true)
                            UTHelper.commonEvent(utEventId, "ActivationResult", "开通成功")
                            UTHelper.commonEvent(
                                UTConstant.Mine.VipP_Success,
                                "pay",
                                payType.str,
                                "name",
                                vipPriceEntity.month.toString().plus("个月")
                            )
                        }

                        override fun onFailed(errorCode: String?, errorMsg: String?) {
                            ToastUtil.getInstance()._short(this@VipCenterActivity, errorMsg ?: "")
                            UTHelper.commonEvent(utEventId, "ActivationResult", "开通失败")
                            UTHelper.commonEvent(
                                UTConstant.Mine.VipP_Fail,
                                "pay",
                                payType.str,
                                "name",
                                vipPriceEntity.month.toString().plus("个月")
                            )
                        }

                        override fun onCancel() {
                            UTHelper.commonEvent(utEventId, "ActivationResult", "开通失败")
                            ToastUtil.getInstance()._short(
                                this@VipCenterActivity,
                                resources.getString(R.string.cancel_pay)
                            )
                            UTHelper.commonEvent(UTConstant.Mine.VipP_Fail)
                        }
                    })
            } catch (e: Exception) {
            }
        }
        viewBinding.vipCardLayer.kolResultLayer.setOnClickListener {
            UTHelper.commonEvent(UTConstant.Mine.VipP_click_PromotionRevenue_KOL)
            viewModel.kolInvitations()
        }
        viewBinding.vipCenterChoicerLayer.apply {
            ivFirstPrize.setOnShakeClickListener {
                UTHelper.commonEvent(
                    UTConstant.Mine.EnterSource_Musicvip,
                    "type",
                    if (isVip) "会员" else "非会员",
                    "source",
                    if (currentIndex == 1) "季卡-网易" else if (currentIndex == 2) "年卡-网易" else "数据异常"
                )
                MobileHelper.getInstance()
                    .getCloudUrlAndPutValueRouter(context, "musicMember", "platformType", "cloud")
            }
            ivSecondPrize.setOnShakeClickListener {
                UTHelper.commonEvent(
                    UTConstant.Mine.EnterSource_Musicvip,
                    "type",
                    if (isVip) "会员" else "非会员",
                    "source",
                    if (currentIndex == 1) "季卡-QQ" else if (currentIndex == 2) "年卡-QQ" else "数据异常"
                )
                MobileHelper.getInstance()
                    .getCloudUrlAndPutValueRouter(context, "musicMember", "platformType", "qq")
            }
        }
        viewBinding.vipPrivilegeLayer.tvShowAllVipPower.setOnShakeClickListener {
            val lp: ViewGroup.LayoutParams = viewBinding.vipPrivilegeLayer.mineDrawerRv.layoutParams
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
            viewBinding.vipPrivilegeLayer.mineDrawerRv.layoutParams = lp
            viewBinding.vipPrivilegeLayer.tvShowAllVipPower.hide(true)
        }
    }

    private fun initObserver() {
        viewModel.kolExitLiveData.observe(this) {
            viewBinding.vipCardLayer.kolResultLayer.hide(!(it.showProfit ?: false))
            viewBinding.vipCenterPromotionLayer.kolEditLayer.hide(!(it.showDiscount ?: false))
        }
        viewModel.refreshLiveData.observe(this) {
            if (it) {
                viewBinding.swipeRefreshLayout.isRefreshing = false
            }
        }
        viewModel.priceLiveData.observe(this) {
            prices.clear()
            prices.addAll(it)
            if (it.size >= 3) {
                for (i in 0..2) {
                    setVipPrice(i)
                }
            }
            onChoiceClick(currentIndex)
        }
        viewModel.codeResultLiveData.observe(this) {
            isCodeCheck = it
            if (it) { //确认折扣码
                viewBinding.vipCenterPromotionLayer.kolEdit.isEnabled = false
                viewBinding.vipCenterPromotionLayer.codeCheckTv.text =
                    resources.getString(R.string.cancel)
            } else { //取消折扣码
                viewBinding.vipCenterPromotionLayer.kolEdit.isEnabled = true
                viewBinding.vipCenterPromotionLayer.kolEdit.setText("")
                viewBinding.vipCenterPromotionLayer.codeCheckTv.text =
                    resources.getString(R.string.confirm)
            }
        }
        viewModel.updateUserLiveData.observe(this) {
            if (it) {
                initView()
            }
        }
        viewModel.vipPrivilegeLiveData.observe(this) {
            mAdapter.getDataList().clear()
            if (it != null) {
                setLimitRvHeight()
                mAdapter.addData(it)
                viewBinding.vipPrivilegeLayer.tvShowAllVipPower.hide(it.size <= 8)
            } else {
                mAdapter.addData(arrayListOf())
            }
        }
    }

    private fun initView() {
        val currentUser = LoginUtils.getCurrentUser() ?: return
        viewBinding.vipCardLayer.userIv.loadAvatar(currentUser.avatarUrl)
        viewBinding.vipCardLayer.nickTv.text = currentUser.nickName
        if (currentUser.vipValidity == null) {
            viewBinding.vipCardLayer.vipDecTv.text = "开通会员享更多惊喜特权"
            viewBinding.vipCenterBuyBtn.text = "立即开通"
            isVip = false
        } else {
            if ((currentUser.vipValidity ?: 0) < System.currentTimeMillis()) {
                viewBinding.vipCardLayer.vipDecTv.text = "开通会员享更多惊喜特权"
                viewBinding.vipCenterBuyBtn.text = "立即开通"
                isVip = false
            } else {
                isVip = true
                val dateFormat = SimpleDateFormat("yyyy/MM/dd")
                val formatString = dateFormat.format(currentUser.vipValidity)
                viewBinding.vipCardLayer.vipDecTv.text =
                    String.format(context.getString(R.string.mine_vip_info_template), formatString)
                viewBinding.vipCenterBuyBtn.text = "立即续费"
            }
        }
        for (tv in vipOldPriceTvList) {
            tv.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
        }
        //默认选中微信支付
        onPaySel(PayType.WeChatPay)
    }

    override fun onStatusViewErrorClick() {
        super.onStatusViewErrorClick()
        viewModel.load(false)
    }

    private fun onChoiceClick(index: Int) {
        vipChoicerList.forEach {
            it.isSelected = false
        }
        vipOldPriceTvList.forEach {
            it.isSelected = false
        }
        vipTotalPriceTvList.forEach {
            it.isSelected = false
        }
        vipMonthPriceTvList.forEach {
            it.isSelected = false
        }
        vipChoicerList[index].isSelected = true
        vipOldPriceTvList[index].isSelected = true
        vipTotalPriceTvList[index].isSelected = true
        vipMonthPriceTvList[index].isSelected = true

        //298
        if (prices.isNotEmpty() && prices.size > index && prices[index].presentsDetail != null) {
            refreshBigPrizeView(index)
        } else {
            viewBinding.vipCenterChoicerLayer.clVipMusicHint.hide(true)
            refreshAllSmallPrize()
        }
        currentIndex = index
    }

    private fun refreshAllSmallPrize() {
        if (prices.isEmpty()) {
            return
        }
        for (index in 0..2) {
            vipPriceIvList[index].hide(prices[index].presentsImage.isNullOrEmpty())
            if (!prices[index].presentsImage.isNullOrEmpty()) {
                vipPriceIvList[index].load(prices[index].presentsImage)
            }
        }
    }

    private fun refreshBigPrizeView(index: Int) {
        if (index < 0 || index > 2) {
            for (i in 0..2) {
                vipPriceArrowList[i].hide(true)
            }
            viewBinding.vipCenterChoicerLayer.clVipMusicHint.hide(true)
            return
        }
        for (i in 0..2) {
            vipPriceArrowList[i].hide(index != i)
            vipPriceIvList[i].hide(true)
        }
        viewBinding.vipCenterChoicerLayer.apply {
            clVipMusicHint.hide(false)
            tvVipMusicTitle.text = prices[index].presentsDetail?.title
            tvVipMusicDesc.text = prices[index].presentsDetail?.decs
            ivFirstPrize.load(prices[index].presentsDetail?.wyImgUrl)
            ivSecondPrize.load(prices[index].presentsDetail?.qqImgUrl)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setVipPrice(index: Int) {
        if (index > 2) {
            return
        }
        if (prices.size < 3) {
            return
        }
        vipNamePriceTvList[index].text = prices[index].name.toNullString()
        vipTotalPriceTvList[index].text = prices[index].price?.toInt().toNullString()
        vipOldPriceTvList[index].text = "¥" + prices[index].originalPrice?.toInt()?.toNullString()

        vipDiscountPriceTvList[index].visual(prices[index].discount != null)
        vipDiscountPriceTvList[index].text =
            "${prices[index].discount?.toFormatStr("%.1f").toNullString()}折" +
                    " 省${
                        ((prices[index].originalPrice ?: 0.0) - (prices[index].price ?: 0.0))
                            .toFloat().formatPrice()
                    }元"
    }

    private fun onPaySel(payType: PayType) {
        this.payType = payType
        if (payType == PayType.WeChatPay) {
            viewBinding.vipCenterPayTypeLayer.wechatPayIv.isSelected = true
            viewBinding.vipCenterPayTypeLayer.aliPayIv.isSelected = false
        } else {
            viewBinding.vipCenterPayTypeLayer.wechatPayIv.isSelected = false
            viewBinding.vipCenterPayTypeLayer.aliPayIv.isSelected = true
        }
    }

    private fun clearAllState() {
        viewBinding.vipCenterPromotionLayer.kolEdit.setText("")
        viewBinding.vipCenterPromotionLayer.kolEdit.isEnabled = true
        viewBinding.vipCenterPromotionLayer.codeCheckTv.text = resources.getString(R.string.confirm)
        onChoiceClick(currentIndex)
    }

    override fun moduleTag(): String = ModuleConstant.MINE_MODULE
}

fun Double.toFormatStr(format: String): String {
    return String.format(format, this)
}

fun Any?.toNullString(): String {
    if (this == null) {
        return ""
    }
    return toString()
}