package cool.dingstock.post.ui.post.nearby

import android.location.Location
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicBean
import cool.dingstock.appbase.entity.bean.circle.HomeNearbyLocationEntity
import cool.dingstock.appbase.mvvm.SingleLiveEvent
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.circle.CircleApi
import cool.dingstock.appbase.net.api.home.HomeApi
import cool.dingstock.post.dagger.PostApiHelper
import javax.inject.Inject


/**
 * 类名：NearbyDetailsVM
 * 包名：cool.dingstock.post.ui.post.nearby
 * 创建时间：2021/12/17 3:56 下午
 * 创建人： WhenYoung
 * 描述：
 **/
class NearbyDetailsVM : BaseViewModel() {

    @Inject
    lateinit var circleApi: CircleApi

    @Inject
    lateinit var homeApi: HomeApi

    var locationId: String = ""

    var nextKey: Long? = null

    var location: Location? = null

    val dataList = SingleLiveEvent<MutableList<CircleDynamicBean>>()
    val loadMoreList = SingleLiveEvent<MutableList<CircleDynamicBean>>()
    val locationLiveData = SingleLiveEvent<HomeNearbyLocationEntity>()

    init {
        PostApiHelper.apiPostComponent.inject(this)
    }

    fun loadFirstPost() {
        nextKey = 0
        if (location == null) {
            dataList.postValue(arrayListOf())
            return
        }
        homeApi.requestNearbyPosts(nextKey, location!!, null, locationId)
            .subscribe({
                if (!it.err && it.res != null) {
                    postStateSuccess()
                    if (it.res?.recommendLocation?.size ?: 0 > 0) {
                        it.res?.recommendLocation?.get(0)?.let {
                            locationLiveData.postValue(it)
                        }
                    }
                    nextKey = it.res!!.nextKey
                    dataList.postValue(it.res!!.list)
                } else {
                    postAlertError(it.msg)
                    dataList.postValue(arrayListOf())
                }
            }, {
                postAlertError(it.message?:"")
                dataList.postValue(arrayListOf())
            })
    }

    fun loreMorePost() {
        homeApi.requestNearbyPosts(nextKey,location!!, null, locationId)
            .subscribe({
                if (!it.err && it.res != null) {
                    nextKey = it.res!!.nextKey
                    loadMoreList.postValue(it.res!!.list)
                } else {
                    loadMoreList.postValue(arrayListOf())
                }
            }, {
                loadMoreList.postValue(arrayListOf())
            })
    }

}