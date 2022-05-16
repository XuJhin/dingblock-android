package cool.dingstock.mine.ui.avater

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cool.dingstock.appbase.entity.bean.mine.PendantDetail
import cool.dingstock.appbase.entity.bean.mine.PendantHomeBean
import cool.dingstock.appbase.entity.event.update.EventUpdatePendant
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.account.AccountApi
import cool.dingstock.appbase.net.retrofit.api.getHttpException
import cool.dingstock.lib_base.BaseLibrary
import cool.dingstock.lib_base.util.ToastUtil
import cool.dingstock.mine.dagger.MineApiHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class ModifyPendantViewModel: BaseViewModel() {
    @Inject
    lateinit var accountApi: AccountApi

    init {
        MineApiHelper.apiMineComponent.inject(this)
    }

    val tabNameList = mutableListOf<String>()
    val pendantHomeData = MutableStateFlow<PendantHomeBean?>(null)
    val selectPendant = MutableStateFlow<PendantDetail?>(null)
    val wearResult = MutableStateFlow("")
    val wearSuccess = MutableLiveData<Boolean>()

    fun getPendantHome() = viewModelScope.launch {
        accountApi.getPendantHome()
            .catch {
                postStateError(getHttpException(it).msg)
            }
            .collect {
                if (it.err) {
                    postStateError(it.msg)
                } else {
                    postStateSuccess()
                    pendantHomeData.value = it.res
                }
            }
    }

    fun wearPendant() = viewModelScope.launch {
        if (selectPendant.value == null) return@launch
        val worn = pendantHomeData.value?.user?.avatarPendantId == selectPendant.value!!.id
        accountApi.wearPendant(selectPendant.value!!.id, !worn)
            .catch {
                wearResult.value = getHttpException(it).msg
                wearSuccess.value = false
            }
            .collect {
                if (it.err) {
                    wearResult.value = it.msg
                    wearSuccess.value = false
                } else {
                    if (worn) {
                        EventBus.getDefault().post(EventUpdatePendant(pendantHomeData.value?.user?.id, null))
                    } else {
                        EventBus.getDefault().post(EventUpdatePendant(pendantHomeData.value?.user?.id, selectPendant.value!!.imageUrl))
                    }
                    pendantHomeData.value?.user?.avatarPendantId = if (worn) null else selectPendant.value!!.id
                    wearResult.value = if (worn) "取消佩戴成功" else "佩戴成功"
                    wearSuccess.value = true
                }
            }
    }
}