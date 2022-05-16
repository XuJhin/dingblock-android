package net.dingblock.mobile.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.mobile.MobileHelper
import cool.dingstock.appbase.net.parse.ParseCallback

class BootViewModel: BaseViewModel() {
    private val _cloudUrl = MutableLiveData<String?>()
    val cloudUrl: LiveData<String?> get() = _cloudUrl

    fun getCouldUrl(type: String?) {
        MobileHelper.getInstance().getDingUrl(type, object : ParseCallback<String?> {
            override fun onSucceed(data: String?) {
                _cloudUrl.postValue(data)
            }

            override fun onFailed(errorCode: String, errorMsg: String) {
                _cloudUrl.postValue(null)
            }
        })
    }
}