package cool.dingstock.appbase.net.api.monitor

import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.home.HomeRegionRaffleBean
import cool.dingstock.appbase.entity.bean.mine.RegionsBean
import cool.dingstock.appbase.entity.bean.monitor.*
import cool.dingstock.appbase.net.retrofit.api.BaseApi
import cool.dingstock.appbase.net.retrofit.api.handError
import cool.dingstock.lib_base.thread.scheduler.RxSchedulers
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Retrofit
import java.util.*

class MonitorApi(retrofit: Retrofit) : BaseApi<MonitorApiService>(retrofit) {
    fun fetchRecommendMonitor(): Flowable<BaseResult<ArrayList<MonitorRecommendEntity>>> {
        return service.fetchRecommendMonitor()
            .compose(RxSchedulers.netio_main())
            .handError()
    }

    fun fetchRecommendSubject(subjectId: String = ""): Flowable<BaseResult<MonitorRecommendEntity>> {
        return service.fetchMonitorSubject(subjectId)
            .compose(RxSchedulers.netio_main())
            .handError()
    }

    /**
     * 获取监控Menu
     */
    fun fetchMonitorMenu(source: String?): Flowable<BaseResult<MutableList<MonitorMenuBean>>> {
        return service.fetchMonitorMenu(source)
            .compose(RxSchedulers.netio_main())
            .handError()
    }

    /**
     * 获取用户已订阅列表
     */
    fun fetchUserSubscribed(): Flowable<BaseResult<MutableList<UserSubscribeBean>>> {
        return service.fetchUserSubscribed()
            .compose(RxSchedulers.netio_main())
            .handError()
    }

    fun fetchOffline(longitude: Double?, latitude: Double?, areaId: String)
            : Flowable<BaseResult<MutableList<MonitorOfflineEntity>>> {
        return service.fetchOffline(
            code = areaId,
            longitude = longitude,
            latitude = latitude
        )
            .compose(RxSchedulers.netio_main())
            .handError()
    }

    /**
     * 我的监控页-上方订阅地区日历
     */
    fun subscribedRegions(): Flowable<BaseResult<MutableList<MonitorCenterRegions>?>> {
        return service.subscribedRegions()
            .compose(RxSchedulers.netio_main())
            .handError()
    }

    /**
     * 监控页-FEED流
     */
    fun subscribedFeeds(
        nexKey: Long?,
        channelId: String? = null
    ): Flowable<BaseResult<MonitorDataBean?>> {
        return service.subscribedFeeds(nexKey, channelId)
            .compose(RxSchedulers.netio_main())
            .handError()
    }

    /**
     * 监控页-频道详情
     */
    fun channelDetail(id: String?): Flowable<BaseResult<ChannelDetailBean?>> {
        return service.channelDetail(id)
            .compose(RxSchedulers.netio_main())
            .handError()
    }

    /**
     * 监控页-静音与否
     */
    fun switchMonitorSilent(channelId: String?, muted: Boolean): Flowable<BaseResult<String?>> {
        val body = ParameterBuilder()
            .add("channelId", channelId)
            .add("muted", muted)
            .toBody()
        return service.switchMonitorSilent(body)
            .compose(RxSchedulers.netio_main())
            .handError()
    }

    /**
     * 监控页-货量
     */
    fun fetchStock(itemId: String?): Flowable<BaseResult<MonitorStockEntity?>> {
        return service.fetchStock(itemId)
            .compose(RxSchedulers.netio_main())
            .handError()
    }

    fun fetchOnlineGroup(areaId: String): Flowable<BaseResult<MutableList<MonitorGroupEntity>>> {
        return service.fetchOnlineGroup(areaId)
            .compose(RxSchedulers.netio_main())
            .handError()
    }

    fun switchMonitorState(id: String, targetState: Boolean): Flowable<BaseResult<String>> {
        val body = ParameterBuilder()
            .add("channelId", id)
            .add("isSubscribed", targetState)
            .toBody()
        return service.switchMonitorState(body)
            .compose(RxSchedulers.netio_main())
            .handError()
    }

    fun switchRegionMonitor(id: String, targetState: Boolean): Flowable<BaseResult<String>> {
        val body = ParameterBuilder()
            .add("regionId", id)
            .add("isSubscribed", targetState)
            .toBody()
        return service.switchRegionMonitor(body)
            .compose(RxSchedulers.netio_main())
            .handError()
    }

