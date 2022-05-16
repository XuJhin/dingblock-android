package cool.dingstock.appbase.net.api.tide

import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.tide.HomeTideResultEntity
import io.reactivex.rxjava3.core.Flowable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


/**
 * 类名：TideApiServer
 * 包名：cool.dingstock.appbase.net.api.tide
 * 创建时间：2021/7/21 9:59 上午
 * 创建人： WhenYoung
 * 描述：
 **/
interface TideApiServer {
    @POST("/xserver/calendar/tide/list")
    fun homeTideList(@Body body: RequestBody): Flowable<BaseResult<HomeTideResultEntity>>

    @POST("/xserver/calendar/tide/subscribe")
    fun subscribeChange(@Body body: RequestBody): Flowable<BaseResult<String>>

    @POST("/xserver/calendar/tide/favor")
    fun favoredTideProduct(@Body body: RequestBody): Flowable<BaseResult<String>>
}