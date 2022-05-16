package cool.dingstock.appbase.entity.bean.circle;

data class CircleDynamicDetailBean (
    var sections  : ArrayList<CircleDynamicSectionBean>?,
    //是否关注
    var isMutual  : Boolean = false,
    var post  : CircleDynamicBean?
)
