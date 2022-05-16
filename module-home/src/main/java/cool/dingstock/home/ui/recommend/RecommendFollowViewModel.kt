package cool.dingstock.home.ui.recommend

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.entity.bean.ApiResult
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.mine.AccountInfoBriefEntity
import cool.dingstock.appbase.list.AbsListViewModel
import cool.dingstock.appbase.net.api.home.HomeApi
import cool.dingstock.home.dagger.HomeApiHelper
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import javax.inject.Inject

/**
 * Copyright (C), 2015-2020, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2020/10/29 15:30
 * @Version:         1.1.0
 * @Description:
 */
class RecommendFollowViewModel : AbsListViewModel() {
    companion object {
        const val TAG = "推荐关注"
    }

    val resultLiveData: MutableLiveData<ApiResult> = MutableLiveData()
    val submitClickable: MutableLiveData<Boolean> = MutableLiveData()
    val followLiveData: MutableLiveData<ApiResult> = MutableLiveData()
    val selectList: MutableList<String> = arrayListOf()

    init {
        HomeApiHelper.apiHomeComponent.inject(this)
    }

    @Inject
    lateinit var homeApi: HomeApi

    /**
     * 获取推荐用户
     */
    @SuppressLint("CheckResult")
    fun fetchRecommendAccounts(times: Int = 0) {
        homeApi.fetchRecommendAccount(times = times)
                .subscribe(object : Subscriber<BaseResult<MutableList<AccountInfoBriefEntity>>> {
                    override fun onSubscribe(s: Subscription?) {
                        s?.request(Long.MAX_VALUE)
                    }

                    override fun onNext(it: BaseResult<MutableList<AccountInfoBriefEntity>>) {
                        if (!it.err) {
                            selectList.clear()
                            if (it.res.isNullOrEmpty()) {
                                resultLiveData.postValue(ApiResult.Success<MutableList<AccountInfoBriefEntity>>(mutableListOf()))
                            } else {
                                resultLiveData.postValue(ApiResult.Success(it.res))
                                it.res?.map { entity -> entity.id }?.let { idList -> selectList.addAll(idList) }
                            }
                            postSubmitBtnState()
                        }
                    }

                    override fun onError(t: Throwable?) {
                        resultLiveData.postValue(ApiResult.Error(t))
                    }

                    override fun onComplete() {
                    }
                })
    }

    fun updateSelectedList(param: List<String>) {
        selectList.clear()
        selectList.addAll(param)
        postSubmitBtnState()
    }

    private fun postSubmitBtnState() {
        if (selectList.isNullOrEmpty()) {
            submitClickable.postValue(false)
        } else {
            submitClickable.postValue(true)
        }

    }

    /**
     * 关注用户
     */
    fun followAccounts() {
        if (selectList.isEmpty()) {
            return
        }
        homeApi.followAccounts(idList = selectList)
                .subscribe(object : Subscriber<BaseResult<Boolean>> {
                    override fun onSubscribe(s: Subscription?) {
                        s?.request(Long.MAX_VALUE)
                    }

                    override fun onNext(it: BaseResult<Boolean>) {
                        if (!it.err) {
                            followLiveData.postValue(ApiResult.Success(it.res))
                        }
                    }

                    override fun onError(t: Throwable?) {
                        followLiveData.postValue(ApiResult.Error(t))
                    }

                    override fun onComplete() {
                    }
                })

    }


}


