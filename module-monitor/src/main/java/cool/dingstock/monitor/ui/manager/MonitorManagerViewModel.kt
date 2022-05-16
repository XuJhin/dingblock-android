package cool.dingstock.monitor.ui.manager

import android.location.Location
import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.entity.bean.monitor.*
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.mvvm.status.ViewStatus
import cool.dingstock.appbase.net.api.monitor.MonitorApi
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.monitor.dagger.MonitorApiHelper
import javax.inject.Inject

/**
 * Copyright (C), 2015-2020, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2020/12/25 10:46
 * @Version:         1.1.0
 * @Description:
 */
class MonitorManagerViewModel : BaseViewModel() {
    companion object {
        const val CHINA_REGION = "cn_region"
        private const val MENU_TYPE_REGION = "region"
        private const val MENU_TYPE_CHANNEL = "channel"
        const val MENU_TYPE_SUBSCRIBED = "subscribed"
    }

    //监控menu
    val monitorMenuLiveData: MutableLiveData<MutableList<MonitorMenuBean?>> =
        MutableLiveData()

    //用于已经订阅的频道和地区
    val userSubscribeLiveData: MutableLiveData<MutableList<UserSubscribeBean>> = MutableLiveData()

    //线上频道
    val onLineListLiveData: MutableLiveData<MutableList<MonitorGroupEntity>> = MutableLiveData()

    //线下频道
    val offlineListLiveData: MutableLiveData<MutableList<MonitorOfflineEntity>> = MutableLiveData()
    val groupState: MutableLiveData<ViewStatus> = MutableLiveData()

    //右边导航栏隐藏显示状态
    val indexBarState: MutableLiveData<Boolean> = MutableLiveData()

    //默认选择的TAB

    var typeCode: String? = null

    @Inject
    lateinit var monitorApi: MonitorApi

    var lastSelectedMenu: MonitorMenuItemEntity? = null
    var lastSelectedPosition = -1

    init {
        MonitorApiHelper.apiMonitorComponent.inject(this)
    }

    private var mLocationInfo: Location? = null

    private fun fetchOfflineChannel(areaId: String) {
        if (areaId.isEmpty()) {
            return
        }
        if (areaId == CHINA_REGION) {
            indexBarState.postValue(true)
            if (mLocationInfo == null) {
                fetchOfflineChannel(null, null, areaId)
            } else {
                fetchOfflineChannel(mLocationInfo!!.longitude, mLocationInfo!!.latitude, areaId)
            }
        } else {
            indexBarState.postValue(false)
            fetchOfflineChannel(null, null, areaId)
        }
    }

    /**
     * 获取线下频道列表
     */
    private fun fetchOfflineChannel(longitude: Double?, altitude: Double?, areaId: String) {
        monitorApi.fetchOffline(longitude = longitude, latitude = altitude, areaId = areaId)
            .subscribe({
                if (it.err) {
                    groupState.postValue(ViewStatus.Error(it.msg))
                    return@subscribe
                }
                postStateSuccess()
                if (it.res.isNullOrEmpty()) {
                    groupState.postValue(ViewStatus.Empty("空"))
                }
                it.res?.forEach { entity ->
                    if (entity.data.isNullOrEmpty()) {
                        return@forEach
                    }
                    entity.data.forEach { regionChannelBean ->
                        regionChannelBean.sectionName = entity.title
                            .toCharArray()
                            .first()
                            .toString()
                    }
                }
                offlineListLiveData.postValue(it.res)
            }, {
                groupState.postValue(ViewStatus.Error(it.message ?: "数据错误"))
            })
    }

    /**
     * 获取频道菜单
     */
    fun fetchMonitorMenu(source: String? = null) {
        monitorApi.fetchMonitorMenu(source).subscribe({
            if (it.err) {
                postStateError(it.msg)
                return@subscribe
            }

            if (it.res.isNullOrEmpty()) {
                postStateEmpty()
            } else {
                //当已订阅为空时,将列表的第一个设置为选中
                if (setupSelectedItem(it.res!!)) return@subscribe
                monitorMenuLiveData.postValue(it.res as MutableList<MonitorMenuBean?>?)
                fetchSelectedMenu(lastSelectedMenu)
            }
        }, {
            postStateError(it.message ?: "网络错误")
        })
    }

