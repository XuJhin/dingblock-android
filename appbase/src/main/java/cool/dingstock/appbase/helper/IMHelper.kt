package cool.dingstock.appbase.helper

import android.content.Context
import android.widget.Toast
import com.sankuai.waimai.router.Router
import cool.dingstock.appbase.constant.IMConstant
import cool.dingstock.appbase.dagger.AppBaseApiHelper
import cool.dingstock.appbase.entity.event.im.EventSystemNotice
import cool.dingstock.appbase.net.api.find.FindApi
import cool.dingstock.appbase.net.api.im.IMApi
import cool.dingstock.appbase.net.api.mine.MineApi
import cool.dingstock.appbase.serviceloader.IIMServer
import cool.dingstock.appbase.toast.TopToast
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.lib_base.BaseLibrary
import cool.dingstock.lib_base.stroage.ConfigSPHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * 类名：IMHelper
 * 包名：cool.dingstock.im.helper
 * 创建时间：2021/9/16 4:57 下午
 * 创建人： WhenYoung
 * 描述：
 **/

open class BaseIMHelper {
    @Inject
    lateinit var imApi: IMApi

    @Inject
    lateinit var mineApi: MineApi

    @Inject
    lateinit var findApi: FindApi

    init {
        AppBaseApiHelper.appBaseComponent.inject(this)
    }
}

object IMHelper: BaseIMHelper() {
    const val IM_SERVICE_KEY = "im_service_key"

    val service: IIMServer by lazy {
        Router.getService(
            IIMServer::class.java,
            IM_SERVICE_KEY
        )
    }
    //关注id列表
    val followIdList = mutableListOf<String>()
    //被关注id列表
    val beFollowIdList = mutableListOf<String>()
    //打招呼id列表
    val greetedIdList = mutableListOf<String>()
    //被打招呼id列表
    val beGreetedIdList = mutableListOf<String>()
    //白名单id列表
    private val whiteListIdList = mutableListOf<String>()

    var retryCount = 0
    var reportDelay = 0L
    var reportRetryCount = 0
    var imReconnectCount = 0

    var receiveMessage = false
    var receiveStrangerMessage = false
    var state: String? = null
    var isBlock: Boolean = false
    var isBeBlocked: Boolean = false

    private val schedule: ScheduledExecutorService = Executors.newScheduledThreadPool(8)


    fun getUserRelationList(
        onDatabaseOpened: (code: String) -> Unit,
        onSuccess: (s: String) -> Unit,
        onErr: (code: String) -> Unit
    ) {
        LoginUtils.getCurrentUser()?.let { user ->
            user.id?.let { id ->
                imApi.getUserRelationList(id)
                    .subscribe ({
                        if (!it.err) {
                            it.res?.let { relationBean ->
                                followIdList.clear()
                                beFollowIdList.clear()
                                greetedIdList.clear()
                                beGreetedIdList.clear()
                                whiteListIdList.clear()
                                relationBean.followIds?.run { followIdList.addAll(this) }
                                relationBean.fansIds?.run { beFollowIdList.addAll(this) }
                                relationBean.sentToStrangerIds?.run { greetedIdList.addAll(this) }
                                relationBean.receivedFromStrangerIds?.run { beGreetedIdList.addAll(this) }
                                relationBean.whiteList?.run { whiteListIdList.addAll(this) }
                                connectIm(onDatabaseOpened, onSuccess, onErr)
                            }
                        } else {
                            getUserRelationListError(onDatabaseOpened, onSuccess, onErr)
                        }
                    }, { err ->
                        err.printStackTrace()
                        getUserRelationListError(onDatabaseOpened, onSuccess, onErr)
                    })
            }
        }
    }

    private fun getUserRelationListError(
        onDatabaseOpened: (code: String) -> Unit,
        onSuccess: (s: String) -> Unit,
        onErr: (code: String) -> Unit
    ) {
        if (retryCount < 3) {
            retryCount++
            getUserRelationList(onDatabaseOpened, onSuccess, onErr)
        } else {
            connectIm(onDatabaseOpened, onSuccess, onErr)
        }
    }


    fun reportMessageToStranger(targetId: String) {
        schedule.schedule({
            imApi.reportMessageToStranger(targetId)
                .subscribe({
                    if (it.err) {
                        reportRetry(targetId)
                    } else {
                        reportRetryCount = 0
                        reportDelay = 0
                    }
                }, { err ->
                    err.printStackTrace()
                    reportRetry(targetId)
                })
        }, reportDelay, TimeUnit.SECONDS)
    }

    private fun reportRetry(
        targetId: String
    ) {
        if (reportRetryCount < 5) {
            if (reportDelay == 0L) {
                reportDelay = 5L
            } else {
                reportDelay *= 2
            }
            reportRetryCount++
            reportMessageToStranger(targetId)
        } else {
            reportRetryCount = 0
            reportDelay = 0
        }
    }

