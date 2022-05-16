package cool.dingstock.appbase.net.api.box

import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.box.*
import cool.dingstock.appbase.entity.bean.box.category.BoxCategoryListEntity
import io.reactivex.rxjava3.core.Flowable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


/**
 * 类名：BoxApiService
 * 包名：cool.dingstock.appbase.net.api.box
 * 创建时间：2021/7/26 12:05 下午
 * 创建人： WhenYoung
 * 描述：
 **/
interface BoxApiService {

    @POST("/xserver/fashionplay/xserver/exchange_code/use")
    suspend fun submitExchangeCode(@Body requestBody: RequestBody): BaseResult<Any>

    @GET("/xserver/fashionplay/xserver/user/new_player_reward")
    fun getNewHandReward(): Flowable<BaseResult<Any>>

    @POST("/xserver/fashionplay/xserver/box/open")
    fun openBox(@Body requestBody: RequestBody): Flowable<BaseResult<OpenBoxEntity>>

    @POST("/xserver/fashionplay/xserver/box/multi_use")
    fun useMultipleCard(@Body requestBody: RequestBody): Flowable<BaseResult<AfterUseCardsPrizeListEntity>>

    @POST("/xserver/fashionplay/xserver/box/size_use")
    fun useSizeCard(@Body requestBody: RequestBody): Flowable<BaseResult<AfterUseCardsPrizeListEntity>>

    @POST("/xserver/fashionplay/xserver/box/size_select")
    fun chooseSizePrize(@Body requestBody: RequestBody): Flowable<BaseResult<ChoosePrizeEntity>>

    @GET("/xserver/fashionplay/xserver/box/detail")
    fun getBoxDetail(@Query("id") id: String?): Flowable<BaseResult<BoxDetailEntity>>

    @POST("/xserver/fashionplay/xserver/express_order/create_order")
    fun createReceiveOrder(@Body body: RequestBody): Flowable<BaseResult<Any>>

    @GET("/xserver/fashionplay/xserver/coin_bill/list")
    fun getCoinRecord(@Query("nextKey") nextKey: String?): Flowable<BaseResult<CoinRecordPageEntity>>

    @GET("/xserver/fashionplay/xserver/express_order/list")
    fun getReceiveRecord(@Query("nextKey") nextKey: String?): Flowable<BaseResult<ReceiveRecordPageEntity>>

    @GET("/xserver/fashionplay/xserver/bag/list")
    fun getBagList(
        @Query("type") type: String?,
        @Query("lab") lab: Boolean?,
        @Query("nextKey") nextKey: String?
    ): Flowable<BaseResult<BagPageEntity>>

    @POST("/xserver/fashionplay/xserver/bag/set_read")
    fun setBagGoodIsRead(@Body body: RequestBody): Flowable<BaseResult<Any>>

    @GET("/xserver/fashionplay/xserver/product/detail")
    fun fetchGoodsDetails(@Query("skuId") skuId: String?): Flowable<BaseResult<BoxGoodsDetailsResultEntity>>

    @POST("/xserver/fashionplay/xserver/product/find_sku")
    fun refreshGoodsDetails(@Body body: RequestBody): Flowable<BaseResult<BoxGoodsDetailsResultEntity>>

    @POST("/xserver/oversea/shopcart/add")
    fun addMyPackage(@Body body: RequestBody): Flowable<BaseResult<BoxUpdatePackageResultEntity>>

    @GET("/xserver/fashionplay/xserver/user/info")
    fun packageCount(): Flowable<BaseResult<BoxUpdatePackageResultEntity>>

    @GET("/xserver/fashionplay/xserver/home/info")
    fun fetchHomeInfo(@Query("tabId") tabId: String?): Flowable<BaseResult<BoxHomeResultTabEntity>>

    @GET("/xserver/fashionplay/xserver/home/skus")
    fun fetchTabResult(
        @Query("tabId") tabId: String?,
        @Query("pageNum") pageNum: Int?
    ): Flowable<BaseResult<BoxTabHomeInfoEntity>>

    @GET("/xserver/fashionplay/xserver/home/info")
    fun fetchTabFirstPageResult(
        @Query("tabId") tabId: String?,
    ): Flowable<BaseResult<BoxTabHomeInfoEntity>>

    @GET("/xserver/fashionplay/xserver/search/hot_word")
    fun fetchSearchHotWords(): Flowable<BaseResult<BoxSearchHotListEntity>>

    @POST("/xserver/search/tide")
    fun boxFetchSearchResult(@Body requestBody: RequestBody): Flowable<BaseResult<BoxSearchResultEntity>>

    @POST("/xserver/fashionplay/xserver/order/create")
    fun buyGoods(@Body requestBody: RequestBody): Flowable<BaseResult<BoxBuyOrderEntity>>

    @POST("/xserver/fashionplay/xserver/box/multi_select")
    fun choosePrize(@Body requestBody: RequestBody): Flowable<BaseResult<ChoosePrizeEntity>>

    @POST("/xserver/fashionplay/xserver/order/pre_pay")
    fun aliPrePay(@Body requestBody: RequestBody): Flowable<BaseResult<BoxAliPayPrePayResult>>

    @POST("/xserver/fashionplay/xserver/order/pre_pay")
    fun weChatPrePay(@Body requestBody: RequestBody): Flowable<BaseResult<BoxWeChatPayPrePayResult>>

    @POST("/xserver/fashionplay/xserver/order/fake_pay")
    fun fakePay(@Body requestBody: RequestBody): Flowable<BaseResult<String>>

    @GET("/xserver/fashionplay/xserver/home/categories")
    fun fetchBoxCategory(): Flowable<BaseResult<BoxCategoryListEntity>>

    @GET("/xserver/fashionplay/xserver/box/list")
    fun homeBoxList(): Flowable<BaseResult<BoxHomeIndexResultEntity>>

    @GET("/xserver/fashionplay/xserver/box/others_record")
    fun boxDanMu(): Flowable<BaseResult<BoxDanMuResult>>

    @GET("/xserver/fashionplay/xserver/box/activity_info")
    suspend fun activityInfo(): BaseResult<BoxActivityEntity>

    @GET("/xserver/fashionplay/xserver/box/discount_query")
    suspend fun getDiscountPrice(
        @Query("boxId") boxId: String,
        @Query("count") count: Int,
        @Query("couponId") couponId: String
    ): BaseResult<BoxDiscountBean>
    @GET("/xserver/fashionplay/xserver/box/coupon_list")
    suspend fun fetchUsefulCoupons(
        @Query("boxId") boxId: String,
        @Query("count") count: Int
    ): BaseResult<BoxQueryCouponListEntity>

    @GET("/xserver/fashionplay/xserver/box/popups")
    suspend fun searchCoupon(): BaseResult<BoxCouponSearchBean>

    @POST("/xserver/fashionplay/xserver/order/cancel")
    suspend fun cancelOrder(@Body requestBody: RequestBody): BaseResult<String>

    @GET("/xserver/fashionplay/xserver/box/myinfo")
    suspend fun fetchUserIcon(): BaseResult<BoxUserInfoEntity>
}