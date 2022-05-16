package cool.dingstock.appbase.net.retrofit.exception


object DcError {
    const val DEFAULT_ERROR = "加载失败"
    const val HTTP_ERROR = "网络连接失败"
    const val PARSE_ERROR = "数据解析失败"
    const val HTTP_TIME_OUT = "网络请求超时"


    const val HTTP_ERROR_CODE = -100
    const val PARSE_ERROR_CODE = -200
    const val HTTP_TIME_OUT_CODE = -300
    const val UNKNOW_ERROR_CODE = -500
}