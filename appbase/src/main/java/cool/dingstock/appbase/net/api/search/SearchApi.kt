package cool.dingstock.appbase.net.api.search

import android.text.TextUtils
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.search.SearchResultEntity
import cool.dingstock.appbase.net.retrofit.api.BaseApi
import cool.dingstock.appbase.net.retrofit.api.handError
import cool.dingstock.lib_base.thread.scheduler.RxSchedulers
import io.reactivex.rxjava3.core.Flowable
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/14  10:59
 */
class SearchApi @Inject constructor(retrofit: Retrofit) : BaseApi<SearchApiService>(retrofit) {

    fun search(keyword: String,pageNum  : Int , searchTypes: String?): Flowable<BaseResult<SearchResultEntity>> {
        val parameterBuilder = ParameterBuilder()
        if(!TextUtils.isEmpty(searchTypes)){
            parameterBuilder.add("searchTypes", arrayListOf(searchTypes))
        }
        val body = parameterBuilder
                .add("keyword", keyword)
                .add("pageNum", pageNum )
                .toBody()
        return service.search(body).compose(RxSchedulers.io_main()).handError()
    }

}