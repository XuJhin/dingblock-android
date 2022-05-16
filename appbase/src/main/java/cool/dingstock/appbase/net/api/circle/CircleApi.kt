package cool.dingstock.appbase.net.api.circle

import android.text.TextUtils
import cool.dingstock.appbase.constant.ServerConstant
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.circle.*
import cool.dingstock.appbase.entity.bean.post.CancelShieldResultEntity
import cool.dingstock.appbase.entity.bean.post.ShieldUserEntity
import cool.dingstock.appbase.net.retrofit.api.BaseApi
import cool.dingstock.appbase.net.retrofit.api.handError
import cool.dingstock.lib_base.thread.scheduler.RxSchedulers
import cool.dingstock.lib_base.util.StringUtils
import io.reactivex.rxjava3.core.Flowable
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/14  10:59
 */
class CircleApi @Inject constructor(retrofit: Retrofit) : BaseApi<CircleApiService>(retrofit) {
    @JvmOverloads
    fun postDynamic(
        talkId: String? = null,
        activityId: String? = null,
        discussId: String? = null,
        voteOptions: ArrayList<CircleDynamicVoteEntity>? = null,
        circleLinkBean: CircleLinkBean? = null,
        content: String? = null,
        images: ArrayList<String>? = null,
        locationEntity: LocationEntity? = null,
        isDeal: Boolean = false,
        goodsType: String? = null,
        goodsQuality: String? = null,
        goodsSizeList: List<Pair<String, String?>>? = null,
        productId: String? = null,
        hideSizeSegment: Boolean? = null,
        postType: String? = null,
        seriesId: String? = null,
        score: Int? = null
    ): Flowable<BaseResult<CirclePublishBean>> {
        val parameterBuilder = ParameterBuilder()
        voteOptions?.let {
            if (voteOptions.size > 0) {
                val voteLit = ArrayList<Map<String, String?>>()
                for (voteOption in voteOptions) {
                    val voteItemMap = HashMap<String, String?>()
                    voteItemMap["title"] = voteOption.title
                    voteLit.add(voteItemMap)
                }
                parameterBuilder.add("voteOptions", voteLit)
            }
        }
        //链接
        if (null != circleLinkBean) {
            val linkMap = HashMap<String, String?>()
            linkMap["link"] = circleLinkBean.link
            linkMap["title"] = circleLinkBean.title
            linkMap["imageUrl"] = circleLinkBean.imageUrl
            parameterBuilder.add(ServerConstant.ParamKEY.LINK, linkMap)
        }
        //内容
        if (!TextUtils.isEmpty(content)) {
            parameterBuilder.add(ServerConstant.ParamKEY.CONTENT, content)
        }
        if (null != talkId && talkId != "") {
            parameterBuilder.add(ServerConstant.ParamKEY.TALK_ID, talkId)
        }
        parameterBuilder.add("activityId",activityId)
        if (null != images && images.size > 0) {
            parameterBuilder.add("images", images)
        }
        if (locationEntity != null) {
            val locationMap = HashMap<String, String?>()
            locationMap["name"] = locationEntity.name
            locationMap["address"] = locationEntity.address
            locationMap["latitude"] = locationEntity.latitude.toString()
            locationMap["longitude"] = locationEntity.longitude.toString()
            parameterBuilder.add("locationInfo", locationMap)
        }
        if (isDeal) {
            parameterBuilder.add("isDeal", isDeal)
            parameterBuilder.add("goodsType", goodsType)
            parameterBuilder.add("goodsQuality", goodsQuality)
            parameterBuilder.add("hideSizeSegment", hideSizeSegment)
            val sizes = mutableListOf<HashMap<String, String?>>()
            goodsSizeList?.let {
                for (goodsSize in it) {
                    val size = hashMapOf<String, String?>()
                    size["size"] = goodsSize.first
                    size["price"] = goodsSize.second
                    sizes.add(size)
                }
                parameterBuilder.add("goodsSizeList", sizes)
            }
        }
        if (!seriesId.isNullOrEmpty()) {
            parameterBuilder.add("seriesId", seriesId)
            parameterBuilder.add("score", score)
        }
        if (!postType.isNullOrEmpty()) {
            parameterBuilder.add("postType", postType)
        }
        if (!productId.isNullOrEmpty()) {
            parameterBuilder.add("productId", productId)
        }
        if(!discussId.isNullOrEmpty()){
            parameterBuilder.add("discussId", discussId)
        }

        return service.postDynamic(parameterBuilder.toBody()).compose(RxSchedulers.netio_main())
            .handError()
    }

    /**
     * 发布评论
     *
     * @param postId   动态ID
     * @param content     内容
     * @param mentionedId 评论ID
     * @param dynamicImgUrl 原图
     * @param staticImgUrl 缩略图
     */
    fun communityComment(
        postId: String?,
        mentionedId: String?,
        content: String?,
        dynamicImgUrl: String?,
        staticImgUrl: String?,
        dcEmojiId: String?
    ): Flowable<BaseResult<CircleDynamicDetailCommentsBean>> {
        val parameterBuilder = ParameterBuilder()
        postId?.let {
            parameterBuilder.add("postId", it)
        }
        mentionedId?.let {
            parameterBuilder.add("mentionedId", it)
        }
        content?.let {
            parameterBuilder.add("content", content)
        }
        dynamicImgUrl?.let {
            parameterBuilder.add("dynamicImgUrl", it)
        }
        if (StringUtils.isEmpty(staticImgUrl)) { //如果是空 就把 dynamicImgUrl当做staticImgUrl
            dynamicImgUrl?.let {
                parameterBuilder.add("staticImgUrl", it)
            }
        } else {
            parameterBuilder.add("staticImgUrl", staticImgUrl)
        }
        dcEmojiId?.let {
            parameterBuilder.add("dcEmojiId", dcEmojiId)
        }
        return service.communityComment(parameterBuilder.toBody())
            .compose(RxSchedulers.netio_main()).handError()
    }

