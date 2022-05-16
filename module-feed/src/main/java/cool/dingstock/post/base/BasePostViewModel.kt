package cool.dingstock.post.base

import cool.dingstock.appbase.list.AbsListViewModel
import cool.dingstock.appbase.mvvm.SingleLiveEvent
import cool.dingstock.appbase.entity.bean.circle.PartialUpdateEntity
import cool.dingstock.appbase.entity.bean.circle.VoteOptionEntity

abstract class BasePostViewModel : AbsListViewModel() {

    var postNextKey: Long? = 0

    var pageNum:Int = 0

    /**
     * 局部更新数据
     */
    open val updateLiveData = SingleLiveEvent<PartialUpdateEntity<List<VoteOptionEntity>>>()



    abstract fun loadMorePost()
}