package cool.dingstock.mine.ui.collection

import cool.dingstock.appbase.entity.bean.circle.CircleDynamicBean
import cool.dingstock.appbase.list.AbsListViewModel
import cool.dingstock.appbase.mvvm.SingleLiveEvent
import cool.dingstock.appbase.net.api.account.AccountApi
import cool.dingstock.appbase.net.api.circle.CircleApi
import cool.dingstock.appbase.net.api.mine.MineApi
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.mine.dagger.MineApiHelper
import javax.inject.Inject

class MineCollectionViewModel : AbsListViewModel() {
    @Inject
    lateinit var circleApi: CircleApi

    @Inject
    lateinit var mineApi: MineApi

    @Inject
    lateinit var accountApi: AccountApi

    init {
        MineApiHelper.apiMineComponent.inject(this)
    }

    var userId: String? = ""
        get() {
            userId = LoginUtils.getCurrentUser()?.id
            return field
        }

    val postsRefreshLiveData = SingleLiveEvent<MutableList<CircleDynamicBean>>()
    val postsLoadMoreLiveData = SingleLiveEvent<MutableList<CircleDynamicBean>>()
    var nextKeyLiveData = SingleLiveEvent<Long>()
    var isRefresh = false
    var isDeal = false

    fun refreshData() {
        isRefresh = true
        if (userId.isNullOrEmpty()) {
            return
        }
        fetchAccountPosts(null)
    }

    private fun fetchAccountPosts(nextKey: Long?) {
        if (nextKey == null) {
            isRefresh = true
        }
        mineApi.fetchMineCollections(isDeal, nextKey)
                .subscribe({
                    when {
                        it.err -> {
                        }
                        else -> {
                            it.res?.let { entity ->
                                nextKeyLiveData.value = entity.nextKey
                                if (isRefresh) {
                                    postsRefreshLiveData.postValue(entity.list ?: arrayListOf())
                                } else {
                                    postsLoadMoreLiveData.postValue(entity.list ?: arrayListOf())
                                }
                            }
                        }
                    }
                }, { error ->
                })
    }

    fun loadMorePosts() {
        isRefresh = false
        val nextKey = nextKeyLiveData.value
        fetchAccountPosts(nextKey)
    }
}