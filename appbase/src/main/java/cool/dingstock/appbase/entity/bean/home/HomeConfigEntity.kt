package cool.dingstock.appbase.entity.bean.home

import cool.dingstock.appbase.entity.bean.home.fashion.ActivityEntity

/**
 * @author WhenYoung
 *  CreateAt Time 2020/11/18  16:37
 */
/**
 *
"unreadtip": false,
"unreadcount": 0,
"popConfig":{}, // 开屏弹窗配置
"link":"", // 用户分层活动可选字段
"canJoin":true // 用户分层活动可选字段
 * */
data class HomeConfigEntity(
    var unreadtip: Boolean = false,
    var unreadcount: Int = 0,
    var popConfig: CommonImgDialogEntity? = null,
    var activity: ActivityEntity? = null
) {
}