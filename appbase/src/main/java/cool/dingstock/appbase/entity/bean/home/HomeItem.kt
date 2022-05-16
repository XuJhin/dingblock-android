package cool.dingstock.appbase.entity.bean.home;

data class HomeItem  (
    var bannerItems  : ArrayList<HomeBanner>?,
    var cardItems  : ArrayList<TransverseCardData>?=null,
    var funcItems : ArrayList<HomeFunctionBean>?=null,

)
