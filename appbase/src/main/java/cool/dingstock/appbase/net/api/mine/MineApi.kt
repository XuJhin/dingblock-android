package cool.dingstock.appbase.net.api.mine

import cool.dingstock.appbase.entity.bean.account.DcLoginUser
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.circle.*
import cool.dingstock.appbase.entity.bean.common.CommonPageEntity
import cool.dingstock.appbase.entity.bean.mine.MineFollowEntity
import cool.dingstock.appbase.entity.bean.mine.MineMallData
import cool.dingstock.appbase.entity.bean.mine.VipActivityBean
import cool.dingstock.appbase.entity.bean.mine.VipPrivilegeEntity
import cool.dingstock.appbase.entity.bean.score.*
import cool.dingstock.appbase.net.retrofit.api.BaseApi
import cool.dingstock.appbase.net.retrofit.api.handError
import cool.dingstock.lib_base.thread.scheduler.RxSchedulers
import io.reactivex.rxjava3.core.Flowable
import okhttp3.RequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.POST
import javax.inject.Inject

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/16  10:00
 */
class MineApi @Inject constructor(retrofit: Retrofit) : BaseApi<MineApiService>(retrofit) {
    fun switchFollowState(followUserId: String, follow: Boolean): Flowable<BaseResult<Int>> {
        val body = ParameterBuilder()
            .add("followUserId", followUserId)
            .add("follow", follow)
            .toBody()
        return service.switchFollowState(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun requestFavorList(userId: String, nextKey: Long?): Flowable<BaseResult<MineFollowEntity>> {
        return service.requestFavorList(userId, nextKey).compose(RxSchedulers.netio_main())
            .handError()
    }

    fun requestFavorCommentList(commentId: String, nextKey: Long?): Flowable<BaseResult<MineFollowEntity>> {
        return service.requestFavorCommentList(commentId, nextKey).compose(RxSchedulers.netio_main())
            .handError()
    }

    fun requestFansList(userId: String, nextKey: Long?): Flowable<BaseResult<MineFollowEntity>> {
        return service.requestFansList(userId, nextKey).compose(RxSchedulers.netio_main())
            .handError()
    }

    fun requestFollowUser(userId: String, nextKey: Long?): Flowable<BaseResult<MineFollowEntity>> {
        return service.requestFollowUser(userId, nextKey).compose(RxSchedulers.netio_main())
            .handError()
    }

    fun vipPrivilege(): Flowable<BaseResult<ArrayList<VipPrivilegeEntity>>> {
        return service.vipPrivilege().compose(RxSchedulers.netio_main()).handError()
    }

    fun vipActivityPrices(type: String): Flowable<BaseResult<VipActivityBean>> {
        return service.vipActivityPrices(type).compose(RxSchedulers.netio_main()).handError()
    }

    fun mall(): Flowable<BaseResult<MineMallData>> {
        return service.mall().compose(RxSchedulers.netio_main()).handError()
    }

    fun checkIn(): Flowable<BaseResult<String>> {
        return service.checkIn().compose(RxSchedulers.netio_main()).handError()
    }

    fun redeem(id: String): Flowable<BaseResult<String>> {
        val body = ParameterBuilder().add("redeemId", id).toBody()
        return service.redeem(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun scoreIndexInfo(): Flowable<BaseResult<ScoreIndexInfoEntity>> {
        return service.scoreIndexInfo().compose(RxSchedulers.netio_main()).handError()
    }

    fun scoreSign(): Flowable<BaseResult<ScoreIndexUserInfoEntity>> {
        return service.scoreSign().compose(RxSchedulers.netio_main()).handError()
    }

    fun switchSignAlert(isAlert: Boolean): Flowable<BaseResult<Any>> {
        val body = ParameterBuilder().add("signAlert", isAlert).toBody()
        return service.switchSignAlert(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun scoreExchange(productId: String): Flowable<BaseResult<ScoreExchangeResultEntity>> {
        val body = ParameterBuilder().add("productId", productId).toBody()
        return service.scoreExchange(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun detectTask(taskId: String): Flowable<BaseResult<Any>> {
        val body = ParameterBuilder().add("taskId", taskId).toBody()
        return service.detectTask(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun scorePage(
        type: String? = null,
        nextKey: Long?
    ): Flowable<BaseResult<CommonPageEntity<ScoreRecordEntity>>> {
        return service.scorePage(type, nextKey).compose(RxSchedulers.netio_main()).handError()
    }

    fun receiveScore(taskId: String, taskType: String): Flowable<BaseResult<String>> {
        return service.receiveScore(taskId, taskType).compose(RxSchedulers.netio_main()).handError()
    }


    fun submitinformation(
        id: String,
        consignee: String,
        consignPhone: String,
        address: String
    ): Flowable<BaseResult<Any>> {
        val body = ParameterBuilder()
            .add("id", id)
            .add("consignee", consignee)
            .add("consignPhone", consignPhone)
            .add("address", address).toBody()
        return service.submitinformation(body).compose(RxSchedulers.netio_main()).handError()
    }

    /**
     * 兑换商品详情页
     */
    fun scoreDetail(id: String): Flowable<BaseResult<ExchangeGoodsDetailEntity>> {
        return service.scoreDetail(id).compose(RxSchedulers.netio_main()).handError()
    }

    fun fetchSelfDetail(userId: String): Flowable<BaseResult<AccountBriefEntity>> {
        return service.selfBrief(userId).compose(RxSchedulers.io_main()).handError()
    }

    fun fetchAccountPosts(
        userId: String,
        nextKey: Long?,
        postType: String
    ): Flowable<BaseResult<UserPostsEntity>> {
        return service.accountPost(userId, nextKey, postType).compose(RxSchedulers.io_main())
            .handError()
    }

    fun fetchSelf(): Flowable<BaseResult<DcLoginUser>> {
        return service.selfInfo().compose(RxSchedulers.io_main()).handError()
    }

    fun fetchMineCollections(
        isDeal: Boolean,
        nextKey: Long?
    ): Flowable<BaseResult<UserPostsEntity>> {
        return service.fetchMineCollections(nextKey, isDeal).compose(RxSchedulers.io_main())
            .handError()
    }

    fun fetchMineMedals(
        userId: String,
        achievementId: String
    ): Flowable<BaseResult<MedalListEntity>> {
        return service.fetchMineMedals(userId, achievementId).compose(RxSchedulers.io_main())
            .handError()
    }

    fun getNewMedal(achievementId: String): Flowable<BaseResult<Any>> {
        val body = ParameterBuilder().add("achievementId", achievementId).toBody()
        return service.getNewMedal(body).compose(RxSchedulers.io_main()).handError()
    }

    fun suitUpMedal(achievementId: String, isWear: Boolean): Flowable<BaseResult<Any>> {
        val body = ParameterBuilder().add("achievementId", achievementId)
            .add("isWear", isWear)
            .toBody()
        return service.suitUpMedal(body).compose(RxSchedulers.io_main()).handError()
    }

    suspend fun getMedalList(userId: String) = flow {
        emit(service.getMedalList(userId))
    }.flowOn(Dispatchers.IO)

    /**
     * 获取积分信息
     * */
    fun mineScoreInfo(): Flowable<BaseResult<MineScoreInfoEntity>> {
        return service.mineScoreInfo()
            .compose(RxSchedulers.io_main()).handError()
    }

    fun isHaveNewMedal(): Flowable<BaseResult<IsHaveNewMedalEntity>> {
        return service.isHaveNewMedal().compose(RxSchedulers.io_main()).handError()
    }
}