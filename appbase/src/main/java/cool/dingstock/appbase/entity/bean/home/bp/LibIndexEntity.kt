package cool.dingstock.appbase.entity.bean.home.bp

data class LibIndexEntity(
        val imgUrl: String = "",
        val title: String = "",
        var link: String = "",
        val resId: Int = -1,
        //为true 直接使用 不需要弹窗
        val memberuse: Boolean = true,
        val track: String = ""
)