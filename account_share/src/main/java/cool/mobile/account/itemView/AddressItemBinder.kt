package cool.mobile.account.itemView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.adapter.itembinder.DcBaseItemBinder
import cool.dingstock.appbase.entity.bean.shop.UserAddressEntity
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.mobile.account.R

/**
 * @author wangjiang
 *  CreateAt Time 2021/7/29  11:05
 */

class AddressItemBinder : DcBaseItemBinder<UserAddressEntity, AddressItemBinder.AddressVH>() {
    interface ActionListener {
        fun onClickItem(entity: UserAddressEntity)
        fun onClickEditAddress(entity: UserAddressEntity)
        fun onClickDeleteAddress(id: String)
        fun onClickSwitchDefaultMode(entity: UserAddressEntity)
    }

    var mListener: ActionListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressVH {
        return AddressVH(LayoutInflater.from(context).inflate(R.layout.user_address_item, parent, false))
    }

    override fun onConvert(holder: AddressVH, data: UserAddressEntity) {
        holder.apply {
            mainAddress.text = data.province.plus(data.city).plus(data.district)
            localAddress.text = data.address
            if (data.name.length >= 14) {
                contactMessage.text = data.name.plus("\n").plus(data.mobileZone).plus(" ").plus(data.mobile)
            } else {
                contactMessage.text = data.name.plus("   ").plus(data.mobileZone).plus(" ").plus(data.mobile)
            }

            if (data.isDefault) {
                defaultView.setImageResource(R.drawable.icon_choose)
                defaultView.isClickable = false
            } else {
                defaultView.setImageResource(R.drawable.icon_unchoose)
                defaultView.isClickable = true
            }
            editAddress.setOnShakeClickListener {
                mListener?.onClickEditAddress(data)
            }
            removeAddress.setOnShakeClickListener {
                data.id?.let { it1 -> mListener?.onClickDeleteAddress(it1) }
            }
            defaultView.setOnShakeClickListener {
                defaultView.setImageResource(R.drawable.icon_choose)
                defaultView.isClickable = false
                data.isDefault = true
                mListener?.onClickSwitchDefaultMode(data)
            }
            item.setOnShakeClickListener {
                mListener?.onClickItem(data)
            }
        }
    }

    class AddressVH(view: View) : BaseViewHolder(view) {
        val item: ConstraintLayout = view.findViewById(R.id.cl_address_item)
        val mainAddress: TextView = view.findViewById(R.id.tv_main_address)
        val localAddress: TextView = view.findViewById(R.id.tv_location_address)
        val contactMessage: TextView = view.findViewById(R.id.tv_contact_message)
        val defaultView: ImageView = view.findViewById(R.id.iv_address_chooseed)
        val editAddress: ImageView = view.findViewById(R.id.iv_edit_address)
        val removeAddress: ImageView = view.findViewById(R.id.iv_remove_address)
    }
}