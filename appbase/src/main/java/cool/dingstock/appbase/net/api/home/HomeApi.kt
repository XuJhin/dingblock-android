package cool.dingstock.appbase.net.api.home

import android.location.Location
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicDetailCommentsBean
import cool.dingstock.appbase.entity.bean.circle.HomeNearbyEntity
import cool.dingstock.appbase.entity.bean.circle.HomeTopicResultEntity
import cool.dingstock.appbase.entity.bean.home.*
import cool.dingstock.appbase.entity.bean.home.fashion.FashionListEntity
import cool.dingstock.appbase.entity.bean.mine.AccountInfoBriefEntity
import cool.dingstock.appbase.entity.bean.mine.UnReadMessage
import cool.dingstock.appbase.entity.bean.score.MineScoreInfoEntity
import cool.dingstock.appbase.net.retrofit.api.BaseApi
import cool.dingstock.appbase.net.retrofit.api.handError
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.lib_base.json.JSONHelper
import cool.dingstock.lib_base.thread.scheduler.RxSchedulers
import cool.dingstock.lib_base.util.StringUtils
import io.reactivex.rxjava3.core.Flowable
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit

/**
 * @author WhenYoung
 *  CreateAt Time 2020/11/5  15:49
 */
class HomeApi(retrofit: Retrofit) : BaseApi<HomeApiServer>(retrofit) {

    fun commentProduct(
        productId: String,
        mentionedId: String?,
        commentType: String?,
        content: String?,
        dynamicImgUrl: String?,
        staticImgUrl: String?,
        dcEmojiId: String?
    ): Flowable<BaseResult<CircleDynamicDetailCommentsBean>> {
        val parameterBuilder = ParameterBuilder()
        productId.let {
            parameterBuilder.add("productId", it)
        }
        mentionedId?.let {
            parameterBuilder.add("mentionedId", it)
        }
        content?.let {
            parameterBuilder.add("content", content)
        }
        dynamicImgUrl?.let {
            parameterBuilder.add("dynamicImgUrl", it)
        }
        parameterBuilder.add("commentType", commentType)
        if (StringUtils.isEmpty(staticImgUrl)) {//如果是空 就把 dynamicImgUrl当做staticImgUrl
            dynamicImgUrl?.let {
                parameterBuilder.add("staticImgUrl", it)
            }
        } else {
            parameterBuilder.add("staticImgUrl", staticImgUrl)
        }
        dcEmojiId?.let {
            parameterBuilder.add("dcEmojiId", dcEmojiId)
        }
        return service.commentProduct(parameterBuilder.toBody()).compose(RxSchedulers.netio_main())
            .handError()
    }

    fun fetchRecommendAccount(times: Int = 0): Flowable<BaseResult<MutableList<AccountInfoBriefEntity>>> {
        return service.recommendAccount(times = times).compose(RxSchedulers.netio_main())
            .handError()
    }

    fun followAccounts(idList: MutableList<String>): Flowable<BaseResult<Boolean>> {
        val json = JSONHelper.toJson(idList)
        val requestBody: RequestBody = json.toRequestBody("application/json".toMediaType())
        return service.followAccounts(requestBody).compose(RxSchedulers.io_main()).handError()
    }

    fun fetchAccountCount(): Flowable<BaseResult<FollowCountEntity>> {
        val userId = LoginUtils.getCurrentUser()?.id
        return service.fetchFansCount(userId!!).compose(RxSchedulers.io_main()).handError()
    }

    fun fetchBpPermission(): Flowable<Map<String, Any>> {
        return service.fetchBpPermission().compose(RxSchedulers.io_main()).handError()
    }

    /**
     * 推荐
     * */
    fun recommendPosts(type: String?, nextKey: Long?): Flowable<BaseResult<HomePostData>> {
        val key = if (nextKey == 0L) null else nextKey
        return service
            .recommendPosts(type, key)
            .compose(RxSchedulers.io_main()).handError()
    }

    /**
     * 最新
     * */
    fun newPost(nextKey: Long?): Flowable<BaseResult<HomePostData>> {
        val key = if (nextKey == 0L) null else nextKey
        return service.newPost(key).compose(RxSchedulers.io_main()).handError()
    }

    /**
     * 关注
     * */
    fun followPost(nextKey: Long?): Flowable<BaseResult<HomePostData>> {
        val key = if (nextKey == 0L) null else nextKey
        return service.followPost(
            key, LoginUtils.getCurrentUser()?.id
                ?: ""
        ).compose(RxSchedulers.io_main()).handError()
    }

    /**
     * 话题
     * */
    fun topicPost(nextKey: Long?, topicId: String): Flowable<BaseResult<HomePostData>> {
        val key = if (nextKey == 0L) null else nextKey
        return service.topicPost(
            topicId, key
        ).compose(RxSchedulers.io_main()).handError()
    }


