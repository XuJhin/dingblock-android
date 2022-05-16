package cool.dingstock.post.list

import android.location.Location
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicBean
import cool.dingstock.appbase.entity.bean.circle.HomeNearbyLocationEntity
import cool.dingstock.appbase.entity.bean.circle.ShowWhere
import cool.dingstock.appbase.entity.bean.home.HomePostData
import cool.dingstock.appbase.list.AbsListViewModel
import cool.dingstock.appbase.mvvm.SingleLiveEvent
import cool.dingstock.appbase.net.api.circle.CircleApi
import cool.dingstock.appbase.net.api.home.HomeApi
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.post.dagger.PostApiHelper
import io.reactivex.rxjava3.core.Flowable
import javax.inject.Inject

class PostViewModel : AbsListViewModel() {
    @Inject
    lateinit var circleApi: CircleApi

    @Inject
    lateinit var homeApi: HomeApi
    val dataList = SingleLiveEvent<MutableList<CircleDynamicBean>>()
    val nearbyHeaderLiveData = SingleLiveEvent<ArrayList<HomeNearbyLocationEntity>?>()
    val loadmoreList = SingleLiveEvent<MutableList<CircleDynamicBean>?>()
    var nextKey: Long? = null
    var type: String? = ""
    var loadType: String? = ""
    var topicId: String = ""
    private var isRefreshing = true
    private var location: Location? = null


    fun loadData() {
        isRefreshing = true
        nextKey = 0
        loadPost(false)
    }

    init {
        PostApiHelper.apiPostComponent.inject(this)
    }

    fun loadPost(isLoadMore: Boolean) {
        loadWithType(type, isLoadMore)
    }

    private fun loadWithType(type: String?, isLoadMore: Boolean) {
        val flowable: Flowable<BaseResult<HomePostData>>? =
            when (type) {
                PostConstant.PostType.Fashion -> {
                    requestFashion()
                }
                PostConstant.PostType.Recommend -> {
                    requestRecommend()
                }
                PostConstant.PostType.Latest -> {
                    newPost()
                }
                PostConstant.PostType.Followed -> {
                    requestFollow()
                }
                PostConstant.PostType.Talk -> {
                    if (topicId.isEmpty()) {
                        return
                    }
                    requestTopic()
                }
                PostConstant.PostType.Deal -> {
                    if (topicId.isEmpty()) {
                        return
                    }
                    requestTopic()
                }
                PostConstant.PostType.Nearby -> {
                    location?.let { requestNearby(it) }
                }
                else -> return
            }
        val subscribe = flowable?.subscribe({
            if (!it.err) {
                if (it.res?.posts.isNullOrEmpty()) {
                    loadmoreList.postValue(null)
                    postPageStateEmpty("数据为空", isRefreshing)
                } else {
                    postStateSuccess()
                    val data = it.res
                    nextKey = data?.nextKey ?: 0
                    loadType = data?.type
                    if (type == PostConstant.PostType.Recommend) {
                        data?.posts?.forEach {
                            it.showWhere = ShowWhere.RECOMMEND
                        }
                    }
                    if (!isLoadMore) {
                        dataList.postValue(data?.posts)
                        isRefreshing = false
                    } else {
                        loadmoreList.postValue(data?.posts)
                    }
                }
            } else {
                if (isLoadMore) {
                    loadmoreList.postValue(null)
                } else {
                    postPageStateError(it.msg, isRefreshing)
                }
            }
        }, {
            if (isLoadMore) {
                loadmoreList.postValue(null)
            } else {
                postPageStateError(it.localizedMessage, isRefreshing)
            }
        })
    }

    private fun requestNearby(location: Location): Flowable<BaseResult<HomePostData>>? {
        return homeApi.requestNearbyPosts(nextKey, location = location, loadType).flatMap {
            if (nextKey == null || nextKey == 0L) {
                nearbyHeaderLiveData.postValue(it.res?.recommendLocation)
            }
            return@flatMap Flowable.just(
                BaseResult(
                    it.err,
                    HomePostData(
                        it.res?.list ?: arrayListOf(),
                        it.res?.nextKey ?: 0L,
                        it.res?.type
                    ),
                    it.code,
                    it.msg
                )
            )
        }
    }

    private fun requestFashion(): Flowable<BaseResult<HomePostData>>? {
        return homeApi.fashionPosts(nextKey, null)
    }

    private fun requestRecommend(): Flowable<BaseResult<HomePostData>> {
        return homeApi.recommendPosts(loadType, nextKey)
    }

    //最新
    private fun newPost(): Flowable<BaseResult<HomePostData>> {
        return homeApi.newPost(nextKey)
    }

    private fun requestFollow(): Flowable<BaseResult<HomePostData>> {
        return homeApi.followPost(nextKey)
    }

    private fun requestTopic(): Flowable<BaseResult<HomePostData>> {
        return homeApi.topicPost(nextKey, topicId)
    }

    fun uploadExposure(postId: String) {
        circleApi.uploadExposure(postId)
            .subscribe({
                Logger.d("uploadExposure", "" + it.msg)
            }, {
                Logger.d("uploadExposure", "" + it.message)
            })
    }

    fun updateLocation(location: Location?) {
        this.location = location
    }
}