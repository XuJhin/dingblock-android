package cool.dingstock.appbase.entity.bean.shoes

import com.google.gson.annotations.SerializedName
import cool.dingstock.appbase.entity.bean.account.DcLoginUser
import java.util.ArrayList

data class SeriesBean(
    val header: Header,
    val sections: List<Section>
)

data class Header(
    val imageUrl: String
)

data class Section(
    val sectionTitle: String,
    val series: List<Series>
)

data class Series(
    val id: String,
    val imageUrl: String,
    val name: String,
    val sku: String? = ""
)

data class ShoesSeriesListEntity(
    val title: String? = "",
    @SerializedName(value = "data", alternate = ["list"])
    val data: List<ShoesSeriesEntity>,
    val nextKey: Long?
)

data class ShoesSeriesEntity(
    val year: String,
    val month: String,
    val products: List<Series>
)

data class DealSelectedEntity(
    val id: String,
    val imageUrl: String,
    val name: String,
    val dealCount: Int,
    val blocked: Boolean
)

data class DealSelectedListEntity(
    val list: List<DealSelectedEntity>,
    val nextKey: Long
)

data class AllPicEntity(
    val title: String? = "",
    val showNickname: Boolean? = false,
    val images: ArrayList<RealPicEntity>? = arrayListOf(),
    val nextKey: Long?,
)

data class RealPicEntity(
    val url: String? = "",
    val thumbnail: String? = "",
    val format: String? = "",
    val width: Int = 0,
    val height: Int = 0,
    val user: DcLoginUser? = null,
    var index: Int = 0
)

data class MorePicEntity(
    val list: ArrayList<RealPicEntity>,
    val nextKey: Long?
)
