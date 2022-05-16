package cool.dingstock.appbase.net.api.shoes

import cool.dingstock.appbase.entity.bean.base.BasePageEntity
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicBean
import cool.dingstock.appbase.entity.bean.shoes.*
import cool.dingstock.appbase.net.retrofit.api.BaseApi
import cool.dingstock.appbase.net.retrofit.api.handError
import cool.dingstock.lib_base.thread.scheduler.RxSchedulers
import io.reactivex.rxjava3.core.Flowable
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.ArrayList
import javax.inject.Inject


class ShoesApi @Inject constructor(retrofit: Retrofit) : BaseApi<ShoesApiService>(retrofit) {

    fun getShoesSeries(
        seriesId: String,
        nextKey: Long?
    ): Flowable<BaseResult<ShoesSeriesListEntity>> {
        return service.getShoesSeries(seriesId, nextKey).compose(RxSchedulers.netio_main())
            .handError()
    }

    fun getDealSelect(
        seriesId: String,
        nextKey: Long?
    ): Flowable<BaseResult<DealSelectedListEntity>> {
        return service.getDealSelect(
            seriesId,
            nextKey
        ).compose(RxSchedulers.netio_main()).handError()
    }

    suspend fun seriesDetails(seriesId: String): BaseResult<SeriesDetailsEntity> {
        return service.seriesDetails(seriesId)
    }

    suspend fun productDetails(productId: String): BaseResult<SeriesProductInfo> {
        return service.productDetails(productId)
    }

    suspend fun seriesDetailsPost(
        nextKey: Long?,
        seriesId: String
    ): BaseResult<BasePageEntity<CircleDynamicBean>> {
        return service.seriesDetailsPost(nextKey, seriesId, postType = "series")
    }

    suspend fun ratingScore(productId: String, score: Int, content: String?): BaseResult<Any> {
        val body = ParameterBuilder().add("productId", productId).add("score", score)
            .add("content", content).toBody()
        return service.ratingScore(body)
    }

    suspend fun fetchAllPic(productId: String): BaseResult<ArrayList<AllPicEntity>> {
        return service.fetchAllPic(productId)
    }

    suspend fun fetchMorePic(productId: String, nextKey: Long?): BaseResult<MorePicEntity> {
        return service.fetchMorePic(productId, nextKey)
    }
}