    fun fashionPost(nextKey: Long?): Flowable<BaseResult<HomePostData>> {
        val key = if (nextKey == 0L) null else nextKey
        return service.followPost(
            key, LoginUtils.getCurrentUser()?.id
                ?: ""
        ).compose(RxSchedulers.io_main()).handError()
    }


    /**
     * 获取主页配置
     * */
    fun homeConfig(): Flowable<BaseResult<HomeConfigEntity?>> {
        return service.homeConfig().compose(RxSchedulers.io_main()).handError()
    }

    /**
     * 获取未读消息数
     * */
    fun fetchUnreadMessage(): Flowable<BaseResult<UnReadMessage>> {
        return service.fetchUnreadMessage()
            .compose(RxSchedulers.io_main()).handError()
    }

    /**
     * 获取未读消息数
     * */
    fun home(): Flowable<BaseResult<HomeData>> {
        return service.home().compose(RxSchedulers.io_main()).handError()
            .filter {
                if (!it.err && it.res != null) {
                    HomeHelper.getInstance().cacheHomeData = it.res
                }
                return@filter true
            }
    }


    /**
     * 获取积分信息
     * */
    fun mineScoreInfo(): Flowable<BaseResult<MineScoreInfoEntity>> {
        return service.mineScoreInfo()
            .compose(RxSchedulers.io_main()).handError()
    }

    /**
     * 推荐潮牌列表
     * */
    fun fetchBrandTop(): Flowable<BaseResult<FashionListEntity>> {
        return service.fetchBrandTop("FC").compose(RxSchedulers.netio_main()).handError()
    }

    /**
     * 全部潮牌列表
     * */
    fun fetchAllBrand(nextStr: String?): Flowable<BaseResult<FashionListEntity>> {
        return service.fetchBrandAll("F", nextStr).compose(RxSchedulers.netio_main()).handError()
    }

    /**
     * 全部潮牌列表
     * */
    fun fetchAllBrandNew(): Flowable<BaseResult<FashionListEntity>> {
        return service.fetchBrandAllNew().compose(RxSchedulers.netio_main()).handError()
    }

    /**
     * 潮牌清单
     * */
    fun fashionPosts(nextKey: Long?, talkId: String?): Flowable<BaseResult<HomePostData>> {
        return service.fashionPosts(true, nextKey, talkId).compose(RxSchedulers.netio_main())
            .handError()
    }


    /**
     * 请求球鞋日历
     *
     * @param date
     * @param filterList
     * @param callback
     */
    fun loadSneakerCalendar(
        date: Long,
        brands: ArrayList<String>?,
        types: ArrayList<String>?
    ): Flowable<BaseResult<CalenderDataBean>> {
        var brandsStr: String? = null
        brands?.let {
            if (brands.isNotEmpty()) {
                val brandSb = StringBuilder()
                for (s in it) {
                    brandSb.append("$s,")
                }
                brandsStr = brandSb.toString().removeSuffix(",")
            }
        }
        var typesStr: String? = null
        types?.let {
            if (it.isNotEmpty()) {
                val typeSb = StringBuilder()
                for (s in it) {
                    typeSb.append("$s,")
                }
                typesStr = typeSb.toString().removeSuffix(",")
            }
        }
        return service.loadSneakerCalendar(date, brandsStr, typesStr)
            .compose(RxSchedulers.netio_main())
            .handError()
    }


    fun getProductDetail(productId: String): Flowable<BaseResult<HomeProductDetailData>> {
        return service.getProductDetail(productId, null, null).compose(RxSchedulers.netio_main())
            .handError()
    }

    fun refreshHomeFun(): Flowable<BaseResult<ArrayList<HomeFunctionBean>>> {
        return service.refreshHomeFun().compose(RxSchedulers.netio_main()).handError()
    }

    fun heavyList(): Flowable<BaseResult<ArrayList<CalenderProductEntity>>> {
        return service.heavyList().compose(RxSchedulers.netio_main()).handError()
    }

    fun requestNearbyPosts(
        nextKey: Long?,
        location: Location,
        type: String?,
        recommendLocationId: String? = null
    ): Flowable<BaseResult<HomeNearbyEntity>> {
        val key = if (nextKey == 0L) null else nextKey
        return service.requestNearbyPosts(
            key,
            longitude = location.longitude,
            latitude = location.latitude,
            type,
            recommendLocationId
        ).compose(RxSchedulers.io_main()).handError()
    }

    suspend fun homePageTopicList(): BaseResult<HomeTopicResultEntity> {
        return service.homePageTopicList()
    }

    suspend fun recommendKolUser(): BaseResult<ArrayList<RecommendKolUserEntity>> {
        return service.recommendKolUser()
    }

    fun trackPostFeeds(type: String?): Flowable<BaseResult<Any>> {
        val body = ParameterBuilder()
            .add("action", "community_post_feed")
            .add("type", type)
            .toBody()
        return service.trackPost(body)
            .compose(RxSchedulers.netio_main()).handError()
    }

    suspend fun fetchHotRankList(): BaseResult<ArrayList<HotRankItemEntity>> {
        return service.fetchHotRankList()
    }
}