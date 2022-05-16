package cool.dingstock.appbase.serviceloader

import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import cool.dingstock.appbase.entity.bean.circle.GoodsBean

interface ISelectGoodsDialogServer {
    fun show(
        fragmentManager: FragmentManager,
        showNoGoods: Boolean,
        seriesId: String?,
        seriesName: String?,
        onSelectGoods: (GoodsBean) -> Unit
    )
}