package cool.dingstock.monitor.ui.stock

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import cool.dingstock.appbase.mvvm.SingleLiveEvent
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.util.ZXingUtils
import cool.dingstock.lib_base.util.SizeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MonitorStockViewModel : BaseViewModel() {
    val qrCodeLiveData = SingleLiveEvent<Bitmap?>()
    fun createLinkQRCode(link: String?) {
        viewModelScope.launch {
            val bitmap: Bitmap? = withContext(Dispatchers.IO) {
                ZXingUtils.createQRImage(link, SizeUtils.dp2px(60f), SizeUtils.dp2px(60f), null)
            }
            qrCodeLiveData.postValue(bitmap)
        }
    }
}