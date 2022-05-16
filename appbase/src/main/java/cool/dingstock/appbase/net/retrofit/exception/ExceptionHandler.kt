package cool.dingstock.appbase.net.retrofit.exception

import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException

object ExceptionHandler {
    //该方法用来判断异常类型 并将异常类型封装在ResponeThrowable返回
    fun handleException(e: Throwable?): ResponseThrowable {
        val responseThrowable = when (e) {
            is HttpException,
            is ConnectException -> {
                ResponseThrowable(e, DcError.HTTP_ERROR)
            }
            is SocketTimeoutException, is SocketException -> {
                ResponseThrowable(e, DcError.HTTP_TIME_OUT)
            }
            else -> {
                ResponseThrowable(e, DcError.DEFAULT_ERROR)
            }
        }
        return responseThrowable
    }
}