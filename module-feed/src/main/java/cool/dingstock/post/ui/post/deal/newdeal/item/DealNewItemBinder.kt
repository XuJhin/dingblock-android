package cool.dingstock.post.ui.post.deal.newdeal.item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
import cool.dingstock.lib_base.util.TimeUtils
import cool.dingstock.post.R
import cool.dingstock.post.dagger.PostApiHelper
import cool.dingstock.post.databinding.PostDetailsNewItemLayoutBinding
import javax.inject.Inject


/**
 * 类名：DealNewItemBinder
 * 包名：cool.dingstock.post.ui.post.deal.new.item
 * 创建时间：2022/2/11 4:26 下午
 * 创建人： WhenYoung
 * 描述：
 **/
class DealNewItemBinder :
    BaseViewBindingItemBinder<DealPostItemEntity, PostDetailsNewItemLayoutBinding>() {

    @Inject
    lateinit var circleApi:CircleApi

    init {
        PostApiHelper.apiPostComponent.inject(this)
    }

    override fun provideViewBinding(
        parent: ViewGroup,
        viewType: Int
    ): PostDetailsNewItemLayoutBinding {
        return PostDetailsNewItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    var type: String = ""

    override fun onConvert(vb: PostDetailsNewItemLayoutBinding, data: DealPostItemEntity) {
        vb.avatarIv.apply {
            setAvatarUrl(data.userMap?.avatarUrl)
            setPendantUrl(data.userMap?.avatarPendantUrl)
        }
        vb.userNameTxt.text = data.userMap?.nickName
        vb.userNameTxt.setTextColor(if (data.userMap?.isVip == true) ContextCompat.getColor(context, R.color.colorTextVip) else
            ContextCompat.getColor(context, R.color.common_grey_blue_txt_color))
        vb.ivMedal.load(data.userMap?.achievementIconUrl)
        vb.ivMedal.hide(StringUtils.isEmpty(data.userMap?.achievementIconUrl))
        vb.publishTimeTv.text =  TimeUtils.formatTimestampS2NoYear(data.createdAt ?: 0)
        vb.locationTv.text = data.senderAddress
        vb.priceTv.text = data.price
        vb.descTv.text = data.content
        vb.contactIv.setOnShakeClickListener {
            data.id?.let {id->
                circleApi.wantTrading(id).subscribe({
                    Logger.d(it.toString())
                }, {
                    Logger.d(it.message)
                })
            }
            if(LoginUtils.isLoginAndRequestLogin(context)){
                data.userMap?.id?.let { id ->
                    UTHelper.commonEvent(UTConstant.Deal.TransactionDetailsP_click_Chat, "type", type)
                    IMHelper.routeToConversationActivity(context, 1, id)
                }
            }
        }
        vb.userNameTxt.setOnShakeClickListener {
            data.userMap?.id?.let {
                routeToUserDetail(it)
            }
        }
        vb.ivMedal.setOnShakeClickListener {
            DcUriRequest(context, MineConstant.Uri.MEDAL_DETAIL)
                .putUriParameter(MineConstant.PARAM_KEY.ID, data.userMap?.id)
                .putUriParameter(MineConstant.PARAM_KEY.MEDAL_ID, data.userMap?.achievementId)
                .start()
        }
        vb.avatarIv.setOnShakeClickListener {
            data.userMap?.id?.let {
                routeToUserDetail(it)
            }
        }
        vb.contactIv.hide(LoginUtils.getCurrentUser()?.id == data.userMap?.id)
        vb.root.setOnShakeClickListener {
            UTHelper.commonEvent(UTConstant.Deal.TransactionDetailsP_click_TransactionList,"type",type)
            DcUriRequest(context, CircleConstant.Uri.CIRCLE_DETAIL)
                .putUriParameter(CircleConstant.UriParams.ID, data.id ?: "")
                .start()
        }
    }

    private fun routeToUserDetail(id: String) {
        DcUriRequest(context, MineConstant.Uri.DYNAMIC)
            .putUriParameter(MineConstant.PARAM_KEY.ID, id)
            .putUriParameter(MineConstant.PARAM_KEY.USER_DYNAMIC_PAGE, MineConstant.TRADING_PAGE)
            .start()
    }
}