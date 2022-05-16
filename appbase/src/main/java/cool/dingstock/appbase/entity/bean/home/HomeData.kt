package cool.dingstock.appbase.entity.bean.home;


data class HomeData(
    var categories: ArrayList<HomeCategoryBean>?,
    var items: HomeItem?,
    var fab: HomeFabBean?,
    var extraTabs: ArrayList<HomePostExtraTabBean>?,
    var activity: HomeActivityEntity? = null,
    var posts: HomePostData?,
    var isSign: Boolean = false,
    var funcItem1: FuncItemEntity?,
    var funcItem2: FuncItemEntity?,
    var funcItem3: FuncItemEntity?,
    var boxItem: BoxItemEntity?,
    var creditItem: CreditItemEntity?,
)

data class CreditItemEntity(val id: String?, val imageUrl: String?, val targetUrl: String?) {

}

data class BoxItemEntity(
    val id: String,
    val imageUrl: String,
    val targetUrl: String,

    ) {}

data class FuncItemEntity(
    val id: String,
    val imageUrl: String,
    val targetUrl: String,
    val title: String?,
    val subtitle: String?
)

data class HomeActivityEntity(
    val linkUrl: String? = null,
    val imgUrl: String? = null
) {

}





