package cool.dingstock.uicommon.product.item

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.adapter.LoadMoreBinderAdapter
import cool.dingstock.appbase.adapter.itembinder.DcBaseItemBinder
import cool.dingstock.appbase.constant.*
import cool.dingstock.appbase.entity.bean.home.*
import cool.dingstock.appbase.ext.*
import cool.dingstock.appbase.net.api.account.AccountHelper
import cool.dingstock.appbase.net.api.calendar.CalendarHelper
import cool.dingstock.appbase.net.mobile.MobileHelper
import cool.dingstock.appbase.net.parse.ParseCallback
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.share.ShareParams
import cool.dingstock.appbase.share.ShareServiceHelper
import cool.dingstock.appbase.share.ShareType
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.ClipboardHelper
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.lib_base.json.JSONHelper
import cool.dingstock.lib_base.util.CollectionUtils
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.lib_base.util.ToastUtil
import cool.dingstock.uicommon.R

/**
 * @author wangjiang
 *  CreateAt Time 2021/7/1  11:33
 */

class HomeSmsRegistrationItemBinder :
    DcBaseItemBinder<SmsRegistrationBean, HomeSmsRegistrationItemBinder.HomeSmsRegistrationVH>() {

    interface ActionListener {
        fun onShareClick(name: String, imageUrl: String, smsRaffleBean: SmsRaffleBean?)
        fun onAlarmClick(name: String, imageUrl: String, smsRaffleBean: SmsRaffleBean?)
        fun onSmsClick(name: String, imageUrl: String, smsRaffleBean: SmsRaffleBean?)
        fun onSearchPriceClick(id: String?)
    }

    private var rvAdapter: LoadMoreBinderAdapter? = null
    var whiteList: List<String> = emptyList()
    private val rvItemBinder: HomeSmsRaffleDetailItemBinder by lazy {
        HomeSmsRaffleDetailItemBinder()
    }

    var mListener: ActionListener? = null
    private var isSneakStyle = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeSmsRegistrationVH {
        return HomeSmsRegistrationVH(
            LayoutInflater.from(context).inflate(R.layout.home_item_sms_registration, parent, false)
        )
    }

    private fun productAction(
        action: String,
        productId: String,
        holder: HomeSmsRegistrationVH,
        data: SmsRegistrationBean
    ) {
        if (null == AccountHelper.getInstance().user) {
            showToastShort("未登录")
            return
        }
        CalendarHelper.productAction(action, productId, object : ParseCallback<String> {
            override fun onSucceed(data0: String?) {
                Logger.d(
                    "productAction action=" + action
                            + " productId=" + productId + " success --"
                )
                updateProductView(productId, action, holder, data)
            }

            override fun onFailed(errorCode: String?, errorMsg: String?) {
                Logger.e(
                    "productAction action=" + action
                            + " productId=" + productId + " failed --"
                )
            }
        })
    }

    private fun updateProductView(
        productId: String,
        action: String,
        holder: HomeSmsRegistrationVH,
        data: SmsRegistrationBean
    ) {
        if (TextUtils.isEmpty(action) || TextUtils.isEmpty(productId)) {
            return
        }
        when (action) {
            ServerConstant.Action.LIKE -> refreshLike(true, data, holder)
            ServerConstant.Action.RETRIEVE_LIKE -> refreshLike(false, data, holder)
            ServerConstant.Action.DISLIKE -> refreshDisLike(true, data, holder)
            ServerConstant.Action.RETRIEVE_DISLIKE -> refreshDisLike(false, data, holder)
            else -> {

            }
        }
    }

    private fun refreshLike(
        isLike: Boolean,
        data: SmsRegistrationBean,
        holder: HomeSmsRegistrationVH
    ) {
        data.liked = isLike
        val likeCount = data.likeCount
        if (isLike) {
            data.likeCount = likeCount + 1
        } else {
            data.likeCount = likeCount - 1
        }
        if (data.disliked && isLike) {
            refreshDisLike(false, data, holder)
        }
        setLikeAndDislikeData(
            holder.likeIcon,
            holder.likeTxt,
            holder.disLikeIcon,
            holder.disLikeTxt,
            holder,
            data
        )
    }

    private fun refreshDisLike(
        isDisLike: Boolean,
        data: SmsRegistrationBean,
        holder: HomeSmsRegistrationVH
    ) {
        data.disliked = isDisLike
        val dislikeCount = data.dislikeCount
        if (isDisLike) {
            data.dislikeCount = dislikeCount + 1
        } else {
            data.dislikeCount = dislikeCount - 1
        }
        if (data.liked && isDisLike) {
            refreshLike(false, data, holder)
        }
        setLikeAndDislikeData(
            holder.likeIcon,
            holder.likeTxt,
            holder.disLikeIcon,
            holder.disLikeTxt,
            holder,
            data
        )
    }

    private fun setLikeAndDislikeData(
        productLikeIcon: ImageView,
        productLikeTxt: TextView,
        productDislikeIcon: ImageView,
        productDislikeTxt: TextView,
        holder: HomeSmsRegistrationVH, data: SmsRegistrationBean
    ) {
        productLikeTxt.text = data.likeCount.toString()
        productLikeIcon.isSelected = data.liked
        holder.likeTxt.isSelected = data.liked
        holder.disLikeTxt.isSelected = data.disliked
        //喜欢
        holder.likeLayer.setOnClickListener {
            if (!data.liked) {
                productAction(ServerConstant.Action.LIKE, data.objectId!!, holder, data)
            } else {
                productAction(ServerConstant.Action.RETRIEVE_LIKE, data.objectId!!, holder, data)
            }
        }

        productDislikeTxt.text = data.dislikeCount.toString()
        productDislikeIcon.isSelected = data.disliked
        //不喜欢
        holder.disLikeLayer.setOnClickListener {
            if (!data.disliked) {
                productAction(ServerConstant.Action.DISLIKE, data.objectId!!, holder, data)
            } else {
                productAction(ServerConstant.Action.RETRIEVE_DISLIKE, data.objectId!!, holder, data)
            }
        }
    }

    override fun onConvert(holder: HomeSmsRegistrationVH, data: SmsRegistrationBean) {
        holder.apply {
            productIv.load(data.imageUrl)
            nameTxt.text = data.name
            val price = data.price
            if (TextUtils.isEmpty(price) || "null" == price) {
                priceTxt.text = "-"
            } else {
                "$price".also { priceTxt.text = it }
            }
            infoTxt.text = "${data.raffleCount}条"

            marketPriceLayer.setOnShakeClickListener {
                mListener?.onSearchPriceClick(data.id)
            }
            marketPriceTv.text = if (data.marketPrice.isNullOrEmpty()) "-" else data.marketPrice

            commentTxt.text = data.commentCount.toString()
            //点赞与点踩
            setLikeAndDislikeData(likeIcon, likeTxt, disLikeIcon, disLikeTxt, this, data)
            //分享
            shareLayer.setOnClickListener {
                val path = "pages/start/start?detailId=" + data.objectId
                val type = ShareType.Mp
                val params = ShareParams()
                params.title = data.name + "有新的发售信息"
                params.content = data.name + "有新的发售信息"
                params.imageUrl = data.imageUrl
                params.mpPath = path

                type.params = params
                ShareServiceHelper.share(context, type)
            }
            //评论
            commentLayer.setOnClickListener {
                DcUriRequest(context, HomeConstant.Uri.REGION_COMMENT)
                    .dialogBottomAni()
                    .putUriParameter(CircleConstant.UriParams.ID, data.objectId)
                    .putExtra(CalendarConstant.DataParam.KEY_PRODUCT, JSONHelper.toJson(data.sketch()))
                    .start()
            }
            if (data.dealCount == null || data.dealCount == 0) {
                marketDealLayer.visibility = View.GONE
                viewSpace.visibility = View.GONE
            } else {
                viewSpace.visibility = View.VISIBLE
                marketDealLayer.visibility = View.VISIBLE
                dealTv.text = data.dealCount.toString().plus("条")
            }
            marketDealLayer.setOnClickListener {
//                DcUriRequest(context,CircleConstant.Uri.DEAL_DETAILS)
                DcUriRequest(context,CircleConstant.Uri.DEAL_DETAILS)
                    .putUriParameter(CircleConstant.UriParams.ID,data.objectId)
                    .start()
//                UTHelper.commonEvent(UTConstant.Home.SMSLottery_click_transaction)
//                DcUriRequest(context, CircleConstant.Uri.FIND_TOPIC_DETAIL)
//                    .putUriParameter(CircleConstant.UriParams.PRODUCT_ID, data.objectId)
//                    .putUriParameter(CircleConstant.UriParams.KEYWORD, data.name)
//                    .putUriParameter(
//                        CircleConstant.UriParams.TOPIC_DETAIL_ID,
//                        MobileHelper.getInstance().configData.dealTalkId
//                    )
//                    .start()
            }

            //展开的发售详情
            setDetailRvData(data.raffles, this, data)
            //root
            if (isSneakStyle) {
                rootLayer.setOnClickListener(null)
            } else {
                homeItemDetailsFra.hide(!data.isExpose)
                rootLayer.setOnClickListener {
                    if (homeItemDetailsFra.visibility == View.VISIBLE) {
                        homeItemDetailsFra.visibility = View.GONE
                        data.isExpose = false
                    } else {
                        homeItemDetailsFra.visibility = View.VISIBLE
                        data.isExpose = true
                    }
                }
            }
        }
    }

    private fun setDetailRvData(
        homeRaffleList: List<SmsRaffleBean>,
        holder: HomeSmsRegistrationVH,
        data: SmsRegistrationBean
    ) {
        if (CollectionUtils.isEmpty(homeRaffleList)) {
            rvAdapter?.setList(emptyList())
            rvAdapter = null
            return
        }
        rvAdapter = LoadMoreBinderAdapter()

        rvItemBinder.mListener = object : HomeSmsRaffleDetailItemBinder.ActionListener {
            override fun onShareClick(smsRaffleBean: SmsRaffleBean?) {
                mListener?.onShareClick(data.name ?: "", data.imageUrl ?: "", smsRaffleBean)
            }

            override fun onAlarmClick(smsRaffleBean: SmsRaffleBean?) {
                mListener?.onAlarmClick(data.name ?: "", data.imageUrl ?: "", smsRaffleBean)
            }

            override fun onSmsClick(smsRaffleBean: SmsRaffleBean?) {
                mListener?.onSmsClick(data.name ?: "", data.imageUrl ?: "", smsRaffleBean)
            }
        }

        rvAdapter!!.addItemBinder(SmsRaffleBean::class.java, rvItemBinder)
        holder.detailRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = rvAdapter
        }
        holder.detailGoodNumber.text = "货号   ".plus(data.sku)
        holder.detailGoodNumber.setOnLongClickListener {
            data.sku?.let { it1 ->
                ClipboardHelper.copyMsg(context, it1) {
                    showToastShort("货号已复制")
                }
            }
            true
        }
        val list: List<SmsRaffleBean>
        list = data.raffles
        list[0].isStart = true
        list[list.size - 1].isLast = true
        rvAdapter!!.setList(list)
        rvItemBinder.whiteList = whiteList
    }


    class HomeSmsRegistrationVH(view: View) : BaseViewHolder(view) {
        val rootLayer: RelativeLayout = view.findViewById(R.id.home_item_region_raffle_root)
        val productIv: ImageView = view.findViewById(R.id.home_item_region_raffle_product_iv)
        val nameTxt: TextView = view.findViewById(R.id.home_item_region_raffle_product_name_txt)
        val infoTxt: TextView = view.findViewById(R.id.home_item_region_raffle_info_txt)
        val priceTxt: TextView = view.findViewById(R.id.home_item_region_raffle_product_price_txt)
        val commentLayer: LinearLayout =
            view.findViewById(R.id.home_item_region_raffle_comment_layer)
        val commentTxt: TextView = view.findViewById(R.id.home_item_region_raffle_comment_txt)
        val likeLayer: LinearLayout = view.findViewById(R.id.home_item_region_raffle_like_layer)
        val likeTxt: TextView = view.findViewById(R.id.home_item_region_raffle_like_txt)
        val likeIcon: ImageView = view.findViewById(R.id.home_item_region_raffle_like_icon)
        val disLikeLayer: LinearLayout =
            view.findViewById(R.id.home_item_region_raffle_dislike_layer)
        val disLikeTxt: TextView = view.findViewById(R.id.home_item_region_raffle_dislike_txt)
        val disLikeIcon: ImageView = view.findViewById(R.id.home_item_region_raffle_dislike_icon)
        val shareLayer: LinearLayout =
            view.findViewById(R.id.home_item_region_raffle_product_share_layer)
        val homeItemDetailsFra: View = view.findViewById(R.id.home_item_region_raffle_detail_fra)
        val detailRv: RecyclerView = view.findViewById(R.id.home_item_region_raffle_detail_rv)
        val detailGoodNumber: TextView = view.findViewById(R.id.tv_good_number)
        val marketPriceLayer: View = view.findViewById(R.id.market_price_layer)
        val marketPriceTv: TextView = view.findViewById(R.id.market_price_tv)

        val viewSpace: View = view.findViewById(R.id.view_space)
        val marketDealLayer: View = view.findViewById(R.id.market_deal_layer)
        val dealTv: TextView = view.findViewById(R.id.deal_tv)
    }
}

