package cool.dingstock.monitor.mine.monitor

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.calendar.PriceListResultEntity
import cool.dingstock.appbase.entity.bean.home.bp.ClueProductBean
import cool.dingstock.appbase.entity.bean.monitor.MonitorCenterRegions
import cool.dingstock.appbase.entity.bean.monitor.MonitorDataBean
import cool.dingstock.appbase.entity.bean.monitor.MonitorProductBean
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.calendar.CalendarApi
import cool.dingstock.appbase.net.api.monitor.MonitorApi
import cool.dingstock.lib_base.thread.scheduler.RxSchedulers
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.lib_base.util.CollectionUtils
import cool.dingstock.monitor.dagger.MonitorApiHelper
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.functions.BiFunction
import javax.inject.Inject

/**
 * @author WhenYoung
 *  CreateAt Time 2020/12/25  10:16
 */
class MonitorMineVM : BaseViewModel() {
    enum class MonitorDateStatus {
        //监控地区为空
        RegionNull,

        //有监控地区，但是没有发售
        RegionEmpty,

        //有监控地区，有发售
        RegionNotEmpty
    }

    @Inject
    lateinit var api: MonitorApi

    @Inject
    lateinit var calendarApi: CalendarApi

    val monitorRegionsLiveData = MutableLiveData<List<MonitorCenterRegions>>()

    //监控地区 是否为空
    val monitorRegionStatus = MutableLiveData<MonitorDateStatus>()

    val monitorLiveData = MutableLiveData<List<Any>>()
    val loadMoreMonitorLiveData = MutableLiveData<List<Any>>()
    val subChannelLiveData = MutableLiveData<Boolean>()
    val reset2AllLiveData = MutableLiveData<Boolean>()

    //监控地区，和监控频道都为空
    val stateAllEmpty = MutableLiveData<Boolean>()
    val loadMoreFailed = MutableLiveData<Boolean>()
    val isLoginLiveData = MutableLiveData<Boolean>()

    val priceListLiveData = MutableLiveData<PriceListResultEntity>()


    var nextKey = 0L
    var isLogin = false
    var filterChannelId: String? = null

    init {
        MonitorApiHelper.apiMonitorComponent.inject(this)
    }

    fun checkIsLoginAndRefresh() {
        refresh()
    }

    fun refreshFeeds() {
        nextKey = 0L
        obtainSubscribedFeeds()
            .subscribe({
                if (!it.err && it.res != null) {
                    nextKey = it.res?.nextKey ?: 0
                    val list: ArrayList<Any> = arrayListOf()
                    if (!it.res?.feeds.isNullOrEmpty()) {
                        for (bean in it.res?.feeds!!) {
                            if (bean.type == "coupon" || bean.type == "rebate") {
                                val entity = createClueProductBean(bean)
                                list.add(entity)
                            } else {
                                list.add(bean)
                            }
                        }
                    }
                    monitorLiveData.postValue(list)
                } else {
                }
            }, {
            })
    }

