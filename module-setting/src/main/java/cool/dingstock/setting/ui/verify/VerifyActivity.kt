package cool.dingstock.setting.ui.verify

import android.os.Bundle
import androidx.lifecycle.Observer
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.constant.SettingConstant
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.widget.TitleBar
import cool.dingstock.uicommon.setting.R
import cool.dingstock.uicommon.setting.databinding.ActivityVerifyBinding

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [SettingConstant.Path.VERIFY]
)
class VerifyActivity : VMBindingActivity<VerifyViewModel, ActivityVerifyBinding>() {

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        findViewById<TitleBar>(R.id.title_bar)
            .apply {
                title = "认证申请"
                setLeftIcon(R.drawable.ic_icon_nav_back)
            }
        updateUi()
    }

    private fun updateUi() {
        viewModel.apply {
            successLiveData.observe(this@VerifyActivity, Observer { url ->
                hideLoadingDialog()
                DcRouter(url).start()
            })
            errorLiveData.observe(this@VerifyActivity, Observer {
                showToastShort(it)
            })
        }
    }

    override fun initListeners() {
        viewBinding.tvVerify.setOnClickListener {
            showLoadingDialog()
            viewModel.getUrl()
        }
    }

    override fun moduleTag(): String {
        return ModuleConstant.SETTING
    }


}
