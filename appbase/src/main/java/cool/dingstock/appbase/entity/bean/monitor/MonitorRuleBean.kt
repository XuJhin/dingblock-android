package cool.dingstock.appbase.entity.bean.monitor

import cool.dingstock.appbase.entity.bean.sku.Size

data class MonitorRuleBean(
    val channelId: String,
    val channelName: String,
    var keywords: MutableList<String>?,
    val sizeSupport: Boolean,
    var sizes: MutableList<Size>?
)

data class MsgConfigEntity(
    val title: String? = "",
    val enable: Boolean? = false,
    val rewardStr: String? = "",
    val remain: Int? = 0,
)

data class MsgConfigDataEntity(
    val messageConfig: MsgConfigEntity? = null
)


data class IsShowDialogEntity(
    val msg: String? = "",
    val pop: Boolean? = false
)

data class MonitorCitiesEntity(
    var isSelected: Boolean = false,
    val province: String? = "",
    val cities: ArrayList<MonitorCityEntity>? = arrayListOf()
)

data class MonitorCityEntity(
    val name: String? = "",
    var isSelect: Boolean = false
)