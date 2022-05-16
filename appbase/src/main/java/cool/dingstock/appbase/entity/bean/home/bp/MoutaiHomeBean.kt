package cool.dingstock.appbase.entity.bean.home.bp

data class MoutaiHomeBean(
    val categories: List<Category>?,
    val headerInfo: MoutaiHeaderInfo?,
    val products: Products?
)

data class Category(
    val category: String?,
    val isSelected: Boolean?,
    val title: String?
)

data class MoutaiHeaderInfo(
    val banners: List<Banners>?,
    val monitorMenuId: String?
)

data class Banners(
    val imgUrl: String?,
    val linkUrl: String?
)

data class Products(
    val list: List<MoutaiBean>?,
    val nextKey: Long?
)
