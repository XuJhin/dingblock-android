package cool.dingstock.uicommon.product.item

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import cool.dingstock.appbase.constant.*
import cool.dingstock.appbase.entity.bean.home.HomeProduct
import cool.dingstock.appbase.entity.bean.home.HomeRaffle
import cool.dingstock.appbase.entity.bean.home.HomeRegionRaffleBean
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.net.api.account.AccountHelper
import cool.dingstock.appbase.net.api.calendar.CalendarHelper.productAction
import cool.dingstock.appbase.net.parse.ParseCallback
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.share.ShareParams
import cool.dingstock.appbase.share.ShareServiceHelper.share
import cool.dingstock.appbase.share.ShareType
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.widget.recyclerview.adapter.BaseRVAdapter
import cool.dingstock.appbase.widget.recyclerview.item.BaseItem
import cool.dingstock.appbase.widget.recyclerview.item.BaseViewHolder
import cool.dingstock.lib_base.json.JSONHelper
import cool.dingstock.lib_base.util.CollectionUtils
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.lib_base.util.StringUtils
import cool.dingstock.lib_base.util.ToastUtil
import cool.dingstock.uicommon.R
import cool.dingstock.uicommon.databinding.HomeItemRegionRaffleBinding

class HomeRegionRaffleItem(data: HomeRegionRaffleBean?) :
    BaseItem<HomeRegionRaffleBean, HomeItemRegionRaffleBinding>(data) {
    var utEventId = ""
    var utActionId = ""
    var utJumpEventId = ""
    private var rvAdapter: BaseRVAdapter<HomeRaffleDetailItem>? = null

    interface ActionListener {
        fun onShareClick(homeProduct: HomeProduct?, homeRaffle: HomeRaffle)
        fun onSmsClick(homeProduct: HomeProduct?, homeRaffle: HomeRaffle)
        fun onAlarmClick(homeProduct: HomeProduct?, homeRaffle: HomeRaffle, pos: Int)
        fun onFlashClick(homeProduct: HomeProduct?, homeRaffle: HomeRaffle)
        fun onSearchPriceClick(homeProduct: HomeProduct?)
    }

    private var mListener: ActionListener? = null
    fun setActionListener(param: ActionListener?) {
        mListener = param
    }

    private var isSneakStyle = false
    fun setSneakStyle(sneakStyle: Boolean) {
        isSneakStyle = sneakStyle
    }

    override fun getViewType(): Int {
        return 0
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.home_item_region_raffle
    }

    override fun onReleaseViews(
        holder: BaseViewHolder,
        sectionKey: Int,
        sectionViewPosition: Int
    ) {
    }

    @SuppressLint("SetTextI18n")
    override fun onSetViewsData(holder: BaseViewHolder, sectionKey: Int, sectionViewPosition: Int) {
        data?.let { data ->
            val header = data.header
            viewBinding?.let { viewBinding ->
                viewBinding.homeItemRegionRaffleProductIv.load(header?.imageUrl)
                viewBinding.homeItemRegionRaffleProductNameTxt.text = header?.name
                val price = header?.price
                if (TextUtils.isEmpty(price) || "null".equals(price, ignoreCase = true)) {
                    viewBinding.homeItemRegionRaffleProductPriceTxt.text = "未知"
                } else {
                    viewBinding.homeItemRegionRaffleProductPriceTxt.text = price
                }
                viewBinding.homeItemRegionRaffleInfoTxt.text = header?.raffleCount.toString() + "条"
                if (StringUtils.isEmpty(header?.marketPrice)) {
                    viewBinding.marketPriceTv.text = "-       "
                } else {
                    viewBinding.marketPriceTv.text = header?.marketPrice
                }
                if (header?.dealCount == null || header.dealCount == 0) {
                    viewBinding.marketDealLayer.visibility = View.GONE
                    viewBinding.viewSpace.visibility = View.GONE
                } else {
                    viewBinding.viewSpace.visibility = View.VISIBLE
                    viewBinding.marketDealLayer.visibility = View.VISIBLE
                    viewBinding.dealTv.text = header.dealCount.toString() + "条"
                }
                viewBinding.marketDealLayer.setOnClickListener {
                    DcUriRequest(context, CircleConstant.Uri.DEAL_DETAILS)
                        .putUriParameter(CircleConstant.UriParams.ID, header?.objectId)
                        .start()
                }
                viewBinding.marketPriceLayer.setOnClickListener {
                    mListener!!.onSearchPriceClick(header)
                    UTHelper.commonEvent(UTConstant.Home.FocusAreaP_click_price)
                }
                viewBinding.homeItemRegionRaffleCommentAllLayer.homeItemRegionRaffleCommentTxt.text =
                    header?.commentCount.toString()
                setLikeAndDislikeData(
                    viewBinding.homeItemRegionRaffleCommentAllLayer.homeItemRegionRaffleLikeIcon,
                    viewBinding.homeItemRegionRaffleCommentAllLayer.homeItemRegionRaffleLikeTxt,
                    viewBinding.homeItemRegionRaffleCommentAllLayer.homeItemRegionRaffleDislikeIcon,
                    viewBinding.homeItemRegionRaffleCommentAllLayer.homeItemRegionRaffleDislikeTxt
                )
                //分享
                viewBinding.homeItemRegionRaffleCommentAllLayer.homeItemRegionRaffleProductShareLayer.setOnClickListener { v: View? ->
                    UTHelper.commonEvent(utEventId)
                    UTHelper.commonEvent(utActionId, "ActionName", "点击分享")
                    val path = "pages/start/start?detailId=" + header?.objectId
                    val type = ShareType.Mp
                    val params = ShareParams()
                    params.title = header?.name + "有新的发售信息"
                    params.content = header?.name + "有新的发售信息"
                    params.imageUrl = header?.imageUrl
                    params.mpPath = path
                    type.params = params
                    share(context, type)
                }
                //评论
                viewBinding.homeItemRegionRaffleCommentAllLayer.homeItemRegionRaffleCommentLayer.setOnClickListener { v: View? ->
                    Router(HomeConstant.Uri.REGION_COMMENT)
                        .dialogBottomAni()
                        .putUriParameter(CircleConstant.UriParams.ID, header?.objectId)
                        .putExtra(CalendarConstant.DataParam.KEY_PRODUCT, JSONHelper.toJson(header))
                        .start()
                    UTHelper.commonEvent(utEventId)
                }
                if (!data.data.isNullOrEmpty()) {
                    viewBinding.brandLogoIv.load(data.data!![0].brand?.imageUrl)
                    viewBinding.timeTv.text = data.data!![0].releaseDateString
                }
                //展开的发售详情
                setDetailRvData(data.data)
                //root
                if (isSneakStyle) {
                    viewBinding.homeItemRegionRaffleRoot.setOnClickListener(null)
                } else {
                    viewBinding.homeItemRegionRaffleDetailFra.isVisible = data.isExpose
                    viewBinding.homeItemRegionRaffleDetailShort.isVisible = !data.isExpose
                    viewBinding.homeItemRegionRaffleRoot.setOnClickListener {
                        UTHelper.commonEvent(UTConstant.Home.CLICK_GOOD_LIST)
                        val isExpose =
                            viewBinding.homeItemRegionRaffleDetailFra.visibility == View.GONE
                        data.isExpose = isExpose
                        viewBinding.homeItemRegionRaffleDetailFra.isVisible = data.isExpose
                        viewBinding.homeItemRegionRaffleDetailShort.isVisible = !data.isExpose
                    }
                }
            }
        }
    }

    private fun setDetailRvData(data: List<HomeRaffle>?) {
        if (CollectionUtils.isEmpty(data)) {
            if (null != rvAdapter) {
                rvAdapter!!.clearAllSectionViews()
                rvAdapter = null
            }
            return
        }
        viewBinding?.let { viewBinding ->
            rvAdapter = BaseRVAdapter()
            viewBinding.homeItemRegionRaffleDetailRv.layoutManager = LinearLayoutManager(
                context
            )
            viewBinding.homeItemRegionRaffleDetailRv.adapter = rvAdapter
            val itemList: MutableList<HomeRaffleDetailItem> = ArrayList()
            for (i in data!!.indices) {
                val homeRaffle = data[i]
                val detailItem = HomeRaffleDetailItem(homeRaffle)
                detailItem.setSource("关注地区")
                detailItem.otherActionUtEventId = utActionId
                detailItem.setSource("关注地区")
                detailItem.setLast(i == data.size - 1)
                detailItem.setStart(i == 0)
                detailItem.setShowWhere(HomeRaffleDetailItem.ShowWhere.REGION_ITEM)
                detailItem.setMListener(object : HomeRaffleDetailItem.ActionListener {
                    override fun onShareClick(homeRaffle: HomeRaffle) {
                        mListener?.onShareClick(getData()?.header, homeRaffle)
                    }

                    override fun onSmsClick(homeRaffle: HomeRaffle) {
                        mListener?.onSmsClick(getData()?.header, homeRaffle)
                    }

                    override fun onAlarmClick(homeRaffle: HomeRaffle, pos: Int) {
                        mListener?.onAlarmClick(getData()?.header, homeRaffle, pos)
                    }

                    override fun onFlashClick(homeRaffle: HomeRaffle) {
                        mListener?.onFlashClick(getData()?.header, homeRaffle)
                    }
                })
                itemList.add(detailItem)
            }
            rvAdapter!!.setItemViewList(itemList)
        }
    }

    private fun productAction(action: String, productId: String?) {
        if (null == AccountHelper.getInstance().user) {
            showToastShort("未登录")
            return
        }
        productAction(action, productId, object : ParseCallback<String> {
            override fun onSucceed(data: String) {
                Logger.d(
                    "productAction action=" + action
                            + " productId=" + productId + " success --"
                )
                if (null == holder || null == holder.getItemView()) {
                    return
                }
                updateProductView(productId, action)
            }

            override fun onFailed(errorCode: String, errorMsg: String) {
                Logger.e(
                    "productAction action=" + action
                            + " productId=" + productId + " failed --"
                )
            }
        })
    }

    private fun setLikeAndDislikeData(
        productLikeIcon: ImageView,
        productLikeTxt: TextView,
        productDislikeIcon: ImageView,
        productDislikeTxt: TextView
    ) {
        data?.header?.let { homeProduct ->
            viewBinding?.let { viewBinding ->
                productLikeTxt.text = homeProduct.likeCount.toString()
                productLikeIcon.isSelected = homeProduct.liked
                viewBinding.homeItemRegionRaffleCommentAllLayer.homeItemRegionRaffleLikeTxt.isSelected =
                    homeProduct.liked
                viewBinding.homeItemRegionRaffleCommentAllLayer.homeItemRegionRaffleDislikeTxt.isSelected =
                    homeProduct.disliked
                viewBinding.homeItemRegionRaffleCommentAllLayer.homeItemRegionRaffleLikeLayer.setOnClickListener { v: View? ->
                    UTHelper.commonEvent(utEventId)
                    productAction(
                        if (!homeProduct.liked) ServerConstant.Action.LIKE else ServerConstant.Action.RETRIEVE_LIKE,
                        homeProduct.objectId
                    )
                }
                productDislikeTxt.text = homeProduct.dislikeCount.toString()
                productDislikeIcon.isSelected = homeProduct.disliked
                viewBinding.homeItemRegionRaffleCommentAllLayer.homeItemRegionRaffleDislikeLayer.setOnClickListener { v: View? ->
                    UTHelper.commonEvent(utEventId)
                    productAction(
                        if (!homeProduct.disliked) ServerConstant.Action.DISLIKE else ServerConstant.Action.RETRIEVE_DISLIKE,
                        homeProduct.objectId
                    )
                }
            }
        }

    }

    private fun updateProductView(productId: String?, action: String) {
        if (TextUtils.isEmpty(action) || TextUtils.isEmpty(productId)) {
            return
        }
        when (action) {
            ServerConstant.Action.LIKE -> refreshLike(true)
            ServerConstant.Action.RETRIEVE_LIKE -> refreshLike(false)
            ServerConstant.Action.DISLIKE -> refreshDisLike(true)
            ServerConstant.Action.RETRIEVE_DISLIKE -> refreshDisLike(false)
            else -> {
            }
        }
    }

    private fun refreshLike(isLike: Boolean) {
        data?.header?.let { header ->
            viewBinding?.let { viewBinding ->
                header.liked = isLike
                var likeCount = header.likeCount
                header.likeCount = if (isLike) ++likeCount else --likeCount
                if (header.disliked && isLike) {
                    refreshDisLike(false)
                }
                setLikeAndDislikeData(
                    viewBinding.homeItemRegionRaffleCommentAllLayer.homeItemRegionRaffleLikeIcon,
                    viewBinding.homeItemRegionRaffleCommentAllLayer.homeItemRegionRaffleLikeTxt,
                    viewBinding.homeItemRegionRaffleCommentAllLayer.homeItemRegionRaffleDislikeIcon,
                    viewBinding.homeItemRegionRaffleCommentAllLayer.homeItemRegionRaffleDislikeTxt
                )
            }
        }
    }

    private fun refreshDisLike(isDisLike: Boolean) {
        data?.header?.let { header ->
            viewBinding?.let { viewBinding ->
                header.disliked = isDisLike
                var dislikeCount = header.dislikeCount
                header.dislikeCount = if (isDisLike) ++dislikeCount else --dislikeCount
                if (header.liked && isDisLike) {
                    refreshLike(false)
                }
                setLikeAndDislikeData(
                    viewBinding.homeItemRegionRaffleCommentAllLayer.homeItemRegionRaffleLikeIcon,
                    viewBinding.homeItemRegionRaffleCommentAllLayer.homeItemRegionRaffleLikeTxt,
                    viewBinding.homeItemRegionRaffleCommentAllLayer.homeItemRegionRaffleDislikeIcon,
                    viewBinding.homeItemRegionRaffleCommentAllLayer.homeItemRegionRaffleDislikeTxt
                )
            }
        }
    }
}