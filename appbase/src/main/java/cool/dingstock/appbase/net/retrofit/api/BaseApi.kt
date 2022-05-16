package cool.dingstock.appbase.net.retrofit.api

import com.google.gson.Gson
import cool.dingstock.appbase.exception.DcException
import cool.dingstock.appbase.net.retrofit.exception.DcError
import cool.dingstock.appbase.net.retrofit.manager.RxRetrofitServiceManager
import io.reactivex.rxjava3.core.Flowable
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/14  10:20
 */
open class BaseApi<T> {
    val service: T


    @Inject
    constructor(retrofit: Retrofit) {
        val type = (javaClass.genericSuperclass as? ParameterizedType)?.actualTypeArguments
        val type1 = type?.get(0) as? Class<T>
        service = retrofit.create(type1)
    }

    fun json2Body(json: String): RequestBody {
        return json.toRequestBody("application/json".toMediaType())
    }

    class ParameterBuilder {
        private val map = hashMapOf<String, Any?>()

        constructor()

        constructor(map: Map<String, Any>) {
            this.map.putAll(map)
        }


        fun add(key: String, value: Any?): ParameterBuilder {
            if (value is ParameterBuilder) {
                map[key] = value.map
            } else {
                map[key] = value
            }
            return this
        }

        fun asJson(): String = Gson().toJson(map)

        fun toBody(): RequestBody = asJson().toRequestBody("application/json".toMediaType())


    }
}

fun String.toBody(): RequestBody {
    return this.toRequestBody("application/json".toMediaType())
}

fun <T> Flowable<T>.handError(): Flowable<T> {
    return onErrorResumeNext {
        val error = getHttpException(it)
        return@onErrorResumeNext Flowable.error<T>(error)
    }
}

fun getHttpException(e: Throwable): DcException {
    val error: DcException = when (e) {
        is DcException -> {
            e
        }
        is HttpException,
        is SocketException,
        is SocketTimeoutException,
        is UnknownHostException,
        is ConnectException,
        is IOException
        -> {
            DcException(DcError.HTTP_ERROR_CODE, DcError.HTTP_ERROR)
        }
        else -> {
            DcException(DcError.UNKNOW_ERROR_CODE, e.message ?: "网络链接失败")
        }
    }
    return error
}


object Api {
    inline fun <reified T> create(): T {
        return RxRetrofitServiceManager.getInstance().basicRetrofit.create(T::class.java)
    }

}