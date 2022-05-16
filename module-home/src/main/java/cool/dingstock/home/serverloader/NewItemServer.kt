package cool.dingstock.home.serverloader

import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.sankuai.waimai.router.annotation.RouterService
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.adapter.delegation.HolderReloadDelegation
import cool.dingstock.appbase.adapter.itembinder.DcBaseItemBinder
import cool.dingstock.appbase.constant.HomeConstant
import cool.dingstock.appbase.serviceloader.INewSaleItemServer
import cool.dingstock.home.adapter.item.NewSaleItem
import cool.dingstock.home.helper.NewSaleReload
import cool.dingstock.post.item.PostItemShowWhere

@RouterService(interfaces = [INewSaleItemServer::class],
    key = [HomeConstant.ServerLoader.NEW_SALE_ITEM])
class NewItemServer: INewSaleItemServer {
    override fun <T, VH : BaseViewHolder> getNewSale(
        needLimit: Boolean,
        postListener: (() -> Unit)?
    ): DcBaseItemBinder<T, VH> {
        return NewSaleItem().apply {
            maxLineLimit = needLimit
            if (!needLimit) {
                showWhere = PostItemShowWhere.Detail
            }
            this.postListener = postListener
        } as DcBaseItemBinder<T, VH>
    }

    override fun getReload(adapter: DcBaseBinderAdapter): HolderReloadDelegation {
        return NewSaleReload(adapter)
    }
}