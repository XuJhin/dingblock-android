package cool.dingstock.appbase.serviceloader

import androidx.fragment.app.FragmentManager
import cool.dingstock.appbase.entity.bean.circle.GoodsBean

interface ITransactionEditDialogServer {
    fun show(
        fragmentManager: FragmentManager,
        goods: GoodsBean
    )
}