package cool.dingstock.mine.ui.message

import android.util.Log
import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.entity.bean.home.NoticeItemEntity
import cool.dingstock.appbase.entity.event.circle.EventFollowerChange
import cool.dingstock.appbase.entity.event.relation.EventFollowChange
import cool.dingstock.appbase.list.AbsListViewModel
import cool.dingstock.appbase.net.api.account.AccountApi
import cool.dingstock.appbase.net.api.mine.MineHelper
import cool.dingstock.appbase.net.parse.ParseCallback
import cool.dingstock.mine.dagger.MineApiHelper
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

/**
 * 通知中心的ViewModel
 */
class NoticeViewModel : AbsListViewModel() {

    @Inject
    lateinit var accountApi: AccountApi

    init {
        MineApiHelper.apiMineComponent.inject(this)
    }

    private var isRefresh: Boolean = false
    val listLiveData = MutableLiveData<MutableList<NoticeItemEntity>>()
    val loadLiveData = MutableLiveData<MutableList<NoticeItemEntity>>()
    val switchFollowResult = MutableLiveData<Int>()
    val followError = MutableLiveData<String>()
    private var nextKey: Long? = null
    private fun requestNotice(firstLoad: Boolean) {
        if (!isRefresh && nextKey == null) {
            Log.e("tag", "数据错误")
            return
        }
        if (firstLoad) {
            postPageStateLoading("加载中……", isRefresh)
        }
        accountApi.requestNotice(nextKey,"")
                .subscribe({ res ->
                    if (!res.err && res.res != null) {
                        val data = res.res
                        postPageStateLoading(isRefresh = isRefresh)
                        if (isRefresh) {
                            listLiveData.postValue(data?.data)
                        } else {
                            loadLiveData.postValue(data?.data)
                        }
                        isRefresh = false
                        nextKey = data?.nextKey
                        if (data?.data.isNullOrEmpty()) {
                            postPageStateEmpty(isRefresh = isRefresh)
                        }
                    } else {
                        postPageStateError(res.msg, isRefresh = isRefresh)
                    }
                }, { err ->
                    postPageStateError(err.message ?: "网络错误", isRefresh = isRefresh)
                })
    }

    fun loadMore() {
        requestNotice(false)
    }

    fun refresh(firstLoad: Boolean = false) {
        isRefresh = true
        nextKey = null
        requestNotice(firstLoad)
    }

    fun switchFollowState(followState: Boolean, userId: String, index: Int) {
        MineHelper.getInstance().switchFollowState(!followState, userId, object : ParseCallback<Int> {
            override fun onSucceed(data: Int?) {
                switchFollowResult.postValue(index)
                EventBus.getDefault().post(EventFollowChange())
                EventBus.getDefault().post(EventFollowerChange(userId, !followState,data?:0))
            }

            override fun onFailed(errorCode: String?, errorMsg: String?) {
                followError.postValue(errorMsg?:"")
            }
        })
    }
}