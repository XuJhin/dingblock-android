package cool.dingstock.mine.itemView

import android.net.Uri
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.binder.BaseItemBinder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.mine.VipPrivilegeEntity
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.mine.R

class VipPrivilegeItemBinder : BaseItemBinder<VipPrivilegeEntity, VipPrivilegeViewHolder>() {
    override fun convert(holder: VipPrivilegeViewHolder, data: VipPrivilegeEntity) {

        holder.ivState.hide(data.cornerIcon.isNullOrEmpty())
        if (!data.cornerIcon.isNullOrEmpty()) {
            holder.ivState.load(data.cornerIcon)
        }

        holder.img.load(data.imageUrl)
        holder.tv.text = data.name
        holder.itemView.setOnClickListener {
            UTHelper.commonEvent(UTConstant.Pay.VipP_click_Prerogative)

            data.forwardUrl?.let {
                if (!TextUtils.isEmpty(it)) {
                    try {
                        if (it.contains("musicMember")) {
                            UTHelper.commonEvent(
                                UTConstant.Mine.EnterSource_Musicvip,
                                "type",
                                if (LoginUtils.getCurrentUser()
                                        ?.isVip() == true
                                ) "会员" else "非会员",
                                "source",
                                "会员中心icon"
                            )
                        }
//                        Uri.parse(it).queryParameterNames.forEach { type ->
//                            if (type == "musicMember") {
//                                UTHelper.commonEvent(
//                                    UTConstant.Mine.EnterSource_Musicvip,
//                                    "type",
//                                    if (LoginUtils.getCurrentUser()
//                                            ?.isVip() == true
//                                    ) "会员" else "非会员",
//                                    "source",
//                                    "会员中心icon"
//                                )
//                            }
//                        }
                        DcUriRequest(context, it).start()
                    } catch (e: Exception) {
                        DcUriRequest(context, it).start()
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VipPrivilegeViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.vip_privilege_item_layout, parent, false)
        return VipPrivilegeViewHolder(view)
    }

}


class VipPrivilegeViewHolder(view: View) : BaseViewHolder(view) {
    val img: ImageView = view.findViewById(R.id.img)
    val ivState: ImageView = view.findViewById(R.id.iv_state)
    val tv: TextView = view.findViewById(R.id.tv)
}