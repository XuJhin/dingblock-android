package cool.dingstock.appbase.net.api.bp

import cool.dingstock.appbase.entity.bean.base.BasePageEntity
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.home.bp.*
import cool.dingstock.appbase.net.retrofit.api.BaseApi
import cool.dingstock.appbase.net.retrofit.api.handError
import cool.dingstock.lib_base.thread.scheduler.RxSchedulers
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import javax.inject.Inject

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/14  10:59
 */
class BpApi @Inject constructor(retrofit: Retrofit) : BaseApi<BpApiService>(retrofit) {
    /**
     * 「实验室」
     * 实验室上方功能列表
     * */
    fun fetchBpList(): Flowable<BaseResult<MutableList<LibIndexEntity>>> {
        return service.fetchBpList().compose(RxSchedulers.netio_main()).handError()
    }

    /**
     * 「实验室」
     * 实验室下方热门发售分页
     * */
    fun fetchBpRecommend(platType: String?, nextKey: Long?): Flowable<BaseResult<PageBpGoods>> {
        return service.fetchBpRecommend(platType, nextKey).compose(RxSchedulers.netio_main())
            .handError()
    }

    /**
     * 实验室主页
     * */
    fun bpHomeIndex(): Flowable<BaseResult<BpLabIndexEntity>> {
        return service.bpHome().compose(RxSchedulers.netio_main()).handError()
    }

    fun bpHomePage(
        startAt: Long,
        type: String,
        nextKey: Long?
    ): Flowable<BaseResult<BasePageEntity<GoodsItemEntity>>> {
        return service.bpHomePage(startAt, type, nextKey).compose(RxSchedulers.netio_main())
            .handError()
    }

    /**
     * 「实验室」
     * 提醒列表分页
     * */
    fun fetchRemindList(nextKey: Long?): Flowable<BaseResult<PageRemindGoods>> {
        return service.fetchRemindList(nextKey).compose(RxSchedulers.netio_main()).handError()
    }

    fun removeRemindList(id: String?): Flowable<BaseResult<Int>> {
        val body = ParameterBuilder().add("id", id).toBody()
        return service.removeRemindList(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun addRemindList(id: String?): Flowable<BaseResult<Int>> {
        val body = ParameterBuilder().add("bpProductId", id).toBody()
        return service.addRemindList(body).compose(RxSchedulers.netio_main()).handError()
    }

    /**
     * 「秒杀神器」
     * 我的奖励
     * */
    fun fetchRewardRecordList(nextKey: Long?): Flowable<BaseResult<RewardRecordsData>> {
        return service.fetchRewardRecordList(nextKey).compose(RxSchedulers.netio_main()).handError()
    }

    fun fetchRewardMoney(): Flowable<BaseResult<MineRewardEntity>> {
        return service.fetchRewardMoney().compose(RxSchedulers.netio_main()).handError()
    }

    fun goodsStock(goodsId: String?, type: String?): Flowable<BaseResult<GoodsExtDetailsEntity>> {
        return service.goodsStock(goodsId, type).compose(RxSchedulers.netio_main()).handError()
    }

    fun checkTbAuth(): Flowable<BaseResult<Boolean>> {
        return service.checkTbAuth().compose(RxSchedulers.netio_main()).handError()
    }

    fun douYinShareCallback(id: String): Flowable<BaseResult<Any>> {
        val body = ParameterBuilder().add("bpProductId", id).toBody()
        return service.douYinShareCallback(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun withDrawReward(name: String?, alipay: String, money: String?): Flowable<BaseResult<Any>> {
        val body = ParameterBuilder()
            .add("realname", name)
            .add("alipay", alipay)
            .add("amount", money)
            .toBody()
        return service.withDrawReward(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun fetchRewardDetailList(nextKey: Long?): Flowable<BaseResult<WithdrawAllRecordList>> {
        return service.fetchRewardDetailList(nextKey).compose(RxSchedulers.netio_main()).handError()
    }

    fun fetchDouyinShareId(
        bpGoodId: String,
        douyinAuthorCode: String,
        date: Long?
    ): Flowable<BaseResult<String>> {
        return service.fetchDouyinShareId(bpGoodId, douyinAuthorCode, date)
            .compose(RxSchedulers.netio_main()).handError()
    }

    suspend fun addGoodsAlert(
        goodDetailEntity: GoodDetailEntity,
        sku: SKUEntity?,
        startTime: Long
    ): BaseResult<Int> {
        val body = ParameterBuilder()
            .add("id", goodDetailEntity.id)
            .add("startAt", startTime)
            .add("imgUrl", goodDetailEntity.imageUrl)
            .add("linkUrl", goodDetailEntity.linkUrl)
            .add("shopUrl", goodDetailEntity.shopUrl)
            .add("name", goodDetailEntity.title)
            .add("type", goodDetailEntity.type)
            .add("price", sku?.price)
            .toBody()
        return service.addGoodsAlert(body)
    }

    fun fetchCluePageData(nextKey: String): Flowable<BaseResult<ClueDataBean>> {
        return service.fetchCluePageData(nextKey).compose(RxSchedulers.netio_main()).handError()
    }

    fun fetchMoreClue(
        channelId: String,
        nextKey: Long,
        type: String
    ): Flowable<BaseResult<ClueDataBean>> {
        return service.fetchMoreClue(channelId, nextKey, type).compose(RxSchedulers.netio_main())
            .handError()
    }

    fun trackDo(productId: String, isOpenAward: Boolean): Flowable<BaseResult<String>> {
        val body = ParameterBuilder()
            .add("productId", productId)
            .add("isOpenAward", isOpenAward)
            .toBody()
        return service.trackDo(body).compose(RxSchedulers.netio_main()).handError()
    }

    suspend fun addRemindList1(id: String?): BaseResult<Int> {
        val body = ParameterBuilder().add("bpProductId", id).toBody()
        return service.addRemindList1(body)
    }

    suspend fun getSignGoods(nextKey: Long, type: String): BaseResult<SignGoodsPageEntity> {
        return service.getSignGoods(type, nextKey)
    }

    suspend fun johnSignGoods(raffleId: String, userId: String): BaseResult<Any> {
        val body = ParameterBuilder()
            .add("raffleId", raffleId)
            .add("userId", userId)
            .toBody()
        return service.johnSignGoods(body)
    }

    suspend fun getTimePlatList(): BaseResult<ArrayList<TimePlatEntity>> {
        return service.getTimePlatList()
    }

    suspend fun getBuyStrategies(type: String, nextStr: String?): BaseResult<BuyStrategyList> {
        return service.getBuyStrategies(type, nextStr)
    }

    suspend fun getBuyStrategyDetail(monitorId: String?, moutaiChannelId: String?): BaseResult<BuyStrategyEntity> {
        return service.getBuyStrategyDetail(monitorId, moutaiChannelId)
    }

    fun getMoutaiHome(isSubscribe: Boolean = false) = flow {
        emit(service.getMoutaiHome(isSubscribe))
    }.flowOn(Dispatchers.IO)

    fun getMoutaiPage(nextKey: Long?, groupId: String, isSubscribe: Boolean) = flow {
        emit(service.getMoutaiPage(nextKey, groupId, isSubscribe))
    }.flowOn(Dispatchers.IO)
}