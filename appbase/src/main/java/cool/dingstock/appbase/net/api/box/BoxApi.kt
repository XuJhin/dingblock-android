package cool.dingstock.appbase.net.api.box

import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.box.*
import cool.dingstock.appbase.entity.bean.box.category.BoxCategoryListEntity
import cool.dingstock.appbase.net.retrofit.api.BaseApi
import cool.dingstock.appbase.net.retrofit.api.handError
import cool.dingstock.lib_base.thread.scheduler.RxSchedulers
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Retrofit
import javax.inject.Inject


/**
 * 类名：BoxApi
 * 包名：cool.dingstock.appbase.net.api.box
 * 创建时间：2021/7/26 12:05 下午
 * 创建人： WhenYoung
 * 描述：
 **/
class BoxApi @Inject constructor(retrofit: Retrofit) : BaseApi<BoxApiService>(retrofit) {


    suspend fun submitExchangeCode(code: String): BaseResult<Any> {
        val body = ParameterBuilder()
            .add("code", code)
            .toBody()
        return service.submitExchangeCode(body)
    }

    fun getNewHandReward(): Flowable<BaseResult<Any>> {
        return service.getNewHandReward().compose(RxSchedulers.netio_main()).handError()
    }

    fun openBox(
        boxId: String,
        count: Int,
        isTry: Boolean,
        isReOpen: Boolean,
        lastCardId: String,
    ): Flowable<BaseResult<OpenBoxEntity>> {
        val body = ParameterBuilder()
            .add("boxId", boxId)
            .add("count", count)
            .add("try", isTry)
            .add("reopen", isReOpen) // 是否重抽
            .add("preRecordId", lastCardId)// 上次抽卡记录ID
            .toBody()
        return service.openBox(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun useMultipleCard(preRecordId: String): Flowable<BaseResult<AfterUseCardsPrizeListEntity>> {
        val body = ParameterBuilder()
            .add("preRecordId", preRecordId)
            .toBody()
        return service.useMultipleCard(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun useSizeCard(preRecordId: String): Flowable<BaseResult<AfterUseCardsPrizeListEntity>> {
        val body = ParameterBuilder()
            .add("preRecordId", preRecordId)
            .toBody()
        return service.useSizeCard(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun getBoxDetail(id: String): Flowable<BaseResult<BoxDetailEntity>> {
        return service.getBoxDetail(id).compose(RxSchedulers.netio_main()).handError()
    }

    fun setBagGoodIsRead(id: String): Flowable<BaseResult<Any>> {
        val body = ParameterBuilder()
            .add("id", id)
            .toBody()
        return service.setBagGoodIsRead(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun getBagList(
        type: String,
        lab: Boolean,
        nextKey: String
    ): Flowable<BaseResult<BagPageEntity>> {
        return service.getBagList(type, lab, nextKey).compose(RxSchedulers.netio_main()).handError()
    }

    fun getCoinRecord(nextKey: String): Flowable<BaseResult<CoinRecordPageEntity>> {
        return service.getCoinRecord(nextKey).compose(RxSchedulers.netio_main()).handError()
    }

    fun getReceiveRecord(nextKey: String?): Flowable<BaseResult<ReceiveRecordPageEntity>> {
        return service.getReceiveRecord(nextKey).compose(RxSchedulers.netio_main()).handError()
    }

    fun fetchGoodsDetails(skuId: String?): Flowable<BaseResult<BoxGoodsDetailsResultEntity>> {
        return service.fetchGoodsDetails(skuId).compose(RxSchedulers.netio_main()).handError()
    }

    fun createReceiveOrder(
        id: String,
        count: Int,
        addressId: String
    ): Flowable<BaseResult<Any>> {
        val body = ParameterBuilder().add("id", id)
            .add("count", count)
            .add("addressId", addressId)
            .toBody()
        return service.createReceiveOrder(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun refreshGoodsDetails(
        spuId: String,
        attributes: Map<String, String>
    ): Flowable<BaseResult<BoxGoodsDetailsResultEntity>> {
        val list = arrayListOf<Map<String, String>>()
        attributes.forEach {
            list.add(mapOf("key" to it.key, "value" to it.value))
        }
        val body = ParameterBuilder().add("spuId", spuId)
            .add("attributes", list)
            .toBody()
        return service.refreshGoodsDetails(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun addMyPackage(
        count: Int,
        skuId: String
    ): Flowable<BaseResult<BoxUpdatePackageResultEntity>> {
        val body = ParameterBuilder().add("count", count)
            .add("skuId", skuId)
            .toBody()
        return service.addMyPackage(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun packageCount(): Flowable<BaseResult<BoxUpdatePackageResultEntity>> {
        return service.packageCount().compose(RxSchedulers.netio_main()).handError()
    }

    fun fetchHomeInfo(tabId: String? = null): Flowable<BaseResult<BoxHomeResultTabEntity>> {
        return service.fetchHomeInfo(tabId).compose(RxSchedulers.netio_main()).handError()
    }

    fun fetchTabResult(
        tabId: String? = null,
        pageNum: Int? = 0
    ): Flowable<BaseResult<BoxTabHomeInfoEntity>> {
        return service.fetchTabResult(tabId, pageNum).compose(RxSchedulers.netio_main()).handError()
    }

    fun fetchTabFirstPageResult(
        tabId: String? = null,
    ): Flowable<BaseResult<BoxTabHomeInfoEntity>> {
        return service.fetchTabFirstPageResult(tabId).compose(RxSchedulers.netio_main()).handError()
    }

    fun fetchSearchHotWords(): Flowable<BaseResult<BoxSearchHotListEntity>> {
        return service.fetchSearchHotWords().compose(RxSchedulers.netio_main()).handError()
    }


    fun boxFetchSearchResult(
        keyword: String,
        sortBy: String? = null,
        sortOrder: Int? = null,
        pageNum: Int? = null,
        pageSize: Int? = null,
        filter: MutableList<BoxAttributesEntity>? = null
    ): Flowable<BaseResult<BoxSearchResultEntity>> {
        val builder = ParameterBuilder()
        builder.add("keyword", keyword)
            .toBody()
        sortBy?.let {
            builder.add("sortBy", it)
        }
        sortOrder?.let {
            builder.add("sortOrder", it)
        }
        pageNum?.let {
            builder.add("pageNum", it)
        }
        pageSize?.let {
            builder.add("pageSize", it)
        }
        filter.let {
            builder.add("filter", it)
        }
        return service.boxFetchSearchResult(builder.toBody()).compose(RxSchedulers.netio_main())
            .handError()
    }

    fun buyGoods(
        currentType: String,
        count: Int,
        skuId: String,
    ): Flowable<BaseResult<BoxBuyOrderEntity>> {
        val skuMap = mapOf("type" to "sku", "id" to skuId, "count" to count)
        val body = ParameterBuilder().add("currencyType", currentType)
            .add("skus", listOf(skuMap))
            .toBody()
        return service.buyGoods(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun choosePrize(
        preRecordId: String,
        targetId: String
    ): Flowable<BaseResult<ChoosePrizeEntity>> {
        val body = ParameterBuilder()
            .add("preRecordId", preRecordId)
            .add("targetId", targetId)
            .toBody()
        return service.choosePrize(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun chooseSizePrize(
        preRecordId: String,
        targetId: String
    ): Flowable<BaseResult<ChoosePrizeEntity>> {
        val body = ParameterBuilder()
            .add("preRecordId", preRecordId)
            .add("targetId", targetId)
            .toBody()
        return service.chooseSizePrize(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun useCardBuyGoods(
        currentType: String,
        count: Int,
        skuId: String,
    ): Flowable<BaseResult<BoxBuyOrderEntity>> {
        val skuMap = mapOf("type" to "card", "id" to skuId, "count" to count)
        val body = ParameterBuilder().add("currencyType", currentType)
            .add("skus", listOf(skuMap))
            .toBody()
        return service.buyGoods(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun aliPrePay(orderId: String): Flowable<BaseResult<BoxAliPayPrePayResult>> {
        val body = ParameterBuilder()
            .add("orderId", orderId)
            .add("payMethod", "ali_pay")
            .toBody()
        return service.aliPrePay(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun wechatPrePay(orderId: String): Flowable<BaseResult<BoxWeChatPayPrePayResult>> {
        val body = ParameterBuilder()
            .add("orderId", orderId)
            .add("payMethod", "wechat_pay")
            .toBody()
        return service.weChatPrePay(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun fakePay(orderId: String): Flowable<BaseResult<String>> {
        val body = ParameterBuilder()
            .add("orderId", orderId)
            .toBody()
        return service.fakePay(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun fetchBoxCategory(): Flowable<BaseResult<BoxCategoryListEntity>> {
        return service.fetchBoxCategory().compose(RxSchedulers.netio_main()).handError()
    }

    fun homeBoxList(): Flowable<BaseResult<BoxHomeIndexResultEntity>> {
        return service.homeBoxList().compose(RxSchedulers.netio_main()).handError()
    }

    fun cashBuyBox(
        boxId: String,
        count: Int,
        couponId: String?
    ): Flowable<BaseResult<BoxBuyOrderEntity>> {
        val skuMap = mapOf("type" to "box", "id" to boxId, "count" to count)
        val body = ParameterBuilder().add("currencyType", "cash")
            .add("skus", listOf(skuMap))
            .add("couponId", couponId)
            .toBody()
        return service.buyGoods(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun boxDanMu(): Flowable<BaseResult<BoxDanMuResult>> {
        return service.boxDanMu().compose(RxSchedulers.netio_main()).handError()
    }

    suspend fun activityInfo(): BaseResult<BoxActivityEntity> {
        return service.activityInfo()
    }

    suspend fun getDiscountPrice(boxId: String, count: Int, couponId: String) = flow {
        emit(service.getDiscountPrice(boxId, count, couponId))
    }.flowOn(Dispatchers.IO)

    suspend fun fetchUsefulCoupons(
        boxId: String,
        count: Int
    ): BaseResult<BoxQueryCouponListEntity> {
        return service.fetchUsefulCoupons(boxId, count)
    }

    suspend fun searchCoupon() = flow {
        emit(service.searchCoupon())
    }.flowOn(Dispatchers.IO)

    suspend fun cancelOrder(orderId: String) = flow {
        val body = ParameterBuilder().add("orderId", orderId)
            .toBody()
        emit(service.cancelOrder(body))
    }.flowOn(Dispatchers.IO)

    suspend fun fetchUserIcon(): BaseResult<BoxUserInfoEntity> {
        return service.fetchUserIcon()
    }
}