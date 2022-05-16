package cool.dingstock.appbase.net.api.monitor

import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.home.HomeRegionRaffleBean
import cool.dingstock.appbase.entity.bean.mine.RegionsBean
import cool.dingstock.appbase.entity.bean.monitor.*
import io.reactivex.rxjava3.core.Flowable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MonitorApiService {
    /**
     * 推荐监控
     */
    @GET("/xserver/monitor/subject/recommend")
    fun fetchRecommendMonitor(): Flowable<BaseResult<ArrayList<MonitorRecommendEntity>>>

    /**
     * 监控专题
     */
    @GET("/xserver/monitor/subject/detail")
    fun fetchMonitorSubject(@Query("id") subjectId: String)
            : Flowable<BaseResult<MonitorRecommendEntity>>

    /**
     * 获取菜单Menu
     */
    @GET("/xserver/monitor/subscribe/menu")
    fun fetchMonitorMenu(@Query("source") source: String?)
            : Flowable<BaseResult<MutableList<MonitorMenuBean>>>

    /**
     * 我的监控页-上方订阅地区日历
     */
    @GET("/xserver/calendar/subscribe/region/get")
    fun subscribedRegions(): Flowable<BaseResult<MutableList<MonitorCenterRegions>?>>

    /**
     * 我的监控页-FEED流
     */
    @GET("/xserver/monitor/feed/page")
    fun subscribedFeeds(
        @Query("nextKey") nextKey: Long? = null,
        @Query("channelId") channelId: String? = null
    ): Flowable<BaseResult<MonitorDataBean?>>

    /**
     * 我的监控页-FEED流
     */
    @GET("/xserver/monitor/channel/detail")
    fun channelDetail(
        @Query("id") id: String? = null
    ): Flowable<BaseResult<ChannelDetailBean?>>

    /**
     * 我的监控页-FEED流
     */
    @POST("/xserver/monitor/mute")
    fun switchMonitorSilent(
        @Body body: RequestBody
    ): Flowable<BaseResult<String?>>

    /**
     * 我的监控页-FEED流
     */
    @GET("/xserver/monitor/stock")
    fun fetchStock(
        @Query("itemId") itemId: String? = null
    ): Flowable<BaseResult<MonitorStockEntity?>>

    /**
     * 获取用户已订阅
     */
    @GET("/xserver/monitor/subscribe/get")
    fun fetchUserSubscribed(): Flowable<BaseResult<MutableList<UserSubscribeBean>>>

    /**
     * 线下监控
     */
    @GET("/xserver/calendar/region/group")
    fun fetchOffline(
        @Query("type") code: String,
        @Query("longitude") longitude: Double? = null,
        @Query("latitude") latitude: Double? = null
    ): Flowable<BaseResult<MutableList<MonitorOfflineEntity>>>

    @GET("/xserver/monitor/group/config")
    fun fetchOnlineGroup(@Query("type") areaId: String)
            : Flowable<BaseResult<MutableList<MonitorGroupEntity>>>

    @POST("/xserver/monitor/subscribe")
    fun switchMonitorState(@Body body: RequestBody): Flowable<BaseResult<String>>

    @GET("/xserver/monitor/blocked/page")
    fun fetchShieldList(@Query("nextKey") nextKey: Long?): Flowable<BaseResult<MonitorDataBean>>

    @POST("/xserver/monitor/block")
    fun monitorBlockItem(@Body body: RequestBody): Flowable<BaseResult<String>>

    @POST("/xserver/calendar/region/subscribe")
    fun switchRegionMonitor(@Body body: RequestBody): Flowable<BaseResult<String>>

    @GET("xserver/calendar/raffle/list/region")
    fun getRegionShopList(
        @Query("regionId") regionId: String,
        @Query("latitude") latitude: Double?,
        @Query("longitude") longitude: Double?
    ): Flowable<BaseResult<ArrayList<HomeRegionRaffleBean?>?>>

    @GET("xserver/calendar/subscribe/region/get")
    fun getUserRegion(
        @Query("latitude") latitude: Double?,
        @Query("longitude") longitude: Double?,
        @Query("filter") filter: String?
    ): Flowable<BaseResult<ArrayList<RegionsBean?>?>>

    @GET("/xserver/monitor/group/filter/menu")
    fun getSubChannelFlow(): Flowable<BaseResult<SubChannelResultEntity>>

    @GET("/xserver/monitor/group/filter/menu")
    suspend fun getSubChannel(): BaseResult<SubChannelResultEntity>

    @GET("xserver/monitor/logs/page")
    suspend fun getMonitorLogs(@Query("nextKey") nextKey: Long?): BaseResult<MonitorLogEntity>
    @GET("/xserver/monitor/subscribe/page")
    fun getRuleChannel(@Query("nextKey") nextKey: Long?): Flowable<BaseResult<MonitorRuleChannelBean>>

    @GET("/xserver/monitor/custom/get")
    suspend fun getChannelRule(@Query("channelId")channelId: String): BaseResult<MonitorRuleBean>

    @POST("/xserver/monitor/custom/upsert")
    suspend fun saveChannelRule(@Body body: RequestBody): BaseResult<MonitorUpdateRuleResultBean>

    @GET("/xserver/monitor/config/get")
    suspend fun getDisturbTime(): BaseResult<MonitorDisturbTimeSetBean>

    @POST("/xserver/monitor/config/save")
    suspend fun setDisturbTime(@Body body: RequestBody): BaseResult<Any>

    @GET("/xserver/monitor/message/status")
    suspend fun getMsgState(): BaseResult<MsgConfigDataEntity>

    @POST("/xserver/monitor/message/config/update")
    suspend fun setMsgState(@Body body: RequestBody): BaseResult<Any>

    @GET("/xserver/monitor/custom/city/list")
    suspend fun fetchQueryCities(@Query("channelId")channelId: String): BaseResult<ArrayList<MonitorCitiesEntity>>

    @POST("/xserver/monitor/custom/cate/update")
    suspend fun upDateMonitorType(@Body body: RequestBody): BaseResult<Any>

    @POST("/xserver/monitor/custom/city/update")
    suspend fun upDateMonitorCity(@Body body: RequestBody): BaseResult<Any>
}