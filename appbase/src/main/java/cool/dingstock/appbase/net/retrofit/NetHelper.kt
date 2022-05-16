package cool.dingstock.appbase.net.retrofit

import cool.dingstock.lib_base.util.HttpsCertUtils
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class NetHelper {

    private var mDefaultClient: OkHttpClient? = null
    private val serverUrl = "https://apiv2.dingstock.net";
    fun getHttpClient(): OkHttpClient? {
        if (null == mDefaultClient) {
            mDefaultClient = getHttpClientBuilder().build()
        }
        return mDefaultClient
    }

    private fun getHttpClientBuilder(): OkHttpClient.Builder {
        val sslParams = HttpsCertUtils.getSSLParams(
            HttpsCertUtils.Protocol.SSL,
            null, null, null
        )
        return OkHttpClient().newBuilder()
            .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
            .writeTimeout(WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .connectTimeout(CONNECT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
    }

    companion object {
        private const val CONNECT_TIMEOUT = 15 // client请求超时时间
        private const val READ_TIMEOUT = 30 // 读超时
        private const val WRITE_TIMEOUT = 30 // 写超时
    }

}