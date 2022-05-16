package cool.dingstock.appbase.mvvm.status

/**
 * Copyright (C), 2015-2020, 盯链成都科技有限公司
 *
 * @Author: xujing
 * @Date: 2020/12/9 18:23
 * @Version: 1.1.0
 * @Description:
 */

sealed class PageLoadState(val msg: String, val isRefresh: Boolean) {
    class Loading(msg: String, isRefresh: Boolean) : PageLoadState(msg, isRefresh)

    class Empty(msg: String, isRefresh: Boolean) : PageLoadState(msg, isRefresh)

    class Success(msg: String, isRefresh: Boolean) : PageLoadState(msg, isRefresh)

    class Error(msg: String, isRefresh: Boolean) : PageLoadState(msg, isRefresh)
}