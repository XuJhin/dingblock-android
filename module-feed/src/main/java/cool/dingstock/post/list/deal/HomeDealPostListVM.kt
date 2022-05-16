package cool.dingstock.post.list.deal

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cool.dingstock.appbase.entity.bean.circle.HomeHotTradingEntity
import cool.dingstock.appbase.entity.bean.circle.HomeTopicEntity
import cool.dingstock.appbase.ext.doRequest
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.home.HomeApi
import cool.dingstock.post.dagger.PostApiHelper
import javax.inject.Inject


/**
 * 类名：HomeTopicPostListVM
 * 包名：cool.dingstock.post.list.topic
 * 创建时间：2021/9/28 12:23 下午
 * 创建人： WhenYoung
 * 描述：
 **/
class HomeDealPostListVM : BaseViewModel() {

    @Inject
    lateinit var homeApi: HomeApi

    init {
        PostApiHelper.apiPostComponent.inject(this)
    }

    var selHomeTopicEntity: HomeTopicEntity? = null

    val hotTradingLiveDate = MutableLiveData<ArrayList<HomeHotTradingEntity>?>()
    val loadPostListViewData = MutableLiveData<Boolean>()

    fun loadTopicList() {
        postStateLoading()
        viewModelScope.doRequest({ homeApi.homePageTopicList() }, {
            if (!it.err && it.res != null) {
                postStateSuccess()
                hotTradingLiveDate.postValue(it.res?.dealShoes)
                loadPostListViewData.postValue(true)
            } else {
                postStateError(it.msg)
            }
        }, {
            postStateError(it.message)
        })
    }
}