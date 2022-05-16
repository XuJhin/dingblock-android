package cool.dingstock.appbase.net.api.mine

import cool.dingstock.appbase.entity.bean.account.DcLoginUser
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.circle.*
import cool.dingstock.appbase.entity.bean.common.CommonPageEntity
import cool.dingstock.appbase.entity.bean.mine.*
import cool.dingstock.appbase.entity.bean.score.*

import io.reactivex.rxjava3.core.Flowable
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/14  9:57
 */
interface MineApiService {
    @POST("/xserver/community/relation")
    fun switchFollowState(@Body body: RequestBody): Flowable<BaseResult<Int>>

    @GET("/xserver/community/favor/post/page")
    fun requestFavorList(
        @Query("postId") userId: String,
        @Query("nextKey") nextKey: Long?
    ): Flowable<BaseResult<MineFollowEntity>>

    @GET("/xserver/community/favor/comment/page")
    fun requestFavorCommentList(
        @Query("commentId") commentId: String,
        @Query("nextKey") nextKey: Long?
    ): Flowable<BaseResult<MineFollowEntity>>

    @GET("/xserver/community/relation/followme")
    fun requestFansList(
        @Query("userId") userId: String,
        @Query("nextKey") nextKey: Long?
    ): Flowable<BaseResult<MineFollowEntity>>

    @GET("/xserver/community/relation/me")
    fun requestFollowUser(
        @Query("userId") userId: String,
        @Query("nextKey") nextKey: Long?
    ): Flowable<BaseResult<MineFollowEntity>>

    @GET("/xserver/user/privilege/list")
    fun vipPrivilege(): Flowable<BaseResult<ArrayList<VipPrivilegeEntity>>>

    @GET("/xserver/vip/activityprices")
    fun vipActivityPrices(@Query("type") type: String): Flowable<BaseResult<VipActivityBean>>

    @GET("/xserver/mall/init")
    fun mall(): Flowable<BaseResult<MineMallData>>

    @POST("/xserver/mall/checkin")
    fun checkIn(): Flowable<BaseResult<String>>

    @POST("/xserver/mall/redeem")
    fun redeem(@Body body: RequestBody): Flowable<BaseResult<String>>

    @GET("/xserver/user/credits/center")
    fun scoreIndexInfo(): Flowable<BaseResult<ScoreIndexInfoEntity>>

    @POST("/xserver/user/fortune/checkin")
    fun scoreSign(): Flowable<BaseResult<ScoreIndexUserInfoEntity>>

    @POST("/xserver/user/fortune/alert/switch")
    fun switchSignAlert(@Body body: RequestBody): Flowable<BaseResult<Any>>

    @POST("/xserver/user/credit/product/redeem")
    fun scoreExchange(@Body body: RequestBody): Flowable<BaseResult<ScoreExchangeResultEntity>>

    @POST("/xserver/user/task/detect")
    fun detectTask(@Body body: RequestBody): Flowable<BaseResult<Any>>

    @GET("/xserver/user/credit/page")
    fun scorePage(@Query("eventType") eventType: String? = null, @Query("nextKey") nextKey: Long?)
            : Flowable<BaseResult<CommonPageEntity<ScoreRecordEntity>>>

    @GET("/xserver/user/credit/get")
    fun receiveScore(@Query("taskId") taskId: String, @Query("taskType") taskType: String)
            : Flowable<BaseResult<String>>

    @GET("/xserver/user/credit/product/detail")
    fun scoreDetail(@Query("id") id: String): Flowable<BaseResult<ExchangeGoodsDetailEntity>>

    @POST("/xserver/user/credit/consign")
    fun submitinformation(@Body body: RequestBody): Flowable<BaseResult<Any>>


    /**
     * 获取用户动态
     * @param userId    用户ID
     * @param nextKey   分页标记
     */
    @GET("/xserver/community/post/page")
    fun accountPost(
        @Query("userId") userId: String,
        @Query("nextKey") nextKey: Long?,
        @Query("postType") postType: String,
    ): Flowable<BaseResult<UserPostsEntity>>

    /**
     * 获取自己信息
     */
    @GET("/xserver/community/userdetail")
    fun selfBrief(@Query("userId") userId: String): Flowable<BaseResult<AccountBriefEntity>>

    @GET("xserver/user/u/detail")
    fun selfInfo(): Flowable<BaseResult<DcLoginUser>>

    @GET("xserver/community/post/collect/page")
    fun fetchMineCollections(
        @Query("nextKey") nextKey: Long?,
        @Query("isDeal") isDeal: Boolean
    ): Flowable<BaseResult<UserPostsEntity>>

    @GET("xserver/user/achievement/detail")
    fun fetchMineMedals(
        @Query("userId") userId: String,
        @Query("achievementId") achievementId: String
    ): Flowable<BaseResult<MedalListEntity>>

    @POST("xserver/user/achievement/receive")
    fun getNewMedal(@Body body: RequestBody): Flowable<BaseResult<Any>>

    @POST("xserver/user/achievement/choose")
    fun suitUpMedal(@Body body: RequestBody): Flowable<BaseResult<Any>>


    @GET("/xserver/user/achievement/list")
    suspend fun getMedalList(@Query("userId") userId: String): BaseResult<MedalListBean>

    /**
     * 获取主页配置
     */
    @GET("xserver/user/credit/retrieve")
    fun mineScoreInfo(): Flowable<BaseResult<MineScoreInfoEntity>>

    @GET("xserver/user/achievement/receivable")
    fun isHaveNewMedal(): Flowable<BaseResult<IsHaveNewMedalEntity>>

}