    /**
     * 获取左边menu
     */
    fun fetchSelectedMenu(value: MonitorMenuItemEntity?) {
        if (value == null) {
            return
        }
        when (value.type) {
            MENU_TYPE_CHANNEL -> {
                indexBarState.postValue(false)
                loadChannelListOfGroup(value.code ?: "")
            }
            MENU_TYPE_REGION -> {
                indexBarState.postValue(true)
                fetchOfflineChannel(value.code ?: "")
            }
            MENU_TYPE_SUBSCRIBED -> {
                indexBarState.postValue(false)
                fetchUserSubscribed()
            }
            else -> {
                return
            }
        }
    }

    /**
     * 设置选中的Item
     */
    private fun setupSelectedItem(data: List<MonitorMenuBean?>): Boolean {
        if (data.isNullOrEmpty()) {
            postStateEmpty()
            return true
        }
        var subjectItem: MonitorMenuItemEntity? = null

        if (lastSelectedMenu != null) {
            for (menu in data) {
                menu?.list?.let {
                    for (menuItem in it) {
                        if (menuItem.code == lastSelectedMenu!!.code) {
                            menuItem.isSelected = true
                            subjectItem = menuItem
                        } else {
                            menuItem.isSelected = false
                        }
                    }
                }
            }
            return false
        }
        if (typeCode == null) {
            //遍历列表 找到isSelected为True的item
            val filterSelected = data.filter { it?.selectedSubject() != null }
            //未找到,使列表的 第一个不为空的Group 选中
            if (!filterSelected.isNullOrEmpty()) {
                //找到对应的Group
                subjectItem = filterSelected[0]?.selectedSubject()
            }
        } else {
            for (menu in data) {
                menu?.list?.let {
                    for (menuItem in it) {
                        if (menuItem.code == typeCode) {
                            menuItem.isSelected = true
                            subjectItem = menuItem
                        } else {
                            menuItem.isSelected = false
                        }
                    }
                }
            }
        }
        if (subjectItem == null) {
            val first = data.first { it?.list != null && it.list.size > 0 }
            subjectItem = first?.list?.get(0)
            subjectItem?.let { entity ->
                entity.isSelected = true
            }
        }
        lastSelectedMenu = subjectItem
        return false
    }

    private fun loadChannelListOfGroup(areaId: String) {
        monitorApi.fetchOnlineGroup(areaId)
            .subscribe({
                postStateSuccess()
                if (it.err) {
                    groupState.postValue(ViewStatus.Empty(it.msg))
                    return@subscribe
                }
                if (it.res.isNullOrEmpty()) {
                    groupState.postValue(ViewStatus.Empty("无可用频道"))
                } else {
                    onLineListLiveData.postValue(it.res)
                }
            }, {
                groupState.postValue(ViewStatus.Empty(it.message ?: it.localizedMessage))
            })
    }

    /**
     * 获取用户订阅内容
     */
    private fun fetchUserSubscribed() {
        if (!LoginUtils.isLogin()) {
            return
        }
        monitorApi.fetchUserSubscribed()
            .subscribe({
                postStateSuccess()
                if (it.err) {
                    groupState.postValue(ViewStatus.Error(it.msg))
                    return@subscribe
                }
                val dataList = it.res
                if (dataList.isNullOrEmpty() || (dataList.filter { entity -> !entity.data.isNullOrEmpty() }
                        .isNullOrEmpty())) {
                    groupState.postValue(ViewStatus.Empty("数据为空"))
                    return@subscribe
                }
                userSubscribeLiveData.postValue(dataList)
            }, {
                postStateError()
                groupState.postValue(ViewStatus.Error(it.localizedMessage))
            })
    }

    fun updateLocation(location: Location?) {
        mLocationInfo = location
    }
}