    suspend fun requestFollowEachOtherList(userId: String) {
        flow {
            emit(findApi.requestNotificationSettingList(userId))
        }.flowOn(Dispatchers.IO)
            .catch { e ->
                e.printStackTrace()
            }
            .collectLatest { res ->
                if (!res.err) {
                    res.res?.let {
                        receiveMessage = it.receiveMessage
                        receiveStrangerMessage = it.receiveStrangerMessage
                    }
                }
            }
    }

    private fun refreshIMToken(
        onDatabaseOpened: (code: String) -> Unit,
        onSuccess: (s: String) -> Unit,
        onErr: (code: String) -> Unit
    ) {
        imApi.refreshIMToken().subscribe({
            if (!it.err) {
                it.res?.let { token ->
                    LoginUtils.getCurrentUser()?.let { user ->
                        LoginUtils.updateUser(user.apply { imToken = token })
                    }
                    connectIm(onDatabaseOpened, onSuccess, onErr)
                }
            }
        }, { e ->
            e.printStackTrace()
        })
    }

    private fun connectIm(
        onDatabaseOpened: (code: String) -> Unit,
        onSuccess: (s: String) -> Unit,
        onErr: (code: String) -> Unit
    ) {
        LoginUtils.getCurrentUser()?.let {
            state = it.state
            if (it.imToken.isNullOrEmpty()) {
                refreshIMToken(onDatabaseOpened, onSuccess, onErr)
            } else {
                service.connectIm(it.imToken!!, onDatabaseOpened, {
                    imReconnectCount = 0
                    schedule.schedule( {
                        addTag(IMConstant.TAG.TAG_STRANGER, IMConstant.TAG.TAG_NAME_STRANGER) {}
                    }, 1000, TimeUnit.MILLISECONDS)
                    schedule.schedule( {
                        addTag(IMConstant.TAG.TAG_FRIEND, IMConstant.TAG.TAG_NAME_FRIEND) {}
                    }, 2000, TimeUnit.MILLISECONDS)
                }) { error ->
                    onErr(error)
                    when(error) {
                        "RC_CONN_TOKEN_EXPIRE", "RC_CONN_TOKEN_INCORRECT" -> {
                            if(imReconnectCount <= 5){
                                schedule.schedule({
                                    imReconnectCount++
                                    refreshIMToken(onDatabaseOpened, onSuccess, onErr)
                                }, 3, TimeUnit.SECONDS)
                            }
                        }
                        "RC_CONNECT_TIMEOUT" -> {
                            TopToast.INSTANCE.showToast(BaseLibrary.getInstance().context, "连接超时", Toast.LENGTH_SHORT)
                        }
                    }
                }
            }
        }
    }

     fun isWhiteUser(targetId: String): Boolean {
        var whiteUser = false
        LoginUtils.getCurrentUser()?.let {
            if (whiteListIdList.contains(it.id)) {
                whiteUser = true
            }
        }
        if (whiteListIdList.contains(targetId)) {
            whiteUser = true
        }
        return whiteUser
    }

     fun mineIsWhiteUser(): Boolean {
        var whiteUser = false
        LoginUtils.getCurrentUser()?.let {
            if (whiteListIdList.contains(it.id)) {
                whiteUser = true
            }
        }
        return whiteUser
    }

    fun disConnectIm() {
        service.disConnectIm()
    }

    fun logout() {
        service.logout()
    }

    fun routeToConversationListActivity(context: Context, title: String) {
        service.routeToConversationListActivity(context, title)
    }

    fun routeToConversationActivity(context: Context, type: Int, targetId: String){
        service.routeToConversationActivity(context, type, targetId)
    }

    fun addOnReceiveMessageListener(listener: (String) -> Unit) {
        service.addOnReceiveMessageListener(listener)
    }

    fun setUserInfoProvider(userName: String, portraitUri: String) {
        service.setUserInfoProvider(userName, portraitUri)
    }

    fun refreshUserInfoCache(userId: String, userName: String, portraitUri: String) {
        service.refreshUserInfoCache(userId, userName, portraitUri)
    }

    fun addTag(tagId: String, tagName: String, onSuccess:() -> Unit) {
        service.addTag(tagId, tagName, onSuccess) {
            addTagRetry(tagId, tagName, onSuccess)
        }
    }

    private fun addTagRetry(tagId: String, tagName: String, onSuccess: () -> Unit, ) {
        schedule.schedule( { addTag(tagId, tagName, onSuccess) }, 1000, TimeUnit.MILLISECONDS)
    }

    fun getTotalUnreadCount(count: (Int) -> Unit) {
        service.getTotalUnreadCount(count)
    }

    fun getUnreadCount(conversationType: Int, targetId: String, count: (Int) -> Unit) {
        service.getUnreadCount(conversationType, targetId, count)
    }

    fun clearAllMessagesUnread() {
        service.clearAllMessagesUnread()
    }

    fun updateConversation(targetId: String, type: Int) {
        service.updateConversation(targetId, type)
    }
}