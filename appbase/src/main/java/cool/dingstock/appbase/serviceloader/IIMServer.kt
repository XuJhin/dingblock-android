package cool.dingstock.appbase.serviceloader

import android.content.Context


/**
 * 类名：IIMServer
 * 包名：cool.dingstock.appbase.serviceloader
 * 创建时间：2021/9/16 5:10 下午
 * 创建人： WhenYoung
 * 描述：
 **/
interface IIMServer {
    fun connectIm(
        token: String,
        onDatabaseOpened: (code: String) -> Unit,
        onSuccess: (s: String) -> Unit,
        onErr: (code: String) -> Unit
    )

    fun disConnectIm()

    fun logout()

    fun routeToConversationListActivity(context: Context,title:String)

    fun routeToConversationActivity(context: Context, type: Int, targetId: String)

    fun addOnReceiveMessageListener (listener: (String) -> Unit)

    fun setUserInfoProvider(userName: String, portraitUri: String?)

    fun refreshUserInfoCache(userId: String, userName: String, portraitUri: String?)

    fun addTag(tagId: String, tagName: String, onSuccess: () -> Unit, onRetry: () -> Unit)

    fun getTotalUnreadCount(totalCount: (Int) -> Unit)

    fun getUnreadCount(conversationType: Int, targetId: String, count: (Int) -> Unit)

    fun clearAllMessagesUnread()

    fun updateConversation(targetId: String, type: Int)
}