    fun fetchShieldList(nextKey: Long?): Flowable<BaseResult<MonitorDataBean>> {
        return service.fetchShieldList(nextKey)
            .compose(RxSchedulers.netio_main())
            .handError()
    }

    fun monitorBlockItem(bizId: String, blocked: Boolean): Flowable<BaseResult<String>> {
        val body = ParameterBuilder()
            .add("bizId", bizId)
            .add("block", blocked)
            .toBody()
        return service.monitorBlockItem(body)
            .compose(RxSchedulers.netio_main())
            .handError()
    }


    fun getRegionShopList(
        regionId: String,
        latitude: Double?,
        longitude: Double?
    ): Flowable<BaseResult<ArrayList<HomeRegionRaffleBean?>?>> {
        return service.getRegionShopList(regionId, latitude, longitude)
            .compose(RxSchedulers.netio_main()).handError()
    }

    fun getUserRegion(
        latitude: Double?,
        longitude: Double?,
        filter: ArrayList<String>?
    ): Flowable<BaseResult<ArrayList<RegionsBean?>?>> {
        val toString = Arrays.toString(filter?.toArray())
        return service.getUserRegion(latitude, longitude, toString)
            .compose(RxSchedulers.netio_main()).handError()
    }

    fun getSubChannelFlow(): Flowable<BaseResult<SubChannelResultEntity>> {
        return service.getSubChannelFlow().compose(RxSchedulers.netio_main()).handError()
    }

    suspend fun getSubChannel(): BaseResult<SubChannelResultEntity> {
        return service.getSubChannel()
    }

    suspend fun getMonitorLogs(nexKey: Long?): BaseResult<MonitorLogEntity> {
        return service.getMonitorLogs(nexKey)
    }

    fun getRuleChannel(nextKey: Long?): Flowable<BaseResult<MonitorRuleChannelBean>> {
        return service.getRuleChannel(nextKey).compose(RxSchedulers.netio_main()).handError()
    }

    suspend fun getChannelRule(channelId: String): Flow<BaseResult<MonitorRuleBean>> = flow {
        emit(service.getChannelRule(channelId))
    }.flowOn(Dispatchers.IO)

    suspend fun saveChannelRule(
        channelId: String,
        keywords: List<String>,
        sizes: List<String>
    ): Flow<BaseResult<MonitorUpdateRuleResultBean>> = flow {
        val body = ParameterBuilder()
            .add("channelId", channelId)
            .add("keywords", keywords)
            .add("sizes", sizes)
            .toBody()
        emit(service.saveChannelRule(body))
    }.flowOn(Dispatchers.IO)


    suspend fun getDisturbTime(): BaseResult<MonitorDisturbTimeSetBean> {
        return service.getDisturbTime()
    }

    suspend fun setDisturbTime(
        isSilent: Boolean,
        startHour: Int,
        endHour: Int
    ): BaseResult<Any> {
        val body = ParameterBuilder()
            .add("isSilent", isSilent)
            .add("startHour", startHour)
            .add("endHour", endHour)
            .toBody()
        return service.setDisturbTime(body)
    }

    suspend fun getMsgState(): BaseResult<MsgConfigDataEntity> {
        return service.getMsgState()
    }

    suspend fun setMsgState(enable: Boolean): BaseResult<Any> {
        val body = ParameterBuilder()
            .add("enable", enable)
            .toBody()
        return service.setMsgState(body)
    }

    suspend fun fetchQueryCities(
        channelId: String
    ): BaseResult<ArrayList<MonitorCitiesEntity>> {
        return service.fetchQueryCities(channelId)
    }

    suspend fun upDateMonitorType(
        channelId: String,
        categories: ArrayList<String>
    ): BaseResult<Any> {
        val body = ParameterBuilder()
            .add("channelId", channelId)
            .add("categories", categories)
            .toBody()
        return service.upDateMonitorType(body)
    }

    suspend fun upDateMonitorCity(
        channelId: String,
        city: String,
        select: Boolean
    ): BaseResult<Any> {
        val body = ParameterBuilder()
            .add("channelId", channelId)
            .add("city", city)
            .add("select", select)
            .toBody()
        return service.upDateMonitorCity(body)
    }
}