package cool.dingstock.appbase.entity.bean.box

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/6/25 15:26
 * @Version:         1.1.0
 * @Description:
 */
data class BoxHomeResultTabEntity(
    val serviceDescUrl: String? = "",
    val shopcartCount: Int? = 0,
    val searchPlaceholder: String = "",
    val currentTab: String? = "",
    val searchBgUrl: String? = "",
    val iconList: MutableList<BoxHomeIconGroupEntity>? = arrayListOf(),
    val nextKey: String? = null,
    val shopTabId: String,
    val tabList: MutableList<BoxTabEntity>? = arrayListOf(),
    val title: String?,
    val subTitle: String?,
)

data class BoxTabEntity(
    val key: String,
    val title: String
)

