package cool.dingstock.appbase.net.api.shop

import android.text.TextUtils
import cool.dingstock.appbase.entity.bean.base.BasePageStringEntity
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.shop.*
import cool.dingstock.appbase.entity.bean.shop.category.OverseaHomeTabEntity
import cool.dingstock.appbase.entity.bean.shop.category.ShopCategoryListEntity
import cool.dingstock.appbase.net.retrofit.api.BaseApi
import cool.dingstock.appbase.net.retrofit.api.handError
import cool.dingstock.lib_base.thread.scheduler.RxSchedulers
import io.reactivex.rxjava3.core.Flowable
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * @author wangjiang
 *  CreateAt Time 2021/6/1  10:13
 */
class ShopApi @Inject constructor(retrofit: Retrofit) : BaseApi<ShopApiService>(retrofit) {
    fun getCouponList(): Flowable<BaseResult<CouPonListEntity>> {
        val body = ParameterBuilder()
                .toBody()
        return service.getCouponList(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun getAddressList(): Flowable<BaseResult<AddressList>> {
        return service.getAddressList().compose(RxSchedulers.netio_main()).handError()
    }

    fun deleteAddress(id: String): Flowable<BaseResult<Any>> {
        return service.deleteAddress(id).compose(RxSchedulers.netio_main()).handError()
    }

    fun addNewAddress(entity: AddressEntity): Flowable<BaseResult<AddressId>> {
        val body = ParameterBuilder()
                .add("name", entity.name)
                .add("mobile", entity.mobile)
                .add("mobileZone", entity.mobileZone)
                .add("province", entity.province)
                .add("city", entity.city)
                .add("district", entity.district)
                .add("address", entity.address)
                .add("idNo", entity.idNo)
                .add("idCard", entity.idCard)
                .add("isDefault", entity.isDefault)
                .toBody()
        return service.addNewAddress(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun getIdCardFrontMessageByPicture(imageUrl: String): Flowable<BaseResult<IdCardFrontMessage>> {
        val body = ParameterBuilder()
                .add("imageURL", imageUrl)
                .add("side", "face")
                .toBody()
        return service.getIdCardFrontMessageByPicture(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun getIdCardBackMessageByPicture(imageUrl: String): Flowable<BaseResult<IdCradBackMessage>> {
        val body = ParameterBuilder()
                .add("imageURL", imageUrl)
                .add("side", "back")
                .toBody()
        return service.getIdCardBackMessageByPicture(body).compose(RxSchedulers.netio_main()).handError()
    }


    fun shopFetchSearchResult(
        keyword: String,
        sortBy: String? = null,
        sortOrder: Int? = null,
        pageNum: Int? = null,
        pageSize: Int? = null,
        filter: MutableList<AttributesEntity>? = null
    ): Flowable<BaseResult<ShopSearchResultEntity>> {
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
        return service.shopFetchSearchResult(builder.toBody()).compose(RxSchedulers.netio_main())
            .handError()
    }

    fun editAddress(entity: AddressEntity): Flowable<BaseResult<Any>> {
        val body = ParameterBuilder()
            .add("id", entity.id)
            .add("name", entity.name)
            .add("mobile", entity.mobile)
            .add("mobileZone", entity.mobileZone)
            .add("province", entity.province)
            .add("city", entity.city)
            .add("district", entity.district)
            .add("address", entity.address)
            .add("idNo", entity.idNo)
            .add("idCard", entity.idCard)
            .add("isDefault", entity.isDefault)
            .toBody()
        return service.editAddress(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun getOrderList(state: String, nextKey: String): Flowable<BaseResult<OrderListEntity>> {
        return service.getOrderList(state, nextKey).compose(RxSchedulers.netio_main()).handError()
    }

    fun getOrderDetail(orderId: String): Flowable<BaseResult<OrderTailPageEntity>> {
        return service.getOrderDetail(orderId).compose(RxSchedulers.netio_main()).handError()
    }

    fun cancelOrder(orderId: String): Flowable<BaseResult<Any>> {
        val body = ParameterBuilder()
            .add("orderId", orderId)
            .toBody()
        return service.cancelOrder(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun fetchShopCategory(): Flowable<BaseResult<ShopCategoryListEntity>> {
        return service.fetchShopCategory().compose(RxSchedulers.netio_main()).handError()
    }

    fun fetchHomeInfo(tabId: String? = null): Flowable<BaseResult<ShopHomeInfoEntity>> {
        return service.fetchHomeInfo(tabId).compose(RxSchedulers.netio_main()).handError()
    }

    fun fetchClothData(tabId: String?, nextKey: String): Flowable<BaseResult<ShopIndexGoods>> {
        return service.fetchClothData(nextKey = nextKey, tabId = tabId)
            .compose(RxSchedulers.netio_main()).handError()
    }

    fun fetchGoodsDetails(skuId: String?): Flowable<BaseResult<GoodsDetailsResultEntity>> {
        return service.fetchGoodsDetails(skuId).compose(RxSchedulers.netio_main()).handError()
    }

    fun refreshGoodsDetails(
        spuId: String,
        attributes: Map<String, String>
    ): Flowable<BaseResult<GoodsDetailsResultEntity>> {
        val list = arrayListOf<Map<String, String>>()
        attributes.forEach {
            list.add(mapOf("key" to it.key, "value" to it.value))
        }
        val body = ParameterBuilder().add("spuId", spuId)
            .add("attributes", list)
            .toBody()
        return service.refreshGoodsDetails(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun addShopCar(count: Int, skuId: String): Flowable<BaseResult<UpdateShopResultEntity>> {
        val body = ParameterBuilder().add("count", count)
            .add("skuId", skuId)
            .toBody()
        return service.addShopCar(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun changeShopCarGoodsCount(
        count: Int,
        skuId: String
    ): Flowable<BaseResult<UpdateShopResultEntity>> {
        val body = ParameterBuilder().add("count", count)
            .add("skuId", skuId)
            .toBody()
        return service.updateShopCarCount(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun orderHistory(
        spuId: String,
        nextKey: String?
    ): Flowable<BaseResult<BasePageStringEntity<GoodsOrdersItemEntity>>> {
        return service.orderHistory(spuId, nextKey).compose(RxSchedulers.netio_main()).handError()
    }

    fun orderLogistics(
        skuId: String,
        orderId: String
    ): Flowable<BaseResult<LogisticsResultEntity>> {
        return service.orderLogistics(skuId, orderId).compose(RxSchedulers.netio_main()).handError()
    }

    fun shopCarList(): Flowable<BaseResult<GoodsCarResultEntity>> {
        return service.shopCarList().compose(RxSchedulers.netio_main()).handError()
    }

    fun computePrice(selSkus: ArrayList<GoodsCarSelSkuEntity>): Flowable<BaseResult<GoodsCarPriceResultEntity>> {
        val body = ParameterBuilder()
            .add("skus", selSkus)
            .toBody()
        return service.computePrice(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun fetchStoreList(
            tabId: String?,
            nextKey: String?
    ): Flowable<BaseResult<ShopStoreListEntity>> {
        return service.fetchStoreList(nextKey = nextKey, tabId = tabId)
                .compose(RxSchedulers.netio_main()).handError()
    }

    fun fetchSearchHotWords(): Flowable<BaseResult<SearchHotListEntity>> {
        return service.fetchSearchHotWords().compose(RxSchedulers.netio_main()).handError()
    }

    fun fetchOverseaHome(): Flowable<BaseResult<OverseaHomeTabEntity>> {
        return service.fetchOverseaHome().compose(RxSchedulers.netio_main()).handError()
    }

    fun buyAgain(orderId: String): Flowable<BaseResult<Any>> {
        val body = ParameterBuilder()
                .add("orderId", orderId)
                .toBody()
        return service.buyAgain(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun shopCarDelete(skuIdList: ArrayList<String>): Flowable<BaseResult<UpdateShopResultEntity>> {
        val body = ParameterBuilder()
            .add("skuIdList", skuIdList)
            .toBody()
        return service.shopCarDelete(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun checkStock(selSkus: ArrayList<GoodsCarSelSkuEntity>): Flowable<BaseResult<CheckStockResultEntity>> {
        val builder = ParameterBuilder()
            .add("skus", selSkus)
        val body = builder.toBody()
        return service.checkStock(body).compose(RxSchedulers.netio_main()).handError()
    }


    fun shopCarSubmit(
        selSkus: ArrayList<GoodsCarSelSkuEntity>,
        couponId: String?,
        useDefaultCoupon: Boolean
    ): Flowable<BaseResult<PreOrderResultEntity>> {
        val builder = ParameterBuilder()
            .add("skus", selSkus)
            .add("useDefaultCoupon", useDefaultCoupon)
        if (!TextUtils.isEmpty(couponId)) {
            builder.add("couponId", couponId)
        }
        val body = builder.toBody()
        return service.shopCarSubmit(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun createOrder(
        selSkus: ArrayList<GoodsCarSelSkuEntity>,
        addressId: String,
        remark: String,
        couponId: String
    )
            : Flowable<BaseResult<CreateOrderEntity>> {
        val builder = ParameterBuilder()
                .add("skus", selSkus)
                .add("addressId", addressId)
        if (remark.isNotEmpty()) {
            builder.add("remark", remark)
        }
        if (couponId.isNotEmpty()) {
            builder.add("couponId", couponId)
        }
        val body = builder.toBody()
        return service.createOrder(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun shopCarCount(): Flowable<BaseResult<UpdateShopResultEntity>> {
        return service.shopCarCount().compose(RxSchedulers.netio_main()).handError()
    }

    fun testPay(orderId: String): Flowable<BaseResult<Any>> {
        val body = ParameterBuilder()
            .add("orderId", orderId)
            .toBody()
        return service.testPay(body).compose(RxSchedulers.netio_main()).handError()
    }


    fun aliPrePay(orderId: String): Flowable<BaseResult<ShopAliPayPrePayResult>> {
        val body = ParameterBuilder()
            .add("orderId", orderId)
            .add("payMethod", "ali_pay")
            .toBody()
        return service.aliPrePay(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun wechatPrePay(orderId: String): Flowable<BaseResult<ShopWeChatResultOutEntity>> {
        val body = ParameterBuilder()
            .add("orderId", orderId)
            .add("payMethod", "wechat_pay")
            .toBody()
        return service.wechatPrePay(body).compose(RxSchedulers.netio_main()).handError()
    }


}