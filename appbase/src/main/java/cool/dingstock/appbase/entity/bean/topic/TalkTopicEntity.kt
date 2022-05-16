package cool.dingstock.appbase.entity.bean.topic

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import cool.dingstock.appbase.entity.bean.account.UserInfoEntity
import cool.dingstock.appbase.entity.bean.topic.party.PartyEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class TalkTopicEntity(
        var id: String? = null,
        var createdAt: Long = 0,
        var userId: String? = null,
        var name: String? = null,
        var title: String? = null,
        var desc: String? = null,
        var isDefaultImg: Boolean? = null,
        var isMutual: Boolean = false,
        var imageUrl: String? = "",
        var blocked: Boolean? = false,
        var imageMap: ImageEntity? = null,
        @SerializedName(value = "recommendMedias", alternate = ["list"])
        var recommendMedias: MutableList<RecommendMedias>? = null,
        var user: TopicUserInfo? = null,
        var hideArrow: Boolean = false,
        var posts: ArrayList<TopicPost>? = null,
        //2.9.0
        var isDeal: Boolean? = false,
        // 2.9.8
        var activities: ArrayList<PartyEntity>? = null,
        //单独处理，由于活动一定属于某个话题
        var activity: PartyEntity? = null,
        //自定义字段
        var isStart: Boolean = false,
        var isEnd: Boolean = false,
        var postCountStr: String? = "",

        //2.12.0热议详情适配
        var latestUserAvatarUrls: ArrayList<String>? = null,
        var userCountStr: String? = null,
        val isExpired: Boolean? = false,
) : Parcelable

@Parcelize
data class RecommendMedias(
        var url: String = "",
        @SerializedName(value = "thumbnail", alternate = ["imageUrl"])
        var thumbnail: String = "",
        var width: String = "",
        var height: String = "",
        var format: String = "",
        var title: String = ""
) : Parcelable

@Parcelize
data class ImageEntity(
        var thumbnail: String? = "",
        var url: String? = ""
) : Parcelable

@Parcelize
data class TopicUserInfo(
        var nickName: String = "",
        var id: String = "",
        var isVerified: Boolean = false,
        var avatarUrl: String? = ""
) : Parcelable

@Parcelize
data class TopicPost(
        var id: String?,
        var content: String = "",
        var showImage: String? = "",
        var objectId: String? = "",
        var shareLink: String? = "",
        var createdAt: Long? = 0,
        var user: UserInfo? = null,
        var webpageLink: WebpageLink? = null

) : Parcelable


@Parcelize
data class WebpageLink(
        var title: String? = "",
        var type: String? = "",
        var link: String? = null,
        var imageUrl: String? = ""
) : Parcelable

@Parcelize
data class UserInfo(
        var id: String? = "",
        var nickName: String? = "",
        var avatarUrl: String? = null
) : Parcelable