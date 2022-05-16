package cool.dingstock.appbase.entity.bean


/**
 * 请求状态的封装
 *
 * @param isGlobal 是否当前页面全局
 */
sealed class RequestState(open var isGlobal: Boolean = false) {

    //网络请求中
    class Loading(override var isGlobal: Boolean = false) : RequestState()

    //请求成功
    class Success(override var isGlobal: Boolean = false, val successTips: String? = "") : RequestState()

    //请求错误
    data class Error(
            val isRefresh: Boolean = false,
            val errorMsg: String? = "",
            override var isGlobal: Boolean = false
    ) : RequestState()

    //请求结果为空
    class Empty(
            override var isGlobal: Boolean = false,
            val isRefresh: Boolean = false
    ) : RequestState()

}

