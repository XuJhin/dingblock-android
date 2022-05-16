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
import cool.dingstock.appbase.net.retrofit.api.BaseApi
import cool.dingstock.appbase.net.retrofit.api.handError
import cool.dingstock.lib_base.thread.scheduler.RxSchedulers
import cool.dingstock.lib_base.util.StringUtils
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Retrofit
import retrofit2.http.Body

/**
 * @author WhenYoung
 *  CreateAt Time 2020/11/5  15:49
 */
class CalendarApi(retrofit: Retrofit) : BaseApi<CalendarServer>(retrofit) {

    fun getSmsRegistrationList(): Flowable<BaseResult<SmsRegistrationEntity>> {
        return service.getSmsRegistrationList().compose(RxSchedulers.netio_main()).handError()
    }

    fun raffleAction(raffle: String?, joined: Boolean?): Flowable<BaseResult<String>> {
        val body = ParameterBuilder()
            .add("raffleId", raffle)
            .add("joined", joined)
            .toBody()
        return service.raffleAction(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun productAction(action: String?, productId: String?): Flowable<BaseResult<String>> {
        val body = ParameterBuilder()
            .add("action", action)
            .add("productId", productId)
            .toBody()
        return service.productAction(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun productComments(productId: String?): Flowable<BaseResult<ProductCommentEntity>> {
        return service.productComments(productId).compose(RxSchedulers.netio_main()).handError()
    }

    fun productCommentPage(
        productId: String?,
        nextKey: Long?,
        nextStr: String?
    ): Flowable<BaseResult<CircleProductCommentPage>> {
        return service.productCommentPage(productId, nextKey, nextStr)
            .compose(RxSchedulers.netio_main())
            .handError()
    }

    fun favoredCommentHomeRaffle(
        commentId: String?,
        favored: Boolean
    ): Flowable<BaseResult<String>> {
        val body = ParameterBuilder()
            .add("commentId", commentId)
            .add("favored", favored)
            .toBody()
        return service.favoredCommentHomeRaffle(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun productPostCommentReport(commentId: String?): Flowable<BaseResult<String>> {
        val body = ParameterBuilder()
            .add("reportedId", commentId)
            .toBody()
        return service.productPostCommentReport(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun productCommentDelete(id: String): Flowable<BaseResult<String>> {
        val body = ParameterBuilder()
            .add("id", id)
            .toBody()
        return service.productCommentDelete(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun productCommentDetail(id: String): Flowable<BaseResult<CircleCommentDetailBean>> {
        return service.productCommentDetail(id, null).compose(RxSchedulers.netio_main()).handError()
    }

    fun productCommentDetailCommentPage(
        id: String,
        nextKey: Long?
    ): Flowable<BaseResult<CircleDetailLoadBean>> {
        return service.productCommentDetailCommentPage(id, nextKey)
            .compose(RxSchedulers.netio_main()).handError()
    }

    fun smsInputTemplate(raffleId: String): Flowable<BaseResult<SmsInputTemplateEntity>> {
        return service.smsInputTemplate(raffleId).compose(RxSchedulers.netio_main()).handError()
    }

    fun smsUpload(@Body raffleId: String): Flowable<BaseResult<Any>> {
        val body = ParameterBuilder()
            .add("raffleId", raffleId)
            .toBody()
        return service.smsUpload(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun priceList(productId: String): Flowable<BaseResult<PriceListResultEntity>> {
        return service.priceList(productId).compose(RxSchedulers.netio_main()).handError()
    }

    suspend fun getRecordList(nextKey: Long?, date: Long, state: String?) = flow {
        emit(service.getRecordList(nextKey, date, state))
    }.flowOn(Dispatchers.IO)

    suspend fun deleteRecord(id: String) = flow {
        val body = ParameterBuilder()
            .add("id", id)
            .toBody()
        emit(service.deleteRecord(body))
    }

    suspend fun updateRecord(id: String, state: String?) = flow {
        val body = ParameterBuilder()
            .add("id", id)
            .add("state", state)
            .toBody()
        emit(service.updateRecord(body))
    }.flowOn(Dispatchers.IO)

    suspend fun getShoesHome() = flow {
        emit(service.getShoesHome())
    }.flowOn(Dispatchers.IO)

    suspend fun getShoesSeriesList(brandId: String) = flow {
        emit(service.getShoesSeriesList(brandId))
    }.flowOn(Dispatchers.IO)

    fun shoesCommentPage(
        productId: String?,
        nextKey: Long?
    ): Flowable<BaseResult<CircleProductCommentPage>> {
        return service.shoesCommentPage(productId, nextKey).compose(RxSchedulers.netio_main())
            .handError()
    }

    fun shoesCommentDetail(id: String): Flowable<BaseResult<CircleCommentDetailBean>> {
        return service.shoesCommentDetail(id, null).compose(RxSchedulers.netio_main()).handError()
    }

    fun shoesCommentDelete(id: String): Flowable<BaseResult<String>> {
        val body = ParameterBuilder()
            .add("id", id)
            .toBody()
        return service.shoesCommentDelete(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun commentShoes(
        productId: String,
        mentionedId: String?,
        content: String?,
        dynamicImgUrl: String?,
        staticImgUrl: String?
    ): Flowable<BaseResult<CircleDynamicDetailCommentsBean>> {
        val parameterBuilder = ParameterBuilder()
        productId.let {
            parameterBuilder.add("seriesId", it)
        }
        mentionedId?.let {
            parameterBuilder.add("mentionedId", it)
        }
        content?.let {
            parameterBuilder.add("content", content)
        }
        dynamicImgUrl?.let {
            parameterBuilder.add("dynamicImgUrl", it)
        }
        if (StringUtils.isEmpty(staticImgUrl)) {//如果是空 就把 dynamicImgUrl当做staticImgUrl
            dynamicImgUrl?.let {
                parameterBuilder.add("staticImgUrl", it)
            }
        } else {
            parameterBuilder.add("staticImgUrl", staticImgUrl)
        }
        return service.commentShoes(parameterBuilder.toBody()).compose(RxSchedulers.netio_main())
            .handError()
    }

    fun favoredCommentShoesSeries(
        commentId: String?,
        favored: Boolean
    ): Flowable<BaseResult<String>> {
        val body = ParameterBuilder()
            .add("commentId", commentId)
            .add("favored", favored)
            .toBody()
        return service.favoredCommentShoesSeries(body).compose(RxSchedulers.netio_main())
            .handError()
    }


    suspend fun dealDetails(id: String): BaseResult<DealDetailsEntity> {
        return service.dealDetails(id)
    }


    suspend fun dealDetailsPostPage(
        productId: String?,
        goodsQuality: String?,
        goodsSize: String?,
        nextKey: Long?
    ): BaseResult<BasePageEntity<DealPostItemEntity>> {
        return service.dealDetailsPostPage(productId, goodsQuality, goodsSize, nextKey)
    }


    suspend fun getProductDetail(productId: String): Flow<BaseResult<HomeProductDetailData>> {
        return flow {
            emit(service.getProductDetail(productId, null, null))
        }.flowOn(Dispatchers.IO)
    }
}