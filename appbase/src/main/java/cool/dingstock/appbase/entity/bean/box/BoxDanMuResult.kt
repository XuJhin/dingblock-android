package cool.dingstock.appbase.entity.bean.box


/**
 * 类名：BoxDanMuResult
 * 包名：cool.dingstock.appbase.entity.bean.box
 * 创建时间：2021/8/4 2:39 下午
 * 创建人： WhenYoung
 * 描述：
 **/
data class BoxDanMuResult(
    val list: ArrayList<String>,
    val minDeltaTime: Long,
    val maxDeltaTime: Long
) {

}