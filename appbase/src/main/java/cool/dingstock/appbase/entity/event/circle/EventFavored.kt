package cool.dingstock.appbase.entity.event.circle

import cool.dingstock.appbase.entity.bean.circle.CircleDynamicBean


data class EventFavored(val dynamicId: String, val favored: Boolean) {
    var dynamicBean: CircleDynamicBean? = null

    constructor(dynamicId: String, favored: Boolean, dynamicBean: CircleDynamicBean) : this(dynamicId, favored) {
        this.dynamicBean = dynamicBean
    }
}