package cool.dingstock.appbase.entity.bean.tide


/**
 * 类名：TideFilterEntity
 * 包名：cool.dingstock.appbase.entity.bean.tide
 * 创建时间：2021/7/20 5:38 下午
 * 创建人： WhenYoung
 * 描述：
 **/
data class TideCompanyFilterEntity(val company: String,val isAll :Boolean = false) {
    var isSelected: Boolean = false
}