package cool.dingstock.home.ui.basic

import android.location.Location
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicBean
import cool.dingstock.appbase.entity.bean.circle.HomeNearbyLocationEntity
import cool.dingstock.appbase.entity.bean.circle.ShowWhere
import cool.dingstock.appbase.entity.bean.home.HomePostData
import cool.dingstock.appbase.list.AbsListViewModel
import cool.dingstock.appbase.mvvm.SingleLiveEvent
import cool.dingstock.post.list.PostConstant
import io.reactivex.rxjava3.core.Flowable

class BasicPostViewModel : AbsListViewModel() {

    val basicApi = BasicRepository()

    //    @Inject
//    lateinit var circleApi: CircleApi
//
//    @Inject
//    lateinit var homeApi: HomeApi
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

    fun loadPost(isLoadMore: Boolean) {
        loadWithType(type, isLoadMore)
    }

    private fun loadWithType(type: String?, isLoadMore: Boolean) {
        val flowable: Flowable<BaseResult<HomePostData>>? =
            when (type) {

                PostConstant.PostType.Recommend -> {
                    requestRecommend()
                }
                PostConstant.PostType.Latest -> {
                    newPost()
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

    private fun requestRecommend(): Flowable<BaseResult<HomePostData>> {
        return basicApi.recommendPosts(loadType, nextKey)
    }

    //最新
    private fun newPost(): Flowable<BaseResult<HomePostData>> {
        return basicApi.newPost(nextKey)
    }


}