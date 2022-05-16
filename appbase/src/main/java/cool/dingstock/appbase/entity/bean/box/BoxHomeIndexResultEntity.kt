package cool.dingstock.appbase.entity.bean.box


/**
 * 类名：BoxHomeIndexResultEntity
 * 包名：cool.dingstock.appbase.entity.bean.box
 * 创建时间：2021/8/3 7:19 下午
 * 创建人： WhenYoung
 * 描述：
 **/
data class BoxHomeIndexResultEntity(
        var title: String? = "",
//        val sizeList: ArrayList<Int>,
        val list: ArrayList<BoxHomeIndexItemEntity>,
        val leftTryTimes: Int
) {
}

