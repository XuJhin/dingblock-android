package cool.dingstock.appbase.widget.dialog

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.R
import cool.dingstock.appbase.constant.CommonConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.mvvm.activity.VMActivity
import cool.dingstock.appbase.util.NotificationsUtils
import cool.dingstock.appbase.util.StatusBarUtil

/**
 *引导用户开启推送弹窗
 *
 */
@RouterUri(scheme = RouterConstant.SCHEME, host = RouterConstant.HOST, path = [CommonConstant.Path.PUSH_DIALOG])
class OpenPushDialog : VMActivity<BaseViewModel>() {

    private lateinit var titleTxt: TextView
    private lateinit var contentTxt: TextView
    private lateinit var btn: TextView
    private lateinit var iv: ImageView
    private lateinit var cancelBtn: TextView
    override fun setSystemStatusBar() {
        StatusBarUtil.transparentStatus(this)
        StatusBarUtil.setLightMode(this)
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        contentTxt = findViewById(R.id.common_push_dialog_content_txt)
        btn = findViewById(R.id.common_push_action_txt)
        cancelBtn = findViewById(R.id.common_push_next_tips)
        cancelBtn.setOnClickListener(View.OnClickListener { v: View? -> finish() })
        btn.setOnClickListener {
            NotificationsUtils.gotoSysNotification(this)
            finish()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_open_push
    }

    override fun initListeners() {
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0,R.anim.dialog_window_scale_out)
    }

    override fun moduleTag(): String {
        return "main"
    }
}