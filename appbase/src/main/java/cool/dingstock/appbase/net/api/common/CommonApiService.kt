package cool.dingstock.appbase.net.api.common

import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.common.CommonConfigEntity
import cool.dingstock.appbase.entity.bean.config.*
import cool.dingstock.appbase.entity.bean.home.bp.GoodDetailEntity
import cool.dingstock.appbase.entity.bean.party.PartyDialogEntity
import cool.dingstock.appbase.entity.bean.pay.WeChatOrderData

import io.reactivex.rxjava3.core.Flowable
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/14  9:57
 */
interface CommonApiService {

    @GET("xserver/oss/url")
    fun getOssFileUrl(
        @Query("filename") fileName: String,
        @Query("filetype") filetype: String,
    ): Flowable<BaseResult<String>>

    @POST("xserver/oss/urls")
    fun getOssFilesUrls(
        @Body requestBody: RequestBody,
    ): Flowable<BaseResult<ArrayList<String>>>

    @POST("xserver/bp/resolve/common")
    fun resolveCommon(
        @Body requestBody: RequestBody,
        ): Flowable<BaseResult<GoodDetailEntity>>

    @POST("xserver/bp/resolve/tkl")
    fun resolveTkl(
        @Body requestBody: RequestBody,
        ): Flowable<BaseResult<String?>>

    @POST("xserver/bp/report")
    fun reportResolve(
        @Body requestBody: RequestBody,

        ): Flowable<BaseResult<Any>>

    @POST("xserver/activity/action/save")
    fun obtainParityWord(
        @Body requestBody: RequestBody,): Flowable<BaseResult<String?>>

    @POST("xserver/activity/parse/pwd")
    fun resolveParityWord(
        @Body requestBody: RequestBody,
        ): Flowable<BaseResult<PartyDialogEntity?>>

    @POST("xserver/activity/result/save")
    fun completeParityWord(
        @Body requestBody: RequestBody,

        ): Flowable<BaseResult<Any?>>

    @GET("xserver/home/appconfig")
    fun appConfig(): Flowable<BaseResult<ConfigData?>>

    @GET("xserver/common/info")
    fun commonInfo(
        @Query("type") type: String,
     
    ): Flowable<BaseResult<CommonConfigEntity>>

    @GET("xserver/common/info")
    suspend fun bjLineTime(
        @Query("type") type: String,

        ): BaseResult<Long>

    @GET("/xserver/monitor/group/oldconfig")
    fun monitorConfig(): Flowable<BaseResult<MonitorConfig>>

    @GET("/xserver/vip/pay")
    fun aliPayInfo(
        @Query("goodsId") goodsId: String,
        @Query("provider") provider: String,

        ): Flowable<BaseResult<String>>

    @GET("/xserver/vip/pay")
    fun wechatPayInfo(
        @Query("goodsId") goodsId: String,
        @Query("provider") provider: String,

        ): Flowable<BaseResult<WeChatOrderData>>

    @POST("/xserver/user/openinstall")
    fun openinstall(
        @Body requestBody: RequestBody,

        ): Flowable<BaseResult<String>>

    @GET("xserver/home/emoji/config")
    fun emojiConfig(): Flowable<BaseResult<List<EmojiConfig>>>

    @GET("xserver/home/share/config")
    fun shareConfig(): Flowable<BaseResult<List<ShareConfig>>>

    @POST("/xserver/calendar/raffle/remind")
    fun setRemind(
        @Body requestBody: RequestBody,

        ): Flowable<BaseResult<Any>>

    @POST("/xserver/calendar/raffle/remind/cancel")
    fun cancelRemind(
        @Body requestBody: RequestBody,

        ): Flowable<BaseResult<Any>>

    @POST("/xserver/calendar/raffle/remind/config/update")
    fun updateRemindSetting(
        @Body requestBody: RequestBody,

        ): Flowable<BaseResult<Any>>

    @GET("/xserver/calendar/raffle/remind/config")
    fun fetchRemindSetting(): Flowable<BaseResult<RemindConfigEntity>>
}
