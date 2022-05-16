package cool.dingstock.appbase.net.api.find

import cool.dingstock.appbase.entity.bean.base.BasePageEntity
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.circle.*
import cool.dingstock.appbase.entity.bean.home.HotRankItemEntity
import cool.dingstock.appbase.entity.bean.mine.IMNotificationSettingEntity
import cool.dingstock.appbase.entity.bean.mine.MineFollowEntity
import cool.dingstock.appbase.entity.bean.topic.TopListEntity
import cool.dingstock.appbase.entity.bean.topic.TopicDetailEntity
import cool.dingstock.appbase.entity.bean.topic.TopicDiscoverEntity
import cool.dingstock.appbase.net.retrofit.api.BaseApi
import cool.dingstock.appbase.net.retrofit.api.handError
import cool.dingstock.lib_base.thread.scheduler.RxSchedulers
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/14  10:59
 */
class FindApi @Inject constructor(retrofit: Retrofit) : BaseApi<FindApiService>(retrofit) {
    suspend fun requestNotificationSettingList(userId: String? = null): BaseResult<IMNotificationSettingEntity> {
        return service.requestNotificationSettingList(userId)
    }

    suspend fun upDateNotificationSettingList(type: String, isOpen: Boolean): BaseResult<Any> {
        val body = ParameterBuilder()
            .add(type, isOpen)
            .toBody()
        return service.upDateNotificationSettingList(body)
    }

    suspend fun requestFollowEachOtherList(nextKey: Long?): BaseResult<MineFollowEntity> {
        return service.requestFollowEachOtherList(nextKey)
    }

    fun requestFollowUser(userId: String, nextKey: Long?): Flowable<BaseResult<MineFollowEntity>> {
        return service.requestFollowUser(userId, nextKey).compose(RxSchedulers.netio_main())
            .handError()
    }

    fun requestFansList(userId: String, nextKey: Long?): Flowable<BaseResult<MineFollowEntity>> {
        return service.requestFansList(userId, nextKey).compose(RxSchedulers.netio_main())
            .handError()
    }

    fun requestFavorList(userId: String, nextKey: Long?): Flowable<BaseResult<MineFollowEntity>> {
        return service.requestFavorList(userId, nextKey).compose(RxSchedulers.netio_main())
            .handError()
    }

    fun fetchFindIndex(nextStr: String?): Flowable<BaseResult<TopicDiscoverEntity>> {
        return service.fetchFindIndex(nextStr).compose(RxSchedulers.netio_main()).handError()
    }

    fun talkFollowed(nextKey: Long?): Flowable<BaseResult<TopListEntity>> {
        return service.talkFollowed(nextKey).compose(RxSchedulers.netio_main()).handError()
    }

    fun fetchTopicDetail(
        id: String,
        nextKey: Long? = null
    ): Flowable<BaseResult<TopicDetailEntity>> {
        return service.fetchTopicDetail(id, nextKey).compose(RxSchedulers.netio_main()).handError()
    }

    fun fetchTopicPost(
        id: String,
        nextKey: Long?
    ): Flowable<BaseResult<BasePageEntity<CircleDynamicBean>>> {
        return service.fetchTopicPost(id, nextKey).compose(RxSchedulers.netio_main()).handError()
    }

    fun fetchTopicTradingPost(
        pageNum: Int?,
        filter: TradingTopicFilterEntity?,
        productId: String?,
        keyword: String?,
    ): Flowable<BaseResult<BasePageEntity<CircleDynamicBean>>> {
        val filterEntity = filter ?: TradingTopicFilterEntity()
        productId?.let { filterEntity.putProductId(it) }
        val body = ParameterBuilder()
            .add("pageNum", pageNum)
            .add("keyword", keyword)
            .add("filter", filterEntity?.getFilterFields())
            .toBody()
        return service.fetchTopicTradingPost(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun fetchAllTopic(
        userId: String,
        nextKey: Long?,
        showDealTalk: Boolean = true
    ): Flowable<BaseResult<TopListEntity>> {
        return service.fetchAllTopic(userId, nextKey, showDealTalk)
            .compose(RxSchedulers.netio_main()).handError()
    }

    fun followTopic(talkId: String, follow: Boolean): Flowable<BaseResult<String>> {
        val body = ParameterBuilder()
            .add("follow", follow)
            .add("talkId", talkId)
            .toBody()
        return service.followTopic(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun fetchTopic(nextStr: String?): Flowable<BaseResult<TopListEntity>> {
        return service.fetchTopic(nextStr).compose(RxSchedulers.netio_main()).handError()
    }

    fun locationListRecommend(longitude: Double, latitude: Double, pageNo: Int)
            : Flowable<BaseResult<MutableList<LocationEntity>>> {
        return service.locationListRecommend(longitude, latitude, pageNo)
            .compose(RxSchedulers.netio_main())
            .handError()
    }

    fun searchLocation(longitude: Double, latitude: Double, pageNo: Int, keyword: String)
            : Flowable<BaseResult<MutableList<LocationEntity>>> {
        return service.searchLocation(longitude, latitude, pageNo, keyword)
            .compose(RxSchedulers.netio_main())
            .handError()
    }

    fun getCity(longitude: Double, latitude: Double): Flow<BaseResult<CityBean>> = flow {
        emit(service.getCity(longitude, latitude))
    }.flowOn(Dispatchers.IO)

    fun getCities(): Flow<BaseResult<List<GroupCityBean>>> = flow {
        emit(service.getCities())
    }.flowOn(Dispatchers.IO)

    fun getPartyDetails(id: String) = flow {
        emit(service.getPartyDetails(id))
    }.flowOn(Dispatchers.IO)

    fun getPartyPost(id: String, nextKey: Long?) = flow {
        emit(service.getPartyPost(id, nextKey))
    }.flowOn(Dispatchers.IO)

    fun searchGoods(pageNum: Int, keyword: String?, seriesIds: List<String>?) = flow {
        val filter = mutableListOf<HashMap<String, Any>>()
        if (!seriesIds.isNullOrEmpty()) {
            filter.add(hashMapOf("id" to "seriesId", "list" to seriesIds))
        }
        val body = ParameterBuilder()
            .add("pageNum", pageNum)
            .add("keyword", keyword)
            .add("pageSize", 20)
            .apply {
                if (filter.isNotEmpty()) {
                    add("filter", filter)
                }
            }
            .toBody()
        emit(service.searchGoods(body))
    }.flowOn(Dispatchers.IO)

    fun getHotTopicDetail(id: String) = flow {
        emit(service.getHotTopicDetail(id))
    }.flowOn(Dispatchers.IO)

    fun getMoreHotTopicList(id: String, nextKey: Long?) = flow {
        emit(service.getMoreHotTopicList(id, nextKey))
    }.flowOn(Dispatchers.IO)

    fun fetchHotRankList() = flow {
        emit(service.fetchHotRankList())
    }.flowOn(Dispatchers.IO)
}