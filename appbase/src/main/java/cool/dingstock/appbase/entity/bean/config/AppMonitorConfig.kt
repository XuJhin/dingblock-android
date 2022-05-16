package cool.dingstock.appbase.entity.bean.config

/**
 * @author WhenYoung
 *  CreateAt Time 2020/12/25  17:23
 */
data class AppMonitorConfig(var lastUpdateTime : Long = 0 )


data class RemindTimeEntity(
    var isChoose1: Boolean = true,
    var isChoose2: Boolean = false,
    var isChoose3: Boolean = false,
    var isChoose4: Boolean = false,

    var isCalendar: Boolean = true,
    var isAppPush: Boolean = false,
    var isSetDefault: Boolean = false
)