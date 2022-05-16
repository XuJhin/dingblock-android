package cool.dingstock.appbase.helper.bp

import cool.dingstock.lib_base.util.Logger
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * @author WhenYoung
 *  CreateAt Time 2020/11/16  17:04
 */
object BpHttpClientManager {
    val httpClient: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
        builder.followRedirects(false)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
        builder.addInterceptor(RedirectInterceptor())
        return@lazy builder.build()

    }


}

//处理重定向的拦截器
class RedirectInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        var response: Response = chain.proceed(request)
        val code: Int = response.code
        if (code == 302) {
            //获取重定向的地址
            var location: String = response.headers.get("Location") ?: return response
            Logger.e("重定向一次:${request.url} -> $location")
            if (!location.startsWith("https:")) {
                location = "https:$location"
            }
            //重新构建请求
            val newRequest: Request =
                request.newBuilder().url(location).build()
            response = chain.proceed(newRequest)
        }
        return response
    }
}




