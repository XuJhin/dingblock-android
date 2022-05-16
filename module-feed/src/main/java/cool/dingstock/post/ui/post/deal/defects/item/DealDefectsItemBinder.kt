package cool.dingstock.post.ui.post.deal.defects.item

import android.view.LayoutInflater
import android.view.ViewGroup
import cool.dingstock.appbase.adapter.itembinder.BaseViewBindingItemBinder
import cool.dingstock.appbase.constant.CircleConstant
import cool.dingstock.appbase.constant.MineConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.calendar.DealPostItemEntity
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.helper.IMHelper
import cool.dingstock.appbase.net.api.circle.CircleApi
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.lib_base.util.StringUtils
import cool.dingstock.post.dagger.PostApiHelper
import cool.dingstock.post.databinding.DealDefectsItemLayoutBinding
import javax.inject.Inject


/**
 * 类名：DealDefectsItemBinder
 * 包名：cool.dingstock.post.ui.post.deal.defects.item
 * 创建时间：2022/2/11 6:02 下午
 * 创建人： WhenYoung
 * 描述：
 **/
class DealDefectsItemBinder :
    BaseViewBindingItemBinder<DealPostItemEntity, DealDefectsItemLayoutBinding>() {

    @Inject
    lateinit var circleApi: CircleApi

    init {
        PostApiHelper.apiPostComponent.inject(this)
    }


    override fun provideViewBinding(
        parent: ViewGroup,
        viewType: Int
    ): DealDefectsItemLayoutBinding {
        return DealDefectsItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    var type: String = ""

    override fun onConvert(vb: DealDefectsItemLayoutBinding, data: DealPostItemEntity) {
        var imageUrl = ""
        if (data.images?.size ?: 0 > 0) {
            imageUrl = data.images?.get(0)?.thumbnail ?: ""
        }
        vb.productIv.load(imageUrl)
        vb.avatarIv.apply {
            setAvatarUrl(data.userMap?.avatarUrl)
            setPendantUrl(data.userMap?.avatarPendantUrl)
        }
        vb.userNameTv.text = data.userMap?.nickName
        vb.userNameTv.isSelected = data.userMap?.isVip == true
        vb.locationTv.text = data.senderAddress
        vb.locationLayer.hide(StringUtils.isEmpty(data.senderAddress))
        vb.priceTv.text = data.price
        vb.contactLayer.setOnShakeClickListener {
            data.id?.let { id ->
                circleApi.wantTrading(id).subscribe({
                    Logger.d(it.toString())
                }, {
                    Logger.d(it.message)
                })
            }
            if (LoginUtils.isLoginAndRequestLogin(context)) {
                data.userMap?.id?.let { id ->
                    UTHelper.commonEvent(
                        UTConstant.Deal.TransactionDetailsP_click_Chat,
                        "type",
                        type
                    )
                    IMHelper.routeToConversationActivity(context, 1, id)
                }
            }
        }
        vb.userNameTv.setOnShakeClickListener {
            data.userMap?.id?.let {
                routeToUserDetail(it)
            }
        }
        vb.avatarIv.setOnShakeClickListener {
            data.userMap?.id?.let {
                routeToUserDetail(it)
            }
        }
        vb.contactLayer.hide(LoginUtils.getCurrentUser()?.id == data.userMap?.id)
        vb.root.setOnShakeClickListener {
            UTHelper.commonEvent(
                UTConstant.Deal.TransactionDetailsP_click_TransactionList,
                "type",
                type
            )
            DcUriRequest(context, CircleConstant.Uri.CIRCLE_DETAIL)
                .putUriParameter(CircleConstant.UriParams.ID, data.id ?: "")
                .start()
        }
    }

    private fun routeToUserDetail(id: String) {
        DcUriRequest(context, MineConstant.Uri.DYNAMIC)
            .putUriParameter(MineConstant.PARAM_KEY.USER_DYNAMIC_PAGE, MineConstant.TRADING_PAGE)
            .putUriParameter(MineConstant.PARAM_KEY.ID, id)
            .start()
    }
}