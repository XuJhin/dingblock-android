package cool.dingstock.appbase.entity.bean.home

data class CalenderBrandEntity(
        val name: String,
        val imageUrl: String,
        val type: String,
        val objectId: String,
        val isSelected: Boolean = false
)