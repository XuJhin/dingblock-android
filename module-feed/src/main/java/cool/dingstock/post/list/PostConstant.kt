package cool.dingstock.post.list

interface PostConstant {
	interface PostType {
		companion object {
			const val Recommend = "recommend" // 推荐
			const val OFFICIAl = "official" // 订阅号
			const val Webpage = "webpage" // 网页
			const val Latest = "latest" // 最新
			const val Followed = "followed" //关注
			const val Fashion = "fashion"
			const val Deal = "deal"
			const val Nearby = "nearby" //附近
			const val Talk = "talk" //话题
		}
	}
}