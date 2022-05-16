package cool.dingstock.appbase.net.api.find

import cool.dingstock.appbase.entity.bean.base.BasePageEntity
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.circle.*
import cool.dingstock.appbase.entity.bean.home.HotRankItemEntity
import cool.dingstock.appbase.entity.bean.mine.IMNotificationSettingEntity
import cool.dingstock.appbase.entity.bean.mine.MineFollowEntity
import cool.dingstock.appbase.entity.bean.topic.TopListEntity
import cool.dingstock.appbase.entity.bean.topic.TopicDetailEntity
import cool.dingstock.appbase.entity.bean.topic.TopicDiscoverEntity
import cool.dingstock.appbase.entity.bean.topic.party.HotRankDetailEntity
import cool.dingstock.appbase.entity.bean.topic.party.PartyDetailsResultEntity
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
interface FindApiService {
    @GET("/xserver/community/message/options/find")
    suspend fun requestNotificationSettingList(@Query("userId") userId: String?): BaseResult<IMNotificationSettingEntity>

    @POST("/xserver/community/message/options/update")
    suspend fun upDateNotificationSettingList(@Body requestBody: RequestBody): BaseResult<Any>

    @GET("/xserver/community/relation/mutual")
    suspend fun requestFollowEachOtherList(@Query("nextKey") nextKey: Long?): BaseResult<MineFollowEntity>

    @GET("/xserver/community/relation/me")
    fun requestFollowUser(
        @Query("userId") userId: String,
        @Query("nextKey") nextKey: Long?
    ): Flowable<BaseResult<MineFollowEntity>>

    @GET("/xserver/community/relation/followme")
    fun requestFansList(
        @Query("userId") userId: String,
        @Query("nextKey") nextKey: Long?
    ): Flowable<BaseResult<MineFollowEntity>>

    @GET("/xserver/community/favor/post/page")
    fun requestFavorList(
        @Query("postId") userId: String,
        @Query("nextKey") nextKey: Long?
    ): Flowable<BaseResult<MineFollowEntity>>

    @GET("/xserver/community/talk/discover")
    fun fetchFindIndex(@Query("nextStr") nextStr: String?): Flowable<BaseResult<TopicDiscoverEntity>>

    @GET("/xserver/community/talk/relation/page")
    fun talkFollowed(@Query("nextKey") nextKey: Long?): Flowable<BaseResult<TopListEntity>>

    @GET("/xserver/community/talk/detail")
    fun fetchTopicDetail(
        @Query("id") id: String,
        @Query("nextKey") nextKey: Long?
    ): Flowable<BaseResult<TopicDetailEntity>>


    @GET("/xserver/community/talk/detail")
    fun fetchTopicPost(
        @Query("id") id: String,
        @Query("nextKey") nextKey: Long?,
        @Query("requestPost") requestPost: String = "requestPost",
    ): Flowable<BaseResult<BasePageEntity<CircleDynamicBean>>>

    @POST("/xserver/search/post/deal")
    fun fetchTopicTradingPost(@Body body: RequestBody): Flowable<BaseResult<BasePageEntity<CircleDynamicBean>>>

    @GET("/xserver/community/talk/page")
    fun fetchAllTopic(
        @Query("userId") userId: String,
        @Query("nextKey") nextKey: Long?,
        @Query("showDealTalk") showDealTalk: Boolean
    ): Flowable<BaseResult<TopListEntity>>

    @POST("/xserver/community/talk/relation")
    fun followTopic(@Body body: RequestBody): Flowable<BaseResult<String>>

    @GET("/xserver/community/talk/recommend")
    fun fetchTopic(@Query("nextStr") nextStr: String?): Flowable<BaseResult<TopListEntity>>

    @GET("/xserver/community/place/explore")
    fun locationListRecommend(
        @Query("longitude") longitude: Double,
        @Query("latitude") latitude: Double,
        @Query("pageNo") pageNo: Int
    )
            : Flowable<BaseResult<MutableList<LocationEntity>>>

    @GET("/xserver/community/place/search")
    fun searchLocation(
        @Query("longitude") longitude: Double,
        @Query("latitude") latitude: Double,
        @Query("pageNo") pageNo: Int,
        @Query("keyword") keyword: String
    )
            : Flowable<BaseResult<MutableList<LocationEntity>>>

    @GET("/xserver/community/place/city")
    suspend fun getCity(
        @Query("longitude") longitude: Double,
        @Query("latitude") latitude: Double
    ): BaseResult<CityBean>

    @GET("/xserver/community/place/citys")
    suspend fun getCities(): BaseResult<List<GroupCityBean>>


    @GET("/xserver/community/activity/detail")
    suspend fun getPartyDetails(
        @Query("id") id: String?
    ): BaseResult<PartyDetailsResultEntity>

    @GET("/xserver/community/activity/detail")
    suspend fun getPartyPost(
        @Query("id") id: String?,
        @Query("nextKey") nextKey: Long?,
    ): BaseResult<BasePageEntity<CircleDynamicBean>>

    @POST("/xserver/search/product")
    suspend fun searchGoods(@Body body: RequestBody): BaseResult<GoodsListBean>

    @GET("/xserver/community/discuss/detail")
    suspend fun getHotTopicDetail(
        @Query("id") id: String?
    ): BaseResult<HotRankDetailEntity>

    @GET("/xserver/community/discuss/detail")
    suspend fun getMoreHotTopicList(
        @Query("id") id: String?,
        @Query("nextKey") nextKey: Long?,
    ): BaseResult<BasePageEntity<CircleDynamicBean>>

    @GET("/xserver/community/discuss/hot/list")
    suspend fun fetchHotRankList(): BaseResult<ArrayList<HotRankItemEntity>>
}