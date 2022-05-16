package cool.dingstock.mine.ui.follow

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.entity.bean.RequestState
import cool.dingstock.appbase.entity.bean.mine.AccountInfoBriefEntity
import cool.dingstock.appbase.entity.event.circle.EventFollowerChange
import cool.dingstock.appbase.entity.event.relation.EventFollowChange
import cool.dingstock.appbase.list.AbsListViewModel
import cool.dingstock.appbase.net.api.mine.MineApi
import cool.dingstock.appbase.net.api.mine.MineHelper
import cool.dingstock.appbase.net.parse.ParseCallback
import cool.dingstock.mine.dagger.MineApiHelper
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class FollowViewModel : AbsListViewModel() {
    @Inject
    lateinit var mineApi: MineApi

    init {
        MineApiHelper.apiMineComponent.inject(this)
    }

    var currentType: String? = ""
    var objectId: String? = null
    val dataList = MutableLiveData<List<AccountInfoBriefEntity>>()
    val resultLiveData = MutableLiveData<RequestState>()
    val loadLiveData = MutableLiveData<List<AccountInfoBriefEntity>>()
    val switchFollowResult = MutableLiveData<AccountInfoBriefEntity>()
    val followError = MutableLiveData<String>()
    var nextKey: Long? = 0
    fun refresh() {
        nextKey = null
        when (currentType) {
            FollowActivity.TYPE_STAR -> {
                requestFollowUser(true)
            }
            FollowActivity.TYPE_FOLLOWED -> {
                requestFansList(true)
            }
            FollowActivity.TYPE_FAVOR -> {
                requestFavorList(true)
            }
            FollowActivity.TYPE_FAVOR_COMMENT -> {
                requestFavorCommentList(true)
            }
            else -> {
                return
            }
        }
    }

    /**
     * 获取点赞人列表
     */
    private fun requestFavorList(isRefresh: Boolean) {
        if ((!isRefresh && nextKey == null) || objectId == null) {
            return
        }
        mineApi.requestFavorList(objectId!!, nextKey)
            .subscribe({ res ->
                if (!res.err) {
                    val data = res.res
                    if (data == null) {
                        resultLiveData.postValue(RequestState.Empty(isRefresh = isRefresh))
                        return@subscribe
                    }
                    data.let {
                        if (data.users.isNullOrEmpty()) {
                            nextKey = 0
                            resultLiveData.postValue(RequestState.Empty(isRefresh = isRefresh))
                            return@let
                        }
                        nextKey = it.nextKey
                        if (isRefresh) {
                            dataList.postValue(data.users)
                        } else {
                            loadLiveData.postValue(data.users)
                        }
                    }
                } else {
                    resultLiveData.postValue(RequestState.Error(isRefresh, res.msg))
                }
            }, { err ->
                resultLiveData.postValue(RequestState.Error(isRefresh, err.message ?: "网络连接错误"))
            })
    }

    /**
     * 获取点赞人评论列表
     */
    private fun requestFavorCommentList(isRefresh: Boolean) {
        if ((!isRefresh && nextKey == null) || objectId == null) {
            return
        }
        mineApi.requestFavorCommentList(objectId!!, nextKey)
            .subscribe({ res ->
                if (!res.err) {
                    val data = res.res
                    if (data == null) {
                        resultLiveData.postValue(RequestState.Empty(isRefresh = isRefresh))
                        return@subscribe
                    }
                    data.let {
                        if (data.users.isNullOrEmpty()) {
                            nextKey = 0
                            resultLiveData.postValue(RequestState.Empty(isRefresh = isRefresh))
                            return@let
                        }
                        nextKey = it.nextKey
                        if (isRefresh) {
                            dataList.postValue(data.users)
                        } else {
                            loadLiveData.postValue(data.users)
                        }
                    }
                } else {
                    resultLiveData.postValue(RequestState.Error(isRefresh, res.msg))
                }
            }, { err ->
                resultLiveData.postValue(RequestState.Error(isRefresh, err.message ?: "网络连接错误"))
            })
    }

    /**
     * 获取关注我的人列表
     */
    private fun requestFansList(isRefresh: Boolean) {
        if ((!isRefresh && nextKey == null) || objectId == null) {
            return
        }
        mineApi.requestFansList(objectId!!, nextKey)
            .subscribe({ res ->
                if (!res.err) {
                    val data = res.res
                    if (data == null) {
                        resultLiveData.postValue(RequestState.Empty(isRefresh = isRefresh))
                        return@subscribe
                    }
                    data.let {
                        if (data.users.isNullOrEmpty()) {
                            nextKey = 0
                            resultLiveData.postValue(RequestState.Empty(isRefresh = isRefresh))
                            return@let
                        }
                        nextKey = it.nextKey
                        if (isRefresh) {
                            dataList.postValue(data.users)
                        } else {
                            loadLiveData.postValue(data.users)
                        }
                    }
                } else {
                    resultLiveData.postValue(RequestState.Error(isRefresh, res.msg))
                }
            }, { err ->
                resultLiveData.postValue(RequestState.Error(isRefresh, err.message ?: "网络连接错误"))
            })
    }

    /**
     * 获取我关注的人列表
     */
    private fun requestFollowUser(isRefresh: Boolean) {
        if (!isRefresh && nextKey == null) {
            return
        }
        mineApi.requestFollowUser(objectId!!, nextKey)
            .subscribe({ res ->
                if (!res.err) {
                    val data = res.res
                    if (data == null) {
                        resultLiveData.postValue(RequestState.Empty(isRefresh = isRefresh))
                        return@subscribe
                    }
                    data.let {
                        if (data.users.isNullOrEmpty()) {
                            nextKey = 0
                            resultLiveData.postValue(RequestState.Empty(isRefresh = isRefresh))
                            return@let
                        }
                        nextKey = it.nextKey
                        if (isRefresh) {
                            dataList.postValue(data.users)
                        } else {
                            loadLiveData.postValue(data.users)
                        }
                    }
                } else {
                    resultLiveData.postValue(RequestState.Error(isRefresh, res.msg))
                }
            }, { err ->
                resultLiveData.postValue(RequestState.Error(isRefresh, err.message ?: "网络连接错误"))
            })
    }

    fun switchFollowState(userEntity: AccountInfoBriefEntity) {
        MineHelper.getInstance()
            .switchFollowState(!userEntity.followed, userEntity.id, object : ParseCallback<Int> {
                override fun onSucceed(data: Int?) {
                    userEntity.followed = !userEntity.followed
                    switchFollowResult.postValue(userEntity)
                    EventBus.getDefault()
                        .post(EventFollowerChange(userEntity.id, userEntity.followed, data ?: 0))
                    EventBus.getDefault().post(EventFollowChange())
                }

                override fun onFailed(errorCode: String?, errorMsg: String?) {
                    if (!TextUtils.isEmpty(errorMsg)) {
                        followError.postValue(errorMsg ?: "")
                    }
                }
            })

    }

    fun loadMoreData() {
        when (currentType) {
            FollowActivity.TYPE_STAR -> {
                requestFollowUser(false)
            }
            FollowActivity.TYPE_FOLLOWED -> {
                requestFansList(false)
            }
            FollowActivity.TYPE_FAVOR -> {
                requestFavorList(false)
            }
            FollowActivity.TYPE_FAVOR_COMMENT -> {
                requestFavorCommentList(false)
            }
            else -> {
                return
            }
        }
    }

    fun initType(type: String?) {
        this.currentType = type
    }
}


