package cool.dingstock.appbase.entity.event.circle

import cool.dingstock.appbase.entity.bean.circle.CircleDynamicBean

data class EventViewCount(val dynamicId: String) {
    var dynamicBean: CircleDynamicBean? = null

    constructor(dynamicId: String, dynamicBean: CircleDynamicBean) : this(dynamicId) {
        this.dynamicBean = dynamicBean
    }
}
