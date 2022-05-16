package cool.mobile.account.share

import android.content.Context
import com.sankuai.waimai.router.annotation.RouterService
import cool.dingstock.appbase.serviceloader.IShareServer
import cool.dingstock.appbase.share.SharePlatform
import cool.dingstock.appbase.share.ShareServiceHelper
import cool.dingstock.appbase.share.ShareType

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/13  10:21
 */
@RouterService(interfaces = [IShareServer::class],key = [ShareServiceHelper.SHARE_SERVICE_KEY],singleton = true)
class ShareServerImp : IShareServer{

    override fun share(context: Context, shareType: ShareType) {
        val shareDialog = ShareDialog(context)
        shareDialog.shareType = shareType
        shareDialog.show()
    }

    override fun share(context: Context, shareType: ShareType, params: SharePlatform) {
        ShareHelper().share(context,shareType,params)
    }


    override fun share(context: Context, shareType: ShareType, needWindowBlack: Boolean) {
        val shareDialog = ShareDialog(context,needWindowBlack)
        shareDialog.shareType = shareType
        shareDialog.show()
    }

}