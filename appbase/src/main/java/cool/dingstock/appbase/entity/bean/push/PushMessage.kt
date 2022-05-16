package cool.dingstock.appbase.entity.bean.push

data class PushMessage(

		/**
		 * link :
		 * imageUrl :
		 * notice : ("content":"","link":"")
		 */
		var link: String?,
		var imageUrl: String?,
		var notice: NoticeBean?,
		var route: String?,
		var body: String?,
		var bizId: String?,
		var localize: Boolean = false,
		var event: String?,
		var channel: String?,
		var notificationId: Int = 0,
) {
	data class NoticeBean(
			/**
			 * content :
			 * link :
			 */
			var content: String?,
			var link: String?
	)
}



