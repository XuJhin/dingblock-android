package cool.dingstock.post.activity

import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.mvvm.status.ViewStatus
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicBean
import cool.dingstock.appbase.entity.bean.topic.TopicDetailEntity
import cool.dingstock.appbase.net.api.find.FindApi
import cool.dingstock.post.base.BasePostViewModel
import cool.dingstock.post.dagger.PostApiHelper
import javax.inject.Inject

class MoreVideoViewModel : BasePostViewModel() {

    @Inject
    lateinit var findApi: FindApi

    init {
        PostApiHelper.apiPostComponent.inject(this)
    }

    val refreshData = MutableLiveData<TopicDetailEntity>()
    val liveDataLoadMore = MutableLiveData<MutableList<CircleDynamicBean>>()
    var topicId: String? = ""
    var isRefresh: Boolean = false
    fun updateTopicId(id: String?) {
        this.topicId = id
    }

    fun fetchTopicDetail(topicId: String?, nextKey: Long? = 0) {
        if (topicId == null || topicId.isNullOrEmpty()) {
            return
        }
        this.topicId = topicId
        findApi.fetchTopicDetail(topicId,nextKey).subscribe({res->
            val data = res.res
            if(!res.err&&res.res!=null){
                postNextKey = data?.nextKey
                if (isRefresh) {
                    statusViewLiveData.postValue(ViewStatus.Success(""))
                    refreshData.postValue(data)
                } else {
                    liveDataLoadMore.postValue(data?.posts)
                }
            }else{
                statusViewLiveData.postValue(ViewStatus.Error(res.msg))
            }
        },{err->
            statusViewLiveData.postValue(ViewStatus.Error(err.message?:"加载失败"))
        })
    }

    fun refresh() {
        if (topicId == null || topicId.isNullOrEmpty()) {
            return
        }
        postNextKey = 0
        isRefresh = true
        fetchTopicDetail(topicId, postNextKey)
    }

    override fun loadMorePost() {
        isRefresh = false
        if (null == postNextKey || postNextKey == 0L) {
            return
        }
        fetchTopicDetail(topicId = topicId, nextKey = postNextKey)
    }
}