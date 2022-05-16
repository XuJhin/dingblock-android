package cool.dingstock.appbase.entity.bean.circle

data class GroupCityBean(
    val citys: List<City>,
    val title: String
)

data class City(
    val latitude: Double,
    val letter: String,
    val longitude: Double,
    val name: String
)
