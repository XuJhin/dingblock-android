package cool.dingstock.appbase.entity.bean.drawer

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/6/17 16:23
 * @Version:         1.1.0
 * @Description:
 */
data class MineDrawerEntity(
		val head: String?,
		val items: List<MineDrawerItemEntity>
)

data class MineDrawerItemEntity(
		val actionType: String = "",
		val name: String,
		val resId: String,
		val type: String,
		val value: String,
		val link: String
)