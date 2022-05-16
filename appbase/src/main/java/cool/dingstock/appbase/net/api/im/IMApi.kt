package cool.dingstock.appbase.net.api.im

import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.im.RelationBean
import cool.dingstock.appbase.net.retrofit.api.BaseApi
import cool.dingstock.appbase.net.retrofit.api.handError
import cool.dingstock.lib_base.thread.scheduler.RxSchedulers
import io.reactivex.rxjava3.core.Flowable
import retrofit2.Retrofit
import javax.inject.Inject

class IMApi @Inject constructor(retrofit: Retrofit) : BaseApi<IMApiService>(retrofit) {
    fun getUserRelationList(userId: String): Flowable<BaseResult<RelationBean>> {
        return service.getUserRelationList(userId).compose(RxSchedulers.netio_main()).handError()
    }

    fun reportMessageToStranger(targetId: String): Flowable<BaseResult<Any>> {
        val body = ParameterBuilder().add(
            "userId", targetId
        ).toBody()
        return service.reportMessageToStranger(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun refreshIMToken(): Flowable<BaseResult<String>> {
        return service.refreshIMToken().compose(RxSchedulers.netio_main()).handError()
    }
}