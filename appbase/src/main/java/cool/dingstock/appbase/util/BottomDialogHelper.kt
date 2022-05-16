package cool.dingstock.appbase.util

import androidx.fragment.app.FragmentManager
import com.sankuai.waimai.router.Router
import cool.dingstock.appbase.constant.CircleConstant
import cool.dingstock.appbase.constant.ShoesConstant
import cool.dingstock.appbase.entity.bean.circle.GoodsBean
import cool.dingstock.appbase.serviceloader.IAuthDialogServer
import cool.dingstock.appbase.serviceloader.ISelectGoodsDialogServer
import cool.dingstock.appbase.serviceloader.ITransactionEditDialogServer

fun FragmentManager.authDialogShow(
    seriesId: String? = null,
    seriesName: String? = null,
    seriesScore: Float = 0f,
    id: String? = null
) {
    Router.getService(
        IAuthDialogServer::class.java,
        ShoesConstant.ServerLoader.AUTH_DIALOG
    )?.show(this, seriesId, seriesName, seriesScore, id)
}

fun FragmentManager.selectGoodsDialogShow(
    showNoGoods: Boolean,
    seriesId: String?,
    seriesName: String?,
    onSelectGoods: (GoodsBean) -> Unit
) {
    Router.getService(
        ISelectGoodsDialogServer::class.java,
        CircleConstant.ServerLoader.SELECT_GOODS_DIALOG
    )?.show(this, showNoGoods, seriesId, seriesName, onSelectGoods)
}

fun FragmentManager.transactionEditDialogShow(
    goods: GoodsBean
) {
    Router.getService(
        ITransactionEditDialogServer::class.java,
        CircleConstant.ServerLoader.TRANSACTION_EDIT_DIALOG
    )?.show(this, goods)
}