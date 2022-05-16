package cool.dingstock.mine.ui.exchange.good

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.ShapeAppearanceModel
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.constant.*
import cool.dingstock.appbase.entity.bean.score.ScoreExchangeItemEntity
import cool.dingstock.appbase.entity.bean.score.ScoreIndexUserInfoEntity
import cool.dingstock.appbase.entity.bean.score.SourceType
import cool.dingstock.appbase.entity.event.box.EventConfirmAddress
import cool.dingstock.appbase.ext.dp
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.ext.loadAvatar
import cool.dingstock.appbase.mvvm.activity.VMActivity
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.appbase.widget.TitleBar
import cool.dingstock.appbase.widget.commondialog.CommonDialog
import cool.dingstock.lib_base.util.ToastUtil
import cool.dingstock.mine.R
import cool.dingstock.mine.dialog.ScoreExchangeSuccessDialog
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 积分兑换商品详情页面
 */
@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [MineConstant.Path.EXCHANGE_GOOD_DETAIL]
)
class MineExchangeGoodsActivity : VMActivity<MineExchangeGoodsViewModel>() {
    private var goodsCover: ShapeableImageView? = null
    private var tvExchangeGoodTitle: TextView? = null
    private var tvExchangeGoodDesc: TextView? = null
    private var btnExchangeGood: AppCompatButton? = null
    private var tvGoodDesc: TextView? = null
    private var tvGoodExpireDate: TextView? = null
    private var tvGoodInstructions: TextView? = null
    private var clMedalView: ConstraintLayout? = null
    private var ivHead: ImageView? = null
    private var tvUserName: TextView? = null
    private var tvUserDesc: TextView? = null
    private var ivMedalPre: ImageView? = null
    private var tvMedalViewTitle: TextView? = null

    private var titleBar: TitleBar? = null
    private var paramData: ScoreExchangeItemEntity? = null
    private var userInfo: ScoreIndexUserInfoEntity? = null

    private var layoutHaveAddress: View? = null
    private var layoutNoAddress: View? = null
    private var tvName: TextView? = null
    private var tvNumber: TextView? = null
    private var tvAddress: TextView? = null
    private var tvAddressTitle: TextView? = null
    private var pageType = ""

    override fun getLayoutId(): Int {
        return R.layout.activity_mine_exchange_goods
    }

    init {
        EventBus.getDefault().register(this)
    }

    override fun initBundleData() {
        super.initBundleData()
        pageType = intent?.getStringExtra(MineConstant.ExtraParam.FROM_WHERE) ?: ""
        when (pageType) {
            SourceType.FROM_SCORE_INDEX.value -> {
                paramData = intent?.extras?.getParcelable(MineConstant.ExtraParam.EXCHANGE_GOOD)
                userInfo = intent?.extras?.getParcelable(MineConstant.ExtraParam.EXCHANGE_USER_INFO)
            }
            SourceType.FROM_EXCHANGE_PAGE.value -> {
                viewModel.id = intent?.getStringExtra("id") ?: ""
                viewModel.eventId = intent?.getStringExtra("eventId") ?: ""//商品id
            }
            SourceType.FROM_MINE_MEDAL_PAGE.value -> {
                viewModel.id = intent?.getStringExtra("id") ?: ""
                viewModel.eventId = intent?.getStringExtra("eventId") ?: ""//商品id
                viewModel.scoreNum = intent?.getIntExtra("scoreNumber", -1) ?: -1
            }
            else -> {
            }
        }
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        findViewById()
        initView()
        observerLivedata()
        when (pageType) {
            SourceType.FROM_SCORE_INDEX.value -> {
                paramData?.id?.let { fetchData(it) }
            }
            SourceType.FROM_EXCHANGE_PAGE.value -> {
                fetchData(viewModel.eventId)
                viewModel.fetchAddressDataAndUI()
            }
            SourceType.FROM_MINE_MEDAL_PAGE.value -> {
                fetchData(viewModel.eventId)
            }
            else -> {
            }
        }
    }

    override fun onStatusViewErrorClick() {
        super.onStatusViewErrorClick()
        fetchData(viewModel.eventId)
    }

    private fun fetchData(id: String) {
        viewModel.requestGoodsDetail(id)
    }

