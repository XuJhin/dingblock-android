package cool.dingstock.setting.api

import cool.dingstock.appbase.entity.bean.base.BasePageEntity
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.mine.AccountInfoBriefEntity
import cool.dingstock.appbase.entity.bean.post.CancelShieldResultEntity
import cool.dingstock.appbase.net.retrofit.api.BaseApi
import cool.dingstock.appbase.net.retrofit.api.handError
import cool.dingstock.lib_base.thread.scheduler.RxSchedulers
import cool.dingstock.appbase.util.LoginUtils
import io.reactivex.rxjava3.core.Flowable
import retrofit2.Retrofit
import javax.inject.Inject

class SettingRepository @Inject constructor(retrofit: Retrofit) : BaseApi<SettingService>(retrofit) {
	fun fetchShieldList(page: Int): Flowable<BaseResult<BasePageEntity<AccountInfoBriefEntity>>> {
		val userId = LoginUtils.getCurrentUser()?.id
		return service.fetchShieldList(page).compose(RxSchedulers.io_main()).handError()
	}

	fun cancelShield(userId: String): Flowable<BaseResult<CancelShieldResultEntity>> {
		val body = ParameterBuilder()
				.add("userId", userId)
				.toBody()
		return service.cancelShield(body).compose(RxSchedulers.io_main()).handError()
	}
}