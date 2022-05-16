package cool.dingstock.appbase.entity.bean.home

import kotlinx.serialization.Serializable


@Serializable
data class HomeTabBean(
    val tabText: String = "首页",
    val tabIconRes: String = "home.json",
    val tabIconDarkRes: String = "home_dark.json",
    val tabId: String = "home",
)