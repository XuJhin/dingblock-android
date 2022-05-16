package cool.dingstock.appbase.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.*
import android.os.Bundle
import android.telephony.CarrierConfigManager
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import android.widget.Toast

import java.lang.Exception
import java.util.*


class LocationHelper(private val mContext: Context, val mOnLocationListener: OnLocationResultListener)
    : LifecycleObserver {
    var lastLocation: Location? = null
    var providers: MutableList<String>? = null
    var locationProvider: String? = ""
    var locationManager: LocationManager? = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    lateinit var lifecycleOwner: LifecycleOwner


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroyLocation() {
        stopLocation()
    }

    fun bind(lifecycleOwner: LifecycleOwner): LocationHelper {
        this.lifecycleOwner = lifecycleOwner
        return this
    }


    fun startLocation() {
        realLocation()
    }

    @SuppressLint("MissingPermission")
    private fun realLocation() {
        //获取Location
        val lastLocation = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        if (lastLocation != null) {
            //不为空,显示地理位置经纬度
            mOnLocationListener.onLocationResult(lastLocation)
            return
        }else{
            mOnLocationListener.onLocationFail()
        }
        //监视地理位置变化
        locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f,
                object : LocationListener {
                    // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
                    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
                        Log.e("location", status.toString())
                    }

                    // Provider被enable时触发此函数，比如GPS被打开
                    override fun onProviderEnabled(provider: String) {
                        Log.e("location", provider)
                    }

                    // Provider被disable时触发此函数，比如GPS被关闭
                    override fun onProviderDisabled(provider: String) {
                        Log.e("location", provider)
                    }

                    //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
                    override fun onLocationChanged(location: Location) {
                        Log.e("location", location?.altitude?.toString())
                        mOnLocationListener?.onLocationChange(location)
                    }
                })
        return
    }

    fun stopLocation() {
        providers = null
        locationProvider = ""
        locationManager = null
    }
}

fun Context.getAddress(location: Location?): List<Address>? {
    var result: List<Address>? = null
    try {
        location?.let {
            val gc = Geocoder(this, Locale.getDefault())
            result = gc.getFromLocation(it.latitude, it.longitude, 1)
            Log.v("TAG", "获取地址信息：$result")
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return result
}

interface OnLocationResultListener {
    fun onLocationResult(location: Location?)
    fun onLocationChange(location: Location?)
    fun onLocationFail()
}