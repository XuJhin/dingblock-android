package cool.dingstock.appbase.entity.event.home

import cool.dingstock.appbase.entity.bean.home.HomeBrandBean
import cool.dingstock.appbase.entity.bean.home.HomeTypeBean

data class EventBrandChooseChange(
		var changeList: List<HomeBrandBean> = emptyList(),
		var typeList: List<HomeTypeBean> = emptyList()
)

