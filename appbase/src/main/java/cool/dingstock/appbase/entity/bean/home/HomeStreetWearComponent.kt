package cool.dingstock.appbase.entity.bean.home;








data class HomeStreetWearComponent (
    var type  : String?,
    var data  : Object?,
    var header  : String?,

    var brandList  : List<HomeBrandBean>?,
    var groupList  : List<HomeStreetWearGroup>?,
    var recommendList  : List<HomeStreetWearRecommend>?
)
