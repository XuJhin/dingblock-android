package cool.dingstock.appbase.serviceloader

import android.content.Context
import cool.dingstock.appbase.share.ShareParams
import cool.dingstock.appbase.share.SharePlatform
import cool.dingstock.appbase.share.ShareType

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/13  10:19
 */

interface IShareServer {

    fun share(context: Context, shareType: ShareType)

    fun share(context: Context, shareType: ShareType,params: SharePlatform)

    fun share(context: Context, shareType: ShareType, needWindowBlack: Boolean)
}