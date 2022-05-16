package cool.dingstock.appbase.net.api.calendar

import cool.dingstock.appbase.entity.bean.base.BasePageEntity
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.calendar.DealDetailsEntity
import cool.dingstock.appbase.entity.bean.calendar.DealPostItemEntity
import cool.dingstock.appbase.entity.bean.calendar.PriceListResultEntity
import cool.dingstock.appbase.entity.bean.calendar.SmsInputTemplateEntity
import cool.dingstock.appbase.entity.bean.circle.*
import cool.dingstock.appbase.entity.bean.home.HomeProductDetailData
import cool.dingstock.appbase.entity.bean.home.SmsRegistrationEntity
import cool.dingstock.appbase.entity.bean.mine.LotteryNoteListBean
import cool.dingstock.appbase.entity.bean.mine.UpdateLotteryBean
import cool.dingstock.appbase.entity.bean.shoes.SeriesBean
import cool.dingstock.appbase.entity.bean.shoes.ShoesHomeBean
import io.reactivex.rxjava3.core.Flowable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * @author WhenYoung
 *  CreateAt Time 2020/11/5  15:50
 */
interface CalendarServer {
    @GET("xserver/calendar/raffle/sms/list")
    fun getSmsRegistrationList(): Flowable<BaseResult<SmsRegistrationEntity>>

    @POST("/xserver/calendar/raffle/join")
    fun raffleAction(@Body productId: RequestBody): Flowable<BaseResult<String>>

    @POST("/xserver/calendar/product/favor")
    fun productAction(@Body productId: RequestBody): Flowable<BaseResult<String>>

    @GET("/xserver/calendar/product/detail")
    fun productComments(@Query("id") productId: String?): Flowable<BaseResult<ProductCommentEntity>>

    @GET("/xserver/calendar/product/comment/page")
    fun productCommentPage(
        @Query("productId") productId: String?,
        @Query("nextKey") nextKey: Long?,
        @Query("nextStr") nextStr: String?
    ): Flowable<BaseResult<CircleProductCommentPage>>

    @POST("/xserver/calendar/comment/favor")
    fun favoredCommentHomeRaffle(@Body body: RequestBody): Flowable<BaseResult<String>>

    @POST("/xserver/calendar/report/comment/do")
    fun productPostCommentReport(@Body body: RequestBody): Flowable<BaseResult<String>>

    @POST("/xserver/calendar/product/comment/update")
    fun productCommentDelete(@Body body: RequestBody): Flowable<BaseResult<String>>

    @GET("/xserver/calendar/product/comment/detail")
    fun productCommentDetail(
        @Query("id") id: String,
        @Query("nextKey") nextKey: Long?
    ): Flowable<BaseResult<CircleCommentDetailBean>>

    @GET("/xserver/calendar/product/comment/detail")
    fun productCommentDetailCommentPage(
        @Query("id") id: String,
        @Query("nextKey") nextKey: Long?
    ): Flowable<BaseResult<CircleDetailLoadBean>>

    @GET("/xserver/calendar/raffle/sms/list")
    fun smsInfo(
        @Query("id") id: String,
        @Query("nextKey") nextKey: Long?
    ): Flowable<BaseResult<CircleDetailLoadBean>>

    @GET("/xserver/calendar/raffle/sms/template")
    fun smsInputTemplate(@Query("raffleId") raffleId: String): Flowable<BaseResult<SmsInputTemplateEntity>>

    @POST("/xserver/calendar/raffle/sms_history")
    fun smsUpload(@Body body: RequestBody): Flowable<BaseResult<Any>>

    @GET("/xserver/calendar/product/priceList")
    fun priceList(@Query("productId") productId: String): Flowable<BaseResult<PriceListResultEntity>>

    @GET("/xserver/calendar/raffle/join/record/page")
    suspend fun getRecordList(
        @Query("nextKey") nextKey: Long?,
        @Query("date") date: Long,
        @Query("state") state: String?
    ): BaseResult<LotteryNoteListBean>

    @POST("/xserver/calendar/raffle/join/record/delete")
    suspend fun deleteRecord(@Body body: RequestBody): BaseResult<Any>

    @POST("/xserver/calendar/raffle/join/record/update")
    suspend fun updateRecord(@Body body: RequestBody): BaseResult<UpdateLotteryBean>

    @GET("/xserver/calendar/shoes/home")
    suspend fun getShoesHome(): BaseResult<ShoesHomeBean>

    @GET("/xserver/calendar/shoes/series/list")
    suspend fun getShoesSeriesList(@Query("brandId") brandId: String): BaseResult<SeriesBean>

    @GET("/xserver/calendar/shoes/series/comment/page")
    fun shoesCommentPage(
        @Query("seriesId") seriesId: String?,
        @Query("nextKey") nextKey: Long?
    ): Flowable<BaseResult<CircleProductCommentPage>>

    @GET("/xserver/calendar/shoes/series/comment/detail")
    fun shoesCommentDetail(
        @Query("id") seriesId: String?,
        @Query("nextKey") nextKey: Long?
    ): Flowable<BaseResult<CircleCommentDetailBean>>

    @POST("xserver/calendar/shoes/series/comment/save")
    fun commentShoes(@Body requestBody: RequestBody): Flowable<BaseResult<CircleDynamicDetailCommentsBean>>

    @POST("/xserver/calendar/shoes/series/comment/update")
    fun shoesCommentDelete(@Body body: RequestBody): Flowable<BaseResult<String>>

    @POST("/xserver/calendar/shoes/series/comment/favor")
    fun favoredCommentShoesSeries(@Body body: RequestBody): Flowable<BaseResult<String>>


    @GET("/xserver/calendar/deal/search/page")
    suspend fun dealDetails(
        @Query("productId") productId: String?,
    ): BaseResult<DealDetailsEntity>


    @GET("/xserver/calendar/deal/search/page")
    suspend fun dealDetailsPostPage(
        @Query("productId") productId: String?,
        @Query("goodsQuality") goodsQuality: String?,
        @Query("goodsSize") goodsSize: String?,
        @Query("nextKey") nextKey: Long?
    ): BaseResult<BasePageEntity<DealPostItemEntity>>

    @GET("/xserver/calendar/raffle/detail")
    suspend fun getProductDetail(
        @Query("productId") productId: String,
        @Query("longitude") longitude: Double?,
        @Query("latitude") latitude: Double?
    ): BaseResult<HomeProductDetailData>

}