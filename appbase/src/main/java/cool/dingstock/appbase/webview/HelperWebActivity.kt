package cool.dingstock.appbase.webview

import android.os.Bundle
import android.widget.TextView
import androidx.core.view.isVisible
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.R
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.constant.WebviewConstant
import cool.dingstock.appbase.custom.CustomerManager
import cool.dingstock.appbase.mvvm.activity.BaseViewModel


@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [WebviewConstant.Path.HELPER_CENTER]
)
class HelperWebActivity : DCWebViewActivity() {

    override fun getLayoutId(): Int = R.layout.activity_helper_web

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = BaseViewModel()
        super.onCreate(savedInstanceState)
        val rightTxtTv = findViewById<TextView>(R.id.webview_tv_right)
        rightTxtTv.isVisible = true
        rightTxtTv.setOnClickListener {
            CustomerManager.getInstance().showCustomServiceActivity(this)
        }
        tvTitle.text = "帮助与反馈"
    }

    override fun setTitleBarTitle(title: String) {
    }

}