package cool.dingstock.appbase.net.retrofit.utils

import cool.dingstock.appbase.entity.bean.base.BaseResult
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.FlowableOnSubscribe


/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/28  18:27
 */
object RxNetUtils {
    fun success(): Flowable<BaseResult<Any>> {
        return Flowable.create<BaseResult<Any>>(FlowableOnSubscribe {
            it.onNext(BaseResult(false, "", 0, ""))
        }, BackpressureStrategy.BUFFER)
    }

    fun <T> success(t: T): Flowable<BaseResult<T>> {
        return Flowable.create<BaseResult<T>>(FlowableOnSubscribe {
            it.onNext(BaseResult(false, t, 0, ""))
        }, BackpressureStrategy.BUFFER)
    }



}