package cool.dingstock.appbase.net.api.search

import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.home.HomeProductDetailData
import cool.dingstock.appbase.entity.bean.search.SearchResultEntity
import io.reactivex.rxjava3.core.Flowable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/14  9:57
 */
interface SearchApiService {

    @POST("/xserver/search/everything")
    fun search(@Body body:RequestBody): Flowable<BaseResult<SearchResultEntity>>

}