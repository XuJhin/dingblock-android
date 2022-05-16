package cool.dingstock.mine.ui.exchange.good

import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.entity.bean.mine.ScoreRefreshEvent
import cool.dingstock.appbase.entity.bean.score.ExchangeGoodsDetailEntity
import cool.dingstock.appbase.entity.bean.score.ScoreExchangeResultEntity
import cool.dingstock.appbase.entity.bean.shop.UserAddressEntity
import cool.dingstock.appbase.entity.event.update.EventMedalStateChange
import cool.dingstock.appbase.entity.event.update.EventScoreChange
import cool.dingstock.appbase.entity.event.update.EventUserVipChange
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.account.AccountApi
import cool.dingstock.appbase.net.api.mine.MineApi
import cool.dingstock.mine.dagger.MineApiHelper
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/5/25 14:15
 * @Version:         1.1.0
 * @Description:
 */
class MineExchangeGoodsViewModel : BaseViewModel() {
    @Inject
    lateinit var mineApi: MineApi

    @Inject
    lateinit var accountApi: AccountApi
    val exchangeLiveData = MutableLiveData<ScoreExchangeResultEntity>()
    val goodDetailLiveData = MutableLiveData<ExchangeGoodsDetailEntity>()
    var isNoAddress = MutableLiveData<Boolean>()

    var getGoodSuccessLiveData = MutableLiveData<Boolean>()

    var addressList: List<UserAddressEntity> = arrayListOf()

    init {
        MineApiHelper.apiMineComponent.inject(this)
    }

    var name = ""
    var phone = ""
    var address = ""
    var id = ""
    var eventId = ""
    var scoreNum = -1

    fun chooseAddress() {
        mineApi.submitinformation(id, name, phone.replace("+86 ", ""), address)
            .subscribe({
                if (!it.err) {
                    EventBus.getDefault().post(ScoreRefreshEvent("REFRESH"))
                    getGoodSuccessLiveData.postValue(true)
                } else {
                    postAlertError(it.msg)
                }
            }, {
                it.message?.let { it1 -> postAlertError(it1) }
            })
    }

    fun fetchAddressDataAndUI() {
        accountApi.getAddressList()
            .subscribe({
                if (!it.err && it.res != null) {
                    if (!it.res.isNullOrEmpty()) {
                        addressList = it.res!!
                    }
                    isNoAddress.postValue(it.res.isNullOrEmpty())
                } else {
                    postAlertError(it.msg)
                }
            }, { err ->
                err.message?.let { postAlertError(it) }
            })
    }

    fun requestGoodsDetail(productId: String) {
        postStateLoading("加载中……")
        mineApi.scoreDetail(productId)
            .subscribe(
                {
                    if (!it.err) {
                        it.res?.let { entity ->
                            goodDetailLiveData.postValue(entity)
                            postStateSuccess()
                        }
                    } else {
                        postAlertError(it.msg)
                    }
                },
            )
            {
                postStateError(it.localizedMessage)
            }
    }

    fun scoreExchange(productId: String) {
        postAlertLoading()
        mineApi.scoreExchange(productId)
            .subscribe({
                postAlertHide()
                if (!it.err) {
                    EventBus.getDefault().post(EventScoreChange(true))
                    it.res?.let { entity ->
                        exchangeLiveData.postValue(entity)
                    }
                    //						refreshInfo()
                    //根据type 来处理,如果是会员则发送事件更新会员
                    if (it.res?.type == ScoreExchangeResultEntity.Type.vip) {
                        accountApi.getUserByNet()
                            .subscribe({
                                if (!it.err) {
                                    EventBus.getDefault().post(EventUserVipChange(true))
                                }
                            }, {})
                    } else if (it.res?.type == ScoreExchangeResultEntity.Type.achievement) {
                        EventBus.getDefault().post(EventMedalStateChange(id))
                    }
                } else {
                    toastLiveData.postValue(it.msg)
                }
            }, {
                postAlertHide()
                toastLiveData.postValue(it.message)
            })
    }
}