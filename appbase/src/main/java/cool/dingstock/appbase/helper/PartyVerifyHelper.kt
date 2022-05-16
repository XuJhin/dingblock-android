package cool.dingstock.appbase.helper

import android.content.Context
import android.os.Bundle
import cool.dingstock.appbase.dagger.AppBaseApiHelper
import cool.dingstock.appbase.util.ClipboardHelper
import cool.dingstock.appbase.net.api.common.CommonApi
import cool.dingstock.appbase.constant.CommonConstant
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.lib_base.util.StringUtils
import javax.inject.Inject

/**
 * @author WhenYoung
 *  CreateAt Time 2020/12/4  17:05
 */
object PartyVerifyHelper {

    val net:Net by lazy {
        Net()
    }


    fun verify(context: Context){
        val copyMsg = ClipboardHelper.getCopyMsg(context)
        Logger.e("verifyCopyMsg",copyMsg)
        //判断不是刚刚本机生成的
        if (!StringUtils.isEmpty(copyMsg) && !ClipboardHelper.DingStockLabel.equals(ClipboardHelper.getCopyMsgLabel(context),true)) {
            if (copyMsg!!.contains("#DS")) { //满足活动的口令
                ClipboardHelper.copyMsg(context,"") {}
                net.commonApi.resolveParityWord(copyMsg)
                        .subscribe({
                            if(!it.err){
                                //显示dialog
                                val bundle = Bundle()
                                bundle.putParcelable(CommonConstant.Extra.PARTY_DIALOG_ENTITY,it.res)
                                HomeDialogHelper.showHighLevel(context, DialogIntent(CommonConstant.Uri.PARTY_DIALOG,10000,bundle))
                            }else{
                            }
                        },{
                        })
            }
        }
    }

    class Net{
        @Inject
        lateinit var commonApi: CommonApi

        init {
            AppBaseApiHelper.appBaseComponent.inject(this)
        }
    }

}