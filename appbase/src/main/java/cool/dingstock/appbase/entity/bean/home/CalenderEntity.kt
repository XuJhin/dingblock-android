package cool.dingstock.appbase.entity.bean.home

data class CalenderDataBean(
        //产品
        var sections: MutableList<HomeProductGroup>? = null,
        //重磅
        val featured: CalenderFeaturedEntity? = null,
        //本地数据
        var isRefresh : Boolean = false
)