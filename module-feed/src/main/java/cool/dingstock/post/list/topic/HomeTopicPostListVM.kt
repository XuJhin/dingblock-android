package cool.dingstock.post.list.topic

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
class HomeTopicPostListVM : BaseViewModel() {

    @Inject
    lateinit var homeApi: HomeApi

    init {
        PostApiHelper.apiPostComponent.inject(this)
    }

    var selTopicId = ""
    var selHomeTopicEntity: HomeTopicEntity? = null


    val topicListLiveData = MutableLiveData<ArrayList<HomeTopicEntity>>()
    val hotTradingLiveDate = MutableLiveData<ArrayList<HomeHotTradingEntity>?>()
    val loadPostListViewData = MutableLiveData<HomeTopicEntity>()
    val scroll2Topic = MutableLiveData<Int>()

    fun loadTopicList() {
        postStateLoading()
        viewModelScope.doRequest({ homeApi.homePageTopicList() }, {
            if (!it.err && it.res != null) {
                postStateSuccess()
                var selIndex = 0
                //这里判断是那个被选中
                if (selTopicId.isNotEmpty()) {
                    it.res?.list?.forEachIndexed { index, entity ->
                        entity.isSelect = selTopicId == entity.id
                        if (selTopicId == entity.id) {
                            selHomeTopicEntity = entity
                            selIndex = index
                        }
                    }
                } else {
                    run {
                        it.res?.list?.forEachIndexed() { index, entity ->
                            if (entity.isSelect) {
                                selTopicId = entity.id
                                selHomeTopicEntity = entity
                                selIndex = index
                            }
                        }
                    }
                }
                it.res?.list?.let {
                    topicListLiveData.postValue(it)
                }
                hotTradingLiveDate.postValue(it.res?.dealShoes)
                selHomeTopicEntity?.let {
                    loadPostListViewData.postValue(it)
                }

                scroll2Topic.postValue(selIndex)

            } else {
                postStateError(it.msg)
            }
        }, {
            postStateError(it.message)
        })
    }
}