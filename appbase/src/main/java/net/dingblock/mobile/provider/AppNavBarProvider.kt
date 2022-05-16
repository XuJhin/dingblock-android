package net.dingblock.mobile.provider

import cool.dingstock.appbase.entity.bean.home.HomeTabBean
import cool.dingstock.lib_base.json.JSONHelper
import cool.dingstock.lib_base.stroage.ConfigFileHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

object AppNavBarProvider {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = true
        encodeDefaults = true
        coerceInputValues = true
    }

    suspend fun providerNavAppBar(): MutableList<AppTabEntity> {
        return CoroutineScope(Dispatchers.IO).async {
            val configJson: String = ConfigFileHelper.getConfigJson("dingblock_app_tab.json")
            val homeTabBeanList = json.decodeFromString<MutableList<AppTabEntity>>(configJson)
            return@async homeTabBeanList
        }.await()
    }

    /**
     * 获取首页的tab列表
     *
     * @return 回调
     */
    suspend fun getTabDataList(): List<HomeTabBean> {
        return parseData().await()

    }

    private fun parseData(): Deferred<MutableList<HomeTabBean>> {
        return CoroutineScope(Dispatchers.IO).async {
            val configJson = ConfigFileHelper.getConfigJson("dingblock_home_tab.json")
            val homeTabBeanList = JSONHelper.fromJsonList(
                configJson, HomeTabBean::class.java
            )
            return@async homeTabBeanList
        }
    }
}


@Serializable
enum class EAppTabType {
    Home, Market, Profile
}


@Serializable
data class AppTabEntity(
    val type: EAppTabType = EAppTabType.Home,
    val tabInfo: HomeTabBean,
    var needLogin: Boolean = false
)