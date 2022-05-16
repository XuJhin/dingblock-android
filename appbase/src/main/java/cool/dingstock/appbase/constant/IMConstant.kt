package cool.dingstock.appbase.constant

import android.os.Build
import cool.dingstock.appbase.BuildConfig


/**
 * 类名：IMConstant
 * 包名：cool.dingstock.appbase.constant
 * 创建时间：2021/9/16 10:23 上午
 * 创建人： WhenYoung
 * 描述：
 **/
object IMConstant {

    object TAG {
        const val TAG_ID = "TAG_ID"
        const val TAG_STRANGER = "dcStranger"
        const val TAG_NAME_STRANGER = "陌生人"
        const val TAG_FRIEND = "dcFriend"
        const val TAG_NAME_FRIEND = "好友"
        const val SYSTEM_SALES = "systemSales"
        const val LAST_MESSAGE_ID = "last_message_id"
        const val SYSTEM_NOTICE_TITLE = "SYSTEM_NOTICE_TITLE"
    }

    object Path {
        const val CONVERSATION = "/im/conversation"
        const val SYSTEM_NOTICE = "/im/system/notice"
    }

    object Uri {
        val CONVERSATION = RouterConstant.getSchemeHost() + Path.CONVERSATION
        val SYSTEM_NOTICE = RouterConstant.getSchemeHost() + Path.SYSTEM_NOTICE
    }


}