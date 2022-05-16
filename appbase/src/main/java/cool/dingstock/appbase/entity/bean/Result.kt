package cool.dingstock.appbase.entity.bean

/**
 * Copyright (C), 2015-2020, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2020/11/4 14:40
 * @Version:         1.1.0
 * @Description:
 */
sealed class ApiResult {
    class Success<T>(val data: T) : ApiResult()
    class Error(val throwable: Throwable?) : ApiResult()
}