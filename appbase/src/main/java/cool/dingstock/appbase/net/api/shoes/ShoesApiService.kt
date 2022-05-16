package cool.dingstock.appbase.net.api.shoes

import cool.dingstock.appbase.entity.bean.base.BasePageEntity
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicBean
import cool.dingstock.appbase.entity.bean.shoes.*
import io.reactivex.rxjava3.core.Flowable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface ShoesApiService {

    @GET("/xserver/calendar/shoes/series/timeline")
    fun getShoesSeries(
        @Query("seriesId") seriesId: String,
        @Query("nextKey") nextKey: Long?
    ): Flowable<BaseResult<ShoesSeriesListEntity>>

    @GET("/xserver/calendar/shoes/series/deal/page")
    fun getDealSelect(
        @Query("seriesId") seriesId: String,
        @Query("nextKey") nextKey: Long?
    ): Flowable<BaseResult<DealSelectedListEntity>>

    @GET("/xserver/calendar/shoes/series/detail")
    suspend fun seriesDetails(
        @Query("seriesId") seriesId: String,
    ): BaseResult<SeriesDetailsEntity>

    @GET("/xserver/calendar/product/simple/info")
    suspend fun productDetails(
        @Query("productId") seriesId: String,
    ): BaseResult<SeriesProductInfo>

    @GET("/xserver/community/post/page")
    suspend fun seriesDetailsPost(
        @Query("nextKey") nextKey: Long?,
        @Query("seriesId") seriesId: String,
        @Query("postType") postType: String = "series",
    ): BaseResult<BasePageEntity<CircleDynamicBean>>

    @POST("/xserver/calendar/product/comment/save")
    suspend fun ratingScore(@Body body: RequestBody): BaseResult<Any>

    @GET("/xserver/community/post/series/image/home")
    suspend fun fetchAllPic(@Query("productId") productId: String): BaseResult<ArrayList<AllPicEntity>>

    @GET("/xserver/community/post/series/image/page")
    suspend fun fetchMorePic(
        @Query("productId") productId: String,
        @Query("nextKey") nextKey: Long?
    ): BaseResult<MorePicEntity>

}