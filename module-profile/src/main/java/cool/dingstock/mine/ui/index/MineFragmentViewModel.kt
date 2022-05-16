package cool.dingstock.mine.ui.index

import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicBean
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicDetailUserBean
import cool.dingstock.appbase.entity.bean.mine.VipPrivilegeEntity
import cool.dingstock.appbase.entity.event.circle.EventFollowerChange
import cool.dingstock.appbase.entity.event.circle.EventRemoveDynamicOfAccount
import cool.dingstock.appbase.entity.event.relation.EventFollowChange
import cool.dingstock.appbase.entity.event.update.EventRefreshMineCollectionAndMinePage
import cool.dingstock.appbase.list.AbsListViewModel
import cool.dingstock.appbase.mvvm.SingleLiveEvent
import cool.dingstock.appbase.net.api.account.AccountApi
import cool.dingstock.appbase.net.api.circle.CircleApi
import cool.dingstock.appbase.net.api.mine.MineApi
import cool.dingstock.appbase.net.api.mine.MineHelper
import cool.dingstock.appbase.net.parse.ParseCallback
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.mine.dagger.MineApiHelper
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class MineFragmentViewModel : AbsListViewModel() {
    @Inject
    lateinit var circleApi: CircleApi

    @Inject
    lateinit var mineApi: MineApi

    @Inject
    lateinit var accountApi: AccountApi

    init {
        MineApiHelper.apiMineComponent.inject(this)
    }

    var userId: String = ""
        get() {
            if (isMine) {
                userId = LoginUtils.getCurrentUser()?.id ?: ""
            }
            return field
        }
    val followResult = SingleLiveEvent<String>()
    val shieldResult = SingleLiveEvent<String>()
    val cancelFollow = SingleLiveEvent<Boolean>()
    val cancelShield = SingleLiveEvent<Boolean>()
    val isMineSelfPage = SingleLiveEvent<Boolean>()

    val isHaveNewMedal = SingleLiveEvent<Boolean>()

    //v1.7.8版本以后进行调整
    val accountInfoEntityLiveData = SingleLiveEvent<CircleDynamicDetailUserBean>()
    var refreshLayoutLiveEvent = MutableLiveData<Boolean>()
    val vipPrivilegeLiveData = MutableLiveData<ArrayList<VipPrivilegeEntity>>()

    var isRefresh: Boolean = false
    var isMine = false

    fun syncVipHint() {
        mineApi.vipPrivilege()
            .subscribe({
                if (!it.err && it.res != null) {
                    it.res?.let { data -> vipPrivilegeLiveData.postValue(data) }
                }
            }, {

            })
    }

    fun refreshData(isFirstLoad: Boolean) {
        isRefresh = true
        if (userId.isEmpty()) {
            return
        }
        fetchSelfBrief(userId, isFirstLoad)
    }


    fun updateUserId(objectId: String) {
        this.userId = objectId
    }

    fun followUser(ObjectId: String) {
        MineHelper.getInstance().switchFollowState(true, ObjectId, object : ParseCallback<Int> {
            override fun onSucceed(data: Int?) {
                followResult.postValue(ObjectId)
                EventBus.getDefault().post(EventFollowerChange(ObjectId, true, data ?: 0))
            }

            override fun onFailed(errorCode: String?, errorMsg: String?) {
            }
        })
    }

    fun shieldUser(userId: String) {
        circleApi.shieldAccount(userId)
            .subscribe({
                if (it.err) {
                    shieldResult.postValue("屏蔽失败")
                } else {
                    shieldResult.postValue(userId)
                    EventBus.getDefault().post(EventRefreshMineCollectionAndMinePage())
                    EventBus.getDefault().post(EventRemoveDynamicOfAccount(userId))
                }
            }, { shieldResult.postValue(it.message) })
    }

    fun cancelFollow(userId: String) {
        MineHelper.getInstance().switchFollowState(false, userId, object : ParseCallback<Int> {
            override fun onSucceed(data: Int?) {
                cancelFollow.postValue(true)
                val bean = CircleDynamicBean()
                EventBus.getDefault().post(EventFollowerChange(userId, false, data ?: 0))
                EventBus.getDefault().post(EventFollowChange())
            }

            override fun onFailed(errorCode: String?, errorMsg: String?) {
                cancelFollow.postValue(false)
            }
        })
    }

    /**
     * 取消屏蔽
     */
    fun cancelShield(userId: String) {
        circleApi.cancelShield(userId)
            .subscribe({
                if (!it.err) {
                    cancelShield.postValue(true)
                    EventBus.getDefault().post(EventRefreshMineCollectionAndMinePage())
                } else {
                    cancelShield.postValue(false)
                }
            }, {
                cancelShield.postValue(false)
            })
    }

    fun fetchSelfBrief(userId: String, isFirstLoad: Boolean = false) {
        if (this.userId.isEmpty()) {
            return
        }
        if (this.userId == LoginUtils.getCurrentUser()?.id) {
            isMineSelfPage.postValue(true)
            accountApi.getUserByNet()
                .subscribe({
                    fetchAccountInfo(userId, isFirstLoad)
                }, {
                    fetchAccountInfo(userId, isFirstLoad)
                })
            fetchAccountInfo(userId, isFirstLoad)
        } else {
            isMineSelfPage.postValue(false)
            fetchAccountInfo(userId, isFirstLoad)
        }
    }

    /**
     * 获取自己的信息
     */
    private fun fetchSelf() {
        mineApi.fetchSelf().subscribe {
            if (!it.err) {
                it.res?.let { dcLoginUser ->
                    LoginUtils.updateUser(dcLoginUser)
                }
            }
        }
    }

    private fun fetchAccountInfo(userId: String, isFirstLoad: Boolean) {
        mineApi.fetchSelfDetail(userId)
            .subscribe({
                refreshLayoutLiveEvent.postValue(true)
                if (it.err) {
                    if (isFirstLoad) {
                        postStateError(it.msg)
                    }
                } else {
                    it.res?.userInfo?.let { entity ->
                        postStateSuccess("加载成功")
                        accountInfoEntityLiveData.postValue(entity)
                    }
                }
            }, { error ->
                refreshLayoutLiveEvent.postValue(false)
                if (isFirstLoad) {
                    postStateError(error.message ?: "")
                }
            })
    }

    fun isHaveNewMedal() {
        mineApi.isHaveNewMedal()
            .subscribe({
                if (!it.err) {
                    isHaveNewMedal.postValue(it.res?.receivable ?: false)
                } else {
                    isHaveNewMedal.postValue(false)
                }
            }, {
                isHaveNewMedal.postValue(false)
            })
    }
}