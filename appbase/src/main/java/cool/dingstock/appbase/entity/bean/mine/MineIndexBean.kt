package cool.dingstock.appbase.entity.bean.mine;


import androidx.annotation.IntegerRes;






data class MineIndexBean (
    /**
     * name : 关注地区
     * iconRes : icon_tab_home
     * linkedUrl :
     */
    var name  : String?,
    var iconRes  : String?,
    var type  : String?,
    @IntegerRes
    var iconIntRes  : Int?
)
