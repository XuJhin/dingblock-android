package cool.dingstock.appbase.helper

import okhttp3.OkHttpClient

object DcHttpClientHelper {
    val client by lazy {
        OkHttpClient()
    }
}