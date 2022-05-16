package cool.dingstock.appbase.entity.bean.home

data class CalenderSectionEntity(
        val header: String,
        val brands: MutableList<CalenderBrandEntity>,
        val products: MutableList<HomeProduct> = arrayListOf()
)