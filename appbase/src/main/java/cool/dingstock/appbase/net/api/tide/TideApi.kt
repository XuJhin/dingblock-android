package cool.dingstock.appbase.net.api.tide

import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.tide.HomeTideResultEntity
import cool.dingstock.appbase.net.retrofit.api.BaseApi
import cool.dingstock.appbase.net.retrofit.api.handError
import cool.dingstock.lib_base.thread.scheduler.RxSchedulers
import io.reactivex.rxjava3.core.Flowable
import retrofit2.Retrofit
import javax.inject.Inject


/**
 * 类名：TideApi
 * 包名：cool.dingstock.appbase.net.api.tide
 * 创建时间：2021/7/21 9:59 上午
 * 创建人： WhenYoung
 * 描述：
 **/
class TideApi @Inject constructor(retrofit: Retrofit) : BaseApi<TideApiServer>(retrofit) {

    fun homeTideList(
        date: Long,
        type:String?,
        company: ArrayList<String>,
        dealType: ArrayList<String>,
        isSubscribe: Boolean = false
    ): Flowable<BaseResult<HomeTideResultEntity>> {
        val body = ParameterBuilder()
            .add("date", date)
            .add("type", type)
            .add("company", company)
            .add("dealType", dealType)
            .add("isSubscribe", isSubscribe)
            .toBody()
        return service.homeTideList(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun subscribeChange(
        id: String,
        isSubscribe: Boolean
    ): Flowable<BaseResult<String>> {
        val body = ParameterBuilder()
            .add("id", id)
            .add("isSubscribe", isSubscribe)
            .toBody()
        return service.subscribeChange(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun favoredTideProduct(
        commentId: String?,
        favored: Boolean
    ): Flowable<BaseResult<String>> {

        val body = ParameterBuilder()
            .add("productId", commentId)
            .add("action", if(favored) "like" else "retrieveLike")
            .toBody()
        return service.favoredTideProduct(body).compose(RxSchedulers.netio_main())
            .handError()
    }

    fun disLikeTideProduct(
        commentId: String?,
        disLike: Boolean
    ): Flowable<BaseResult<String>> {

        val body = ParameterBuilder()
            .add("productId", commentId)
            .add("action", if(disLike) "dislike" else "retrieveDislike")
            .toBody()
        return service.favoredTideProduct(body).compose(RxSchedulers.netio_main())
            .handError()
    }

}