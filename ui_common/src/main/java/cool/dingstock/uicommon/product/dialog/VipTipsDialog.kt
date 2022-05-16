package cool.dingstock.uicommon.product.dialog

import android.content.Context
import android.view.View
import android.widget.ImageView
import cool.dingstock.appbase.constant.MineConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.dialog.BaseCenterDialog
import cool.dingstock.appbase.net.mobile.MobileHelper
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.uicommon.R


/**
 * 类名：VipTipsDIalog
 * 包名：cool.dingstock.uicommon.product.dialog
 * 创建时间：2021/7/7 2:54 下午
 * 创建人： WhenYoung
 * 描述：
 **/
class VipTipsDialog(context:Context) :BaseCenterDialog(context){

    override fun getLayoutId(): Int {
        return R.layout.vip_tips_dialog_layout
    }

    init {
        findViewById<ImageView>(R.id.dialog_cover)
            .setOnClickListener {
                UTHelper.commonEvent(UTConstant.Mine.VipP_ent,"source","短信登记入口")
                MobileHelper.getInstance().getCloudUrlAndRouter(context, MineConstant.VIP_CENTER)
                dismiss()
            }
        findViewById<View>(R.id.icon_close_vip_dialog).setOnShakeClickListener {
            dismiss()
        }
    }

}