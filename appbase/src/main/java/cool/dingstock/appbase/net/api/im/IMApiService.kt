package cool.dingstock.appbase.net.api.im

import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.im.RelationBean
import io.reactivex.rxjava3.core.Flowable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface IMApiService {
    @GET("/xserver/community/relation/all")
    fun getUserRelationList(@Query("userId") userId: String, @Query("nextKey") nextKey: Long? = null): Flowable<BaseResult<RelationBean>>

    @POST("/xserver/community/message/stranger/report")
    fun reportMessageToStranger(@Body body: RequestBody): Flowable<BaseResult<Any>>

    @GET("/xserver/user/u/im/token/refresh")
    fun refreshIMToken(): Flowable<BaseResult<String>>
}