package cool.dingstock.post.dialog

import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.circle.CircleApi
import cool.dingstock.appbase.net.api.circle.CircleHelper
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicBean
import cool.dingstock.appbase.entity.event.circle.EventDynamicRemove
import cool.dingstock.appbase.entity.event.circle.EventRemoveDynamicOfAccount
import cool.dingstock.appbase.entity.event.update.EventRefreshMineCollectionAndMinePage
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.post.dagger.PostApiHelper
import cool.dingstock.post.item.PostItemShowWhere
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class PostItemViewModel : BaseViewModel() {
    private var showWhere: PostItemShowWhere = PostItemShowWhere.Default

    @Inject
    lateinit var cirApi: CircleApi

    init {
        PostApiHelper.apiPostComponent.inject(this)
    }

    var postEntity: CircleDynamicBean? = null
    var postActionPosition: Int? = 0
    val actionList = MutableLiveData<List<Action>>()
    val overlayResult = MutableLiveData<PostOverlayType>()


    /**
     * 删除
     */
    fun deletePost() {
        val postId = getPostId()
        if (postId.isNullOrEmpty()) {
            return
        }
        val subscribe = cirApi.postDelete(postId)
                .subscribe({ res ->
                    val data = res.res
                    if (!res.err && data != null) {
                        getPostId()?.let { postId ->
                            EventBus.getDefault().post(EventDynamicRemove(postId))
                        }
                        EventBus.getDefault().post(EventRefreshMineCollectionAndMinePage())
                        overlayResult.postValue(
                                PostOverlayType.PostDeleteResult(
                                        true,
                                        data,
                                        postActionPosition
                                )
                        )
                    } else {
                        overlayResult.postValue(
                                PostOverlayType.PostDeleteResult(
                                        false,
                                        res.msg,
                                        postActionPosition
                                )
                        )
                    }
                }, { err ->
                    overlayResult.postValue(
                            PostOverlayType.PostDeleteResult(
                                    false,
                                    err.message,
                                    postActionPosition
                            )
                    )
                })
        addDisposable(subscribe)
    }

    /**
     * 对当前动态不感兴趣
     */
    fun lostInterested() {
        val postId = getPostId()
        if (postId.isNullOrEmpty()) {
            return
        }
        CircleHelper.getInstance().postBlock(postId)
                .subscribe({ res ->
                    if (!res.err) {
                        getPostId()?.let { postId ->
                            EventBus.getDefault().post(EventDynamicRemove(postId))
                        }
                        EventBus.getDefault().post(EventRefreshMineCollectionAndMinePage())
                        overlayResult.postValue(
                                PostOverlayType.PostBlockResult(
                                        true,
                                        res.res,
                                        postActionPosition
                                )
                        )
                    } else {
                        overlayResult.postValue(
                                PostOverlayType.PostBlockResult(
                                        false,
                                        res.msg,
                                        postActionPosition
                                )
                        )
                    }
                }, { err ->
                    overlayResult.postValue(
                            PostOverlayType.PostBlockResult(
                                    false,
                                    err.message,
                                    postActionPosition
                            )
                    )
                })
    }

    /**
     * 屏蔽用户
     */
    fun shield() {
        val userId = getUserID()
        userId?.let {
            cirApi.shieldAccount(it)
                    .subscribe({
                        EventBus.getDefault().post(EventRefreshMineCollectionAndMinePage())
                        EventBus.getDefault().post(EventRemoveDynamicOfAccount(userId))
                        overlayResult.postValue(
                                PostOverlayType.UserBlockResult(
                                        true,
                                        "已屏蔽",
                                        postActionPosition
                                )
                        )
                    }, { error ->
                        overlayResult.postValue(
                                PostOverlayType.UserBlockResult(
                                        false,
                                        error.message,
                                        postActionPosition
                                )
                        )
                    })
        }
    }

    /**
     * 取消屏蔽
     */
    fun cancelShield() {
        val userId = getUserID()
        userId?.let { it ->
            cirApi.cancelShield(it)
                    .subscribe({
                        EventBus.getDefault().post(EventRefreshMineCollectionAndMinePage())
                        EventBus.getDefault().post(EventRemoveDynamicOfAccount(userId))
                        overlayResult.postValue(
                                PostOverlayType.UserBlockResult(
                                        true,
                                        "已取消屏蔽",
                                        postActionPosition
                                )
                        )
                    }, {
                        overlayResult.postValue(
                                PostOverlayType.UserBlockResult(
                                        false,
                                        it.message,
                                        postActionPosition
                                )
                        )
                    })
        }
    }


    /**
     * 举报动态
     */
    fun report() {
        val postId = getPostId()
        postId?.let {
            CircleHelper.getInstance().postReport(it).subscribe({ res ->
                if (!res.err) {
                    overlayResult.postValue(
                            PostOverlayType.PostReportResult(
                                    true,
                                    res.res,
                                    postActionPosition
                            )
                    )
                } else {
                    overlayResult.postValue(
                            PostOverlayType.PostReportResult(
                                    false,
                                    res.msg,
                                    postActionPosition
                            )
                    )
                }
            }, { err ->
                overlayResult.postValue(
                        PostOverlayType.PostReportResult(
                                false,
                                err.message,
                                postActionPosition
                        )
                )
            })
        }
    }

    private fun getPostId(): String? {
        if (postEntity == null) {
            return null
        }
        val postId = postEntity?.id
        if (postId.isNullOrEmpty()) {
            return null
        }
        return postId
    }

    private fun getUserID(): String? {
        if (postEntity == null || postEntity?.user == null) {
            return null
        }
        val userId = postEntity?.user?.objectId
        if (userId.isNullOrEmpty()) {
            return null
        }
        return userId
    }

    fun initPostEntity(data: CircleDynamicBean?, position: Int?) {
        this.postEntity = data
        this.postActionPosition = position
        if (postEntity == null) {
            return
        }
        val user = LoginUtils.getCurrentUser()
        when {
            user == null -> {
                val list: MutableList<Action> = arrayListOf()
                list.add(Action.NotInterested)
                if (postEntity?.user?.isBlock == true) {
                    list.add(Action.ShieldingUsers)
                } else {
                    list.add(Action.CancelShielding)
                }
                list.add(Action.Report)
                actionList.postValue(list)
            }
            user.id == postEntity?.user?.objectId -> {
                val list: MutableList<Action> = arrayListOf()
                list.add(Action.Delete)
                actionList.postValue(list)
            }
            postEntity?.user?.followed == true -> {
                val list: MutableList<Action> = arrayListOf()
                list.add(Action.NotInterested)
                if (postEntity?.user?.isBlock == true) {
                    list.add(Action.ShieldingUsers)
                } else {
                    list.add(Action.CancelShielding)
                }
                list.add(Action.Report)
                actionList.postValue(list)
            }
            else -> {
                val list: MutableList<Action> = arrayListOf()
                list.add(Action.NotInterested)
                if (postEntity?.user?.isBlock == true) {
                    list.add(Action.ShieldingUsers)
                } else {
                    list.add(Action.CancelShielding)
                }
                list.add(Action.Report)
                actionList.postValue(list)
            }
        }
    }

    fun updateShowWhere(data: PostItemShowWhere) {
        this.showWhere = data
    }
}

enum class Action(var cnName: String, var id: Int) {
    NotInterested("不感兴趣", 1),
    CancelShielding("屏蔽用户", 2),
    Report("举报", 3),
    Delete("删除", 4),
    ShieldingUsers("取消屏蔽", 5);

    override fun toString(): String {
        return cnName
    }
}