package cool.dingstock.appbase.entity.bean.shoes

data class ShoesHomeBean(
    val brands: List<BrandBean>,
    val seriesInfo: SeriesBean
)

data class BrandBean(
    val id: String,
    val name: String,
    var selected: Boolean = false
)