    fun uploadExposure(postId: String): Flowable<BaseResult<Any>> {
        return service.uploadExposure(postId).compose(RxSchedulers.netio_main()).handError()
    }

    fun communityPostDetail(id: String): Flowable<BaseResult<CircleDynamicDetailBean>> {
        return service.communityPostDetail(id).compose(RxSchedulers.netio_main()).handError()
    }

    fun shieldAccount(userId: String): Flowable<BaseResult<ShieldUserEntity>> {
        val body = ParameterBuilder()
            .add("userId", userId)
            .toBody()
        return service.shieldAccount(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun cancelShield(userId: String): Flowable<BaseResult<CancelShieldResultEntity>> {
        val body = ParameterBuilder()
            .add("userId", userId)
            .toBody()
        return service.cancelShield(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun postDelete(communityPost: String): Flowable<BaseResult<String?>> {
        val body = ParameterBuilder()
            .add("id", communityPost)
            .toBody()
        return service.postDelete(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun voteCircle(
        voteOptionEntity: VoteOptionEntity,
        postId: String,
        optionIndex: Int
    ): Flowable<BaseResult<ArrayList<VoteOptionEntity>>> {
        val body = ParameterBuilder()
            .add("isVoted", !voteOptionEntity.isVote)
            .add("optionIndex", optionIndex)
            .add("postId", postId)
            .add("optionTitle", voteOptionEntity.title)
            .toBody()
        return service.voteCircle(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun communityPostFavored(isFavored: Boolean, postId: String): Flowable<BaseResult<String>> {
        val body = ParameterBuilder()
            .add("favored", isFavored)
            .add("postId", postId)
            .toBody()
        return service.communityPostFavored(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun postReport(postId: String): Flowable<BaseResult<String>> {
        val body = ParameterBuilder()
            .add("reportedId", postId)
            .add("type", "post")
            .toBody()
        return service.postReport(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun communityPostCommentReport(commentId: String): Flowable<BaseResult<String>> {
        val body = ParameterBuilder()
            .add("reportedId", commentId)
            .add("type", "comment")
            .toBody()
        return service.communityPostCommentReport(body).compose(RxSchedulers.netio_main())
            .handError()
    }

    fun postBlock(id: String): Flowable<BaseResult<String>> {
        val body = ParameterBuilder()
            .add("postId", id)
            .toBody()
        return service.postBlock(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun communityPostComments(
        postId: String,
        nextKey: Long
    ): Flowable<BaseResult<CircleDetailLoadBean>> {
        return service.communityPostComments(postId, nextKey).compose(RxSchedulers.netio_main())
            .handError()
    }

    fun communityPostCommentFavored(
        commentId: String,
        favored: Boolean
    ): Flowable<BaseResult<String>> {
        val body = ParameterBuilder()
            .add("commentId", commentId)
            .add("favored", favored)
            .toBody()
        return service.communityPostCommentFavored(body).compose(RxSchedulers.netio_main())
            .handError()
    }

    fun communityCommentDetail(id: String): Flowable<BaseResult<CircleCommentDetailBean>> {
        return service.communityCommentDetail(id, null).compose(RxSchedulers.netio_main())
            .handError()
    }

    fun communityCommentDetailCommentPage(
        id: String,
        nextKey: Long?
    ): Flowable<BaseResult<CircleDetailLoadBean>> {
        return service.communityCommentDetailCommentPage(id, nextKey)
            .compose(RxSchedulers.netio_main()).handError()
    }

    fun trackShare(postId: String?): Flowable<BaseResult<Any>> {
        return service.track("community_post_share", postId, null)
            .compose(RxSchedulers.netio_main()).handError()
    }

    fun trackUserLayer(type: String?,currentUserId:String?): Flowable<BaseResult<Any>> {
        val body = ParameterBuilder()
            .add("action", "activity_layer_pop")
            .add("type", type)
            .add("currentUserId", currentUserId)
            .toBody()
        return service.trackPost(body)
            .compose(RxSchedulers.netio_main()).handError()
    }

    fun communityCommentDelete(id: String): Flowable<BaseResult<String>> {
        val body = ParameterBuilder()
            .add("id", id)
            .toBody()
        return service.communityCommentDelete(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun videoInfo(postId: String): Flowable<BaseResult<CircleDynamicVideoDetailBean>> {
        return service.videoInfo(postId).compose(RxSchedulers.netio_main()).handError()
    }

    fun resolveLink(link: String): Flowable<BaseResult<CircleLinkBean>> {
        val body = ParameterBuilder()
            .add("link", link)
            .toBody()
        return service.resolveLink(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun collectPost(postId: String): Flowable<BaseResult<String>> {
        val body = ParameterBuilder()
            .add("postId", postId)
            .toBody()
        return service.collectPost(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun wantTrading(postId: String): Flowable<BaseResult<String>> {
        val body = ParameterBuilder()
            .add("postId", postId)
            .toBody()
        return service.wantTrading(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun track(action: String?, postId: String?, type: String?): Flowable<BaseResult<Any>> {
        return service.track(action, postId, type)
            .compose(RxSchedulers.netio_main()).handError()
    }
}