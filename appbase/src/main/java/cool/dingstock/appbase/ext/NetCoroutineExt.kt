package cool.dingstock.appbase.ext

import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.exception.DcException
import cool.dingstock.appbase.net.mobile.MobileHelper
import cool.dingstock.appbase.net.parse.ParseCallback
import cool.dingstock.appbase.net.retrofit.api.getHttpException
import cool.dingstock.appbase.net.retrofit.api.handError
import cool.dingstock.appbase.net.retrofit.exception.DcError
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


/**
 * 类名：NetCorutineExt
 * 包名：cool.dingstock.appbase.ext
 * 创建时间：2021/8/20 6:01 下午
 * 创建人： WhenYoung
 * 描述：
 **/
fun <T> CoroutineScope.doRequest(
    call: suspend () -> BaseResult<T>,
    success: suspend (BaseResult<T>) -> Unit,
    fail: (err: Throwable) -> Unit = {}
) {
    launch(Dispatchers.IO) {
        try {
            val result = call.invoke()
            launch(Dispatchers.Main) {
                success.invoke(result)
            }
        } catch (e: Exception) {
            launch(Dispatchers.Main) {
                fail(getHttpException(e))
            }
        }
    }
}

fun <T> CoroutineScope.doRequestAsync(call: suspend () -> BaseResult<T>): Deferred<BaseResult<T>> {
    return this.async(Dispatchers.IO) {
        try {
            return@async call()
        } catch (e: Exception) {
            val error = getHttpException(e)
            return@async BaseResult<T>(err = true, code = error.code, msg = error.msg)
        }
    }
}

//回调 转换为协程的写法
fun callBack2Coroutine() {
    CoroutineScope(Dispatchers.Main).async {
        val x = suspendCancellableCoroutine<BaseResult<String>> { cont ->
            MobileHelper.getInstance().getDingUrl("type", object : ParseCallback<String?> {
                override fun onSucceed(data: String?) {
                    data?.let {
                        cont.resume(BaseResult(err = false,res = it))
                    }
                }
                override fun onFailed(errorCode: String, errorMsg: String) {
//                    cont.resumeWithException(DcException(-100, errorMsg))
                    //或者
                    cont.resume(BaseResult(err = true ,res = errorMsg))
                }
            })
        }
        return@async x
    }
}



