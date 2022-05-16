package cool.dingstock.appbase.entity.bean.tide

data class TideDealInfoFilterEntity(
    val dealDesc: String,
    val isAll :Boolean = false,
    var isSelected: Boolean = false
)