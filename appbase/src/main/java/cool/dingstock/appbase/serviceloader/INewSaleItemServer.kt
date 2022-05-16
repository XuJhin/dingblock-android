package cool.dingstock.appbase.serviceloader

import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.adapter.delegation.HolderReloadDelegation
import cool.dingstock.appbase.adapter.itembinder.DcBaseItemBinder
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicBean

interface INewSaleItemServer {
    fun <T, VH: BaseViewHolder>getNewSale(needLimit: Boolean, postListener: (() -> Unit)? = null): DcBaseItemBinder<T, VH>
    fun getReload(adapter: DcBaseBinderAdapter): HolderReloadDelegation
}