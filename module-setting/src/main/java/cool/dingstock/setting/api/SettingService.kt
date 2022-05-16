package cool.dingstock.setting.api

import cool.dingstock.appbase.entity.bean.base.BasePageEntity
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.mine.AccountInfoBriefEntity
import cool.dingstock.appbase.entity.bean.post.CancelShieldResultEntity
import io.reactivex.rxjava3.core.Flowable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SettingService {
	@GET("xserver/user/block/page")
	fun fetchShieldList(@Query("pageNum") page: Int = 0)
			: Flowable<BaseResult<BasePageEntity<AccountInfoBriefEntity>>>

	@POST("xserver/user/block/cancel")
	fun cancelShield(@Body body: RequestBody): Flowable<BaseResult<CancelShieldResultEntity>>
}