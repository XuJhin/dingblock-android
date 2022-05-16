package cool.dingstock.monitor.setting

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.internal.LinkedTreeMap
import cool.dingstock.appbase.entity.bean.monitor.IsShowDialogEntity
import cool.dingstock.appbase.entity.bean.monitor.MonitorDisturbTimeSetBean
import cool.dingstock.appbase.entity.bean.monitor.MsgConfigDataEntity
import cool.dingstock.appbase.entity.event.account.EventActivated
import cool.dingstock.appbase.ext.doRequest
import cool.dingstock.appbase.helper.SettingHelper
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.monitor.MonitorApi
import cool.dingstock.appbase.net.mobile.MobileHelper
import cool.dingstock.appbase.net.parse.ParseCallback
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.monitor.dagger.MonitorApiHelper
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class SettingViewModel : BaseViewModel() {
    @Inject
    lateinit var monitorApi: MonitorApi


    var startTime = 0
    var endTime = 0

    init {
        MonitorApiHelper.apiMonitorComponent.inject(this)
    }

    val couldLiveData = MutableLiveData<String>()
    val hideLoadingDialog = MutableLiveData<Boolean>()
    val successDialog = MutableLiveData<String>()
    val errorDialog = MutableLiveData<String>()

    val disturbTimeLiveData = MutableLiveData<MonitorDisturbTimeSetBean>()
    val msgConfigLiveData = MutableLiveData<MsgConfigDataEntity>()

    val msgDialogLiveData = MutableLiveData<IsShowDialogEntity>()

    fun activate(code: String?) {
        SettingHelper.getInstance().activate(code, object : ParseCallback<String?> {
            override fun onSucceed(data: String?) {
                hideLoadingDialog.postValue(true)
                successDialog.postValue(data!!)
                EventBus.getDefault().post(EventActivated())
            }

            override fun onFailed(errorCode: String, errorMsg: String) {
                hideLoadingDialog.postValue(true)
                errorDialog.postValue(errorMsg)
            }
        })
    }

    fun getDisturbTime() {
        viewModelScope.doRequest({ monitorApi.getDisturbTime() }, { result ->
            if (!result.err) {
                disturbTimeLiveData.postValue(result.res ?: null)
            } else {
                shortToast(result.msg)
            }
        }, {
            shortToast(it.message)
        })
    }

    fun setDisturbTime(
        isSilent: Boolean,
        startHour: Int,
        endHour: Int
    ) {
        viewModelScope.doRequest(
            { monitorApi.setDisturbTime(isSilent, startHour, endHour) },
            { result ->
                if (!result.err) {
                    startTime = startHour
                    endTime = endHour
                } else {
                    shortToast(result.msg)
                }
            },
            {
                shortToast(it.message)
            })
    }

    fun setMsgState(enable: Boolean) {
        viewModelScope.doRequest({ monitorApi.setMsgState(enable) }, { result ->
            if (!result.err) {
                result.res?.let {
                    if (it is LinkedTreeMap<*, *>) {
                        val msg = it["msg"] as? String
                        val pop = it["pop"] as? Boolean
                        msgDialogLiveData.postValue(IsShowDialogEntity(msg, pop))
                    }
                }
            } else {
                shortToast(result.msg)
            }
        }, {
            shortToast(it.message)
        })
    }

    fun getMsgState() {
        viewModelScope.doRequest({ monitorApi.getMsgState() }, { result ->
            if (!result.err) {
                msgConfigLiveData.postValue(result.res ?: null)
            } else {
                shortToast(result.msg)
            }
        }, {
            shortToast(it.message)
        })
    }

    fun getCouldUrl(type: String?, context: Context) {
        MobileHelper.getInstance().getDingUrl(type, object : ParseCallback<String?> {
            override fun onSucceed(data: String?) {
                data?.let {
                    DcUriRequest(context, it).start()
                }
            }

            override fun onFailed(errorCode: String, errorMsg: String) {
                postAlertHide()
            }
        })
    }
}