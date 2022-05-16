package cool.dingstock.appbase.entity.bean.circle

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class GoodsListBean(
    val pageNum: Int,
    val list: List<GoodsBean>
)

@Parcelize
data class GoodsBean(
    val id: String,
    val name: String?,
    val sku: String?,
    val imageUrl: String?,
    val goodsType: String?
): Parcelable
