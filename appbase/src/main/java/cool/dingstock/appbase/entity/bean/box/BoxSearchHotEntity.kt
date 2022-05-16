package cool.dingstock.appbase.entity.bean.box

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/6/3 16:50
 * @Version:         1.1.0
 * @Description:
 */
data class BoxSearchHotListEntity(
		val list: MutableList<BoxSearchHotEntity>? = arrayListOf()
)

data class BoxSearchHotEntity(
		val word: String? = ""
)