package cool.dingstock.appbase.net.refactor

import cool.dingstock.lib_base.util.HttpsCertUtils
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class ApiManager {

    val retrofit: Retrofit by lazy {
        Retrofit.Builder().build()
    }
    val okHttpClient: OkHttpClient by lazy {
        val sslParams = HttpsCertUtils.getSSLParams(
            HttpsCertUtils.Protocol.SSL,
            null, null, null
        )
        OkHttpClient().newBuilder()
            .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
            .writeTimeout(WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .connectTimeout(CONNECT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build()
    }

    inline fun <reified T> create(): T {
        return retrofit.create(T::class.java)
    }

    companion object {
        private const val CONNECT_TIMEOUT = 15 // client请求超时时间
        private const val READ_TIMEOUT = 30 // 读超时
        private const val WRITE_TIMEOUT = 30 // 写超时
    }
}