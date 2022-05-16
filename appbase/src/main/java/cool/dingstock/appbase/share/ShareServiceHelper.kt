package cool.dingstock.appbase.share

import android.content.Context
import com.sankuai.waimai.router.Router
import cool.dingstock.appbase.serviceloader.IShareServer

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/13  10:25
 */
object ShareServiceHelper {
    const val SHARE_SERVICE_KEY = "share_service_key"

    fun share(context: Context, shareType: ShareType) {
        val service = Router.getService(IShareServer::class.java, SHARE_SERVICE_KEY)
        service?.share(context, shareType)
    }
    fun share(context: Context, shareType: ShareType,params: SharePlatform) {
        val service = Router.getService(
            IShareServer::class.java,
            SHARE_SERVICE_KEY
        )
        service?.share(context, shareType,params)
    }

    fun share(context: Context, shareType: ShareType, needWindowBlack: Boolean) {
        val service = Router.getService(IShareServer::class.java, SHARE_SERVICE_KEY)
        service?.share(context, shareType,needWindowBlack)
    }

}