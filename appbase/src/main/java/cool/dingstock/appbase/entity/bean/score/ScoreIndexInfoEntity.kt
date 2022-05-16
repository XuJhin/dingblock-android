package cool.dingstock.appbase.entity.bean.score

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class ScoreIndexInfoEntity(val userInfo: ScoreIndexUserInfoEntity?,
								val taskInfo: ScoreTaskInfoEntity?,
								val productInfo: ArrayList<ScoreExchangeItemEntity>?)

/**
 *
"avatarUrl": "https://oss-community.dingstock.net/avatar/-378838387_1612338540006.png",
"userId": "sh92F65kwp",
"userCredit": 5,
"nickName": "打工人👷",
"fortuneType": "上签",
"fortuneImageUrl": "",
"fortuneContent": "可以可以!"
"isFortune":true
 *
 * */
@Parcelize
data class ScoreIndexUserInfoEntity(val avatarUrl: String,
									val signAlert: Boolean = false,
									val userId: String,
									val userCredit: Int,
									val nickName: String,
									val isSign: Boolean = false,
									val fortuneType: String,
									val fortuneId: Fortune = Fortune.superLucky,
									val fortuneImageUrl: String,
									val fortuneContent: String,
									val thisTimeCreditsStr: String,
									val nextTimeCreditsStr: String,
									val fortuneDetailImageUrl: String,
									val fortuneShareImageUrl: String,
									val signCountStr: String? = "",
									val reward:String? = "",
									val creditsStr: String? = "",
									val signCalendar: MutableList<SignCalendarEntity>? = arrayListOf()
) : Parcelable {
	constructor() : this(avatarUrl = "",
			signAlert = false,
			userId = "",
			userCredit = 0,
			nickName = "",
			isSign = false,
			fortuneType = "",
			fortuneId = Fortune.lucky,
			fortuneImageUrl = "",
			fortuneContent = "",
			thisTimeCreditsStr = "",
			nextTimeCreditsStr = "",
			fortuneDetailImageUrl = "",
			fortuneShareImageUrl = "")
}

/**
 * reward  是否双倍
 *
 */
@Parcelize
data class SignCalendarEntity(
		val desc: String? = "",
		val credit: String? = "",
		val isSigned: Boolean? = false,
		val reward: SignCalendarReward? = SignCalendarReward.normal
) : Parcelable

data class ScoreTaskInfoEntity(val dailies: ArrayList<ScoreTaskItemEntity>,
							   val newbies: ArrayList<ScoreTaskItemEntity>)

@Parcelize
enum class SignCalendarReward : Parcelable {
	double,
	VIP1, VIP3, normal
}

@Parcelize
enum class Fortune : Parcelable {
	superLucky,
	lucky,
	common,
	unlucky
}