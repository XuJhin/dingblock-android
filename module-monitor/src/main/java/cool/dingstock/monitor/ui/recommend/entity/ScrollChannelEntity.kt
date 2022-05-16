package cool.dingstock.monitor.ui.recommend.entity

import cool.dingstock.appbase.entity.bean.monitor.MonitorRecommendChannelEntity

/**
 * Copyright (C), 2015-2020, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2020/12/24 15:33
 * @Version:         1.1.0
 * @Description:
 */
data class ScrollChannelEntity(
		val items: List<MonitorRecommendChannelEntity> = arrayListOf()
)