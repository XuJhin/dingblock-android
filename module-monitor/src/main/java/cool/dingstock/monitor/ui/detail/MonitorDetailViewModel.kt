package cool.dingstock.monitor.ui.detail

import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cool.dingstock.appbase.constant.ServerConstant
import cool.dingstock.appbase.entity.bean.calendar.PriceListResultEntity
import cool.dingstock.appbase.entity.bean.home.bp.ClueProductBean
import cool.dingstock.appbase.entity.bean.mine.MineVipChargeEntity
import cool.dingstock.appbase.entity.bean.monitor.ChannelInfoEntity
import cool.dingstock.appbase.entity.bean.monitor.MonitorProductBean
import cool.dingstock.appbase.ext.doRequest
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.calendar.CalendarApi
import cool.dingstock.appbase.net.api.monitor.MonitorApi
import cool.dingstock.appbase.net.mobile.MobileHelper
import cool.dingstock.appbase.net.parse.ParseCallback
import cool.dingstock.lib_base.util.ToastUtil
import cool.dingstock.monitor.dagger.MonitorApiHelper
import javax.inject.Inject

class MonitorDetailViewModel : BaseViewModel() {
    val channelInfoLiveData = MutableLiveData<ChannelInfoEntity>()

    val listLiveData = MutableLiveData<List<MonitorProductBean>>()
    val loadMoreMonitorLiveData = MutableLiveData<List<MonitorProductBean>>()

    val couponListLiveData = MutableLiveData<List<ClueProductBean>>()
    val couponLoadMoreMonitorLiveData = MutableLiveData<List<ClueProductBean>>()

    //    val refreshMoreErrorLiveData = MutableLiveData<String>()
    val monitorToastLiveData = MutableLiveData<String>()
    val routeData = MutableLiveData<String>()
    var dismissDialog = MutableLiveData<Boolean>()
    val priceListLiveData = MutableLiveData<PriceListResultEntity>()

    private var nextKey: Long = 0
    var isRefresh = true
    private var channelId: String = ""
    var isOffline = false

    @Inject
    lateinit var api: MonitorApi

    @Inject
    lateinit var calendarApi: CalendarApi

    init {
        MonitorApiHelper.apiMonitorComponent.inject(this)
    }

    /**
     * 校验参数
     */
    private fun checkParams(): Boolean {
        if (channelId.isEmpty()) {
            return false
        }
        return true
    }

    fun refresh() {
        if (!checkParams()) {
            return
        }
        isRefresh = true
        nextKey = 0L
        fetchData()
    }

    fun loadMore() {
        if (!checkParams()) {
            return
        }
        isRefresh = false
        loadMoreData()
    }

    private fun fetchData() {
        api.channelDetail(channelId)
            .subscribe({ res ->
                if (!res.err && res.res != null) {
                    nextKey = res.res?.nextKey ?: 0
                    isOffline = res.res?.channel?.isOffline ?: false
                    res.res?.channel?.let { channelInfoLiveData.postValue(it) }
                    res.res?.feeds?.map { feedsBean ->
                        feedsBean.isShowRightArrow = false
                    }
                    if (res.res?.feeds!![0].type == "coupon" || res.res?.feeds!![0].type == "rebate") {
                        res.res?.feeds.let {
                            val list: ArrayList<ClueProductBean> = arrayListOf()
                            for (bean in res.res?.feeds!!) {
                                val entity = ClueProductBean(
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
                                list.add(entity)
                            }
                            couponListLiveData.postValue(list)
                        }
                    } else {
                        res.res?.feeds?.let { listLiveData.postValue(it) }
                    }
                }
            }, {
                updateEmptyData()
            })

    }

    private fun loadMoreData() {
        api.subscribedFeeds(nextKey, channelId)
            .subscribe({ res ->
                if (!res.err && res.res != null) {
                    nextKey = res.res?.nextKey ?: 0
                    if (res.res?.feeds!![0].type == "coupon" || res.res?.feeds!![0].type == "rebate") {
                        res.res?.feeds.let {
                            val list: ArrayList<ClueProductBean> = arrayListOf()
                            for (bean in res.res?.feeds!!) {
                                val entity = ClueProductBean(
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
                                list.add(entity)
                            }
                            couponLoadMoreMonitorLiveData.postValue(list)
                        }
                    } else {
                        res.res?.feeds.let {
                            loadMoreMonitorLiveData.postValue(it)
                        }
                    }
                } else {
                    updateEmptyData()
                }
            }, {
                updateEmptyData()
            })
    }

    private fun updateEmptyData() {
        if (isRefresh) {
            listLiveData.postValue(emptyList())
            couponListLiveData.postValue(emptyList())
        } else {
            loadMoreMonitorLiveData.postValue(emptyList())
            couponLoadMoreMonitorLiveData.postValue(emptyList())
        }
    }

    fun setChannelId(id: String) {
        this.channelId = id
    }

    fun switchMonitorState(id: String, state: Boolean, success: () -> Unit) {
        api.switchMonitorState(id, state)
            .subscribe({
                success()
            }, {
                monitorToastLiveData.postValue(it.message)
            })
    }

    fun switchMonitorSilent(context: Context, id: String, silent: Boolean, openToast: Boolean) {
        api.switchMonitorSilent(id, silent)
            .subscribe({ res ->
                if (!res.err) {
                    if (openToast) {
                        ToastUtil.getInstance()._short(context, if (!silent) "已开启提示" else "已关闭提示")
                    }
                } else {
                    if (openToast) {
                        monitorToastLiveData.postValue(res.msg)
                    }
                }
            }, { err ->
                if (openToast) {
                    monitorToastLiveData.postValue(err.message)
                }
            })
    }

    fun getCloudUrl(s: String) {
        MobileHelper.getInstance().getCloudUrl(s, object : ParseCallback<MineVipChargeEntity> {
            override fun onSucceed(data: MineVipChargeEntity) {
                routeData.postValue(data.url)
            }

            override fun onFailed(errorCode: String, errorMsg: String) {
                monitorToastLiveData.postValue(errorMsg)
            }
        })
    }

    fun vipUrl() {
        dismissDialog.postValue(false)
        MobileHelper.getInstance().getDingUrl(ServerConstant.Function.VIP_INSTRUCTION,
            object : ParseCallback<String?> {
                override fun onFailed(errorCode: String, errorMsg: String) {
                    dismissDialog.postValue(true)
                }

                override fun onSucceed(data: String?) {
                    dismissDialog.postValue(true)
                    routeData.postValue(data!!)
                }
            })
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

    fun upDateMonitorType(channelId: String, monitorTypes: ArrayList<String>) {
        viewModelScope.doRequest({ api.upDateMonitorType(channelId, monitorTypes) }, { result ->
            if (!result.err) {
                result.res?.let {
                    refresh()
                }
            } else {
                shortToast(result.msg)
            }
        }, {
            shortToast(it.message)
        })
    }
}