    private fun observerLivedata() {
        viewModel.exchangeLiveData.observe(this) {
            ScoreExchangeSuccessDialog(this)
                .setData(it, tvExchangeGoodTitle?.text.toString())
                .show()
        }

        viewModel.getGoodSuccessLiveData.observe(this) {
            showToastShort("领取成功")
            finish()
        }

        viewModel.goodDetailLiveData.observe(this) {
            val isHideMedalView = it.user == null || it.achievement == null
            tvMedalViewTitle?.hide(isHideMedalView)
            clMedalView?.hide(isHideMedalView)
            if (!isHideMedalView) {
                ivHead?.loadAvatar(it.user?.avatarUrl)
                tvUserName?.text = it.user?.nickName
                tvUserDesc?.text = "1分钟前"
                ivMedalPre?.load(it.achievement?.iconUrl)
            }

            goodsCover?.load(it.imageUrl)
            tvExchangeGoodTitle?.text = it.name
            tvExchangeGoodDesc?.text = it.desc
            tvGoodInstructions?.text = it.introduce
            tvGoodExpireDate?.text = it.validityStr
            when (pageType) {
                SourceType.FROM_SCORE_INDEX.value -> {
                    btnExchangeGood?.text = it.buttonStr
                    btnExchangeGood?.isEnabled = it.soldOut == false
                }
                SourceType.FROM_EXCHANGE_PAGE.value -> {

                }
                SourceType.FROM_MINE_MEDAL_PAGE.value -> {
                    btnExchangeGood?.text = it.buttonStr
                    btnExchangeGood?.isEnabled = it.soldOut == false
                }
                else -> {
                }
            }
            tvGoodDesc?.text = it.explain
        }

        viewModel.isNoAddress.observe(this) {
            layoutHaveAddress?.hide(it)
            layoutNoAddress?.hide(!it)
            upDateButtonState(!it)
            if (!it) {
                val addressList = viewModel.addressList
                addressList.forEach { entity ->
                    if (entity.isDefault) {
                        tvName!!.text = entity.name
                        tvNumber!!.text = entity.mobileZone.plus(" " + entity.mobile)
                        tvAddress!!.text =
                            entity.province.plus(entity.city + entity.district + entity.address)
                    }
                }
            }
        }
    }

    private fun upDateButtonState(isClick: Boolean) {
        when (isClick) {
            true -> {
                btnExchangeGood!!.background =
                    AppCompatResources.getDrawable(
                        context,
                        cool.mobile.account.R.drawable.common_btn_r8_black
                    )
                btnExchangeGood!!.isClickable = true
            }
            false -> {
                btnExchangeGood!!.background =
                    AppCompatResources.getDrawable(
                        context,
                        cool.mobile.account.R.drawable.common_btn_r8_gray
                    )
                btnExchangeGood!!.isClickable = false
            }
        }
    }

    private fun initView() {
        when (pageType) {
            SourceType.FROM_SCORE_INDEX.value -> {
                btnExchangeGood?.text = "立即兑换"
                titleBar?.title = "商品详情"
            }
            SourceType.FROM_EXCHANGE_PAGE.value -> {
                tvAddressTitle?.hide(false)
                btnExchangeGood?.text = "立即领取"
                titleBar?.title = "确认领取"
            }
            SourceType.FROM_MINE_MEDAL_PAGE.value -> {
                btnExchangeGood?.text = "立即兑换"
                titleBar?.title = "商品详情"
            }
            else -> {
            }
        }
        btnExchangeGood?.isClickable = false
        titleBar?.setLeftOnClickListener { finish() }
        goodsCover?.apply {
            shapeAppearanceModel = ShapeAppearanceModel
                .builder()
                .setAllCornerSizes(7.dp)
                .build()
        }
    }

