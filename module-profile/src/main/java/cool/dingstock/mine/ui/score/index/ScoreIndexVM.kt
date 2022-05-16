package cool.dingstock.mine.ui.score.index

import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.score.ScoreExchangeResultEntity
import cool.dingstock.appbase.entity.bean.score.ScoreIndexInfoEntity
import cool.dingstock.appbase.entity.bean.score.ScoreIndexUserInfoEntity
import cool.dingstock.appbase.entity.bean.score.ScoreTaskItemEntity
import cool.dingstock.appbase.entity.event.update.EventScoreChange
import cool.dingstock.appbase.entity.event.update.EventUserVipChange
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.account.AccountApi
import cool.dingstock.appbase.net.api.common.CommonApi
import cool.dingstock.appbase.net.api.mine.MineApi
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.mine.dagger.MineApiHelper
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class ScoreIndexVM : BaseViewModel() {

    @Inject
    lateinit var commonApi: CommonApi

    @Inject
    lateinit var mineApi: MineApi

    @Inject
    lateinit var accountApi: AccountApi

    val infoLiveData = MutableLiveData<ScoreIndexInfoEntity>()
    val refreshInfoLiveData = MutableLiveData<ScoreIndexInfoEntity>()
    val signLiveData = MutableLiveData<ScoreIndexUserInfoEntity>()
    val switchAlertLiveData = MutableLiveData<Boolean>()
    val exchangeLiveData = MutableLiveData<ScoreExchangeResultEntity>()
    val refreshFailedLivedata = MutableLiveData<String>()

    init {
        MineApiHelper.apiMineComponent.inject(this)
    }

    fun gotoIntegralDetail() {
        commonApi.commonInfo("integralDetail")
            .subscribe({
                if (!it.err) {
                    it.res?.url?.let { url ->
                        routerLiveData.postValue(url)
                    }
                }
            }, {

            })
    }

    fun fetchData() {
        postStateLoading()
        mineApi.scoreIndexInfo()
            .subscribe({
                if (!it.err) {
                    postStateSuccess()
                    infoLiveData.postValue(it.res)
                } else {
                    postStateError(it.msg)
                }
            }, {
                postStateError(it.message ?: "")
            })
    }

    fun refreshInfo() {
        mineApi.scoreIndexInfo()
            .subscribe({
                if (!it.err) {
                    refreshInfoLiveData.postValue(it.res)
                }
            }, {
                refreshFailedLivedata.postValue(it.message)
            })
    }

    fun startSign() {
        postAlertLoading()
        mineApi.scoreSign()
            .subscribe({
                postAlertHide()
                if (!it.err) {
                    signLiveData.postValue(it.res)
                    UTHelper.commonEvent(UTConstant.Score.IntegralP_click_Sign, "result", "签到成功")
                    //判断是否获取了会员。获取了就发送事件
                    if (it.res?.reward == "VIP1" || it.res?.reward == "VIP3") {
                        accountApi.getUserByNet().subscribe({
                            if (!it.err) {
                                EventBus.getDefault().post(EventUserVipChange(true))
                            }
                        }, {})
                    }
                } else {
                    UTHelper.commonEvent(UTConstant.Score.IntegralP_click_Sign, "result", "签到失败")
                    toastLiveData.postValue(it.msg)
                }
            }, {
                UTHelper.commonEvent(UTConstant.Score.IntegralP_click_Sign, "result", "签到失败")
                postAlertHide()
                toastLiveData.postValue(it.message)
            })
    }

    fun switchAlert(alert: Boolean) {
        mineApi.switchSignAlert(alert).subscribe({
            if (!it.err) {
                switchAlertLiveData.postValue(alert)
                toastLiveData.postValue(if (alert) "已开启提示" else "已关闭提示")
            }
        }, {
        })
    }


    fun receiveScore(data: ScoreTaskItemEntity) {
        mineApi.receiveScore(data.id, data.type.toString())
            .subscribe({
                if (!it.err) {
                    refreshInfo()
                    EventBus.getDefault().post(EventScoreChange(true))
                    toastLiveData.postValue(it.res)
                } else {
                    toastLiveData.postValue(it.msg)
                }
            }, {
                toastLiveData.postValue(it.message)
            })

    }
}