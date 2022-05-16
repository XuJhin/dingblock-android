package cool.dingstock.appbase.net.api.shop

import cool.dingstock.appbase.entity.bean.base.BasePageStringEntity
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.shop.*
import cool.dingstock.appbase.entity.bean.shop.category.OverseaHomeTabEntity
import cool.dingstock.appbase.entity.bean.shop.category.ShopCategoryListEntity
import io.reactivex.rxjava3.core.Flowable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * @author wangjiang
 *  CreateAt Time 2020/10/14  9:57
 */
interface ShopApiService {
    @POST("/xserver/oversea/address/create")
    fun addNewAddress(@Body body: RequestBody): Flowable<BaseResult<AddressId>>

    @GET("/xserver/oversea/address/delete")
    fun deleteAddress(@Query("id") id: String): Flowable<BaseResult<Any>>

    @POST("/xserver/oversea/address/update")
    fun editAddress(@Body body: RequestBody): Flowable<BaseResult<Any>>

    @GET("/xserver/oversea/address/find")
    fun getAddressList(): Flowable<BaseResult<AddressList>>

    @POST("/xserver/ocr/identitycard/recognize")
    fun getIdCardBackMessageByPicture(@Body body: RequestBody): Flowable<BaseResult<IdCradBackMessage>>

    @POST("/xserver/ocr/identitycard/recognize")
    fun getIdCardFrontMessageByPicture(@Body body: RequestBody): Flowable<BaseResult<IdCardFrontMessage>>

    @GET("/xserver/oversea/order/find_my_orders")
    fun getOrderList(
            @Query("state") state: String,
            @Query("nextKey") nextKey: String
    ): Flowable<BaseResult<OrderListEntity>>

    @POST("xserver/oversea/coupon/list")
    fun getCouponList(@Body body: RequestBody): Flowable<BaseResult<CouPonListEntity>>

    @GET("/xserver/oversea/order/detail")
    fun getOrderDetail(@Query("orderId") orderId: String): Flowable<BaseResult<OrderTailPageEntity>>

    @POST("/xserver/oversea/order/cancel")
    fun cancelOrder(@Body body: RequestBody): Flowable<BaseResult<Any>>

    @GET("/xserver/oversea/home/categories")
    fun fetchShopCategory(): Flowable<BaseResult<ShopCategoryListEntity>>

    @GET("/xserver/oversea/home/info")
    fun fetchHomeInfo(@Query("tabId") tabId: String?): Flowable<BaseResult<ShopHomeInfoEntity>>

    @GET("/xserver/oversea/home/skus")
    fun fetchClothData(
            @Query("nextKey") nextKey: String?,
            @Query("tabId") tabId: String?
    ): Flowable<BaseResult<ShopIndexGoods>>

    @GET("/xserver/oversea/product/detail")
    fun fetchGoodsDetails(@Query("skuId") skuId: String?): Flowable<BaseResult<GoodsDetailsResultEntity>>

    @POST("/xserver/oversea/product/find_sku")
    fun refreshGoodsDetails(@Body body: RequestBody): Flowable<BaseResult<GoodsDetailsResultEntity>>

    //    @POST("/xserver/oversea/shopcart/upsert")
    //    fun updateShopCarCount(@Body body: RequestBody): Flowable<BaseResult<AddShopResultEntity>>
    @POST("/xserver/oversea/shopcart/upsert")
    fun updateShopCarCount(@Body body: RequestBody): Flowable<BaseResult<UpdateShopResultEntity>>

    @POST("/xserver/oversea/shopcart/add")
    fun addShopCar(@Body body: RequestBody): Flowable<BaseResult<UpdateShopResultEntity>>

    @GET("/xserver/oversea/product/order_history?")
    fun orderHistory(
            @Query("spuId") spuId: String,
            @Query("nextKey") nextKey: String?
    ): Flowable<BaseResult<BasePageStringEntity<GoodsOrdersItemEntity>>>

    @GET("/xserver/oversea/order/express")
    fun orderLogistics(
            @Query("skuId") skuId: String,
            @Query("orderId") orderId: String
    ): Flowable<BaseResult<LogisticsResultEntity>>

    @POST("/xserver/oversea/shopcart/compute_price")
    fun computePrice(@Body body: RequestBody): Flowable<BaseResult<GoodsCarPriceResultEntity>>

    @GET("/xserver/oversea/shopcart/list")
    fun shopCarList(): Flowable<BaseResult<GoodsCarResultEntity>>

    @GET("/xserver/oversea/home/shops")
    fun fetchStoreList(
            @Query("nextKey") nextKey: String?,
            @Query("tabId") tabId: String?
    ): Flowable<BaseResult<ShopStoreListEntity>>

    @GET("/xserver/oversea/search/hot_word")
    fun fetchSearchHotWords(): Flowable<BaseResult<SearchHotListEntity>>

    @GET("/xserver/oversea/home/home_tab")
    fun fetchOverseaHome(): Flowable<BaseResult<OverseaHomeTabEntity>>

    @POST("/xserver/oversea/order/buy_again")
    fun buyAgain(@Body body: RequestBody): Flowable<BaseResult<Any>>

    @POST("/xserver/search/oversea")
    fun shopFetchSearchResult(@Body requestBody: RequestBody): Flowable<BaseResult<ShopSearchResultEntity>>

    @POST("/xserver/oversea/shopcart/delete")
    fun shopCarDelete(@Body requestBody: RequestBody): Flowable<BaseResult<UpdateShopResultEntity>>

    @POST("/xserver/oversea/shopcart/check_stock")
    fun checkStock(@Body requestBody: RequestBody): Flowable<BaseResult<CheckStockResultEntity>>

    @POST("/xserver/oversea/shopcart/pre_order")
    fun shopCarSubmit(@Body requestBody: RequestBody): Flowable<BaseResult<PreOrderResultEntity>>

    @POST("/xserver/oversea/order/create")
    fun createOrder(@Body body: RequestBody): Flowable<BaseResult<CreateOrderEntity>>

    @GET("/xserver/oversea/shopcart/count")
    fun shopCarCount(): Flowable<BaseResult<UpdateShopResultEntity>>

    @POST("/xserver/oversea/order/fake_pay")
    fun testPay(@Body body: RequestBody): Flowable<BaseResult<Any>>

    @POST("/xserver/oversea/order/pre_pay")
    fun aliPrePay(@Body body: RequestBody): Flowable<BaseResult<ShopAliPayPrePayResult>>

    @POST("/xserver/oversea/order/pre_pay")
    fun wechatPrePay(@Body body: RequestBody): Flowable<BaseResult<ShopWeChatResultOutEntity>>

}