    fun refresh() {
        nextKey = 0L
        if (!LoginUtils.isLogin()) {
            postStateSuccess()
            isLogin = false
            isLoginLiveData.postValue(false)
            return
        }
        isLogin = true
        isLoginLiveData.postValue(true)
        api.getSubChannelFlow()
            .flatMap {
                val subChanneled = !CollectionUtils.isEmpty(it.res?.groups)
                subChannelLiveData.postValue(subChanneled)
                //这里判断我的筛选条件是否被取消订阅,如果被取消就重置为 全部 筛选
                var isContains = false
                it.res?.groups?.forEach { entity ->
                    entity.channels.forEach { channel ->
                        if (channel.id == filterChannelId) {
                            isContains = true
                        }
                    }
                }
                if (!isContains) {
                    filterChannelId = null
                    reset2AllLiveData.postValue(true)
                }
                return@flatMap obtainSubscribedFeeds()
            }
            .zipWith(subscribedRegions(), { t1, t2 ->
                val map = hashMapOf("feeds" to t1.res, "regions" to t2.res)
                map
            }, true)
            .subscribe({ map ->
                //加载成功
                postStateSuccess()
                val regions = map["regions"] as? MutableList<MonitorCenterRegions>?
                val feeds = map["feeds"] as? MonitorDataBean
                if (checkAllisEmpty(regions, feeds)) {
                    stateAllEmpty.postValue(true)
                    return@subscribe
                } else {
                    stateAllEmpty.postValue(false)
                }
                val list: ArrayList<Any> = arrayListOf()
                if (!feeds?.feeds.isNullOrEmpty()) {
                    for (bean in feeds?.feeds!!) {
                        if (bean.type == "coupon" || bean.type == "rebate") {
                            val entity = createClueProductBean(bean)
                            list.add(entity)
                        } else {
                            list.add(bean)
                        }
                    }
                }
                monitorLiveData.postValue(list)
                nextKey = feeds?.nextKey ?: 0
                //监控地区 线下
                if (regions == null) {
                    monitorRegionStatus.postValue(MonitorDateStatus.RegionNull)
                } else {
                    val filterList = regions.filter { it.raffleCount > 0 }
                    if (filterList.isEmpty()) {
                        monitorRegionStatus.postValue(if (regions.isEmpty()) MonitorDateStatus.RegionNull else MonitorDateStatus.RegionEmpty)
                    } else {
                        monitorRegionStatus.postValue(MonitorDateStatus.RegionNotEmpty)
                        monitorRegionsLiveData.postValue(filterList)
                    }
                }
            }, {
                postStateError(it.message ?: "加载失败")
            })
    }

    //订阅的地区信息
    private fun subscribedRegions(): Flowable<BaseResult<MutableList<MonitorCenterRegions>?>> {
        return api.subscribedRegions()
    }

    private fun obtainSubscribedFeeds(): Flowable<BaseResult<MonitorDataBean?>> {
        return api.subscribedFeeds(nextKey, filterChannelId)
    }

    //订阅的频道信息
    fun subscribedNexFeeds() {
        obtainSubscribedFeeds()
            .compose(RxSchedulers.netio_main())
            .subscribe({
                if (!it.err && it.res != null) {
                    nextKey = it.res?.nextKey ?: 0
                    val list: ArrayList<Any> = arrayListOf()
                    if (!it.res?.feeds.isNullOrEmpty()) {
                        for (bean in it.res?.feeds!!) {
                            if (bean.type == "coupon" || bean.type == "rebate") {
                                val entity = createClueProductBean(bean)
                                list.add(entity)
                            } else {
                                list.add(bean)
                            }
                        }
                    }
                    loadMoreMonitorLiveData.postValue(list)
                } else {
                    loadMoreFailed.postValue(true)
                }
            }, {
                loadMoreFailed.postValue(true)
            })
    }

    private fun checkAllisEmpty(
        regions: List<MonitorCenterRegions>?,
        feeds: MonitorDataBean?
    ): Boolean {
        //监控频道是否为空
        val feedsIsEmpty = feeds?.feeds?.size ?: 0 == 0
        var regionsIsEmpty = false
        //监控地区是否为空
        if (regions == null || regions.size == 0) {
            regionsIsEmpty = true
        }
        return feedsIsEmpty && regionsIsEmpty
    }

    fun bizAction(
        bizId: String,
        blocked: Boolean,
        success: () -> Unit,
        failed: (msg: String) -> Unit
    ) {
        api.monitorBlockItem(bizId, blocked)
            .subscribe({
                if (it.err) {
                    failed(it.msg)
                    return@subscribe
                }
                success()
            }, {
                failed(it.message ?: it.localizedMessage)
            })
    }

    fun searchPrice(id: String?) {
        if (TextUtils.isEmpty(id)) {
            return
        }
        postAlertLoading()
        calendarApi.priceList(id ?: "")
            .subscribe({
                postAlertHide()
                if (!it.err && it.res != null) {
                    priceListLiveData.postValue(it.res!!)
                } else {
                    shortToast(it.msg)
                }
            }, {
                postAlertHide()
                shortToast(it.message)
            })

    }


    private fun createClueProductBean(bean: MonitorProductBean) =
        ClueProductBean(
            bean.id,
            bean.createdAt,
            bean.channelId,
            bean.detail,
            bean.title,
            bean.link,
            bean.imageUrl,
            bean.bizId,
            bean.stock,
            bean.blocked,
            bean.couponPrice,
            bean.type,
            bean.couponConditions,
            bean.channel
        )
}