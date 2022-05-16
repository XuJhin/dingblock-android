package cool.dingstock.appbase.entity.bean.home

data class HomeStreetWearGroup(
		var name: String?,
		var brand: HomeBrandBean?,
		var objectId: String?,
		var snapshotImageUrl: String?,
		var desc: String?,
		var imageUrls: List<String>?,
		var bgImageUrl: String?,
		//本地
		var headerName: String?
)
