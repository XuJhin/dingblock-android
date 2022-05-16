package cool.dingstock.appbase.entity.bean.shop

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/6/3 16:50
 * @Version:         1.1.0
 * @Description:
 */
data class SearchHotListEntity(
		val list: MutableList<SearchHotEntity>? = arrayListOf()
)

data class SearchHotEntity(
		val word: String? = ""
)