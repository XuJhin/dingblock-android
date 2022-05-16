package cool.dingstock.setting.ui.shield

import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.entity.bean.ApiResult
import cool.dingstock.appbase.entity.bean.mine.AccountInfoBriefEntity
import cool.dingstock.appbase.entity.event.relation.EventShieldChange
import cool.dingstock.appbase.exception.ServiceException
import cool.dingstock.appbase.list.AbsListViewModel
import cool.dingstock.setting.api.SettingRepository
import cool.dingstock.setting.dagger.SettingApiHelper
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

/**
 * Copyright (C), 2015-2020, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2020/11/2 10:51
 * @Version:         1.1.0
 * @Description:
 */
class ShieldViewModel : AbsListViewModel() {

    val refreshResult: MutableLiveData<ApiResult> = MutableLiveData()
    val loadMoreResult: MutableLiveData<ApiResult> = MutableLiveData()
    val cancelResult: MutableLiveData<ApiResult> = MutableLiveData()
    private val pageLiveData: MutableLiveData<Int> = MutableLiveData()

    @Inject
    lateinit var settingRepository: SettingRepository

    init {
        SettingApiHelper.apiSettingComponent.inject(this)
    }

    fun fetchMoreData() {
        val lastPage: Int = pageLiveData.value ?: 0
        val currentPage = lastPage + 1
        pageLiveData.value = currentPage
        realRequest(currentPage, isLoadMore = true)
    }

    fun refresh() {
        pageLiveData.value = 0
        realRequest(pageLiveData.value!!)
    }

    private fun realRequest(page: Int, isLoadMore: Boolean = false) {
        settingRepository.fetchShieldList(page)
                .subscribe({
                    if (!it.err) {
                        if (isLoadMore) {
                            loadMoreResult.postValue(ApiResult.Success(it.res?.list))
                        } else {
                            refreshResult.postValue(ApiResult.Success(it.res?.list))
                        }
                    } else {
                        if (isLoadMore) {
                            loadMoreResult.postValue(ApiResult.Error(ServiceException()))
                        } else {
                            refreshResult.postValue(ApiResult.Error(ServiceException()))
                        }
                    }
                }, {
                    if (isLoadMore) {
                        loadMoreResult.postValue(ApiResult.Error(it))
                    } else {
                        refreshResult.postValue(ApiResult.Error(it))
                    }
                })
    }

    /**
     * 取消屏蔽
     */
    fun cancelShieldAccount(data: AccountInfoBriefEntity) {
        settingRepository.cancelShield(data.id)
                .subscribe({
                    if (it.err) {
                        cancelResult.postValue(ApiResult.Error(ServiceException()))
                    } else {
                        cancelResult.postValue(ApiResult.Success(it))
                        EventBus.getDefault().post(EventShieldChange())
                    }
                }, { error ->
                    cancelResult.postValue(ApiResult.Error(error))
                })

    }
}