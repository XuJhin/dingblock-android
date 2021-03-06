package cool.dingstock.uicommon.product.item

import android.graphics.Color
import android.text.TextUtils
import android.view.View
import com.bumptech.glide.Glide
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.home.HomeRaffle
import cool.dingstock.appbase.entity.event.update.EventRefreshLotteryNote
import cool.dingstock.appbase.net.api.calendar.CalendarHelper.raffleAction
import cool.dingstock.appbase.router.ExternalRouter
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.LoginUtils.isLoginAndRequestLogin
import cool.dingstock.appbase.widget.commondialog.CommonDialog
import cool.dingstock.appbase.widget.recyclerview.item.BaseItem
import cool.dingstock.appbase.widget.recyclerview.item.BaseViewHolder
import cool.dingstock.lib_base.util.CollectionUtils
import cool.dingstock.lib_base.util.SizeUtils
import cool.dingstock.lib_base.util.StringUtils
import cool.dingstock.uicommon.R
import cool.dingstock.uicommon.databinding.HomeItemRegionDetailStyleProductBinding
import cool.dingstock.uicommon.helper.AnimationDrawHelper
import io.reactivex.rxjava3.functions.Consumer
import org.greenrobot.eventbus.EventBus
import org.libpag.PAGFile

class HomeRaffleDetailItem(data: HomeRaffle) :
    BaseItem<HomeRaffle, HomeItemRegionDetailStyleProductBinding>(data) {
    var otherActionUtEventId = ""
    private var animationDrawHelper: AnimationDrawHelper? = null
    private var utSource = ""

    fun setSource(source: String) {
        utSource = source
    }

    override fun onSetViewsData(holder: BaseViewHolder, sectionKey: Int, sectionViewPosition: Int) {
        val brand = data.brand
        if (null == brand || TextUtils.isEmpty(brand.imageUrl)) {
            viewBinding.homeItemRegionDetailIv.setImageResource(R.drawable.common_item_bg_radius_4)
        } else {
            Glide.with(context)
                .load(brand.imageUrl)
                .into(viewBinding.homeItemRegionDetailIv)
        }
        viewBinding.homeItemRegionDetailNameTxt.text = if (null == brand) "" else brand.name
        viewBinding.homeItemRegionDetailTimeTxt.text = data.releaseDateString
        viewBinding.homeItemRegionDetailMethodTxt.text = data.method
        ///??????????????????
        if (data.joined) {
            viewBinding.tvIsJohn.setTextColor(Color.parseColor("#9FA5B3"))
            viewBinding.tvIsJohn.setBackgroundResource(R.drawable.little_gray_r100_bg)
        } else {
            viewBinding.tvIsJohn.setTextColor(Color.parseColor("#438FFF"))
            viewBinding.tvIsJohn.setBackgroundResource(R.drawable.gray_r100_bg)
        }
        ///???????????????bp????????????
        setupActionIcon()
        ///??????????????????
        viewBinding.homeItemRegionDetailShareIcon.setOnClickListener { v: View? ->
            if (null != mListener) {
                UTHelper.commonEvent(UTConstant.SaleDetails.SaleDetailsP_click_Sale_informationShare)
                UTHelper.commonEvent(otherActionUtEventId, "????????????")
                mListener?.onShareClick(data)
            }
        }
        setJoinedData()
        //????????????
        holder.getItemView().setOnClickListener { v: View? ->
            if (utSource == "????????????") {
                UTHelper.commonEvent(UTConstant.BP.HotSaleP_click_ProductList) /////
            }
            onItemClick()
        }
        viewBinding.homeItemRegionDetailJoinedLin.setOnClickListener { v: View? ->
            if (utSource == "????????????") {
                UTHelper.commonEvent(UTConstant.BP.HotSaleP_click_ProductList) /////
            }
            UTHelper.commonEvent(UTConstant.SaleDetails.SaleDetailsP_click_Togo)
            onItemClick()
        }

        //??????ui????????????
        viewBinding.homeItemRegionDetailLine.visibility = if (isLast) View.GONE else View.VISIBLE
        if (showWhere == ShowWhere.PRODUCT_DETAILS) {
            viewBinding.rootItem.setPadding(SizeUtils.dp2px(12f), 0, SizeUtils.dp2px(12f), 0)
        } else {
            viewBinding.rootItem.setPadding(0, 0, 0, 0)
        }
        if (isLast) { //??????????????????????????????
            if (isStart) {
                viewBinding.rootItem.setBackgroundResource(R.drawable.common_item_bg_radius_6)
            } else { //??????????????? ??????????????????
                viewBinding.rootItem.setBackgroundResource(R.drawable.common_item_bg_radius_bottom_6)
            }
        } else {
            if (isStart) { //??????????????????????????????
                viewBinding.rootItem.setBackgroundResource(R.drawable.common_item_bg_radius_top_6)
            } else { //????????????????????????????????????
                viewBinding.rootItem.setBackgroundColor(getColor(R.color.white))
            }
        }
    }

    private fun onItemClick() {
//        UTHelper.commonEvent(UTConstant.Home.CLICK_GOOD_ROUTE);
        if (!isLoginAndRequestLogin(context)) {
            return
        }
        raffleAction()
        if (StringUtils.isEmpty(data.link)) {
            CommonDialog.Builder(context)
                .content(if (StringUtils.isEmpty(data.message)) "?????????????????????" else data.message)
                .builder()
                .show()
            return
        }
        when (utSource) {
            "????????????" -> {
                run { UTHelper.commonEvent(UTConstant.SaleDetails.SaleDetailsP_click_SalesInformationJump) }
                run { UTHelper.commonEvent(UTConstant.Home.CLICK_GOOD_ROUTE) }
                run { UTHelper.commonEvent(UTConstant.SaleDetails.SaleDetailsP_click_SalesInformationJump) }
            }
            "????????????" -> {
                run { UTHelper.commonEvent(UTConstant.Home.CLICK_GOOD_ROUTE) }
                run { UTHelper.commonEvent(UTConstant.SaleDetails.SaleDetailsP_click_SalesInformationJump) }
            }
            "" -> {
                UTHelper.commonEvent(UTConstant.SaleDetails.SaleDetailsP_click_SalesInformationJump)
            }
        }
        ExternalRouter.route(data.link)
    }

    private var showWhere = ShowWhere.PRODUCT_DETAILS

    interface ActionListener {
        fun onShareClick(homeRaffle: HomeRaffle)
        fun onSmsClick(homeRaffle: HomeRaffle)
        fun onAlarmClick(homeRaffle: HomeRaffle, pos: Int)
        fun onFlashClick(homeRaffle: HomeRaffle)
    }

    private var mListener: ActionListener? = null
    private var isLast = false
    private var isStart = true
    fun setLast(last: Boolean) {
        isLast = last
    }

    fun setStart(start: Boolean) {
        isStart = start
    }

    fun setMListener(mListener: ActionListener?) {
        this.mListener = mListener
    }

    fun setShowWhere(showWhere: ShowWhere) {
        this.showWhere = showWhere
    }

    override fun getViewType(): Int {
        return 0
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.home_item_region_detail_style_product
    }

    override fun onReleaseViews(
        holder: BaseViewHolder,
        sectionKey: Int,
        sectionViewPosition: Int
    ) {
    }

    enum class ShowWhere {
        PRODUCT_DETAILS, REGION_ITEM
    }

    private fun setJoinedData() {
        val joinedCount = data.joinedCount
        viewBinding.homeItemRegionDetailJoinedTxt.text =
            StringUtils.formatIntAsThousand(joinedCount) + "????????????"
    }

    private fun raffleAction() {
        val joined = data?.joined
        if (joined == true) { //?????????????????????????????????
            return
        }
        raffleAction(data?.objectId, true)
            .subscribe(Consumer<BaseResult<String>> { (err) ->
                if (!err) {
                    if (null == holder || null == holder.getItemView()) {
                        return@Consumer
                    }
                    EventBus.getDefault().post(EventRefreshLotteryNote())
                    var joinedCount = data.joinedCount
                    data?.joinedCount = joinedCount + 1
                    data?.joined = true
                    viewBinding.tvIsJohn.setTextColor(Color.parseColor("#9FA5B3"))
                    viewBinding.tvIsJohn.setBackgroundResource(R.drawable.little_gray_r100_bg)
                    setJoinedData()
                }
            }, Consumer { err: Throwable? -> })
    }

    /**
     * ??????????????????
     *
     *
     * 'bp??????'    ???  '????????????'??????????????????
     * '????????????'   ??? '?????????'????????????????????????
     */
    private fun setupActionIcon() {
        if (data.hasFinished) {
            viewBinding.pagView.visibility = View.GONE
            viewBinding.messagePagView.visibility = View.GONE
            viewBinding.calendarRaffleItemLayerSms.visibility = View.GONE
            viewBinding.calendarRaffleItemLayerBp.visibility = View.GONE
            return
        }
        if (animationDrawHelper != null || animationDrawHelper != null
            && animationDrawHelper?.checkEnableShowSms(data?.objectId) == true
            && animationDrawHelper?.checkEnableShowFlash(data!!.objectId) == true
        ) {
            viewBinding.pagView.visibility = View.GONE
            viewBinding.messagePagView.visibility = View.GONE
        }
        val smsBean = data.sms
        val bpLink = data.bpLink
        // ?????? ?????? ??????
        if (null != bpLink && bpLink != "" && !data.hasFinished) {
            viewBinding.calendarRaffleItemLayerBp.visibility = View.VISIBLE
            with(viewBinding.homeItemFlashOrSmsActionIcon) {
                visibility = View.VISIBLE
                setImageResource(R.drawable.calendar_icon_bp)
                setOnClickListener { _: View? ->
                    mListener?.onFlashClick(data)
                    UTHelper.commonEvent(
                        UTConstant.SaleDetails.BpSecKill_click_hotsale,
                        "source",
                        utSource
                    )
                    UTHelper.commonEvent(otherActionUtEventId, "???????????????")
                }
            }

            if (animationDrawHelper?.checkEnableShowFlash() == true
                && animationDrawHelper?.checkEnableShowFlash(data?.objectId) == false
            ) {
                viewBinding.pagView.visibility = View.VISIBLE
                data?.objectId?.let { animationDrawHelper?.putShowFlash(it, true) }
                val pagFile: PAGFile
                val isDarkMode = animationDrawHelper?.isDarkMode()
                pagFile = if (isDarkMode == true) {
                    PAGFile.Load(context.assets, "sms_draw_dark.pag")
                } else {
                    PAGFile.Load(context.assets, "sms_draw.pag")
                }
                val numTexts = pagFile.numTexts()
                if (numTexts < 0) {
                    return
                }
                for (index in 0 until numTexts) {
                    val netTextData = pagFile.getTextData(index)
                    when (index) {
                        0 -> {
                            netTextData.text = "???"
                        }
                        1 -> {
                            netTextData.text = "???"
                        }
                        2 -> {
                            netTextData.text = "???"
                        }
                        3 -> {
                            netTextData.text = "???"
                        }
                    }
                    pagFile.replaceText(index, netTextData)
                }
                viewBinding.pagView.setRepeatCount(1)
                viewBinding.pagView.progress = 0.0
                viewBinding.pagView.composition = pagFile
                viewBinding.pagView.post { viewBinding.pagView.play() }
                animationDrawHelper?.showEndFlashDraw()
            } else {
                viewBinding.pagView.clearAnimation()
                viewBinding.pagView.visibility = View.GONE
            }
        } else {
            viewBinding.pagView.visibility = View.GONE
            viewBinding.homeItemFlashOrSmsActionIcon.setOnClickListener(null)
            viewBinding.homeItemFlashOrSmsActionIcon.visibility = View.GONE
        }


        viewBinding.messagePagView.visibility = View.GONE
        ///???????????? ??????????????????
        if (data.notifyDate - System.currentTimeMillis() > 0) {
            viewBinding.calendarRaffleItemLayerSms.visibility = View.VISIBLE
            viewBinding.homeItemRegionDetailActionIcon.visibility = View.VISIBLE
            if (data.reminded) {
                viewBinding.homeItemRegionDetailActionIcon.setImageResource(R.drawable.calendar_icon_clock_selected)
            } else {
                viewBinding.homeItemRegionDetailActionIcon.setImageResource(R.drawable.calendar_icon_clock)
            }
            viewBinding.homeItemRegionDetailActionIcon.setOnClickListener { v: View? ->
                if (null != mListener) {
                    UTHelper.commonEvent(
                        UTConstant.SaleDetails.SaleDetailsP_click_AlarmClock_icon,
                        "source",
                        utSource
                    )
                    mListener?.onAlarmClick(data, holder.layoutPosition)
                    UTHelper.commonEvent(otherActionUtEventId, "????????????")
                }
            }
        } else {
            if (null != smsBean && !CollectionUtils.isEmpty(smsBean.sections) && !data.hasFinished) {
                viewBinding.calendarRaffleItemLayerSms.visibility = View.VISIBLE
                viewBinding.homeItemRegionDetailActionIcon.setImageResource(R.drawable.calendar_icon_message)
                viewBinding.homeItemRegionDetailActionIcon.setOnClickListener { v: View? ->
                    if (null != mListener) {
                        UTHelper.commonEvent(
                            UTConstant.SaleDetails.SaleDetailsP_click_SMS_icon,
                            "source",
                            utSource
                        )
                        mListener?.onSmsClick(data)
                        UTHelper.commonEvent(otherActionUtEventId, "????????????")
                    }
                }
                if (animationDrawHelper == null) {
                    viewBinding.pagView.visibility = View.GONE
                    return
                }

                //????????????????????? ??????????????? sms??????
                if (animationDrawHelper?.checkEnableShowSms() == true
                    && animationDrawHelper?.checkEnableShowSms(data?.objectId) == false
                ) {
                    data?.objectId?.let { animationDrawHelper?.putShowSms(it, true) }
                    viewBinding.messagePagView.visibility = View.VISIBLE
                    val pagFile: PAGFile
                    val isDarkMode = animationDrawHelper?.isDarkMode()
                    pagFile = if (isDarkMode == true) {
                        PAGFile.Load(context.assets, "sms_draw_dark.pag")
                    } else {
                        PAGFile.Load(context.assets, "sms_draw.pag")
                    }
                    viewBinding.messagePagView.setRepeatCount(1)
                    viewBinding.messagePagView.progress = 0.0
                    viewBinding.messagePagView.composition = pagFile
                    viewBinding.messagePagView.play()
                    animationDrawHelper?.showEndSmsDraw()
                } else {
                    viewBinding.messagePagView.clearAnimation()
                    viewBinding.messagePagView.visibility = View.GONE
                }
            } else {
                viewBinding.calendarRaffleItemLayerSms.visibility = View.GONE
                viewBinding.homeItemRegionDetailActionIcon.visibility = View.GONE
            }
        }
    }

    fun setAnimationDrawHelper(animationDrawHelper: AnimationDrawHelper?) {
        this.animationDrawHelper = animationDrawHelper
    }
}