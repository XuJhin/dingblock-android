package cool.dingstock.appbase.net.api.circle

import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.circle.*
import cool.dingstock.appbase.entity.bean.post.CancelShieldResultEntity
import cool.dingstock.appbase.entity.bean.post.ShieldUserEntity
import io.reactivex.rxjava3.core.Flowable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/14  9:57
 */
interface CircleApiService {
    @POST("xserver/community/post/save")
    fun postDynamic(@Body requestBody: RequestBody): Flowable<BaseResult<CirclePublishBean>>

    @POST("xserver/community/comment/save")
    fun communityComment(@Body requestBody: RequestBody): Flowable<BaseResult<CircleDynamicDetailCommentsBean>>

    @GET("xserver/community/post/read")
    fun uploadExposure(
        @Query("postId") postId: String,
    ): Flowable<BaseResult<Any>>

    @GET("/xserver/community/post/detail")
    fun communityPostDetail(@Query("id") postId: String): Flowable<BaseResult<CircleDynamicDetailBean>>

    @POST("xserver/user/block/save")
    fun shieldAccount(@Body body: RequestBody): Flowable<BaseResult<ShieldUserEntity>>

    @POST("xserver/user/block/cancel")
    fun cancelShield(@Body body: RequestBody): Flowable<BaseResult<CancelShieldResultEntity>>

    @POST("/xserver/community/post/update")
    fun postDelete(@Body body: RequestBody): Flowable<BaseResult<String?>>

    @POST("/xserver/community/vote")
    fun voteCircle(@Body body: RequestBody): Flowable<BaseResult<ArrayList<VoteOptionEntity>>>

    @POST("/xserver/community/post/favor")
    fun communityPostFavored(@Body body: RequestBody): Flowable<BaseResult<String>>

    @POST("/xserver/community/report/do")
    fun postReport(@Body body: RequestBody): Flowable<BaseResult<String>>

    @POST("/xserver/community/report/do")
    fun communityPostCommentReport(@Body body: RequestBody): Flowable<BaseResult<String>>

    @POST("/xserver/community/post/uninterested")
    fun postBlock(@Body body: RequestBody): Flowable<BaseResult<String>>

    @GET("/xserver/community/comment/page")
    fun communityPostComments(
        @Query("postId") postId: String,
        @Query("nextKey") nextKey: Long
    ): Flowable<BaseResult<CircleDetailLoadBean>>

    @POST("/xserver/community/comment/favor")
    fun communityPostCommentFavored(@Body body: RequestBody): Flowable<BaseResult<String>>

    @GET("/xserver/community/comment/detail")
    fun communityCommentDetail(
        @Query("id") id: String,
        @Query("nextKey") nextKey: Long?
    ): Flowable<BaseResult<CircleCommentDetailBean>>

    @GET("/xserver/community/comment/detail")
    fun communityCommentDetailCommentPage(
        @Query("id") id: String,
        @Query("nextKey") nextKey: Long?
    ): Flowable<BaseResult<CircleDetailLoadBean>>

    @POST("/xserver/community/comment/update")
    fun communityCommentDelete(@Body body: RequestBody): Flowable<BaseResult<String>>

    @GET("/xserver/community/cache/media")
    fun videoInfo(@Query("postId") postId: String): Flowable<BaseResult<CircleDynamicVideoDetailBean>>

    @POST("/xserver/community/resolvelink")
    fun resolveLink(@Body body: RequestBody): Flowable<BaseResult<CircleLinkBean>>

    @POST("/xserver/community/post/collect")
    fun collectPost(@Body body: RequestBody): Flowable<BaseResult<String>>

    @GET("/xserver/blank/track")
    fun track(
        @Query("action") action: String?,
        @Query("postId") postId: String?,
        @Query("type") type: String?
    ): Flowable<BaseResult<Any>>


    @POST("/xserver/blank/track")
    fun trackPost(@Body body: RequestBody): Flowable<BaseResult<Any>>

    @POST("/xserver/community/post/want")
    fun wantTrading(@Body body: RequestBody): Flowable<BaseResult<String>>
}