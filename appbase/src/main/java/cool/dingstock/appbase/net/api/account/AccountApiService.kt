package cool.dingstock.appbase.net.api.account

import cool.dingstock.appbase.entity.bean.PageEntity
import cool.dingstock.appbase.entity.bean.account.DcLoginUser
import cool.dingstock.appbase.entity.bean.account.LayeredConfirmEntity
import cool.dingstock.appbase.entity.bean.account.UserInfoEntity
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.home.NoticeItemEntity
import cool.dingstock.appbase.entity.bean.mine.AccountSettingEntity
import cool.dingstock.appbase.entity.bean.mine.PendantHomeBean
import cool.dingstock.appbase.entity.bean.shop.AddressId
import cool.dingstock.appbase.entity.bean.shop.UserAddressEntity
import cool.dingstock.appbase.entity.bean.vip.KOLEntity
import cool.dingstock.appbase.entity.bean.vip.VipPriceEntity
import io.reactivex.rxjava3.core.Flowable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/14  9:57
 */
interface AccountApiService {

    @POST("/xserver/fashionplay/xserver/express_order/create_order")
    fun createReceiveOrder(@Body body: RequestBody): Flowable<BaseResult<Any>>

    @POST("/xserver/user/address/create")
    fun addNewAddress(@Body body: RequestBody): Flowable<BaseResult<AddressId>>

    @GET("/xserver/user/address/delete")
    fun deleteAddress(@Query("id") id: String): Flowable<BaseResult<Any>>

    @POST("/xserver/user/address/update")
    fun editAddress(@Body body: RequestBody): Flowable<BaseResult<Any>>

    @GET("/xserver/user/address/find")
    fun getAddressList(): Flowable<BaseResult<List<UserAddressEntity>>>

    @POST("xserver/user/u/change/token")
    fun changeToken(@Body body: RequestBody): Flowable<BaseResult<DcLoginUser>>

    @POST("xserver/user/u/login")
    fun loginMobile(@Body body: RequestBody): Flowable<BaseResult<DcLoginUser>>

    @POST("xserver/user/u/wechat/login")
    fun loginWeChat(@Body body: RequestBody): Flowable<BaseResult<DcLoginUser>>

    @POST("xserver/user/u/login")
    fun loginCid(@Body body: RequestBody): Flowable<BaseResult<DcLoginUser>>

    @GET("xserver/user/u/exit")
    fun loginOut(): Flowable<BaseResult<Any>>

    @POST("xserver/user/logout")
    fun registerOut(): Flowable<BaseResult<Any>>

    @POST("xserver/user/u/update")
    fun update(@Body body: RequestBody): Flowable<BaseResult<DcLoginUser>>

    @GET("xserver/user/u/detail")
    fun getUser(): Flowable<BaseResult<DcLoginUser>>

    @GET("xserver/user/u/detail")
    fun getUserInfo(): Flowable<BaseResult<UserInfoEntity>>

    @GET("/xserver/community/user/message")
    fun requestNotice(
        @Query("nextKey") nextKey: Long?,
        @Query("type") type: String?
    ): Flowable<BaseResult<PageEntity<NoticeItemEntity>>>

    @GET("/xserver/sms/captcha")
    fun getCaptchaRequirement(): Flowable<BaseResult<Boolean>>

    @GET("/xserver/vip/kolexists")
    fun kolExists(): Flowable<BaseResult<KOLEntity>>

    @GET("/xserver/vip/prices")
    fun vipPrices(): Flowable<BaseResult<ArrayList<VipPriceEntity>>>

    @GET("/xserver/vip/discount/check")
    fun checkCode(@Query("code") code: String): Flowable<BaseResult<ArrayList<VipPriceEntity>>>

    @POST("/xserver/sms/send")
    fun getSmsCode(@Body body: RequestBody): Flowable<BaseResult<String>>

    @GET("xserver/activity/layer/confirm")
    fun layerConfirm(@Query("type") type: String): Flowable<BaseResult<LayeredConfirmEntity>>

    @POST("/xserver/vip/activate")
    fun activate(@Body body: RequestBody): Flowable<BaseResult<String>>

    @GET("/xserver/user/avatar/list")
    suspend fun getPendantHome(): BaseResult<PendantHomeBean>

    @POST("/xserver/user/avatar/equip")
    suspend fun wearPendant(@Body body: RequestBody): BaseResult<Any>

    @GET("/xserver/user/account/manage/info")
    fun fetchAccount(): Flowable<BaseResult<AccountSettingEntity>>

    @POST("/xserver/user/account/manage/mobile/confirm")
    fun checkCurrentPhone(@Body body: RequestBody): Flowable<BaseResult<Any>>

    @POST("/xserver/user/account/manage/mobile/exchange")
    fun updateBindPhone(@Body body: RequestBody): Flowable<BaseResult<DcLoginUser>>

    @POST("/xserver/user/account/manage/wx/unbind")
    fun unBindWeChat(): Flowable<BaseResult<Any>>

    @POST("/xserver/user/account/manage/wx/bind")
    fun bindWeChat(@Body body: RequestBody): Flowable<BaseResult<Any>>

    @POST("/xserver/user/u/update/check")
    fun checkUserMsgUpdate(@Body body: RequestBody): Flowable<BaseResult<Any>>
}