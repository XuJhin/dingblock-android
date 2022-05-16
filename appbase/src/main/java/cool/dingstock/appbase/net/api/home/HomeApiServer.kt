package cool.dingstock.appbase.net.api.home

import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.circle.*
import cool.dingstock.appbase.entity.bean.home.*
import cool.dingstock.appbase.entity.bean.home.fashion.FashionListEntity
import cool.dingstock.appbase.entity.bean.mine.AccountInfoBriefEntity
import cool.dingstock.appbase.entity.bean.mine.UnReadMessage
import cool.dingstock.appbase.entity.bean.score.MineScoreInfoEntity
import io.reactivex.rxjava3.core.Flowable
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * @author WhenYoung
 *  CreateAt Time 2020/11/5  15:50
 */
interface HomeApiServer {
    @POST("xserver/calendar/product/comment/save")
    fun commentProduct(
        @Body requestBody: RequestBody,

        ): Flowable<BaseResult<CircleDynamicDetailCommentsBean>>

    /**
     * 获取推荐用户
     * @param times 换一换  第一次传0;点击换一换传1
     * @param userId 用户id
     */
    @GET("xserver/community/userrecommend/display")
    fun recommendAccount(
        @Query("times") times: Int,

        ): Flowable<BaseResult<MutableList<AccountInfoBriefEntity>>>

    @POST("xserver/community/relation/batch/save")
    fun followAccounts(
        @Body body: RequestBody,

        ): Flowable<BaseResult<Boolean>>

    @GET("xserver/community/relation/count")
    fun fetchFansCount(
        @Query("userId") userId: String,

        ): Flowable<BaseResult<FollowCountEntity>>

    /**
     * 获取用户是否可以使用Bp功能
     */
    @GET("xserver/lab/bp/canuse")
    fun fetchBpPermission(

    ): Flowable<Map<String, Any>>

    /**
     * 获取推荐接口
     */
    @GET("xserver/community/post/recommend/new")
    fun recommendPosts(
        @Query("type") type: String?,
        @Query("nextKey") nextKey: Long?,

        ): Flowable<BaseResult<HomePostData>>

    /**
     * 获取最新
     */
    @GET("xserver/community/post/page")
    fun newPost(
        @Query("nextKey") nextKey: Long?,

        ): Flowable<BaseResult<HomePostData>>

    /**
     * 获取最新
     */
    @GET("xserver/community/post/page")
    fun fashion(
        @Query("nextKey") nextKey: Long?,
        @Query("isFashion") isFashion: Boolean = true,

        ): Flowable<BaseResult<HomePostData>>

    /**
     * 获取关注
     */
    @GET("xserver/community/relation/post")
    fun followPost(
        @Query("nextKey") nextKey: Long?,
        @Query("userId") userId: String,

        ): Flowable<BaseResult<HomePostData>>

    /**
     * 获取话题
     */
    @GET("/xserver/community/talk/detail")
    fun topicPost(
        @Query("id") id: String,
        @Query("nextKey") nextKey: Long?,

        ): Flowable<BaseResult<HomePostData>>


    /**
     * 获取主页配置
     */
    @GET("xserver/user/homeconfig")
    fun homeConfig(

    ): Flowable<BaseResult<HomeConfigEntity?>>

    /**
     * 获取未读消息数
     */
    @GET("xserver/community/user/unreadcount")
    fun fetchUnreadMessage(

    ): Flowable<BaseResult<UnReadMessage>>

    /**
     * 获取主页配置
     */
    @GET("/xserver/home/info/v2")
    fun home(

    ): Flowable<BaseResult<HomeData>>

    /**
     * 获取主页配置
     */
    @GET("xserver/user/credit/retrieve")
    fun mineScoreInfo(

    ): Flowable<BaseResult<MineScoreInfoEntity>>

    @GET("xserver/community/talk/page")
    fun fetchBrandTop(
        @Query("userId") userId: String,

        ): Flowable<BaseResult<FashionListEntity>>

    @GET("xserver/community/talk/page")
    fun fetchBrandAll(
        @Query("userId") userId: String,
        @Query("nextStr") nextStr: String?,

        ): Flowable<BaseResult<FashionListEntity>>

    @GET("xserver/community/talk/fashion/list")
    fun fetchBrandAllNew(

    ): Flowable<BaseResult<FashionListEntity>>

    @GET("xserver/community/post/page")
    fun fashionPosts(
        @Query("isFashion") isFashion: Boolean,
        @Query("nextKey") nextKey: Long?,
        @Query("talkId") talkId: String?,

        ): Flowable<BaseResult<HomePostData>>

    @GET("/xserver/calendar/list")
    fun loadSneakerCalendar(
        @Query("date") date: Long,
        @Query("brands") brands: String?,
        @Query("types") types: String?,

        ): Flowable<BaseResult<CalenderDataBean>>

    @GET("/xserver/calendar/raffle/detail")
    fun getProductDetail(
        @Query("productId") productId: String,
        @Query("longitude") longitude: Double?,
        @Query("latitude") latitude: Double?,

        ): Flowable<BaseResult<HomeProductDetailData>>

    @GET("/xserver/home/funcitems/flush")
    fun refreshHomeFun(

    ): Flowable<BaseResult<ArrayList<HomeFunctionBean>>>

    @GET("/xserver/calendar/featured")
    fun heavyList(
    ): Flowable<BaseResult<ArrayList<CalenderProductEntity>>>

    @GET("/xserver/community/post/nearby/page")
    fun requestNearbyPosts(
        @Query("nextKey") nextKey: Long?,
        @Query("longitude") longitude: Double,
        @Query("latitude") latitude: Double,
        @Query("type") type: String?,
        @Query("recommendLocationId") recommendLocationId: String?,

        ): Flowable<BaseResult<HomeNearbyEntity>>

    @GET("/xserver/community/talk/homepage/list")
    suspend fun homePageTopicList(): BaseResult<HomeTopicResultEntity>

    @GET("/xserver/community/userrecommend/display/new")
    suspend fun recommendKolUser(): BaseResult<ArrayList<RecommendKolUserEntity>>

    @POST("/xserver/blank/track")
    fun trackPost(
        @Body body: RequestBody,

        ): Flowable<BaseResult<Any>>

    @GET("/xserver/community/discuss/hot/list")
    suspend fun fetchHotRankList(): BaseResult<ArrayList<HotRankItemEntity>>

}