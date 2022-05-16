package cool.dingstock.appbase.entity.bean.circle


/**
 * 类名：HomeNearbyEntity
 * 包名：cool.dingstock.appbase.entity.bean.circle
 * 创建时间：2021/12/17 3:19 下午
 * 创建人： WhenYoung
 * 描述：
 **/
data class HomeNearbyEntity(
    val recommendLocation: ArrayList<HomeNearbyLocationEntity>? = null,
    val list: ArrayList<CircleDynamicBean>,
    val type: String?,
    val nextKey: Long?,
)

data class HomeNearbyLocationEntity(
    val id: String,
    val recommendLocation: String,
    val postCountStr: String,
) {

}
