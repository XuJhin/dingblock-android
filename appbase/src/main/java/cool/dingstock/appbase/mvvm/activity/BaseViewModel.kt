package cool.dingstock.appbase.mvvm.activity

import android.content.res.Resources
import androidx.lifecycle.*
import cool.dingstock.appbase.entity.bean.mine.MineVipChargeEntity
import cool.dingstock.appbase.mvvm.SingleLiveEvent
import cool.dingstock.appbase.mvvm.status.LoadingDialogStatus
import cool.dingstock.appbase.mvvm.status.ViewStatus
import cool.dingstock.appbase.net.mobile.MobileHelper
import cool.dingstock.appbase.net.parse.ParseCallback
import cool.dingstock.lib_base.BaseLibrary
import cool.dingstock.lib_base.util.ToastUtil
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

open class BaseViewModel : ViewModel(), LifecycleEventObserver {
    val statusViewLiveData = MutableLiveData<ViewStatus>()
    val loadingDialogLiveData = MutableLiveData<LoadingDialogStatus>()
    val routerLiveData = SingleLiveEvent<String>()
    val toastLiveData = MutableLiveData<String>()

    private val mCompositeDisposable = CompositeDisposable()

    fun addDisposable(disposable: Disposable?) {
        disposable?.let {
            mCompositeDisposable.add(disposable)
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_DESTROY -> {
//                mCompositeDisposable.clear()
            }
            else -> {
            }
        }
    }

    //页面状态
    fun postStateLoading(msg: String = "加载中…") {
        statusViewLiveData.postValue(ViewStatus.Loading(msg))
    }

    fun postStateSuccess(msg: String = "加载成功") {
        statusViewLiveData.postValue(ViewStatus.Success(msg))
    }

    fun postStateError(msg: String? = "网络错误") {
        statusViewLiveData.postValue(ViewStatus.Error(msg ?: "网络错误"))
    }

    fun postStateEmpty(msg: String = "数据为空") {
        statusViewLiveData.postValue(ViewStatus.Empty(msg))
    }


    //alert dialog  状态
    fun postAlertLoading(msg: String = "加载中…") {
        loadingDialogLiveData.postValue(LoadingDialogStatus.Loading(msg))
    }

    fun postAlertHide(msg: String = "") {
        loadingDialogLiveData.postValue(LoadingDialogStatus.Hide)
    }

    fun postAlertSuccess(msg: String) {
        loadingDialogLiveData.postValue(LoadingDialogStatus.Success(msg))
    }

    fun postAlertError(msg: String) {
        loadingDialogLiveData.postValue(LoadingDialogStatus.Error(msg))
    }

    fun router(router: String) {
        routerLiveData.postValue(router)
    }

    fun shortToast(msg: String?) {
        msg?.let {
            BaseLibrary.getInstance().context?.let {
                ToastUtil.getInstance()._short(it, msg)
            }
        }
    }

    fun longToast(msg: String?) {
        msg?.let {
            BaseLibrary.getInstance().context?.let {
                ToastUtil.getInstance()._short(it, msg)
            }
        }
    }

    fun getRes(): Resources {
        return BaseLibrary.getInstance().context.resources
    }

    fun getCloudUrl(type: String, showLoading: Boolean = false, showErrToast: Boolean = true) {
        if (showLoading) {
            postAlertLoading()
        }
        MobileHelper.getInstance().getCloudUrl(type, object : ParseCallback<MineVipChargeEntity> {
            override fun onSucceed(data: MineVipChargeEntity) {
                postAlertHide()
                routerLiveData.postValue(data.url)
            }

            override fun onFailed(errorCode: String, errorMsg: String) {
                postAlertHide()
                if (showErrToast) {
                    toastLiveData.postValue(errorMsg)
                }
            }
        })
    }


}