package cool.dingstock.appbase.net.api.bp

import cool.dingstock.appbase.entity.bean.base.BasePageEntity
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.home.bp.*
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
interface BpApiService {
    @GET("xserver/lab/bp/funclist")
    fun fetchBpList(): Flowable<BaseResult<MutableList<LibIndexEntity>>>

    @GET("xserver/lab/bp/page")
    fun fetchBpRecommend(
        @Query("platType") platType: String?,
        @Query("nextKey") nextKey: Long?
    ): Flowable<BaseResult<PageBpGoods>>

    @GET("xserver/lab/bp/remind/page")
    fun fetchRemindList(@Query("nextKey") nextKey: Long?): Flowable<BaseResult<PageRemindGoods>>

    @GET("xserver/lab/home")
    fun bpHome(): Flowable<BaseResult<BpLabIndexEntity>>

    @GET("xserver/lab/bp/page/new")
    fun bpHomePage(
        @Query("startAt") startAt: Long,
        @Query("type") type: String,
        @Query("nextKey") nextKey: Long?
    ): Flowable<BaseResult<BasePageEntity<GoodsItemEntity>>>

    @POST("xserver/lab/bp/remind/remove")
    fun removeRemindList(@Body body: RequestBody): Flowable<BaseResult<Int>>

    @GET("xserver/lab/stock/get")
    fun goodsStock(
        @Query("goodsId") goodsId: String?,
        @Query("type") type: String?
    ): Flowable<BaseResult<GoodsExtDetailsEntity>>

    @GET("xserver/lab/check/tbauth")
    fun checkTbAuth(): Flowable<BaseResult<Boolean>>

    @POST("xserver/lab/bp/remind/save")
    fun addRemindList(@Body body: RequestBody): Flowable<BaseResult<Int>>

    @POST("xserver/lab/tiktok/callback")
    fun douYinShareCallback(@Body body: RequestBody): Flowable<BaseResult<Any>>

    @GET("xserver/lab/bp/wallet/reward/page")
    fun fetchRewardRecordList(@Query("nextKey") nextKey: Long?)
            : Flowable<BaseResult<RewardRecordsData>>

    @GET("xserver/lab/bp/wallet/amount")
    fun fetchRewardMoney(): Flowable<BaseResult<MineRewardEntity>>

    @POST("xserver/lab/bp/wallet/withdraw")
    fun withDrawReward(@Body body: RequestBody): Flowable<BaseResult<Any>>

    @GET("xserver/lab/bp/wallet/withdraw/page")
    fun fetchRewardDetailList(@Query("nextKey") nextKey: Long?)
            : Flowable<BaseResult<WithdrawAllRecordList>>

    /**
     * 获取抖音分享id
     */
    @GET("xserver/lab/tiktok/shareid")
    fun fetchDouyinShareId(
        @Query("bpProductId") bpGoodId: String,
        @Query("code") douyinAuthorCode: String,
        @Query("date") date: Long?
    )
            : Flowable<BaseResult<String>>

    @POST("xserver/lab/bp/remind/self/save")
    suspend fun addGoodsAlert(@Body body: RequestBody): BaseResult<Int>

    @POST("xserver/lab/track/do")
    fun trackDo(@Body body: RequestBody): Flowable<BaseResult<String>>

    @GET("xserver/monitor/feed/rebate/home")
    fun fetchCluePageData(@Query("nextKey") nextKey: String): Flowable<BaseResult<ClueDataBean>>

    @GET("xserver/monitor/feed/page")
    fun fetchMoreClue(
        @Query("channelId") channelId: String,
        @Query("nextKey") nextKey: Long,
        @Query("type") type: String
    ): Flowable<BaseResult<ClueDataBean>>

    @POST("xserver/lab/bp/remind/save")
    suspend fun addRemindList1(@Body body: RequestBody): BaseResult<Int>

    @GET("xserver/lab/raffle/page")
    suspend fun getSignGoods(
        @Query("type") type: String,
        @Query("nextKey") nextKey: Long
    ): BaseResult<SignGoodsPageEntity>

    @GET("xserver/lab/bp/time/list")
    suspend fun getTimePlatList(): BaseResult<ArrayList<TimePlatEntity>>

    @POST("xserver/lab/raffle/join")
    suspend fun johnSignGoods(@Body body: RequestBody): BaseResult<Any>

    @GET("xserver/lab/mt/page")
    suspend fun getBuyStrategies(
        @Query("type") type: String,
        @Query("nextStr") nextStr: String?
    ): BaseResult<BuyStrategyList>

    @GET("xserver/lab/mt/detail")
    suspend fun getBuyStrategyDetail(
        @Query("channelId") channelId: String? = "",
        @Query("moutaiChannelId") moutaiChannelId: String?
    ): BaseResult<BuyStrategyEntity>

    @GET("xserver/lab/mt/home")
    suspend fun getMoutaiHome(@Query("isSubscribe") isSubscribe: Boolean): BaseResult<MoutaiHomeBean>

    @GET("xserver/lab/feed/mt/page")
    suspend fun getMoutaiPage(
        @Query("nextKey") nextKey: Long?,
        @Query("groupId") groupId: String,
        @Query("isSubscribe") isSubscribe: Boolean
    ): BaseResult<MoutaiListBean>
}