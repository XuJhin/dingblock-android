package cool.dingstock.mine.ui.vip

import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.constant.CommonConstant
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.mine.VipPrivilegeEntity
import cool.dingstock.appbase.entity.bean.vip.KOLEntity
import cool.dingstock.appbase.entity.bean.vip.VipPriceEntity
import cool.dingstock.appbase.entity.event.update.EventUserVipChange
import cool.dingstock.appbase.exception.DcException
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.account.AccountApi
import cool.dingstock.appbase.net.api.common.CommonApi
import cool.dingstock.appbase.net.api.mine.MineApi
import cool.dingstock.mine.R
import cool.dingstock.mine.dagger.MineApiHelper
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.functions.BiFunction
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/19  15:33
 */
class VipCenterVm : BaseViewModel() {


    @Inject
    lateinit var accountApi: AccountApi

    @Inject
    lateinit var commonApi: CommonApi

    @Inject
    lateinit var mineApi: MineApi

    init {
        MineApiHelper.apiMineComponent.inject(this)
    }


    val kolExitLiveData = MutableLiveData<KOLEntity>()
    val priceLiveData = MutableLiveData<ArrayList<VipPriceEntity>>()
    val refreshLiveData = MutableLiveData<Boolean>()
    val codeResultLiveData = MutableLiveData<Boolean>()
    val updateUserLiveData = MutableLiveData<Boolean>()
    val vipPrivilegeLiveData = MutableLiveData<ArrayList<VipPrivilegeEntity>>()


    fun load(isRefresh: Boolean) {
        if (!isRefresh) {
            postStateLoading()
        }
        val subscribe = Flowable.zip(accountApi.kolExists(), accountApi.vipPrices(),
                object : BiFunction<BaseResult<KOLEntity>, BaseResult<ArrayList<VipPriceEntity>>, VipEntity?> {
                    override fun apply(t1: BaseResult<KOLEntity>?, t2: BaseResult<ArrayList<VipPriceEntity>>?): VipEntity? {
                        if (t1 == null || t2 == null) {
                            return null
                        }
                        if (t1.err || t1.res == null) {
                            throw DcException(t1.code, t1.msg)
                        }
                        if (t2.err || t2.res == null) {
                            throw DcException(t2.code, t2.msg)
                        }
                        return VipEntity(t1.res, t2.res)
                    }
                })
                .subscribe({
                    if (!isRefresh) {
                        postStateSuccess()
                    }
                    if (it !== null && it.kol != null && it.prices != null) {
                        priceLiveData.postValue(it.prices)
                        kolExitLiveData.postValue(it.kol)
                        if (isRefresh) {
                            refreshLiveData.postValue(true)
                        }
                    } else {
                        if (isRefresh) {
                            refreshLiveData.postValue(true)
                        } else {
                            postStateError(getRes().getString(R.string.common_status_error_tips))
                        }
                    }
                }, {
                    if (isRefresh) {
                        refreshLiveData.postValue(true)
                    } else {
                        postStateError(it.message
                                ?: getRes().getString(R.string.common_status_error_tips))
                    }
                })
        addDisposable(subscribe)
        val sub = mineApi.vipPrivilege()
                .subscribe({
                    if (!it.err && it.res != null) {
                        vipPrivilegeLiveData.postValue(it.res)
                    }
                }, {

                })
        addDisposable(sub)
    }

    fun checkCode(code: String) {
        postAlertLoading()
        val subscribe = accountApi.checkCode(code)
                .subscribe({
                    postAlertHide()
                    if (!it.err && it.res != null) {
                        codeResultLiveData.postValue(true)
                        priceLiveData.postValue(it.res)
                    } else {
                        postAlertError(it.msg)
                    }
                }, {
                    postAlertHide()
                    postAlertError(it.message
                            ?: getRes().getString(R.string.common_status_error_tips))
                })
        addDisposable(subscribe)
    }

    fun cancelCode() {
        postAlertLoading()
        val subscribe = accountApi.vipPrices()
                .subscribe({
                    postAlertHide()
                    if (!it.err && it.res != null) {
                        codeResultLiveData.postValue(false)
                        priceLiveData.postValue(it.res)
                    } else {
                        postAlertError(it.msg)
                    }
                }, {
                    postAlertHide()
                    postAlertError(it.message
                            ?: getRes().getString(R.string.common_status_error_tips))
                })
        addDisposable(subscribe)
    }

    fun updateUser() {
        accountApi.getUserByNet()
                .subscribe({
                    if (!it.err) {
                        EventBus.getDefault().post(EventUserVipChange(true))
                        updateUserLiveData.postValue(true)
                    }
                }, {})
    }

    fun kolInvitations() {
        postAlertLoading()
        commonApi.commonInfo(CommonConstant.CommonConfigType.kolInvitations)
                .subscribe({
                    postAlertHide()
                    if (!it.err && it.res != null) {
                        it.res?.url?.let { url -> router(url) }
                    }
                }, {
                    postAlertHide()
                })


    }

}

data class VipEntity(val kol: KOLEntity?, val prices: ArrayList<VipPriceEntity>?)