    private fun exchangeGoods(userCredit: Int?, goodsId: String?) {
//        val useCredit = userInfo?.userCredit ?: -1
        val useCredit = userCredit ?: -1
        val cost = viewModel.goodDetailLiveData.value?.cost ?: 0
        if (useCredit < cost || useCredit < 0) {
            ToastUtil.getInstance()._short(this, "积分不足")
            return
        }
        CommonDialog.Builder(this)
            .content("本次兑换将消耗${cost}积分，是否确认兑换？")
            .confirmTxt("确定")
            .cancelTxt("取消")
            .onCancelClick {
                UTHelper.commonEvent(
                    UTConstant.Score.IntegralP_ExchangeConfirmationPopup,
                    "button",
                    "取消"
                )
            }
            .onConfirmClick {
                UTHelper.commonEvent(
                    UTConstant.Score.IntegralP_ExchangeConfirmationPopup,
                    "button",
                    "确认"
                )
                goodsId?.let { Id -> viewModel.scoreExchange(Id) }
            }
            .builder()
            .show()
    }

    private fun findViewById() {
        goodsCover = findViewById(R.id.iv_exchange_good_cover)
        tvExchangeGoodTitle = findViewById(R.id.tv_exchange_good_title)
        tvExchangeGoodDesc = findViewById(R.id.tv_exchange_good_desc)
        btnExchangeGood = findViewById(R.id.mine_btn_exchange_good)
        tvGoodDesc = findViewById(R.id.tv_content_good_desc)
        tvGoodExpireDate = findViewById(R.id.tv_content_expire_date)
        tvGoodInstructions = findViewById(R.id.tv_content_instructions)
        titleBar = findViewById(R.id.title_bar)

        layoutHaveAddress = findViewById(R.id.layout_have_address)
        layoutNoAddress = findViewById(R.id.layout_no_address)
        tvName = findViewById(R.id.tv_name)
        tvNumber = findViewById(R.id.tv_number)
        tvAddress = findViewById(R.id.tv_address)
        tvAddressTitle = findViewById(R.id.tv_address_title)

        tvMedalViewTitle = findViewById(R.id.tv_medal_preview)
        clMedalView = findViewById(R.id.cl_suit_preview)
        ivHead = findViewById(R.id.iv_head)
        tvUserName = findViewById(R.id.tv_user_name)
        tvUserDesc = findViewById(R.id.tv_user_desc)
        ivMedalPre = findViewById(R.id.iv_medal_preview)
    }

    override fun initListeners() {
        btnExchangeGood?.setOnClickListener {
            when (pageType) {
                SourceType.FROM_SCORE_INDEX.value -> {
                    exchangeGoods(userInfo?.userCredit ?: -1, paramData?.id)
                    paramData?.name?.let { goodName ->
                        UTHelper.commonEvent(
                            UTConstant.Score.IntegralP_click_ProductPage_RedeemNowButton,
                            "ProductName", goodName
                        )
                    }
                }
                SourceType.FROM_EXCHANGE_PAGE.value -> {
                    if (layoutHaveAddress!!.visibility == View.VISIBLE) {
                        viewModel.name = tvName?.text.toString()
                        viewModel.phone = tvNumber?.text.toString()
                        viewModel.address = tvAddress?.text.toString()
                        viewModel.chooseAddress()
                    }
                }
                SourceType.FROM_MINE_MEDAL_PAGE.value -> {
                    exchangeGoods(viewModel.scoreNum, viewModel.eventId)
                    // TODO: ??
//                    paramData?.name?.let { goodName ->
//                        UTHelper.commonEvent(
//                            UTConstant.Score.IntegralP_click_ProductPage_RedeemNowButton,
//                            "ProductName", goodName
//                        )
//                    }
                }
                else -> {
                }
            }
        }

        layoutNoAddress?.setOnShakeClickListener {
            DcRouter(AccountConstant.Uri.ADD_ADDRESS)
                .putExtra(
                    AccountConstant.ExtraParam.ADD_ADDRESS,
                    AccountConstant.ExtraParam.FROM_CONFIRM
                )
                .start()
        }
        layoutHaveAddress?.setOnShakeClickListener {
            DcRouter(AccountConstant.Uri.MY_ADDRESS)
                .putExtra(
                    AccountConstant.ExtraParam.MY_ADDRESS,
                    AccountConstant.ExtraParam.FROM_CONFIRM
                )
                .start()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun afterConfirmAddress(data: EventConfirmAddress) {
        tvName?.text = data.name
        tvNumber?.text = data.phone
        tvAddress?.text = data.address
        layoutHaveAddress?.hide(false)
        layoutNoAddress?.hide(true)
        upDateButtonState(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun moduleTag(): String {
        return ModuleConstant.MINE_MODULE
    }
}