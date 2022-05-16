package cool.dingstock.appbase.entity.bean.circle

import android.os.Parcelable
import com.google.gson.JsonObject
import kotlinx.parcelize.Parcelize

/**
 * @author WhenYoung
 *  CreateAt Time 2020/9/18  16:05
 */
@Parcelize
data class UserMap(
        var id:String?=null,
        var nickName:String?=null,
        var vipValidity:Long?=null,
        var state:String?=null,
        var isVerified:Boolean?=null,
        var briefIntro:String?=null,
        var avatarUrl:String?=null,
        var isVip:Boolean?=null,
        var blocks:ArrayList<String?>?=null,
        var achievementIconUrl: String? = "",
        var achievementId: String? ="",
        var avatarPendantId: String? = null,
        var avatarPendantUrl: String? = null
): Parcelable
