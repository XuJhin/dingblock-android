package cool.dingstock.appbase.entity.bean.config

import cool.dingstock.appbase.entity.bean.home.UpdateVerEntity

/**
 * "duration": 3,
 * "mediaUrl": "https://oss.dingstock.net/image/config/0a8d188e697945fe387d630b18cf6bf8bb9f1fc0182bcff3d77cc5ce785863491608636143914.png",
 * "linkType": "link",
 * "link": "https://app.dingstock.net/browser/hybird?url=https://h5-dev.dingstock.net/dingstock/activities/2020-summary.html",
 * "startTime": 1611029739,
 * "endTime": 1622131200,
 * "monitorInterval": "10",
 * "showInterval": 5,
 * "vipInterval": 10,
 * "mediaType": "png"
 *
 * */
data class SplashData(
		val first: String?,
		var mediaType: String?,
		var mediaUrl: String?,
		var link: String?,
		var duration: Int?,
		//单位 秒
		var showInterval: Long? = 0,
		var vipInterval: Int?,
		var startTime: Long?,
		var endTime: Long?,
		var id: String?,
		//本地缓存的文件地址
		var localPath: String?,
		var updateConfig: UpdateVerEntity? = null,
		//=======================================
		//=======================================
		//=======================================
		//=======================================
		//联盟广告展示数据
		var type: String?,
		val ratio: String?,
		var closeStart: Long? = 0,
		var closeEnd: Long? = 0,
		//单位小时
		var afterLaunch: Int? = 0,
) {
	constructor() : this(first = "",
			mediaType = "",
			mediaUrl = "",
			link = "",
			duration = 0,
			showInterval = 0,
			vipInterval = 0,
			startTime = 0L, endTime = 0L,
			id = "",
			localPath = "",
			updateConfig = null,
			type = "",
			ratio = "")
}
